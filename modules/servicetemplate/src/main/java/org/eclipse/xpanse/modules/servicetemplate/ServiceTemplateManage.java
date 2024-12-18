/*
 * SPDX-License-Identifier: Apache-2.0
 * SPDX-FileCopyrightText: Huawei Inc.
 *
 */

package org.eclipse.xpanse.modules.servicetemplate;

import jakarta.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.xpanse.modules.database.service.ServiceDeploymentEntity;
import org.eclipse.xpanse.modules.database.service.ServiceDeploymentStorage;
import org.eclipse.xpanse.modules.database.service.ServiceQueryModel;
import org.eclipse.xpanse.modules.database.servicetemplate.ServiceTemplateEntity;
import org.eclipse.xpanse.modules.database.servicetemplate.ServiceTemplateQueryModel;
import org.eclipse.xpanse.modules.database.servicetemplate.ServiceTemplateStorage;
import org.eclipse.xpanse.modules.database.servicetemplatehistory.ServiceTemplateHistoryEntity;
import org.eclipse.xpanse.modules.database.servicetemplatehistory.ServiceTemplateHistoryStorage;
import org.eclipse.xpanse.modules.deployment.DeployerKindManager;
import org.eclipse.xpanse.modules.models.common.enums.Csp;
import org.eclipse.xpanse.modules.models.common.exceptions.OpenApiFileGenerationException;
import org.eclipse.xpanse.modules.models.service.utils.ServiceDeployVariablesJsonSchemaGenerator;
import org.eclipse.xpanse.modules.models.servicetemplate.Deployment;
import org.eclipse.xpanse.modules.models.servicetemplate.Ocl;
import org.eclipse.xpanse.modules.models.servicetemplate.ReviewRegistrationRequest;
import org.eclipse.xpanse.modules.models.servicetemplate.ServiceFlavor;
import org.eclipse.xpanse.modules.models.servicetemplate.enums.DeployerKind;
import org.eclipse.xpanse.modules.models.servicetemplate.enums.ServiceReviewResult;
import org.eclipse.xpanse.modules.models.servicetemplate.enums.ServiceTemplateChangeStatus;
import org.eclipse.xpanse.modules.models.servicetemplate.enums.ServiceTemplateRegistrationState;
import org.eclipse.xpanse.modules.models.servicetemplate.enums.ServiceTemplateRequestType;
import org.eclipse.xpanse.modules.models.servicetemplate.exceptions.InvalidServiceFlavorsException;
import org.eclipse.xpanse.modules.models.servicetemplate.exceptions.InvalidServiceVersionException;
import org.eclipse.xpanse.modules.models.servicetemplate.exceptions.OpenTofuScriptFormatInvalidException;
import org.eclipse.xpanse.modules.models.servicetemplate.exceptions.ServiceTemplateAlreadyRegistered;
import org.eclipse.xpanse.modules.models.servicetemplate.exceptions.ServiceTemplateAlreadyReviewed;
import org.eclipse.xpanse.modules.models.servicetemplate.exceptions.ServiceTemplateStillInUseException;
import org.eclipse.xpanse.modules.models.servicetemplate.exceptions.ServiceTemplateUpdateNotAllowed;
import org.eclipse.xpanse.modules.models.servicetemplate.exceptions.TerraformScriptFormatInvalidException;
import org.eclipse.xpanse.modules.models.servicetemplate.utils.JsonObjectSchema;
import org.eclipse.xpanse.modules.orchestrator.OrchestratorPlugin;
import org.eclipse.xpanse.modules.orchestrator.PluginManager;
import org.eclipse.xpanse.modules.orchestrator.deployment.DeployValidateDiagnostics;
import org.eclipse.xpanse.modules.orchestrator.deployment.DeploymentScriptValidationResult;
import org.eclipse.xpanse.modules.security.UserServiceHelper;
import org.eclipse.xpanse.modules.servicetemplate.price.BillingConfigValidator;
import org.eclipse.xpanse.modules.servicetemplate.utils.AvailabilityZoneSchemaValidator;
import org.eclipse.xpanse.modules.servicetemplate.utils.DeployVariableSchemaValidator;
import org.eclipse.xpanse.modules.servicetemplate.utils.IconProcessorUtil;
import org.eclipse.xpanse.modules.servicetemplate.utils.ServiceConfigurationParameterValidator;
import org.eclipse.xpanse.modules.servicetemplate.utils.ServiceTemplateOpenApiGenerator;
import org.semver4j.Semver;
import org.semver4j.SemverException;
import org.springframework.beans.BeanUtils;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

