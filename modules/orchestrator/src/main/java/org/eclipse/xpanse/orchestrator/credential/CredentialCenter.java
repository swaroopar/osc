/*
 * SPDX-License-Identifier: Apache-2.0
 * SPDX-FileCopyrightText: Huawei Inc.
 *
 */

package org.eclipse.xpanse.orchestrator.credential;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.xpanse.modules.credential.AbstractCredentialInfo;
import org.eclipse.xpanse.modules.credential.CreateCredential;
import org.eclipse.xpanse.modules.credential.CredentialVariable;
import org.eclipse.xpanse.modules.credential.CredentialVariables;
import org.eclipse.xpanse.modules.credential.CredentialsStore;
import org.eclipse.xpanse.modules.credential.enums.CredentialType;
import org.eclipse.xpanse.modules.models.enums.Csp;
import org.eclipse.xpanse.modules.models.exceptions.CredentialVariablesNotComplete;
import org.eclipse.xpanse.orchestrator.OrchestratorPlugin;
import org.eclipse.xpanse.orchestrator.OrchestratorService;
import org.eclipse.xpanse.orchestrator.utils.CredentialApiUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

/**
 * The credential center.
 */
@Component
public class CredentialCenter {

    private final OrchestratorService orchestratorService;
    private final CredentialsStore credentialsStore;
    private final CredentialApiUtil credentialApiUtil;

    /**
     * Constructor of CredentialCenter.
     */
    @Autowired
    public CredentialCenter(
            OrchestratorService orchestratorService,
            CredentialsStore credentialsStore,
            CredentialApiUtil credentialApiUtil) {
        this.orchestratorService = orchestratorService;
        this.credentialsStore = credentialsStore;
        this.credentialApiUtil = credentialApiUtil;
    }

    /**
     * List the available credential types by @Csp.
     *
     * @param csp The cloud service provider.
     * @return Returns list of credential types.
     */
    public List<CredentialType> getAvailableCredentialTypesByCsp(Csp csp) {
        OrchestratorPlugin plugin = orchestratorService.getOrchestratorPlugin(csp);
        return plugin.getAvailableCredentialTypes();
    }

    /**
     * List the credential capabilities by @Csp and @type.
     *
     * @param csp  The cloud service provider.
     * @param type The type of credential.
     * @return Returns list of credential capabilities.
     */
    public List<AbstractCredentialInfo> getCredentialCapabilitiesByCsp(Csp csp,
                                                                       CredentialType type) {
        OrchestratorPlugin plugin = orchestratorService.getOrchestratorPlugin(csp);
        if (Objects.isNull(type)) {
            return plugin.getCredentialDefinitions();
        }
        return plugin.getCredentialDefinitions().stream().filter(credential ->
                        Objects.equals(credential.getType(), type))
                .collect(Collectors.toList());
    }

    /**
     * Get credential openApi Url.
     *
     * @param csp  The cloud service provider.
     * @param type The type of credential.
     * @return Returns credential openApi Url.
     */
    public String getCredentialOpenApiUrl(Csp csp, CredentialType type) {
        return credentialApiUtil.getCredentialOpenApiUrl(csp, type);
    }

    /**
     * List the credential info of the @csp and @xpanseUser and @type.
     *
     * @param csp                     The cloud service provider.
     * @param xpanseUser              The user who provided the credential info.
     * @param requestedCredentialType The type of the credential.
     * @return the credentials.
     */
    public List<AbstractCredentialInfo> getCredentials(
            Csp csp, String xpanseUser, CredentialType requestedCredentialType) {
        List<AbstractCredentialInfo> abstractCredentialInfos = new ArrayList<>();
        if (Objects.nonNull(requestedCredentialType)) {
            AbstractCredentialInfo abstractCredentialInfo =
                    this.credentialsStore.getCredential(csp, requestedCredentialType, xpanseUser);
            if (Objects.nonNull(abstractCredentialInfo)) {
                checkNullParamAndFillValueFromEnv(abstractCredentialInfo);
                abstractCredentialInfos.add(abstractCredentialInfo);
            } else {
                addCredentialInfoFromEnv(csp, abstractCredentialInfos);
            }
        } else {
            for (CredentialType credentialType : CredentialType.values()) {
                AbstractCredentialInfo abstractCredentialInfo =
                        this.credentialsStore.getCredential(csp, credentialType, xpanseUser);
                if (Objects.nonNull(abstractCredentialInfo)) {
                    checkNullParamAndFillValueFromEnv(abstractCredentialInfo);
                    abstractCredentialInfos.add(abstractCredentialInfo);
                }
            }
            if (CollectionUtils.isEmpty(abstractCredentialInfos)) {
                addCredentialInfoFromEnv(csp, abstractCredentialInfos);
            }
        }
        maskSensitiveValues(abstractCredentialInfos);
        return abstractCredentialInfos;
    }

