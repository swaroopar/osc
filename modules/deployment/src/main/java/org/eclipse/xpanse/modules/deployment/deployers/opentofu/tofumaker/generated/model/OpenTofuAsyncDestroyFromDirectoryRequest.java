/*
 * Tofu-Maker API
 * RESTful Services to interact with Tofu-Maker runtime
 *
 * The version of the OpenAPI document: 1.0.0-SNAPSHOT
 * 
 *
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */


package org.eclipse.xpanse.modules.deployment.deployers.opentofu.tofumaker.generated.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.HashMap;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * OpenTofuAsyncDestroyFromDirectoryRequest
 */
@JsonPropertyOrder({
  OpenTofuAsyncDestroyFromDirectoryRequest.JSON_PROPERTY_DESTROY_SCENARIO,
  OpenTofuAsyncDestroyFromDirectoryRequest.JSON_PROPERTY_VARIABLES,
  OpenTofuAsyncDestroyFromDirectoryRequest.JSON_PROPERTY_ENV_VARIABLES,
  OpenTofuAsyncDestroyFromDirectoryRequest.JSON_PROPERTY_WEBHOOK_CONFIG
})
@jakarta.annotation.Generated(value = "org.openapitools.codegen.languages.JavaClientCodegen")
public class OpenTofuAsyncDestroyFromDirectoryRequest {
  /**
   * The destroy scenario when the Xpanse client send the destroy request. Valid values: destroy,rollback,purge.
   */
  public enum DestroyScenarioEnum {
    DESTROY("destroy"),
    
    ROLLBACK("rollback"),
    
    PURGE("purge");

    private String value;

    DestroyScenarioEnum(String value) {
      this.value = value;
    }

    @JsonValue
    public String getValue() {
      return value;
    }

    @Override
    public String toString() {
      return String.valueOf(value);
    }

    @JsonCreator
    public static DestroyScenarioEnum fromValue(String value) {
      for (DestroyScenarioEnum b : DestroyScenarioEnum.values()) {
        if (b.value.equals(value)) {
          return b;
        }
      }
      throw new IllegalArgumentException("Unexpected value '" + value + "'");
    }
  }

  public static final String JSON_PROPERTY_DESTROY_SCENARIO = "destroyScenario";
  private DestroyScenarioEnum destroyScenario;

  public static final String JSON_PROPERTY_VARIABLES = "variables";
  private Map<String, Object> variables = new HashMap<>();

  public static final String JSON_PROPERTY_ENV_VARIABLES = "envVariables";
  private Map<String, String> envVariables = new HashMap<>();

  public static final String JSON_PROPERTY_WEBHOOK_CONFIG = "webhookConfig";
  private WebhookConfig webhookConfig;

  public OpenTofuAsyncDestroyFromDirectoryRequest() {
  }

  public OpenTofuAsyncDestroyFromDirectoryRequest destroyScenario(DestroyScenarioEnum destroyScenario) {
    
    this.destroyScenario = destroyScenario;
    return this;
  }

   /**
   * The destroy scenario when the Xpanse client send the destroy request. Valid values: destroy,rollback,purge.
   * @return destroyScenario
  **/
  @jakarta.annotation.Nullable
  @JsonProperty(JSON_PROPERTY_DESTROY_SCENARIO)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)

  public DestroyScenarioEnum getDestroyScenario() {
    return destroyScenario;
  }


  @JsonProperty(JSON_PROPERTY_DESTROY_SCENARIO)
  @JsonInclude(value = JsonInclude.Include.USE_DEFAULTS)
  public void setDestroyScenario(DestroyScenarioEnum destroyScenario) {
    this.destroyScenario = destroyScenario;
  }


  public OpenTofuAsyncDestroyFromDirectoryRequest variables(Map<String, Object> variables) {
    
    this.variables = variables;
    return this;
  }

  public OpenTofuAsyncDestroyFromDirectoryRequest putVariablesItem(String key, Object variablesItem) {
    this.variables.put(key, variablesItem);
    return this;
  }

   /**
   * Key-value pairs of regular variables that must be used to execute the OpenTofu request.
   * @return variables
  **/
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


  public OpenTofuAsyncDestroyFromDirectoryRequest envVariables(Map<String, String> envVariables) {
    
    this.envVariables = envVariables;
    return this;
  }

  public OpenTofuAsyncDestroyFromDirectoryRequest putEnvVariablesItem(String key, String envVariablesItem) {
    if (this.envVariables == null) {
      this.envVariables = new HashMap<>();
    }
    this.envVariables.put(key, envVariablesItem);
    return this;
  }

   /**
   * Key-value pairs of variables that must be injected as environment variables to OpenTofu process.
   * @return envVariables
  **/
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


  public OpenTofuAsyncDestroyFromDirectoryRequest webhookConfig(WebhookConfig webhookConfig) {
    
    this.webhookConfig = webhookConfig;
    return this;
  }

   /**
   * Get webhookConfig
   * @return webhookConfig
  **/
  @jakarta.annotation.Nonnull
  @JsonProperty(JSON_PROPERTY_WEBHOOK_CONFIG)
  @JsonInclude(value = JsonInclude.Include.ALWAYS)

  public WebhookConfig getWebhookConfig() {
    return webhookConfig;
  }


  @JsonProperty(JSON_PROPERTY_WEBHOOK_CONFIG)
  @JsonInclude(value = JsonInclude.Include.ALWAYS)
  public void setWebhookConfig(WebhookConfig webhookConfig) {
    this.webhookConfig = webhookConfig;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    OpenTofuAsyncDestroyFromDirectoryRequest openTofuAsyncDestroyFromDirectoryRequest = (OpenTofuAsyncDestroyFromDirectoryRequest) o;
    return Objects.equals(this.destroyScenario, openTofuAsyncDestroyFromDirectoryRequest.destroyScenario) &&
        Objects.equals(this.variables, openTofuAsyncDestroyFromDirectoryRequest.variables) &&
        Objects.equals(this.envVariables, openTofuAsyncDestroyFromDirectoryRequest.envVariables) &&
        Objects.equals(this.webhookConfig, openTofuAsyncDestroyFromDirectoryRequest.webhookConfig);
  }

  @Override
  public int hashCode() {
    return Objects.hash(destroyScenario, variables, envVariables, webhookConfig);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class OpenTofuAsyncDestroyFromDirectoryRequest {\n");
    sb.append("    destroyScenario: ").append(toIndentedString(destroyScenario)).append("\n");
    sb.append("    variables: ").append(toIndentedString(variables)).append("\n");
    sb.append("    envVariables: ").append(toIndentedString(envVariables)).append("\n");
    sb.append("    webhookConfig: ").append(toIndentedString(webhookConfig)).append("\n");
    sb.append("}");
    return sb.toString();
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
