/*
 * SPDX-License-Identifier: Apache-2.0
 * SPDX-FileCopyrightText: Huawei Inc.
 */

package org.eclipse.xpanse.modules.models.common.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.v3.oas.annotations.media.Schema;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.xpanse.modules.models.common.exceptions.UnsupportedEnumValueException;

/** Cloud service providers. */
public enum Csp {
    @JsonProperty("HuaweiCloud") HUAWEI_CLOUD("HuaweiCloud"),

    @JsonProperty("FlexibleEngine")FLEXIBLE_ENGINE("FlexibleEngine"),
    @JsonProperty("OpenstackTestlab") OPENSTACK_TESTLAB("OpenstackTestlab"),
    @JsonProperty("PlusServer") PLUS_SERVER("PlusServer"),
    @JsonProperty("RegioCloud")REGIO_CLOUD("RegioCloud"),
    @JsonProperty("AlibabaCloud")ALIBABA_CLOUD("AlibabaCloud"),
    @JsonProperty("aws")AWS("aws"),
    @JsonProperty("azure")AZURE("azure"),
    @JsonProperty("GoogleCloudPlatform")GCP("GoogleCloudPlatform");

    private final String value;

    Csp(String value) {
        this.value = value;
    }

    /** For CSP deserialize. */
    @JsonCreator
    public static Csp getByValue(String name) {
        for (Csp csp : values()) {
            if (StringUtils.equalsIgnoreCase(csp.value, name)) {
                return csp;
            }
        }
        throw new UnsupportedEnumValueException(
                String.format("Csp value %s is not supported.", name));
    }

    /** For CSP serialize. */
    @JsonValue
    public String toValue() {
        return this.value;
    }
}