    /**
     * Add credential info for the csp.
     *
     * @param csp              The cloud service provider.
     * @param createCredential The credential for the service.
     * @return Deployment bean for the provided deployerKind.
     */
    public boolean addCredential(Csp csp, CreateCredential createCredential) {
        createCredential.setCsp(csp);
        checkInputCredentialIsValid(createCredential);
        AbstractCredentialInfo credential;
        if (createCredential.getType() == CredentialType.VARIABLES) {
            credential = new CredentialVariables(createCredential);
        } else {
            throw new IllegalStateException(
                    String.format("Not supported credential type Csp:%s, Type: %s.",
                            csp, createCredential.getType()));
        }
        return createCredential(credential.getCsp(), credential.getXpanseUser(),
                credential);
    }

    /**
     * Update credential info for the service.
     *
     * @param csp              The cloud service provider.
     * @param updateCredential The credential for the service.
     * @return true of false.
     */
    public boolean updateCredential(Csp csp, CreateCredential updateCredential) {
        updateCredential.setCsp(csp);
        checkInputCredentialIsValid(updateCredential);
        AbstractCredentialInfo credential;
        if (updateCredential.getType() == CredentialType.VARIABLES) {
            credential = new CredentialVariables(updateCredential);
        } else {
            throw new IllegalStateException(
                    String.format("Not supported credential type Csp:%s, Type: %s.",
                            csp, updateCredential.getType()));
        }
        deleteCredentialByType(csp, updateCredential.getXpanseUser(), updateCredential.getType());
        return createCredential(csp, updateCredential.getXpanseUser(), credential);
    }

    /**
     * Delete credential for the @Csp with @xpanseUser.
     *
     * @param csp        The cloud service provider.
     * @param xpanseUser The user who provided the credential info.
     * @return true of false.
     */
    public boolean deleteCredential(Csp csp, String xpanseUser, CredentialType credentialType) {
        credentialsStore.deleteCredential(csp, credentialType, xpanseUser);
        return true;
    }

    /**
     * Delete credential for the @Csp with @xpanseUser.
     *
     * @param csp        The cloud service provider.
     * @param xpanseUser The user who provided the credential info.
     */
    public void deleteCredentialByType(Csp csp, String xpanseUser, CredentialType
            credentialType) {
        credentialsStore.deleteCredential(csp, credentialType, xpanseUser);
    }

    /**
     * Get credential for the @Csp with @xpanseUser.
     *
     * @param csp        The cloud service provider.
     * @param xpanseUser The user who provided the credential info.
     */
    public AbstractCredentialInfo getCredential(Csp csp,
                                                String xpanseUser,
                                                CredentialType credentialType) {
        AbstractCredentialInfo credentialVariables =
                credentialsStore.getCredential(csp, credentialType, xpanseUser);
        if (Objects.nonNull(credentialVariables)) {
            return credentialVariables;
        }
        AbstractCredentialInfo credentialInfo = findCredentialInfoFromEnv(csp);
        if (Objects.isNull(credentialInfo)) {
            throw new IllegalStateException(
                    String.format("No credential information found for the given Csp:%s.", csp));
        }
        return credentialInfo;
    }

    /**
     * Check the input credential whether is valid.
     *
     * @param inputCredential The credential to create or update.
     */
    public void checkInputCredentialIsValid(CreateCredential inputCredential) {

        // filter defined credential abilities by csp and type.
        List<AbstractCredentialInfo> credentialAbilities =
                getCredentialCapabilitiesByCsp(inputCredential.getCsp(), inputCredential.getType());

        if (CollectionUtils.isEmpty(credentialAbilities)) {
            throw new IllegalArgumentException(
                    String.format("Defined credentials with type %s provided by csp %s not found.",
                            inputCredential.getType(), inputCredential.getType()));
        }
        // filter defined credential abilities by name.
        List<AbstractCredentialInfo> sameNameAbilities = credentialAbilities.stream().filter(
                credentialAbility -> StringUtils.equals(credentialAbility.getName(),
                        inputCredential.getName())).toList();
        if (CollectionUtils.isEmpty(sameNameAbilities)) {
            throw new IllegalArgumentException(
                    String.format("Defined credentials with type %s and name %s "
                                    + "provided by csp %s not found.",
                            inputCredential.getType(), inputCredential.getName(),
                            inputCredential.getType()));
        }
        // check all fields in the input credential are valid based on the defined credentials.
        for (AbstractCredentialInfo credentialAbility : credentialAbilities) {
            if (CredentialType.VARIABLES.equals(credentialAbility.getType())) {
                Set<String> errorReasons = new HashSet<>();
                CredentialVariables inputVariables = new CredentialVariables(inputCredential);
                CredentialVariables definedVariables = (CredentialVariables) credentialAbility;
                Set<String> definedVariableNameSet =
                        definedVariables.getVariables().stream()
                                .map(CredentialVariable::getName).collect(Collectors.toSet());
                Set<String> inputVariableNameSet = inputVariables.getVariables().stream()
                        .map(CredentialVariable::getName).collect(Collectors.toSet());
                if (!inputVariableNameSet.containsAll(definedVariableNameSet)) {
                    definedVariableNameSet.removeAll(inputVariableNameSet);
                    errorReasons.add(String.format("Missing variables with names %s.",
                            definedVariableNameSet));
                }
                Set<String> definedMandatoryVariableNameSet =
                        definedVariables.getVariables().stream()
                                .filter(CredentialVariable::isMandatory)
                                .map(CredentialVariable::getName).collect(Collectors.toSet());
                for (CredentialVariable inputVariable : inputCredential.getVariables()) {
                    if (definedMandatoryVariableNameSet.contains(inputVariable.getName())
                            && StringUtils.isBlank(inputVariable.getValue())) {
                        errorReasons.add(
                                String.format("The value of mandatory variable with name %s"
                                        + " could not be empty.", inputVariable.getName()));
                    }
                }
                if (!errorReasons.isEmpty()) {
                    throw new CredentialVariablesNotComplete(errorReasons);
                }
                return;
            }
        }
    }


