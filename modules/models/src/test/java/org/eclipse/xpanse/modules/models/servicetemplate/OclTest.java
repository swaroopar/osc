/*
 * SPDX-License-Identifier: Apache-2.0
 * SPDX-FileCopyrightText: Huawei Inc.
 *
 */

package org.eclipse.xpanse.modules.models.servicetemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.List;
import org.eclipse.xpanse.modules.models.common.enums.Category;
import org.eclipse.xpanse.modules.models.common.enums.Csp;
import org.eclipse.xpanse.modules.models.servicetemplate.enums.BillingMode;
import org.eclipse.xpanse.modules.models.servicetemplate.enums.DeployerKind;
import org.eclipse.xpanse.modules.models.servicetemplate.enums.ServiceHostingType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.BeanUtils;

/**
 * Test of Ocl.
 */
class OclTest {

    private static final Category category = Category.COMPUTE;
    private static final String version = "2.0";
    private static final String name = "kafka";
    private static final String serviceVersion = "1.0.0";
    private static final String description = "description";
    private static final String namespace = "nameSpace";
    private static final String icon = "icon";
    private static final ServiceHostingType serviceHostingType = ServiceHostingType.SELF;
    private static CloudServiceProvider cloudServiceProvider;
    private static Deployment deployment;
    private static Flavors flavors;
    private static Billing billing;
    private static Ocl ocl;
    private static ServiceProviderContactDetails serviceProviderContactDetails;

    @BeforeEach
    void setUp() {
        Region region = new Region();
        region.setName("cn-north-1");
        region.setArea("Area");
        List<Region> regions = List.of(region);

        cloudServiceProvider = new CloudServiceProvider();
        cloudServiceProvider.setName(Csp.AWS);
        cloudServiceProvider.setRegions(regions);

        deployment = new Deployment();
        deployment.setKind(DeployerKind.TERRAFORM);

        flavors = new Flavors();
        ServiceFlavor flavor = new ServiceFlavor();
        flavor.setName("flavor");
        ModificationImpact modificationImpact = new ModificationImpact();
        modificationImpact.setIsDataLost(false);
        modificationImpact.setIsServiceInterrupted(false);
        flavors.setServiceFlavors(List.of(flavor));
        flavors.setModificationImpact(modificationImpact);

        billing = new Billing();
        billing.setBillingModes(List.of(BillingMode.MONTHLY));

        ocl = new Ocl();
        ocl.setCategory(category);
        ocl.setVersion(version);
        ocl.setName(name);
        ocl.setServiceVersion(serviceVersion);
        ocl.setDescription(description);
        ocl.setNamespace(namespace);
        ocl.setIcon(icon);
        ocl.setCloudServiceProvider(cloudServiceProvider);
        ocl.setDeployment(deployment);
        ocl.setFlavors(flavors);
        ocl.setBilling(billing);
        ocl.setServiceHostingType(serviceHostingType);
        ocl.setServiceProviderContactDetails(serviceProviderContactDetails);
    }

    @Test
    public void testDeepCopyAnEmptyOcl() {
        Ocl ocl1 = new Ocl();
        Ocl aCopy = ocl1.deepCopy();
        assertNotSame(ocl1, aCopy);
        assertNull(aCopy.getName());
    }

    @Test
    public void testDeepCopy() {
        Ocl aCopy = ocl.deepCopy();

        assertEquals(ocl.getCategory(), aCopy.getCategory());
        assertEquals(ocl.getVersion(), aCopy.getVersion());
        assertEquals(ocl.getName(), aCopy.getName());
        assertEquals(ocl.getServiceVersion(), aCopy.getServiceVersion());
        assertEquals(ocl.getDescription(), aCopy.getDescription());
        assertEquals(ocl.getNamespace(), aCopy.getNamespace());
        assertEquals(ocl.getIcon(), aCopy.getIcon());
        assertEquals(ocl.getCloudServiceProvider(), aCopy.getCloudServiceProvider());
        assertEquals(ocl.getDeployment(), aCopy.getDeployment());
        assertEquals(ocl.getFlavors(), aCopy.getFlavors());
        assertEquals(ocl.getBilling(), aCopy.getBilling());

        assertNotSame(ocl, aCopy);
        assertNotSame(ocl.getName(), aCopy.getName());
        assertNotSame(ocl.getDeployment(), aCopy.getDeployment());

        assertNotSame(ocl.getDeployment(), aCopy.getDeployment());
        assertEquals(ocl.getDeployment().getKind(), aCopy.getDeployment().getKind());
        assertEquals(ocl.getServiceHostingType(), aCopy.getServiceHostingType());
        assertEquals(ocl.getServiceProviderContactDetails(),
                aCopy.getServiceProviderContactDetails());
    }

    @Test
    public void testEqualsAndHashCode() {
        Object obj = new Object();
        assertNotEquals(ocl, obj);
        assertNotEquals(ocl.hashCode(), obj.hashCode());

        Ocl ocl1 = new Ocl();
        assertNotEquals(ocl, ocl1);
        assertNotEquals(ocl.hashCode(), ocl1.hashCode());

        BeanUtils.copyProperties(ocl, ocl1);
        assertEquals(ocl, ocl1);
        assertEquals(ocl.hashCode(), ocl1.hashCode());
    }

    @Test
    void testToString() {
        String expectedString = "Ocl(" +
                "category=" + category +
                ", version=" + version +
                ", name=" + name +
                ", serviceVersion=" + serviceVersion +
                ", description=" + description +
                ", namespace=" + namespace +
                ", icon=" + icon +
                ", cloudServiceProvider=" + cloudServiceProvider +
                ", deployment=" + deployment +
                ", flavors=" + flavors +
                ", billing=" + billing +
                ", serviceHostingType=" + serviceHostingType +
                ", serviceProviderContactDetails=" + serviceProviderContactDetails +
                ")";

        assertEquals(expectedString, ocl.toString());
    }

}
