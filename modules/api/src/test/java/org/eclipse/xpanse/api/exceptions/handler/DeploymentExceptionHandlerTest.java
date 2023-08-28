/*
 * SPDX-License-Identifier: Apache-2.0
 * SPDX-FileCopyrightText: Huawei Inc.
 */

package org.eclipse.xpanse.api.exceptions.handler;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.UUID;
import org.eclipse.xpanse.api.ServiceDeployerApi;
import org.eclipse.xpanse.modules.deployment.DeployService;
import org.eclipse.xpanse.modules.models.service.deploy.exceptions.DeployerNotFoundException;
import org.eclipse.xpanse.modules.models.service.deploy.exceptions.FlavorInvalidException;
import org.eclipse.xpanse.modules.models.service.deploy.exceptions.InvalidDeploymentVariableException;
import org.eclipse.xpanse.modules.models.service.deploy.exceptions.InvalidServiceStateException;
import org.eclipse.xpanse.modules.models.service.deploy.exceptions.PluginNotFoundException;
import org.eclipse.xpanse.modules.models.service.deploy.exceptions.ServiceNotDeployedException;
import org.eclipse.xpanse.modules.models.service.deploy.exceptions.TerraformExecutorException;
import org.eclipse.xpanse.modules.models.service.deploy.exceptions.TerraformProviderNotFoundException;
import org.eclipse.xpanse.modules.models.service.query.ServiceQueryModel;
import org.eclipse.xpanse.modules.security.IdentityProviderManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {ServiceDeployerApi.class, DeployService.class,
        DeploymentExceptionHandler.class, IdentityProviderManager.class})
@WebMvcTest
class DeploymentExceptionHandlerTest {

    @MockBean
    private DeployService deployService;

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    @Test
    void testFlavorInvalidException() throws Exception {
        when(deployService.listDeployedServices(any(ServiceQueryModel.class)))
                .thenThrow(new FlavorInvalidException("test error"));

        this.mockMvc.perform(get("/xpanse/services"))
                .andExpect(status().is(400))
                .andExpect(jsonPath("$.resultType").value("Flavor Invalid"))
                .andExpect(jsonPath("$.details[0]").value("test error"));
    }

    @Test
    void testTerraformExecutorException() throws Exception {
        when(deployService.listDeployedServices(any(ServiceQueryModel.class)))
                .thenThrow(new TerraformExecutorException("test error"));

        this.mockMvc.perform(get("/xpanse/services"))
                .andExpect(status().is(502))
                .andExpect(jsonPath("$.resultType").value("Terraform Execution Failed"))
                .andExpect(jsonPath("$.details[0]").value("TFExecutor Exception: test error"));
    }

    @Test
    void testPluginNotFoundException() throws Exception {
        when(deployService.listDeployedServices(any(ServiceQueryModel.class)))
                .thenThrow(new PluginNotFoundException("test error"));

        this.mockMvc.perform(get("/xpanse/services"))
                .andExpect(status().is(400))
                .andExpect(jsonPath("$.resultType").value("Plugin Not Found"))
                .andExpect(jsonPath("$.details[0]").value("test error"));
    }

    @Test
    void testDeployerNotFoundException() throws Exception {
        when(deployService.listDeployedServices(any(ServiceQueryModel.class)))
                .thenThrow(new DeployerNotFoundException("test error"));

        this.mockMvc.perform(get("/xpanse/services"))
                .andExpect(status().is(400))
                .andExpect(jsonPath("$.resultType").value("Deployer Not Found"))
                .andExpect(jsonPath("$.details[0]").value("test error"));
    }

    @Test
    void testTerraformProviderNotFoundException() throws Exception {
        when(deployService.listDeployedServices(any(ServiceQueryModel.class)))
                .thenThrow(new TerraformProviderNotFoundException("test error"));

        this.mockMvc.perform(get("/xpanse/services"))
                .andExpect(status().is(400))
                .andExpect(jsonPath("$.resultType").value("Terraform Provider Not Found"))
                .andExpect(jsonPath("$.details[0]").value("test error"));
    }

    @Test
    void testInvalidServiceStateException() throws Exception {
        when(deployService.listDeployedServices(any(ServiceQueryModel.class)))
                .thenThrow(new InvalidServiceStateException("test error"));

        this.mockMvc.perform(get("/xpanse/services"))
                .andExpect(status().is(400))
                .andExpect(jsonPath("$.resultType").value("Invalid Service State"))
                .andExpect(jsonPath("$.details[0]").value("test error"));
    }

    @Test
    void testServiceNotDeployedException() throws Exception {
        when(deployService.getDeployServiceDetails(any(UUID.class)))
                .thenThrow(new ServiceNotDeployedException("test error"));

        this.mockMvc.perform(get("/xpanse/services/{id}", UUID.randomUUID()))
                .andExpect(status().is(400))
                .andExpect(jsonPath("$.resultType").value("Service Deployment Not Found"))
                .andExpect(jsonPath("$.details[0]").value("test error"));
    }

    @Test
    void testInvalidDeploymentVariableException() throws Exception {
        when(deployService.listDeployedServices(any(ServiceQueryModel.class)))
                .thenThrow(new InvalidDeploymentVariableException("test error"));

        this.mockMvc.perform(get("/xpanse/services"))
                .andExpect(status().is(400))
                .andExpect(jsonPath("$.resultType").value("Deployment Variable Invalid"))
                .andExpect(jsonPath("$.details[0]").value("test error"));
    }
}