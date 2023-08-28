/*
 * SPDX-License-Identifier: Apache-2.0
 * SPDX-FileCopyrightText: Huawei Inc.
 *
 */

package org.eclipse.xpanse.modules.models.servicetemplate.utils;

import java.io.File;
import org.eclipse.xpanse.modules.models.servicetemplate.Ocl;
import org.eclipse.xpanse.modules.models.servicetemplate.enums.BillingCurrency;
import org.eclipse.xpanse.modules.models.servicetemplate.enums.BillingPeriod;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

/**
 * Test of OclLoader.
 */
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {OclLoader.class,})
public class OclLoaderTest {

    @Autowired
    OclLoader oclLoader;

    @Test
    public void loading() throws Exception {

        Ocl ocl = oclLoader.getOcl(new File("target/test-classes/test.yaml").toURI().toURL());

        Assertions.assertNotNull(ocl);

        Assertions.assertEquals("flat", ocl.getBilling().getModel());
        Assertions.assertEquals(BillingCurrency.EUR, ocl.getBilling().getCurrency());
        Assertions.assertEquals(BillingPeriod.MONTHLY, ocl.getBilling().getPeriod());
    }
}