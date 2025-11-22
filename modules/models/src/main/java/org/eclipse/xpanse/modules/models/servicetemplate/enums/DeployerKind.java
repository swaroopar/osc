/*
 * SPDX-License-Identifier: Apache-2.0
 * SPDX-FileCopyrightText: Huawei Inc.
 */

package org.eclipse.xpanse.modules.models.servicetemplate.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import org.apache.commons.lang3.Strings;
import org.eclipse.xpanse.modules.models.common.exceptions.UnsupportedEnumValueException;

/** Deployer kinds. */
public enum DeployerKind {
    TERRAFORM("terraform"),
    OPEN_TOFU("opentofu"),
    HELM("helm");

    private final String type;

    DeployerKind(String type) {
        this.type = type;
    }

    /** For DeployerType serialize. */
    @JsonCreator
    public static DeployerKind getByValue(String type) {
        for (DeployerKind kind : values()) {
            if (Strings.CI.equals(kind.type, type)) {
                return kind;
            }
        }
        throw new UnsupportedEnumValueException(
                String.format("DeployerKind value %s is not supported.", type));
    }

    /** For DeployerType deserialize. */
    @JsonValue
    public String toValue() {
        return this.type;
    }
}
