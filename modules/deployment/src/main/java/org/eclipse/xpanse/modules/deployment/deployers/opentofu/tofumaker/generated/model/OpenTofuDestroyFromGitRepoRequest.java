/*
 * Tofu-Maker API
 * RESTful Services to interact with Tofu-Maker runtime
 *
 * The version of the OpenAPI document: 1.0.7-SNAPSHOT
 *
 *
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */


package org.eclipse.xpanse.modules.deployment.deployers.opentofu.tofumaker.generated.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * OpenTofuDestroyFromGitRepoRequest
 */
@JsonPropertyOrder({
        OpenTofuDestroyFromGitRepoRequest.JSON_PROPERTY_REQUEST_ID,
        OpenTofuDestroyFromGitRepoRequest.JSON_PROPERTY_OPEN_TOFU_VERSION,
        OpenTofuDestroyFromGitRepoRequest.JSON_PROPERTY_VARIABLES,
        OpenTofuDestroyFromGitRepoRequest.JSON_PROPERTY_ENV_VARIABLES,
        OpenTofuDestroyFromGitRepoRequest.JSON_PROPERTY_GIT_REPO_DETAILS,
        OpenTofuDestroyFromGitRepoRequest.JSON_PROPERTY_TF_STATE
})
@jakarta.annotation.Generated(value = "org.openapitools.codegen.languages.JavaClientCodegen", comments = "Generator version: 7.9.0")
public class OpenTofuDestroyFromGitRepoRequest {
    public static final String JSON_PROPERTY_REQUEST_ID = "requestId";
    public static final String JSON_PROPERTY_OPEN_TOFU_VERSION = "openTofuVersion";
    public static final String JSON_PROPERTY_VARIABLES = "variables";
    public static final String JSON_PROPERTY_ENV_VARIABLES = "envVariables";
    public static final String JSON_PROPERTY_GIT_REPO_DETAILS = "gitRepoDetails";
    public static final String JSON_PROPERTY_TF_STATE = "tfState";
    private UUID requestId;
    private String openTofuVersion;
    private Map<String, Object> variables = new HashMap<>();
    private Map<String, String> envVariables = new HashMap<>();
    private OpenTofuScriptGitRepoDetails gitRepoDetails;
    private String tfState;

    public OpenTofuDestroyFromGitRepoRequest() {
    }

    public OpenTofuDestroyFromGitRepoRequest requestId(UUID requestId) {

        this.requestId = requestId;
        return this;
    }

