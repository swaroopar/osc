package org.eclipse.xpanse.runtime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

import com.c4_soft.springaddons.security.oauth2.test.annotations.WithJwt;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.ser.OffsetDateTimeSerializer;
import jakarta.annotation.Resource;
import jakarta.transaction.Transactional;
import java.net.URI;
import java.net.URL;
import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.xpanse.modules.models.response.Response;
import org.eclipse.xpanse.modules.models.response.ResultType;
import org.eclipse.xpanse.modules.models.servicetemplate.Ocl;
import org.eclipse.xpanse.modules.models.servicetemplate.ReviewRegistrationRequest;
import org.eclipse.xpanse.modules.models.servicetemplate.enums.ServiceRegistrationState;
import org.eclipse.xpanse.modules.models.servicetemplate.enums.ServiceReviewResult;
import org.eclipse.xpanse.modules.models.servicetemplate.utils.OclLoader;
import org.eclipse.xpanse.modules.models.servicetemplate.view.ServiceTemplateDetailVo;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

@Slf4j
@Transactional
@ExtendWith(SpringExtension.class)
@SpringBootTest(properties = {"spring.profiles.active=zitadel,zitadel-testbed"})
@AutoConfigureMockMvc
class CspServiceTemplateApiTest {

    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static ServiceTemplateDetailVo serviceTemplateDetailVo;
    @Resource
    private MockMvc mockMvc;

    @BeforeAll
    static void configureObjectMapper() {
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.registerModule(new SimpleModule().addSerializer(OffsetDateTime.class,
                OffsetDateTimeSerializer.INSTANCE));
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        objectMapper.configure(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE, false);
    }

    void registerServiceTemplateByUrl(URL url) throws Exception {

        if (serviceTemplateDetailVo == null) {
            Ocl ocl = new OclLoader().getOcl(url);
            ObjectMapper yamlMapper = new ObjectMapper(new YAMLFactory());
            String requestBody = yamlMapper.writeValueAsString(ocl);
            final MockHttpServletResponse registerResponse = mockMvc.perform(
                            post("/xpanse/service_templates").content(requestBody)
                                    .contentType("application/x-yaml").accept(MediaType.APPLICATION_JSON))
                    .andReturn().getResponse();
            serviceTemplateDetailVo = objectMapper.readValue(registerResponse.getContentAsString(),
                    ServiceTemplateDetailVo.class);
        }
    }

    @Test
    @WithJwt(file = "jwt_admin_csp.json")
    void testCspManageServiceTemplates() throws Exception {
        registerServiceTemplateByUrl(URI.create("file:src/test/resources/ocl_test.yaml").toURL());
        testListAllServiceTemplatesWithStateApprovalPending();
        testReviewServiceTemplateRegistration();
        testAllListServiceTemplatesWithStateApproved();
    }

    void testReviewServiceTemplateRegistration() throws Exception {
        // Setup request 1
        UUID id1 = serviceTemplateDetailVo.getId();
        ReviewRegistrationRequest request1 = new ReviewRegistrationRequest();
        request1.setReviewResult(ServiceReviewResult.APPROVED);
        request1.setReviewComment("reviewComment");
        // Run the test case 1
        final MockHttpServletResponse response1 =
                reviewServiceRegistrationWithParams(id1, request1);
        // Verify the result 1
        assertThat(response1.getStatus()).isEqualTo(HttpStatus.NO_CONTENT.value());
        assertThat(response1.getContentAsString()).isEmpty();
        serviceTemplateDetailVo.setServiceRegistrationState(ServiceRegistrationState.APPROVED);

        // Setup request 2
        ReviewRegistrationRequest request2 = new ReviewRegistrationRequest();
        request2.setReviewResult(ServiceReviewResult.REJECTED);
        request2.setReviewComment("reviewComment");
        Response expectedResponse2 =
                Response.errorResponse(ResultType.SERVICE_TEMPLATE_ALREADY_REVIEWED,
                        Collections.singletonList(
                                String.format("Service template with id %s already reviewed.",
                                        id1)));
        String result2 = objectMapper.writeValueAsString(expectedResponse2);
        // Run the test case 2
        final MockHttpServletResponse response2 =
                reviewServiceRegistrationWithParams(serviceTemplateDetailVo.getId(), request2);
        // Verify the result 2
        assertThat(response2.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response2.getContentAsString()).isEqualTo(result2);

        // Setup request 3
        UUID id3 = UUID.randomUUID();
        ReviewRegistrationRequest request3 = new ReviewRegistrationRequest();
        request3.setReviewResult(ServiceReviewResult.APPROVED);
        request3.setReviewComment("reviewComment");
        Response expectedResponse3 =
                Response.errorResponse(ResultType.SERVICE_TEMPLATE_NOT_REGISTERED,
                        Collections.singletonList(
                                String.format("Service template with id %s not found.", id3)));
        String result3 = objectMapper.writeValueAsString(expectedResponse3);
        // Run the test case 3
        final MockHttpServletResponse response3 =
                reviewServiceRegistrationWithParams(id3, request3);
        // Verify the result 3
        assertThat(response3.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response3.getContentAsString()).isEqualTo(result3);
    }

