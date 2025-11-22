/*
 * SPDX-License-Identifier: Apache-2.0
 * SPDX-FileCopyrightText: Huawei Inc.
 */

package org.eclipse.xpanse.modules.models.credential.config;

import org.eclipse.xpanse.modules.models.credential.CredentialVariable;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.deser.std.StdDeserializer;

/**
 * Custom deserializer for CredentialVariable class. This deserializer is used to deserialize JSON
 * data into an instance of CredentialVariable, this is needed because of the final fields which
 * Jackson cannot handle.
 */
public class CredentialVariableDeserializer extends StdDeserializer<CredentialVariable> {

    /** Constructor for CredentialVariableDeserializer. */
    public CredentialVariableDeserializer() {
        this(CredentialVariable.class);
    }

    /** Constructor for CredentialVariableDeserializer. */
    public CredentialVariableDeserializer(Class<?> deserializedClass) {
        super(deserializedClass);
    }

    @Override
    public CredentialVariable deserialize(
            JsonParser jsonParser, DeserializationContext deserializationContext) {

        JsonNode node = jsonParser.readValueAsTree();

        String name = node.get("name").isNull() ? null : node.get("name").asString();
        String description =
                node.get("description").isNull() ? null : node.get("description").asString();
        Boolean isMandatory =
                node.get("isMandatory").isNull() ? null : node.get("isMandatory").asBoolean();
        Boolean isSensitive =
                node.get("isSensitive").isNull() ? null : node.get("isSensitive").asBoolean();
        String value = node.get("value").isNull() ? null : node.get("value").asString();

        return new CredentialVariable(
                name,
                description,
                Boolean.TRUE.equals(isMandatory),
                Boolean.TRUE.equals(isSensitive),
                value);
    }
}