    /**
     * Id of the request.
     *
     * @return requestId
     */
    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_REQUEST_ID)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

    public UUID getRequestId() {
        return requestId;
    }


    @JsonProperty(JSON_PROPERTY_REQUEST_ID)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setRequestId(UUID requestId) {
        this.requestId = requestId;
    }

    public OpenTofuDestroyFromGitRepoRequest openTofuVersion(String openTofuVersion) {

        this.openTofuVersion = openTofuVersion;
        return this;
    }

    /**
     * The required version of the OpenTofu which will execute the scripts.
     *
     * @return openTofuVersion
     */
    @jakarta.annotation.Nonnull
    @JsonProperty(JSON_PROPERTY_OPEN_TOFU_VERSION)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)

    public String getOpenTofuVersion() {
        return openTofuVersion;
    }


    @JsonProperty(JSON_PROPERTY_OPEN_TOFU_VERSION)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    public void setOpenTofuVersion(String openTofuVersion) {
        this.openTofuVersion = openTofuVersion;
    }

    public OpenTofuDestroyFromGitRepoRequest variables(Map<String, Object> variables) {

        this.variables = variables;
        return this;
    }

    public OpenTofuDestroyFromGitRepoRequest putVariablesItem(String key, Object variablesItem) {
        this.variables.put(key, variablesItem);
        return this;
    }

    /**
     * Key-value pairs of regular variables that must be used to execute the OpenTofu request.
     *
     * @return variables
     */
    @jakarta.annotation.Nonnull
    @JsonProperty(JSON_PROPERTY_VARIABLES)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)

    public Map<String, Object> getVariables() {
        return variables;
    }


    @JsonProperty(JSON_PROPERTY_VARIABLES)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    public void setVariables(Map<String, Object> variables) {
        this.variables = variables;
    }

    public OpenTofuDestroyFromGitRepoRequest envVariables(Map<String, String> envVariables) {

        this.envVariables = envVariables;
        return this;
    }

    public OpenTofuDestroyFromGitRepoRequest putEnvVariablesItem(String key,
                                                                 String envVariablesItem) {
        if (this.envVariables == null) {
            this.envVariables = new HashMap<>();
        }
        this.envVariables.put(key, envVariablesItem);
        return this;
    }

    /**
     * Key-value pairs of variables that must be injected as environment variables to OpenTofu process.
     *
     * @return envVariables
     */
    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_ENV_VARIABLES)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

    public Map<String, String> getEnvVariables() {
        return envVariables;
    }


    @JsonProperty(JSON_PROPERTY_ENV_VARIABLES)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setEnvVariables(Map<String, String> envVariables) {
        this.envVariables = envVariables;
    }

    public OpenTofuDestroyFromGitRepoRequest gitRepoDetails(
            OpenTofuScriptGitRepoDetails gitRepoDetails) {

        this.gitRepoDetails = gitRepoDetails;
        return this;
    }

    /**
     * Get gitRepoDetails
     *
     * @return gitRepoDetails
     */
    @jakarta.annotation.Nullable
    @JsonProperty(JSON_PROPERTY_GIT_REPO_DETAILS)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

    public OpenTofuScriptGitRepoDetails getGitRepoDetails() {
        return gitRepoDetails;
    }


    @JsonProperty(JSON_PROPERTY_GIT_REPO_DETAILS)
    @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
    public void setGitRepoDetails(OpenTofuScriptGitRepoDetails gitRepoDetails) {
        this.gitRepoDetails = gitRepoDetails;
    }

    public OpenTofuDestroyFromGitRepoRequest tfState(String tfState) {

        this.tfState = tfState;
        return this;
    }

    /**
     * The .tfState file content after deployment
     *
     * @return tfState
     */
    @jakarta.annotation.Nonnull
    @JsonProperty(JSON_PROPERTY_TF_STATE)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)

    public String getTfState() {
        return tfState;
    }


    @JsonProperty(JSON_PROPERTY_TF_STATE)
    @JsonInclude(value = JsonInclude.Include.ALWAYS)
    public void setTfState(String tfState) {
        this.tfState = tfState;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        OpenTofuDestroyFromGitRepoRequest openTofuDestroyFromGitRepoRequest =
                (OpenTofuDestroyFromGitRepoRequest) o;
        return Objects.equals(this.requestId, openTofuDestroyFromGitRepoRequest.requestId) &&
                Objects.equals(this.openTofuVersion,
                        openTofuDestroyFromGitRepoRequest.openTofuVersion) &&
                Objects.equals(this.variables, openTofuDestroyFromGitRepoRequest.variables) &&
                Objects.equals(this.envVariables, openTofuDestroyFromGitRepoRequest.envVariables) &&
                Objects.equals(this.gitRepoDetails,
                        openTofuDestroyFromGitRepoRequest.gitRepoDetails) &&
                Objects.equals(this.tfState, openTofuDestroyFromGitRepoRequest.tfState);
    }

    @Override
    public int hashCode() {
        return Objects.hash(requestId, openTofuVersion, variables, envVariables, gitRepoDetails,
                tfState);
    }

    @Override
    public String toString() {
        String sb = "class OpenTofuDestroyFromGitRepoRequest {\n"
                + "    requestId: " + toIndentedString(requestId) + "\n"
                + "    openTofuVersion: " + toIndentedString(openTofuVersion) + "\n"
                + "    variables: " + toIndentedString(variables) + "\n"
                + "    envVariables: " + toIndentedString(envVariables) + "\n"
                + "    gitRepoDetails: " + toIndentedString(gitRepoDetails) + "\n"
                + "    tfState: " + toIndentedString(tfState) + "\n"
                + "}";
        return sb;
    }

    /**
     * Convert the given object to string with each line indented by 4 spaces
     * (except the first line).
     */
    private String toIndentedString(Object o) {
        if (o == null) {
            return "null";
        }
        return o.toString().replace("\n", "\n    ");
    }

}

