/*
 * SPDX-License-Identifier: Apache-2.0
 * SPDX-FileCopyrightText: Huawei Inc.
 */

package org.eclipse.xpanse.api.tools;

import io.swagger.v3.oas.annotations.Parameter;
import java.util.Comparator;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.xpanse.api.config.ServiceTemplateEntityConverter;
import org.eclipse.xpanse.modules.database.servicetemplate.ServiceTemplateEntity;
import org.eclipse.xpanse.modules.database.servicetemplate.ServiceTemplateQueryModel;
import org.eclipse.xpanse.modules.models.common.enums.Category;
import org.eclipse.xpanse.modules.models.common.enums.Csp;
import org.eclipse.xpanse.modules.models.servicetemplate.enums.ServiceHostingType;
import org.eclipse.xpanse.modules.models.servicetemplate.view.UserOrderableServiceVo;
import org.eclipse.xpanse.modules.servicetemplate.ServiceTemplateManage;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

@Service
@Slf4j
public class ServiceCatalogTool {

    private final ServiceTemplateManage serviceTemplateManage;

    @Autowired
    public ServiceCatalogTool(ServiceTemplateManage serviceTemplateManage) {
        this.serviceTemplateManage = serviceTemplateManage;
    }


    @Tool(description = "List of all approved services which are available for user to order.")
    public List<UserOrderableServiceVo> getOrderableServices(
            @ToolParam(description = "category of the service")
            Category categoryName,
            @ToolParam(description = "name of the cloud service provider")
            Csp cspName,
            @ToolParam(description = "name of the service", required = false)
            String serviceName,
            @ToolParam(required = false, description = "version of the service")
            String serviceVersion,
            @ToolParam(required = false, description = "who hosts ths cloud resources")
            ServiceHostingType serviceHostingType) {

        ServiceTemplateQueryModel queryRequest =
                ServiceTemplateQueryModel.builder()
                        .category(categoryName)
                        .csp(cspName)
                        .serviceName(serviceName)
                        .serviceVersion(serviceVersion)
                        .serviceHostingType(serviceHostingType)
                        .isAvailableInCatalog(true)
                        .checkServiceVendor(false)
                        .build();
        List<ServiceTemplateEntity> serviceTemplateEntities =
                serviceTemplateManage.listServiceTemplates(queryRequest);
        log.info("{} orderable services found.", serviceTemplateEntities.size());
        return serviceTemplateEntities.stream()
                .sorted(Comparator.comparingInt(template -> template.getCsp().ordinal()))
                .map(ServiceTemplateEntityConverter::convertToUserOrderableServiceVo)
                .toList();
    }
}
