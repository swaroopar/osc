package org.eclipse.xpanse.modules.models.service.deployment;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.eclipse.xpanse.modules.models.billing.enums.BillingMode;
import org.eclipse.xpanse.modules.models.common.enums.Category;
import org.eclipse.xpanse.modules.models.common.enums.Csp;
import org.eclipse.xpanse.modules.models.servicetemplate.Region;
import org.eclipse.xpanse.modules.models.servicetemplate.enums.ServiceHostingType;

import java.util.Map;
import java.util.UUID;

@Data
/** Request body for service deployment. */
public class SimpleDeployRequest {

    /** Customer provided name for the service. */
    @Schema(
            description = "ID of the service template that needs to be deployed.")
    @NotNull
    private UUID serviceTemplateId;

    /** Customer provided name for the service. */
    @Schema(
            description =
                    "Customer's name for the service. Used only for customer's reference."
                            + "If not provided, this value will be auto-generated")
    private String customerServiceName;

    /** The property of the Service. */
    @Schema(
            description = "The properties for the requested service",
            additionalProperties = Schema.AdditionalPropertiesValue.TRUE)
    private Map<String, Object> serviceRequestProperties;

}
