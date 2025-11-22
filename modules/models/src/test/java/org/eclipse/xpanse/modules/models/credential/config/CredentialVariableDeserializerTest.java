/*
 * SPDX-License-Identifier: Apache-2.0
 * SPDX-FileCopyrightText: Huawei Inc.
 *
 */

package org.eclipse.xpanse.modules.models.credential.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import org.eclipse.xpanse.modules.models.credential.CredentialVariable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tools.jackson.core.JsonParser;
import tools.jackson.core.json.JsonFactory;
import tools.jackson.databind.ObjectMapper;

/** Test of CredentialVariableDeserializer. */
class CredentialVariableDeserializerTest {

    private ObjectMapper objectMapper;

    @BeforeEach
    public void setUp() {
        objectMapper = new ObjectMapper();
    }

    @Test
    public void testDeserialize() throws IOException {
        String json =
                "{\"name\": \"variableName\", \"description\": \"variableDescription\","
                        + " \"isMandatory\": true, \"isSensitive\": false, \"value\":"
                        + " \"variableValue\"}";

        JsonFactory jsonFactory = new JsonFactory();
        JsonParser jsonParser =
                jsonFactory.createParser(objectMapper._deserializationContext(), json);

        CredentialVariableDeserializer deserializer = new CredentialVariableDeserializer();
        CredentialVariable credentialVariable = deserializer.deserialize(jsonParser, null);

        assertEquals("variableName", credentialVariable.getName());
        assertEquals("variableDescription", credentialVariable.getDescription());
        assertTrue(credentialVariable.getIsMandatory());
        assertFalse(credentialVariable.getIsSensitive());
        assertEquals("variableValue", credentialVariable.getValue());
    }

    @Test
    public void testDeserializeWithNullValues() throws IOException {
        String json =
                "{\"name\": null, \"description\": null, \"isMandatory\": null, \"isSensitive\":"
                        + " null, \"value\": null}";

        JsonFactory jsonFactory = new JsonFactory();
        JsonParser jsonParser =
                jsonFactory.createParser(objectMapper._deserializationContext(), json);

        CredentialVariableDeserializer deserializer = new CredentialVariableDeserializer();
        CredentialVariable credentialVariable = deserializer.deserialize(jsonParser, null);

        assertNull(credentialVariable.getName());
        assertNull(credentialVariable.getDescription());
        assertFalse(credentialVariable.getIsMandatory());
        assertFalse(credentialVariable.getIsSensitive());
        assertNull(credentialVariable.getValue());
    }

    @Test
    public void testDeserializeWithMissingFields() {
        String json =
                "{\"name\": \"variableName\", \"description\": null, \"isMandatory\": true,"
                        + " \"isSensitive\": null, \"value\": \"variableValue\"}";

        JsonFactory jsonFactory = new JsonFactory();
        JsonParser jsonParser =
                jsonFactory.createParser(objectMapper._deserializationContext(), json);

        CredentialVariableDeserializer deserializer = new CredentialVariableDeserializer();
        CredentialVariable credentialVariable = deserializer.deserialize(jsonParser, null);

        assertEquals("variableName", credentialVariable.getName());
        assertNull(credentialVariable.getDescription());
        assertTrue(credentialVariable.getIsMandatory());
        assertFalse(credentialVariable.getIsSensitive());
        assertEquals("variableValue", credentialVariable.getValue());
    }
}
