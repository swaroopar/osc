/*
 * SPDX-License-Identifier: Apache-2.0
 * SPDX-FileCopyrightText: Huawei Inc.
 *
 */

package org.eclipse.xpanse.orchestrator.plugin.openstack;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.xpanse.modules.credential.AbstractCredentialInfo;
import org.eclipse.xpanse.modules.credential.CredentialDefinition;
import org.eclipse.xpanse.modules.credential.CredentialVariable;
import org.eclipse.xpanse.modules.credential.enums.CredentialType;
import org.eclipse.xpanse.modules.deployment.DeployResourceHandler;
import org.eclipse.xpanse.modules.models.enums.Csp;
import org.eclipse.xpanse.modules.models.service.DeployResource;
import org.eclipse.xpanse.modules.monitor.Metric;
import org.eclipse.xpanse.modules.monitor.enums.MonitorResourceType;
import org.eclipse.xpanse.modules.monitor.exceptions.CredentialsNotFoundException;
import org.eclipse.xpanse.orchestrator.OrchestratorPlugin;
import org.eclipse.xpanse.orchestrator.plugin.openstack.monitor.constants.OpenstackMonitorConstants;
import org.openstack4j.api.OSClient;
import org.openstack4j.model.common.Identifier;
import org.openstack4j.openstack.OSFactory;
import org.springframework.stereotype.Component;

/**
 * xpanse plugin implementation for openstack cloud.
 */
@Slf4j
@Component
public class OpenstackOrchestratorPlugin implements OrchestratorPlugin {

    private final DeployResourceHandler resourceHandler = new OpenstackTerraformResourceHandler();

    /**
     * Get the resource handler for OpenStack.
     */
    @Override
    public DeployResourceHandler getResourceHandler() {
        return resourceHandler;
    }

    /**
     * Get the cloud service provider.
     */
    @Override
    public Csp getCsp() {
        return Csp.OPENSTACK;
    }

    @Override
    public List<CredentialType> getAvailableCredentialTypes() {
        List<CredentialType> credentialTypes = new ArrayList<>();
        credentialTypes.add(CredentialType.VARIABLES);
        return credentialTypes;
    }

    @Override
    public List<AbstractCredentialInfo> getCredentialDefinitions() {
        List<AbstractCredentialInfo> credentialInfos = new ArrayList<>();
        List<CredentialVariable> credentialVariables = new ArrayList<>();
        CredentialDefinition usernamePassword = new CredentialDefinition("USER_PASS",
                "Admin Username and Password to access Openstack API",
                CredentialType.VARIABLES, credentialVariables);
        credentialVariables.add(
                new CredentialVariable(OpenstackMonitorConstants.USERNAME,
                        "Username to connect to Openstack."));
        credentialVariables.add(
                new CredentialVariable(OpenstackMonitorConstants.PASSWORD,
                        "Password to connect to Openstack."));
        credentialVariables.add(
                new CredentialVariable(OpenstackMonitorConstants.TENANT,
                        "Tenant to which to connect to."));
        credentialInfos.add(usernamePassword);
        return credentialInfos;
    }

    @Override
    public List<Metric> getMetrics(AbstractCredentialInfo credential,
            DeployResource deployResource, MonitorResourceType monitorResourceType) {
        OSClient.OSClientV3 osClientV3 = getOpenstackClient((CredentialDefinition) credential);
        //osClientV3.telemetry().meters().statistics('test').
        return null;
    }

    private OSClient.OSClientV3 getOpenstackClient(CredentialDefinition credentialDefinition) {
        String userName = null;
        String password = null;
        String tenant = null;
        if (CredentialType.VARIABLES.toValue().equals(credentialDefinition.getType().toValue())) {
            List<CredentialVariable> variables = credentialDefinition.getVariables();
            for (CredentialVariable credentialVariable : variables) {
                if (OpenstackMonitorConstants.USERNAME.equals(
                        credentialVariable.getName())) {
                    userName = credentialVariable.getValue();
                }
                if (OpenstackMonitorConstants.PASSWORD.equals(
                        credentialVariable.getName())) {
                    password = credentialVariable.getValue();
                }
                if (OpenstackMonitorConstants.TENANT.equals(
                        credentialVariable.getName())) {
                    tenant = credentialVariable.getValue();
                }
            }
        }
        if (Objects.isNull(userName) || Objects.isNull(password) ||Objects.isNull(tenant)) {
            throw new CredentialsNotFoundException("Credentials to connect to Openstack is not found");
        }
        return OSFactory.builderV3().credentials(userName, password).scopeToProject(Identifier.byName(tenant)).authenticate();
    }
}
