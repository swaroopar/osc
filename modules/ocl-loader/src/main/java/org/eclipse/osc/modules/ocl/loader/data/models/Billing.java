package org.eclipse.osc.modules.ocl.loader.data.models;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.HashMap;
import java.util.Map;
import lombok.Data;

/**
 * Defines the billing model of the managed service.
 */
@Data
public class Billing {

    private String model;
    private String period;
    private String currency;
    private Double fixedPrice;
    private Double variablePrice;
    private String variableItem;
    private String backend;
    @SuppressFBWarnings("EI_EXPOSE_REP")
    private Map<String, Object> properties = new HashMap<>();

}
