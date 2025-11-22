/*
 * SPDX-License-Identifier: Apache-2.0
 * SPDX-FileCopyrightText: Huawei Inc.
 */

package org.eclipse.xpanse.modules.models.servicetemplate.utils;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import org.eclipse.xpanse.modules.models.common.exceptions.XpanseUnhandledException;
import org.eclipse.xpanse.modules.models.servicetemplate.Ocl;
import org.springframework.stereotype.Component;
import tools.jackson.dataformat.yaml.YAMLFactory;
import tools.jackson.dataformat.yaml.YAMLMapper;

/** Bean to deserialize Ocl data. */
@Component
public class OclLoader {

    private final YAMLMapper yamlMapper = new YAMLMapper(new YAMLFactory());

    public Ocl getOcl(URL url) throws IOException {
        return yamlMapper.readValue(url.openStream(), Ocl.class);
    }

    /** Loads the OCL file from the provided location. * */
    public Ocl getOclByLocation(String oclLocationValue) {
        try {
            return getOcl(URI.create(oclLocationValue).toURL());
        } catch (Exception e) {
            throw new XpanseUnhandledException(e.getMessage());
        }
    }
}
