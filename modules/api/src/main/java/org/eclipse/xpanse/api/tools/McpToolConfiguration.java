/*
 * SPDX-License-Identifier: Apache-2.0
 * SPDX-FileCopyrightText: Huawei Inc.
 */

package org.eclipse.xpanse.api.tools;

import io.modelcontextprotocol.server.McpServerFeatures;
import io.modelcontextprotocol.spec.McpSchema;
import org.eclipse.xpanse.api.controllers.ServiceCatalogApi;
import org.eclipse.xpanse.api.controllers.ServiceDeployerApi;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class McpToolConfiguration {

    @Bean
    public ToolCallbackProvider xpanseTools(ServiceCatalogApi serviceCatalogApi, ServiceDeployerApi serviceDeployerApi) {

        return MethodToolCallbackProvider.builder().toolObjects(serviceCatalogApi, serviceDeployerApi).build();
    }

}