/**
 * Implement Interface to manage service template newTemplate in database.
 */
@Slf4j
@Service
public class ServiceTemplateManage {

    private static final String AUTO_APPROVED_REVIEW_COMMENT = "auto-approved by CSP";
    @Resource
    private ServiceTemplateStorage templateStorage;
    @Resource
    private ServiceTemplateHistoryStorage serviceTemplateHistoryStorage;
    @Resource
    private ServiceDeploymentStorage serviceDeploymentStorage;
    @Resource
    private ServiceTemplateOpenApiGenerator serviceTemplateOpenApiGenerator;
    @Resource
    private UserServiceHelper userServiceHelper;
    @Resource
    private ServiceDeployVariablesJsonSchemaGenerator serviceDeployVariablesJsonSchemaGenerator;
    @Resource
    private DeployerKindManager deployerKindManager;
    @Resource
    private BillingConfigValidator billingConfigValidator;
    @Resource
    private PluginManager pluginManager;
    @Resource
    private ServiceConfigurationParameterValidator serviceConfigurationParameterValidator;

    /**
     * Update service template using id and the ocl model.
     *
     * @param id                               id of the service template.
     * @param ocl                              the Ocl model describing the service template.
     * @param isRemoveFromCatalogUntilApproved If remove the service template from catalog
     *                                         until the updated one is approved.
     * @return Returns service template history entity.
     */
    public ServiceTemplateHistoryEntity updateServiceTemplate(
            UUID id, Ocl ocl, boolean isRemoveFromCatalogUntilApproved) {
        ServiceTemplateEntity existingServiceTemplate = getServiceTemplateDetails(id, true, false);
        if (existingServiceTemplate.getIsUpdatePending()) {
            ServiceTemplateHistoryEntity updatingHistory =
                    queryAnyInProgressServiceTemplateUpdateRequest(existingServiceTemplate);
            if (Objects.nonNull(updatingHistory)) {
                String errorMsg = String.format("The service template with id %s can not be updated"
                                + " until the updating progress with change id %s is completed.",
                        id, updatingHistory.getChangeId());
                log.error(errorMsg);
                throw new ServiceTemplateUpdateNotAllowed(errorMsg);
            }
        }
        ServiceTemplateEntity serviceTemplateToUpdate = new ServiceTemplateEntity();
        BeanUtils.copyProperties(existingServiceTemplate, serviceTemplateToUpdate);
        iconUpdate(serviceTemplateToUpdate, ocl);
        checkParams(serviceTemplateToUpdate, ocl);
        validateRegions(ocl);
        validateFlavors(ocl);
        validateServiceDeployment(ocl.getDeployment(), serviceTemplateToUpdate);
        billingConfigValidator.validateBillingConfig(ocl);
        if (Objects.nonNull(serviceTemplateToUpdate.getOcl().getServiceConfigurationManage())
                && Objects.nonNull(ocl.getServiceConfigurationManage())) {
            serviceConfigurationParameterValidator.validateServiceConfigurationParameters(ocl);
        }
        serviceTemplateToUpdate.setOcl(ocl);
        boolean isAutoApprovedEnabled =
                isAutoApproveEnabledForCsp(ocl.getCloudServiceProvider().getName());
        final ServiceTemplateHistoryEntity storedUpdateHistory =
                createServiceTemplateHistory(isAutoApprovedEnabled,
                        ServiceTemplateRequestType.UPDATE, serviceTemplateToUpdate);
        // if auto approved is enabled by CSP, approved service template with new ocl directly
        if (isAutoApprovedEnabled) {
            serviceTemplateToUpdate.setAvailableInCatalog(true);
            serviceTemplateToUpdate.setIsUpdatePending(false);
            ServiceTemplateEntity updatedServiceTemplate =
                    templateStorage.storeAndFlush(serviceTemplateToUpdate);
            serviceTemplateOpenApiGenerator.updateServiceApi(updatedServiceTemplate);
        } else {
            existingServiceTemplate.setIsUpdatePending(true);
            if (existingServiceTemplate.getAvailableInCatalog()
                    && isRemoveFromCatalogUntilApproved) {
                existingServiceTemplate.setAvailableInCatalog(false);
            }
            templateStorage.storeAndFlush(existingServiceTemplate);
        }
        return storedUpdateHistory;
    }


