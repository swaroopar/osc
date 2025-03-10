package org.eclipse.xpanse.modules.deployment.deployers.opentofu.tofumaker.config;

import static org.mockito.Mockito.verify;

import org.eclipse.xpanse.modules.deployment.deployers.opentofu.tofumaker.generated.ApiClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class TofuMakerApiClientConfigTest {

    @Mock private ApiClient mockApiClient;

    @InjectMocks private TofuMakerApiClientConfig openTofuMakerApiClientConfigUnderTest;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(
                openTofuMakerApiClientConfigUnderTest,
                "openTofuMakerBaseUrl",
                "http://localhost:9092");
    }

    @Test
    void testApiClientConfig() {
        // Setup
        // Run the test
        openTofuMakerApiClientConfigUnderTest.apiClientConfig();

        // Verify the results
        verify(mockApiClient).setBasePath("http://localhost:9092");
    }
}
