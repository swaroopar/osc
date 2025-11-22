/*
 * SPDX-License-Identifier: Apache-2.0
 * SPDX-FileCopyrightText: Huawei Inc.
 */

package org.eclipse.xpanse.modules.models.credential.config;

import java.util.List;
import java.util.function.Function;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.xpanse.modules.models.common.enums.Csp;
import org.eclipse.xpanse.modules.models.common.exceptions.UnsupportedEnumValueException;
import org.eclipse.xpanse.modules.models.credential.AbstractCredentialInfo;
import org.eclipse.xpanse.modules.models.credential.CredentialVariable;
import org.eclipse.xpanse.modules.models.credential.CredentialVariables;
import org.eclipse.xpanse.modules.models.credential.enums.CredentialType;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.deser.std.StdDeserializer;
import tools.jackson.databind.exc.MismatchedInputException;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.module.SimpleModule;

/**
 * Custom deserializer for AbstractCredentialInfo class. This deserializer is used to deserialize
 * JSON data into an instance of AbstractCredentialInfo, this is needed because of the final fields
 * which Jackson cannot handle.
 */
@Slf4j
public class AbstractCredentialInfoDeserializer extends StdDeserializer<AbstractCredentialInfo> {

    /** Default constructor. */
    public AbstractCredentialInfoDeserializer() {
        this(AbstractCredentialInfo.class);
    }

    /** The constructor with the class to be deserialized. */
    public AbstractCredentialInfoDeserializer(Class<?> deserializedClass) {
        super(deserializedClass);
    }

    @Override
    public AbstractCredentialInfo deserialize(
            JsonParser jsonParser, DeserializationContext deserializationContext) {
        JsonNode node = jsonParser.readValueAsTree();
        Csp csp;
        try {
            csp = Csp.getByValue(node.get("csp").asString());
        } catch (Exception e) {
            log.error("Unsupported csp value: {}", node.get("csp").asString());
            return null;
        }
        CredentialType type;
        try {
            type = CredentialType.getByValue(node.get("type").asString());
        } catch (UnsupportedEnumValueException e) {
            log.error("Unsupported credential type: {}", node.get("type").asString());
            return null;
        }

        try {
            String site = safeGet(node, "site", JsonNode::asString);
            String name = safeGet(node, "name", JsonNode::asString);
            String description = safeGet(node, "description", JsonNode::asString);
            String userId = safeGet(node, "userId", JsonNode::asString);
            Integer timeToLive = safeGet(node, "timeToLive", JsonNode::asInt);
            List<CredentialVariable> variables =
                    deserializeCredentialVariables(node.get("variables"));
            return new CredentialVariables(
                    csp, site, type, name, description, userId, timeToLive, variables);
        } catch (Exception e) {
            log.error("IllegalArgumentException: {}", e.getMessage());
        }
        return null;
    }

    private <T> T safeGet(JsonNode node, String fieldName, Function<JsonNode, T> mapper) {
        if (node == null || node.get(fieldName) == null) {
            return null;
        }
        try {
            return mapper.apply(node.get(fieldName));
        } catch (Exception e) {
            log.error("Failed to map field {} due to: {}", fieldName, e.getMessage());
            return null;
        }
    }

    private List<CredentialVariable> deserializeCredentialVariables(JsonNode node) {
        JsonMapper jsonMapper =
                JsonMapper.builder()
                        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                        .addModule(
                                new SimpleModule()
                                        .addDeserializer(
                                                CredentialVariable.class,
                                                new CredentialVariableDeserializer()))
                        .build();
        List<CredentialVariable> credentialVariable = null;
        try {
            credentialVariable =
                    jsonMapper.convertValue(
                            node,
                            jsonMapper
                                    .getTypeFactory()
                                    .constructCollectionType(List.class, CredentialVariable.class));
        } catch (MismatchedInputException e) {
            log.error("Deserialize CredentialVariables with value:{} failed.", node, e);
        }
        return credentialVariable;
    }
}