    void testAllListServiceTemplatesWithStateApproved() throws Exception {

        // Setup request 1
        String serviceRegistrationState1 = "errorState";
        String errorMessage = "Failed to convert value of type 'java.lang.String' to required type";

        // Run the test case 1
        MockHttpServletResponse response1 =
                listServiceTemplatesWithParams(null, null, null, null, null,
                        serviceRegistrationState1);
        // Verify the results 1
        assertThat(response1.getStatus()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY.value());
        assertThat(errorMessage).isSubstringOf(response1.getContentAsString());

        // Setup request 2
        List<ServiceTemplateDetailVo> serviceTemplateDetailVos =
                List.of(serviceTemplateDetailVo);
        String serviceRegistrationState2 = ServiceRegistrationState.APPROVED.toValue();
        // Run the test case 2
        MockHttpServletResponse response2 =
                listServiceTemplatesWithParams(null, null, null, null, null,
                        serviceRegistrationState2);
        // Verify the results 2
        assertThat(response2.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(serviceTemplateDetailVos).usingRecursiveFieldByFieldElementComparatorIgnoringFields("lastModifiedTime").isEqualTo(
                Arrays.stream(objectMapper.readValue(response2.getContentAsString(),
                        ServiceTemplateDetailVo[].class)).toList());


    }

    void testListAllServiceTemplatesWithStateApprovalPending() throws Exception {
        // Setup
        String serviceRegistrationState = ServiceRegistrationState.APPROVAL_PENDING.toValue();
        List<ServiceTemplateDetailVo> serviceTemplateDetailVos = List.of(serviceTemplateDetailVo);
        // Run the test
        MockHttpServletResponse response =
                listServiceTemplatesWithParams(null, null, null, null, null,
                        serviceRegistrationState);
        // Verify the results
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(serviceTemplateDetailVos).usingRecursiveFieldByFieldElementComparatorIgnoringFields("lastModifiedTime").isEqualTo(
                Arrays.stream(objectMapper.readValue(response.getContentAsString(),
                        ServiceTemplateDetailVo[].class)).toList());
    }

    MockHttpServletResponse listServiceTemplatesWithParams(String categoryName, String cspName,
                                                           String serviceName,
                                                           String serviceVersion,
                                                           String serviceHostingType,
                                                           String serviceRegistrationState)
            throws Exception {
        MockHttpServletRequestBuilder getRequestBuilder = get("/xpanse/service_templates/all");
        if (StringUtils.isNotBlank(categoryName)) {
            getRequestBuilder = getRequestBuilder.param("categoryName", categoryName);
        }
        if (StringUtils.isNotBlank(cspName)) {
            getRequestBuilder = getRequestBuilder.param("cspName", cspName);
        }
        if (StringUtils.isNotBlank(serviceName)) {
            getRequestBuilder = getRequestBuilder.param("serviceName", serviceName);
        }
        if (StringUtils.isNotBlank(serviceVersion)) {
            getRequestBuilder = getRequestBuilder.param("serviceVersion", serviceVersion);
        }
        if (StringUtils.isNotBlank(serviceHostingType)) {
            getRequestBuilder = getRequestBuilder.param("serviceHostingType", serviceHostingType);
        }
        if (StringUtils.isNotBlank(serviceRegistrationState)) {
            getRequestBuilder =
                    getRequestBuilder.param("serviceRegistrationState", serviceRegistrationState);
        }
        return mockMvc.perform(getRequestBuilder).andReturn().getResponse();
    }

    MockHttpServletResponse reviewServiceRegistrationWithParams(UUID id,
                                                                ReviewRegistrationRequest request)
            throws Exception {
        String requestBody = objectMapper.writeValueAsString(request);
        return mockMvc.perform(put("/xpanse/service_templates/review/{id}", id).content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();
    }
}