/*
 * SPDX-License-Identifier: Apache-2.0
 * SPDX-FileCopyrightText: Huawei Inc.
 */

package org.eclipse.xpanse.modules.deployment.deployers.opentofu.tofumaker;

import lombok.extern.slf4j.Slf4j;
import org.eclipse.xpanse.modules.database.service.ServiceDeploymentEntity;
import org.eclipse.xpanse.modules.deployment.ServiceDeploymentEntityHandler;
import org.eclipse.xpanse.modules.deployment.deployers.opentofu.exceptions.OpenTofuMakerRequestFailedException;
import org.eclipse.xpanse.modules.deployment.deployers.opentofu.tofumaker.generated.api.OpenTofuFromGitRepoApi;
import org.eclipse.xpanse.modules.deployment.deployers.opentofu.tofumaker.generated.api.OpenTofuFromScriptsApi;
import org.eclipse.xpanse.modules.deployment.deployers.opentofu.tofumaker.generated.model.OpenTofuAsyncRequestWithScripts;
import org.eclipse.xpanse.modules.deployment.deployers.opentofu.tofumaker.generated.model.OpenTofuAsyncRequestWithScriptsGitRepo;
import org.eclipse.xpanse.modules.deployment.deployers.opentofu.utils.TfResourceTransUtils;
import org.eclipse.xpanse.modules.models.service.deployment.DeployResult;
import org.eclipse.xpanse.modules.orchestrator.deployment.DeployTask;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;

/** Bean to manage service modify via tofu-maker. */
@Slf4j
@Component
@Profile("tofu-maker")
public class TofuMakerServiceModifier {
    private final OpenTofuFromScriptsApi openTofuFromScriptsApi;
    private final OpenTofuFromGitRepoApi openTofuFromGitRepoApi;
    private final TofuMakerHelper tofuMakerHelper;
    private final ServiceDeploymentEntityHandler serviceDeploymentEntityHandler;

    /** Constructor for OpenTofuMakerServiceModifyer bean. */
    public TofuMakerServiceModifier(
            OpenTofuFromScriptsApi openTofuFromScriptsApi,
            OpenTofuFromGitRepoApi openTofuFromGitRepoApi,
            TofuMakerHelper tofuMakerHelper,
            ServiceDeploymentEntityHandler serviceDeploymentEntityHandler) {
        this.openTofuFromScriptsApi = openTofuFromScriptsApi;
        this.openTofuFromGitRepoApi = openTofuFromGitRepoApi;
        this.tofuMakerHelper = tofuMakerHelper;
        this.serviceDeploymentEntityHandler = serviceDeploymentEntityHandler;
    }

    /** method to perform service modify using scripts provided in OCL. */
    public DeployResult modifyFromScripts(DeployTask deployTask) {
        ServiceDeploymentEntity serviceDeploymentEntity =
                this.serviceDeploymentEntityHandler.getServiceDeploymentEntity(
                        deployTask.getServiceId());
        String resourceState = TfResourceTransUtils.getStoredStateContent(serviceDeploymentEntity);
        DeployResult result = new DeployResult();
        OpenTofuAsyncRequestWithScripts request =
                getModifyFromScriptsRequest(deployTask, resourceState);
        try {
            openTofuFromScriptsApi.asyncModifyWithScripts(request);
            result.setOrderId(deployTask.getOrderId());
            return result;
        } catch (RestClientException e) {
            log.error(
                    "tofu-maker modify service failed. service id: {} , error:{} ",
                    deployTask.getServiceId(),
                    e.getMessage());
            throw new OpenTofuMakerRequestFailedException(e.getMessage());
        }
    }

    /** method to perform service modify using scripts form GIT repo. */
    public DeployResult modifyFromGitRepo(DeployTask deployTask) {
        ServiceDeploymentEntity serviceDeploymentEntity =
                this.serviceDeploymentEntityHandler.getServiceDeploymentEntity(
                        deployTask.getServiceId());
        String resourceState = TfResourceTransUtils.getStoredStateContent(serviceDeploymentEntity);
        DeployResult result = new DeployResult();
        OpenTofuAsyncRequestWithScriptsGitRepo request =
                getModifyFromGitRepoRequest(deployTask, resourceState);
        try {
            openTofuFromGitRepoApi.asyncModifyFromGitRepo(request);
            result.setOrderId(deployTask.getOrderId());
            return result;
        } catch (RestClientException e) {
            log.error(
                    "tofu-maker deploy service failed. service id: {} , error:{} ",
                    deployTask.getServiceId(),
                    e.getMessage());
            throw new OpenTofuMakerRequestFailedException(e.getMessage());
        }
    }

    private OpenTofuAsyncRequestWithScripts getModifyFromScriptsRequest(
            DeployTask task, String stateFile) throws OpenTofuMakerRequestFailedException {
        OpenTofuAsyncRequestWithScripts request = new OpenTofuAsyncRequestWithScripts();
        request.setRequestType(OpenTofuAsyncRequestWithScripts.RequestTypeEnum.MODIFY);
        request.setRequestId(task.getOrderId());
        request.setOpenTofuVersion(task.getOcl().getDeployment().getDeployerTool().getVersion());
        request.setScriptFiles(
                task.getOcl().getDeployment().getTerraformDeployment().getScriptFiles());
        request.setTfState(stateFile);
        request.setVariables(tofuMakerHelper.getInputVariables(task, false));
        request.setEnvVariables(tofuMakerHelper.getEnvironmentVariables(task));
        request.setWebhookConfig(tofuMakerHelper.getWebhookConfigWithTask(task));
        return request;
    }

    private OpenTofuAsyncRequestWithScriptsGitRepo getModifyFromGitRepoRequest(
            DeployTask task, String stateFile) throws OpenTofuMakerRequestFailedException {
        OpenTofuAsyncRequestWithScriptsGitRepo request =
                new OpenTofuAsyncRequestWithScriptsGitRepo();
        request.setRequestType(OpenTofuAsyncRequestWithScriptsGitRepo.RequestTypeEnum.MODIFY);
        request.setRequestId(task.getOrderId());
        request.setOpenTofuVersion(task.getOcl().getDeployment().getDeployerTool().getVersion());
        request.setTfState(stateFile);
        request.setVariables(tofuMakerHelper.getInputVariables(task, false));
        request.setEnvVariables(tofuMakerHelper.getEnvironmentVariables(task));
        request.setWebhookConfig(tofuMakerHelper.getWebhookConfigWithTask(task));
        request.setGitRepoDetails(
                tofuMakerHelper.convertOpenTofuScriptsGitRepoDetailsFromDeployFromGitRepo(
                        task.getOcl().getDeployment().getTerraformDeployment().getScriptsRepo()));
        return request;
    }
}