    private ServiceTemplateHistoryEntity queryAnyInProgressServiceTemplateUpdateRequest(
            ServiceTemplateEntity serviceTemplate) {
        ServiceTemplateHistoryEntity queryUpdateHistory = new ServiceTemplateHistoryEntity();
        queryUpdateHistory.setServiceTemplate(serviceTemplate);
        queryUpdateHistory.setRequestType(ServiceTemplateRequestType.UPDATE);
        queryUpdateHistory.setStatus(ServiceTemplateChangeStatus.IN_REVIEW);
        return serviceTemplateHistoryStorage.listServiceTemplateHistory(queryUpdateHistory)
                .getFirst();
    }

    private void checkParams(ServiceTemplateEntity existingTemplate, Ocl ocl) {

        String oldCategory = existingTemplate.getCategory().name();
        String newCategory = ocl.getCategory().name();
        compare(oldCategory, newCategory, "category");

        String oldName = existingTemplate.getName();
        String newName = ocl.getName();
        compare(oldName, newName, "service name");

        String oldVersion = existingTemplate.getVersion();
        String newVersion = getSemverVersion(ocl.getServiceVersion()).getVersion();
        compare(oldVersion, newVersion, "service version");

        String oldCsp = existingTemplate.getCsp().name();
        String newCsp = ocl.getCloudServiceProvider().getName().name();
        compare(oldCsp, newCsp, "csp");

        String oldServiceHostingType = existingTemplate.getServiceHostingType().toValue();
        String newServiceHostingType = ocl.getServiceHostingType().toValue();
        compare(oldServiceHostingType, newServiceHostingType, "service hosting type");
    }

    private void compare(String oldParams, String newParams, String type) {
        if (!newParams.toLowerCase(Locale.ROOT).equals(oldParams.toLowerCase(Locale.ROOT))) {
            String errorMsg = String.format("Update service failed, Value of %s cannot be "
                    + "changed with an update request", type);
            log.error(errorMsg);
            throw new ServiceTemplateUpdateNotAllowed(errorMsg);
        }
    }

    private ServiceTemplateEntity createServiceTemplateEntity(Ocl ocl) {
        ServiceTemplateEntity newServiceTemplate = new ServiceTemplateEntity();
        newServiceTemplate.setId(UUID.randomUUID());
        newServiceTemplate.setCategory(ocl.getCategory());
        newServiceTemplate.setCsp(ocl.getCloudServiceProvider().getName());
        newServiceTemplate.setName(StringUtils.lowerCase(ocl.getName()));
        newServiceTemplate.setVersion(getSemverVersion(ocl.getServiceVersion()).getVersion());
        newServiceTemplate.setServiceHostingType(ocl.getServiceHostingType());
        newServiceTemplate.setOcl(ocl);
        newServiceTemplate.setServiceProviderContactDetails(ocl.getServiceProviderContactDetails());
        validateServiceDeployment(ocl.getDeployment(), newServiceTemplate);
        newServiceTemplate.setNamespace(userServiceHelper.getCurrentUserManageNamespace());
        return newServiceTemplate;
    }

    private ServiceTemplateHistoryEntity createServiceTemplateHistory(
            boolean isAutoApproveEnabled, ServiceTemplateRequestType requestType,
            ServiceTemplateEntity serviceTemplate) {
        ServiceTemplateHistoryEntity serviceTemplateHistory = new ServiceTemplateHistoryEntity();
        serviceTemplateHistory.setChangeId(UUID.randomUUID());
        serviceTemplateHistory.setServiceTemplate(serviceTemplate);
        serviceTemplateHistory.setOcl(serviceTemplate.getOcl());
        serviceTemplateHistory.setRequestType(requestType);
        serviceTemplateHistory.setBlockTemplateUntilReviewed(false);
        if (isAutoApproveEnabled) {
            serviceTemplateHistory.setStatus(ServiceTemplateChangeStatus.ACCEPTED);
            serviceTemplateHistory.setReviewComment(AUTO_APPROVED_REVIEW_COMMENT);
        } else {
            serviceTemplateHistory.setStatus(ServiceTemplateChangeStatus.IN_REVIEW);
        }
        return serviceTemplateHistoryStorage.storeAndFlush(serviceTemplateHistory);
    }

