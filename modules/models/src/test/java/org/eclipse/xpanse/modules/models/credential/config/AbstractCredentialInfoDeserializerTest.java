package org.eclipse.xpanse.modules.models.credential.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import org.eclipse.xpanse.modules.models.common.enums.Csp;
import org.eclipse.xpanse.modules.models.credential.AbstractCredentialInfo;
import org.eclipse.xpanse.modules.models.credential.CredentialVariables;
import org.eclipse.xpanse.modules.models.credential.enums.CredentialType;
import org.junit.jupiter.api.Test;
import tools.jackson.core.JsonParser;
import tools.jackson.core.json.JsonFactory;
import tools.jackson.databind.ObjectMapper;

public class AbstractCredentialInfoDeserializerTest {
    private final AbstractCredentialInfoDeserializer deserializer =
            new AbstractCredentialInfoDeserializer();
    private final JsonFactory jsonFactory = new JsonFactory();
    private final ObjectMapper objectMapper = new ObjectMapper(jsonFactory);

    @Test
    public void deserialize_ValidJson_ShouldReturnCredentialVariables() throws IOException {
        String json =
                "{\"csp\":\"AWS\",\"type\":\"VARIABLES\",\"name\":\"MyCredential\",\"description\":\"Credential"
                    + " for AWS\",\"userId\":\"user123\",\"timeToLive\":3600,\"variables\":[]}";
        JsonParser jsonParser =
                jsonFactory.createParser(objectMapper._deserializationContext(), json);
        AbstractCredentialInfo result = deserializer.deserialize(jsonParser, null);

        assertNotNull(result);
        assertInstanceOf(CredentialVariables.class, result);
        CredentialVariables credentialVariables = (CredentialVariables) result;
        assertEquals(Csp.AWS, credentialVariables.getCsp());
        assertEquals(CredentialType.VARIABLES, credentialVariables.getType());
        assertEquals("MyCredential", credentialVariables.getName());
        assertEquals("Credential for AWS", credentialVariables.getDescription());
        assertEquals("user123", credentialVariables.getUserId());
        assertEquals(Integer.valueOf(3600), credentialVariables.getTimeToLive());
        assertTrue(credentialVariables.getVariables().isEmpty());
    }

    @Test
    public void deserialize_UnsupportedCsp_ShouldReturnNull() throws IOException {
        String json =
                "{\"csp\":\"UNSUPPORTED\",\"type\":\"VARIABLES\",\"name\":\"MyCredential\",\"description\":\"Credential"
                    + " for AWS\",\"userId\":\"user123\",\"timeToLive\":3600,\"variables\":[]}";
        JsonParser jsonParser =
                jsonFactory.createParser(objectMapper._deserializationContext(), json);
        AbstractCredentialInfo result = deserializer.deserialize(jsonParser, null);

        assertNull(result);
    }

    @Test
    public void deserialize_UnsupportedCredentialType_ShouldReturnNull() {
        String json =
                "{\"csp\":\"AWS\",\"type\":\"UNSUPPORTED\",\"name\":\"MyCredential\",\"description\":\"Credential"
                    + " for AWS\",\"userId\":\"user123\",\"timeToLive\":3600,\"variables\":[]}";
        JsonParser jsonParser =
                jsonFactory.createParser(objectMapper._deserializationContext(), json);
        AbstractCredentialInfo result = deserializer.deserialize(jsonParser, null);

        assertNull(result);
    }

    @Test
    public void deserialize_InvalidVariables_ShouldReturnCredentialVariablesWithNullVariables()
            throws IOException {
        String json =
                "{\"csp\":\"AWS\",\"type\":\"VARIABLES\",\"name\":\"MyCredential\",\"description\":\"Credential"
                    + " for AWS\",\"userId\":\"user123\",\"timeToLive\":3600,\"variables\":\"invalid\"}";
        JsonParser jsonParser =
                jsonFactory.createParser(objectMapper._deserializationContext(), json);
        AbstractCredentialInfo result = deserializer.deserialize(jsonParser, null);

        assertNotNull(result);
        assertInstanceOf(CredentialVariables.class, result);
        CredentialVariables credentialVariables = (CredentialVariables) result;
        assertNull(credentialVariables.getVariables());
    }

    @Test
    public void deserialize_MissingOptionalFields_ShouldReturnCredentialVariablesWithNullValues()
            throws IOException {
        String json = "{\"csp\":\"AWS\",\"type\":\"VARIABLES\"}";
        JsonParser jsonParser =
                jsonFactory.createParser(objectMapper._deserializationContext(), json);
        AbstractCredentialInfo result = deserializer.deserialize(jsonParser, null);

        assertNotNull(result);
        assertInstanceOf(CredentialVariables.class, result);
        CredentialVariables credentialVariables = (CredentialVariables) result;
        assertNull(credentialVariables.getName());
        assertNull(credentialVariables.getDescription());
        assertNull(credentialVariables.getUserId());
        assertNull(credentialVariables.getTimeToLive());
        assertNull(credentialVariables.getVariables());
    }
}
