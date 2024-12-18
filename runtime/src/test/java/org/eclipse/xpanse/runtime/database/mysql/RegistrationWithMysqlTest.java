/*
 * SPDX-License-Identifier: Apache-2.0
 * SPDX-FileCopyrightText: Huawei Inc.
 */

package org.eclipse.xpanse.runtime.database.mysql;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.c4_soft.springaddons.security.oauth2.test.annotations.WithJwt;
import java.net.URI;
import java.util.List;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.eclipse.xpanse.api.controllers.ServiceTemplateApi;
import org.eclipse.xpanse.modules.models.servicetemplate.Ocl;
import org.eclipse.xpanse.modules.models.servicetemplate.enums.ServiceTemplateRegistrationState;
import org.eclipse.xpanse.modules.models.servicetemplate.utils.OclLoader;
import org.eclipse.xpanse.modules.models.servicetemplate.view.ServiceTemplateDetailVo;
import org.eclipse.xpanse.modules.models.servicetemplatechange.ServiceTemplateChangeInfo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.semver4j.Semver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@Slf4j
@ExtendWith(SpringExtension.class)
@SpringBootTest(properties = {"spring.profiles.active=oauth,zitadel,zitadel-testbed,mysql,test",
        "huaweicloud.auto.approve.service.template.enabled=true"})
@AutoConfigureMockMvc
class RegistrationWithMysqlTest extends AbstractMysqlIntegrationTest {

    @Autowired
    private ServiceTemplateApi serviceTemplateApi;

    @Autowired
    private OclLoader oclLoader;

    @Test
    @WithJwt(file = "jwt_isv.json")
    void testServiceTemplateApisWorkWell() throws Exception {
        // Setup register request
        Ocl ocl = oclLoader.getOcl(
                URI.create("file:src/test/resources/ocl_terraform_test.yml").toURL());
        ocl.setName(UUID.randomUUID().toString());
        // Run the test
        ServiceTemplateChangeInfo registerChangeInfo = serviceTemplateApi.register(ocl);
        // Verify the results
        assertNotNull(registerChangeInfo.getServiceTemplateId());
        assertNotNull(registerChangeInfo.getChangeId());
        String serviceTemplateId = registerChangeInfo.getServiceTemplateId().toString();
        ServiceTemplateDetailVo serviceTemplateDetailVo =
                serviceTemplateApi.details(serviceTemplateId);
        assertEquals(ocl.getCategory(), serviceTemplateDetailVo.getCategory());
        assertEquals(ocl.getCloudServiceProvider().getName(), serviceTemplateDetailVo.getCsp());
        assertEquals(ocl.getName().toLowerCase(), serviceTemplateDetailVo.getName());
        assertEquals(new Semver(ocl.getServiceVersion()).getVersion(),
                serviceTemplateDetailVo.getVersion());
        assertEquals(ServiceTemplateRegistrationState.APPROVED,
                serviceTemplateDetailVo.getServiceTemplateRegistrationState());
        assertFalse(serviceTemplateDetailVo.getIsUpdatePending());
        assertTrue(serviceTemplateDetailVo.getAvailableInCatalog());

        // Setup list request
        List<ServiceTemplateDetailVo> serviceTemplateDetailVos =
                serviceTemplateApi.getAllServiceTemplatesByIsv(ocl.getCategory(),
                        serviceTemplateDetailVo.getCsp(), ocl.getName(),
                        serviceTemplateDetailVo.getVersion(), ocl.getServiceHostingType(),
                        ServiceTemplateRegistrationState.APPROVED, true, false);
        // Verify the results
        assertTrue(CollectionUtils.isNotEmpty(serviceTemplateDetailVos));
        assertEquals(serviceTemplateDetailVos.getFirst(), serviceTemplateDetailVo);

        // Setup update request
        ocl.setDescription("update-test");
        // Run the update test with 'isRemoveServiceTemplateUntilApproved' is true
        boolean isRemoveServiceTemplateUntilApproved = true;
        ServiceTemplateChangeInfo updateChangeInfo = serviceTemplateApi.
                update(serviceTemplateId, isRemoveServiceTemplateUntilApproved, ocl);
        // Verify the results
        assertNotNull(updateChangeInfo.getServiceTemplateId());
        assertNotNull(updateChangeInfo.getChangeId());
        // Verify the results
        ServiceTemplateDetailVo updatedServiceTemplateDetailVo =
                serviceTemplateApi.details(serviceTemplateId);
        assertFalse(updatedServiceTemplateDetailVo.getIsUpdatePending());
        assertTrue(updatedServiceTemplateDetailVo.getAvailableInCatalog());
        assertEquals(ServiceTemplateRegistrationState.APPROVED,
                serviceTemplateDetailVo.getServiceTemplateRegistrationState());

        // Setup unregister request
        // Run the test
        ServiceTemplateDetailVo unregisteredServiceTemplateDetailVo =
                serviceTemplateApi.unregister(serviceTemplateId);
        // Verify the results
        assertFalse(unregisteredServiceTemplateDetailVo.getAvailableInCatalog());
        assertFalse(unregisteredServiceTemplateDetailVo.getIsUpdatePending());

        // Setup reRegister request
        // Run the test
        ServiceTemplateDetailVo reRegisteredServiceTemplateDetailVo =
                serviceTemplateApi.reRegisterServiceTemplate(serviceTemplateId);
        // Verify the results
        assertTrue(reRegisteredServiceTemplateDetailVo.getAvailableInCatalog());
        assertFalse(reRegisteredServiceTemplateDetailVo.getIsUpdatePending());

        // Setup delete request
        serviceTemplateApi.unregister(serviceTemplateId);
        // Run the test
        assertDoesNotThrow(() -> serviceTemplateApi.deleteServiceTemplate(serviceTemplateId));
    }

}