    private boolean isAutoApproveEnabledForCsp(Csp csp) {
        OrchestratorPlugin cspPlugin = pluginManager.getOrchestratorPlugin(csp);
        return cspPlugin.autoApproveServiceTemplateIsEnabled();
    }


    private Semver getSemverVersion(String serviceVersion) {
        try {
            return new Semver(serviceVersion);
        } catch (SemverException e) {
            String errorMsg = String.format("The service version %s is a invalid semver version.",
                    serviceVersion);
            throw new InvalidServiceVersionException(errorMsg);
        }
    }

    private void validateServiceVersion(Semver newServiceVersion,
                                        List<ServiceTemplateEntity> existingTemplates) {
        if (!CollectionUtils.isEmpty(existingTemplates)) {
            Semver highestVersion = existingTemplates.stream()
                    .map(serviceTemplate -> new Semver(serviceTemplate.getVersion())).sorted()
                    .toList().reversed().getFirst();
            if (!newServiceVersion.isGreaterThan(highestVersion)) {
                String errorMsg = String.format("The version %s of service must be higher than the"
                                + " highest version %s of the registered services with same name",
                        newServiceVersion, highestVersion);
                log.error(errorMsg);
                throw new InvalidServiceVersionException(errorMsg);
            }
        }
    }

    private void iconUpdate(ServiceTemplateEntity serviceTemplateEntity, Ocl ocl) {
        try {
            ocl.setIcon(IconProcessorUtil.processImage(ocl));
        } catch (Exception e) {
            ocl.setIcon(serviceTemplateEntity.getOcl().getIcon());
        }
    }

    /**
     * Register service template using the ocl.
     *
     * @param ocl the Ocl model describing the service template.
     * @return Returns service template history entity.
     */
    public ServiceTemplateHistoryEntity registerServiceTemplate(Ocl ocl) {
        Semver newServiceVersion = getSemverVersion(ocl.getServiceVersion());
        ocl.setVersion(newServiceVersion.getVersion());
        ServiceTemplateQueryModel queryModel = ServiceTemplateQueryModel.builder()
                .category(ocl.getCategory()).csp(ocl.getCloudServiceProvider().getName())
                .serviceName(ocl.getName()).serviceHostingType(ocl.getServiceHostingType()).build();
        List<ServiceTemplateEntity> existingServiceTemplates =
                templateStorage.listServiceTemplates(queryModel);
        ServiceTemplateEntity existingTemplate = existingServiceTemplates.stream()
                .filter(template -> newServiceVersion.isEqualTo(new Semver(template.getVersion())))
                .findAny().orElse(null);
        if (Objects.nonNull(existingTemplate)) {
            String errorMsg = String.format("Service template already registered with id %s",
                    existingTemplate.getId());
            log.error(errorMsg);
            throw new ServiceTemplateAlreadyRegistered(errorMsg);
        }
        validateServiceVersion(newServiceVersion, existingServiceTemplates);
        ocl.setIcon(IconProcessorUtil.processImage(ocl));
        validateRegions(ocl);
        validateFlavors(ocl);
        billingConfigValidator.validateBillingConfig(ocl);
        if (Objects.nonNull(ocl.getServiceConfigurationManage())) {
            serviceConfigurationParameterValidator.validateServiceConfigurationParameters(ocl);
        }
        ServiceTemplateEntity serviceTemplateToRegister = createServiceTemplateEntity(ocl);
        boolean isAutoApprovedEnabled =
                isAutoApproveEnabledForCsp(serviceTemplateToRegister.getCsp());
        if (isAutoApprovedEnabled) {
            serviceTemplateToRegister.setServiceTemplateRegistrationState(
                    ServiceTemplateRegistrationState.APPROVED);
        } else {
            serviceTemplateToRegister.setServiceTemplateRegistrationState(
                    ServiceTemplateRegistrationState.IN_REVIEW);
        }
        serviceTemplateToRegister.setIsUpdatePending(false);
        serviceTemplateToRegister.setAvailableInCatalog(isAutoApprovedEnabled);
        ServiceTemplateEntity registeredServiceTemplate =
                templateStorage.storeAndFlush(serviceTemplateToRegister);
        ServiceTemplateHistoryEntity storedRegisterHistory =
                createServiceTemplateHistory(isAutoApprovedEnabled,
                        ServiceTemplateRequestType.REGISTER, registeredServiceTemplate);
        serviceTemplateOpenApiGenerator.generateServiceApi(registeredServiceTemplate);
        return storedRegisterHistory;
    }