    /**
     * Create credential for the @Csp with @xpanseUser.
     *
     * @param csp        The cloud service provider.
     * @param xpanseUser The user who provided the credential info.
     * @return true of false.
     */

    public boolean createCredential(Csp csp, String xpanseUser,
                                    AbstractCredentialInfo abstractCredentialInfo) {
        credentialsStore.storeCredential(csp, xpanseUser, abstractCredentialInfo);
        return true;
    }

    /**
     * Get credentialInfo from the environment using @Csp.
     *
     * @param csp The cloud service provider.
     * @return Returns credentialInfo.
     */
    private AbstractCredentialInfo findCredentialInfoFromEnv(Csp csp) {
        List<AbstractCredentialInfo> credentialAbilities =
                getCredentialCapabilitiesByCsp(csp, null);
        if (CollectionUtils.isEmpty(credentialAbilities)) {
            return null;
        }
        for (AbstractCredentialInfo credentialAbility : credentialAbilities) {
            if (CredentialType.VARIABLES == credentialAbility.getType()) {
                CredentialVariables credentialVariables = (CredentialVariables) credentialAbility;
                List<CredentialVariable> variables = credentialVariables.getVariables();
                for (CredentialVariable variable : variables) {
                    String envValue = System.getenv(variable.getName());
                    if (StringUtils.isNotBlank(envValue)) {
                        variable.setValue(envValue);
                    }
                }
                // Check if all variables have been successfully set.
                if (!isAnyMandatoryCredentialVariableMissing(credentialVariables)) {
                    return credentialAbility;
                }
            }
        }
        return null;
    }

    private boolean isAnyMandatoryCredentialVariableMissing(
            CredentialVariables credentialVariables) {
        return credentialVariables.getVariables().stream()
                .anyMatch(credentialVariable -> credentialVariable.isMandatory()
                        && Objects.isNull(credentialVariable.getValue()));
    }

    private void checkNullParamAndFillValueFromEnv(AbstractCredentialInfo abstractCredentialInfo) {
        if (abstractCredentialInfo.getType().equals(CredentialType.VARIABLES)) {
            CredentialVariables credentialVariables =
                    (CredentialVariables) abstractCredentialInfo;
            for (CredentialVariable variable : credentialVariables.getVariables()) {
                if (Objects.isNull(variable.getValue())
                        || Objects.equals(variable.getValue(), "null")) {
                    variable.setValue(System.getenv(variable.getName()));
                }
            }
        }
    }

    private void addCredentialInfoFromEnv(Csp csp,
                                          List<AbstractCredentialInfo> abstractCredentialInfos) {
        AbstractCredentialInfo credentialInfoFromEnv = findCredentialInfoFromEnv(csp);
        if (Objects.nonNull(credentialInfoFromEnv)) {
            abstractCredentialInfos.add(credentialInfoFromEnv);
        }
    }

    private void maskSensitiveValues(List<AbstractCredentialInfo> abstractCredentialInfos) {
        for (AbstractCredentialInfo abstractCredentialInfo : abstractCredentialInfos) {
            CredentialVariables credentialVariables =
                    (CredentialVariables) abstractCredentialInfo;
            List<CredentialVariable> variables = credentialVariables.getVariables();
            for (CredentialVariable variable : variables) {
                if (variable.isSensitive()) {
                    variable.setValue("*********");
                }
            }
        }
    }
}