    private void validateFlavors(Ocl ocl) {
        List<String> errors = new ArrayList<>();
        // Check if service flavor names are unique
        Map<String, Long> nameCountMap = ocl.getFlavors().getServiceFlavors().stream()
                .collect(Collectors.groupingBy(ServiceFlavor::getName, Collectors.counting()));
        nameCountMap.entrySet().stream().filter(entry -> entry.getValue() > 1).forEach(entry -> {
            String message =
                    String.format("Duplicate flavor with name %s in service.", entry.getKey());
            errors.add(message);
        });
        if (!CollectionUtils.isEmpty(errors)) {
            throw new InvalidServiceFlavorsException(errors);
        }
    }

    private void validateRegions(Ocl ocl) {
        OrchestratorPlugin plugin =
                pluginManager.getOrchestratorPlugin(ocl.getCloudServiceProvider().getName());
        plugin.validateRegionsOfService(ocl);
    }

    private void validateServiceDeployment(Deployment deployment,
                                           ServiceTemplateEntity serviceTemplate) {
        AvailabilityZoneSchemaValidator.validateServiceAvailabilities(
                deployment.getServiceAvailabilityConfig());
        DeployVariableSchemaValidator.validateDeployVariable(deployment.getVariables());
        JsonObjectSchema jsonObjectSchema =
                serviceDeployVariablesJsonSchemaGenerator.buildDeployVariableJsonSchema(
                        deployment.getVariables());
        serviceTemplate.setJsonObjectSchema(jsonObjectSchema);
        validateTerraformScript(deployment);
    }

    /**
     * Get detail of service template using ID.
     *
     * @param id             the ID of
     * @param checkNamespace check the namespace of the service template belonging to.
     * @param checkCsp       check the cloud service provider of the service template.
     * @return Returns service template DB newTemplate.
     */
    public ServiceTemplateEntity getServiceTemplateDetails(UUID id, boolean checkNamespace,
                                                           boolean checkCsp) {
        ServiceTemplateEntity existingTemplate = getServiceTemplateById(id);
        if (checkNamespace) {
            boolean hasManagePermissions = userServiceHelper.currentUserCanManageNamespace(
                    existingTemplate.getNamespace());
            if (!hasManagePermissions) {
                throw new AccessDeniedException("No permissions to view or manage service template "
                        + "belonging to other namespaces.");
            }
        }
        if (checkCsp) {
            boolean hasManagePermissions =
                    userServiceHelper.currentUserCanManageCsp(existingTemplate.getCsp());
            if (!hasManagePermissions) {
                throw new AccessDeniedException("No permissions to review service template "
                        + "belonging to other cloud service providers.");
            }
        }
        return existingTemplate;
    }

    /**
     * Search service templates with query model.
     *
     * @param query service template query model.
     * @return Returns list of service template newTemplate.
     */
    public List<ServiceTemplateEntity> listServiceTemplates(ServiceTemplateQueryModel query) {
        fillParamFromUserMetadata(query);
        return templateStorage.listServiceTemplates(query);
    }

    /**
     * Review service template registration.
     *
     * @param id      ID of service template.
     * @param request the request of review registration.
     */
    public void reviewServiceTemplateRegistration(UUID id, ReviewRegistrationRequest request) {
        ServiceTemplateEntity existingTemplate = getServiceTemplateDetails(id, false, true);
        ServiceTemplateRegistrationState state =
                existingTemplate.getServiceTemplateRegistrationState();
        if (ServiceTemplateRegistrationState.APPROVED == state
                || ServiceTemplateRegistrationState.REJECTED == state) {
            String errMsg = String.format("Service template with id %s already reviewed.",
                    existingTemplate.getId());
            log.error(errMsg);
            throw new ServiceTemplateAlreadyReviewed(errMsg);
        }
        if (ServiceReviewResult.APPROVED == request.getReviewResult()) {
            existingTemplate.setServiceTemplateRegistrationState(
                    ServiceTemplateRegistrationState.APPROVED);
            existingTemplate.setAvailableInCatalog(true);
        } else if (ServiceReviewResult.REJECTED == request.getReviewResult()) {
            existingTemplate.setServiceTemplateRegistrationState(
                    ServiceTemplateRegistrationState.REJECTED);
            existingTemplate.setAvailableInCatalog(false);
        }
        templateStorage.storeAndFlush(existingTemplate);
    }


    /**
     * Unregister service template using the ID of service template.
     *
     * @param id ID of service template.
     * @return Returns updated service template.
     */
    public ServiceTemplateEntity unregisterServiceTemplate(UUID id) {
        ServiceTemplateEntity existingTemplate = getServiceTemplateDetails(id, true, false);
        existingTemplate.setAvailableInCatalog(false);
        return templateStorage.storeAndFlush(existingTemplate);
    }

    /**
     * Re-register service template using the ID of service template.
     *
     * @param id ID of service template.
     * @return Returns updated service template.
     */
    public ServiceTemplateEntity reRegisterServiceTemplate(UUID id) {
        ServiceTemplateEntity existingTemplate = getServiceTemplateDetails(id, true, false);
        existingTemplate.setAvailableInCatalog(true);
        return templateStorage.storeAndFlush(existingTemplate);
    }

    /**
     * Delete service template using the ID of service template.
     *
     * @param id ID of service template.
     */
    public void deleteServiceTemplate(UUID id) {
        ServiceTemplateEntity existingTemplate = getServiceTemplateDetails(id, true, false);
        if (existingTemplate.getAvailableInCatalog()) {
            String errMsg = String.format("Service template with id %s is not unregistered.", id);
            log.error(errMsg);
            throw new ServiceTemplateStillInUseException(errMsg);
        }
        List<ServiceDeploymentEntity> deployServiceEntities =
                listDeployServicesByTemplateId(existingTemplate.getId());
        if (!deployServiceEntities.isEmpty()) {
            String errMsg = String.format("Service template with id %s is still in use.", id);
            log.error(errMsg);
            throw new ServiceTemplateStillInUseException(errMsg);
        }
        templateStorage.deleteServiceTemplate(existingTemplate);
        serviceTemplateOpenApiGenerator.deleteServiceApi(id.toString());
    }

    /**
     * generate OpenApi for service template using the ID.
     *
     * @param id ID of service template.
     * @return path of openapi.html
     */
    public String getOpenApiUrl(UUID id) {
        String openApiUrl = serviceTemplateOpenApiGenerator.getOpenApi(getServiceTemplateById(id));
        if (StringUtils.isBlank(openApiUrl)) {
            throw new OpenApiFileGenerationException("Get openApi Url is Empty.");
        }
        return openApiUrl;
    }

    private ServiceTemplateEntity getServiceTemplateById(UUID id) {
        return templateStorage.getServiceTemplateById(id);
    }

    private List<ServiceDeploymentEntity> listDeployServicesByTemplateId(UUID serviceTemplateId) {
        ServiceQueryModel query = new ServiceQueryModel();
        query.setServiceTemplateId(serviceTemplateId);
        return serviceDeploymentStorage.listServices(query);
    }


    private void validateTerraformScript(Deployment deployment) {
        DeployerKind deployerKind = deployment.getDeployerTool().getKind();
        if (deployerKind == DeployerKind.TERRAFORM) {
            DeploymentScriptValidationResult tfValidationResult =
                    this.deployerKindManager.getDeployment(deployerKind).validate(deployment);
            if (!tfValidationResult.isValid()) {
                throw new TerraformScriptFormatInvalidException(
                        tfValidationResult.getDiagnostics().stream()
                                .map(DeployValidateDiagnostics::getDetail)
                                .collect(Collectors.toList()));
            }
        }

        if (deployerKind == DeployerKind.OPEN_TOFU) {
            DeploymentScriptValidationResult tfValidationResult =
                    this.deployerKindManager.getDeployment(deployerKind).validate(deployment);
            if (!tfValidationResult.isValid()) {
                throw new OpenTofuScriptFormatInvalidException(
                        tfValidationResult.getDiagnostics().stream()
                                .map(DeployValidateDiagnostics::getDetail)
                                .collect(Collectors.toList()));
            }
        }
    }

    private void fillParamFromUserMetadata(ServiceTemplateQueryModel query) {
        if (Objects.nonNull(query.getCheckNamespace()) && query.getCheckNamespace()) {
            String namespace = userServiceHelper.getCurrentUserManageNamespace();
            query.setNamespace(namespace);
        }
    }


}
