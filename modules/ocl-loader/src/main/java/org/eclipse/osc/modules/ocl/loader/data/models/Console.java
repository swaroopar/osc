package org.eclipse.osc.modules.ocl.loader.data.models;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.HashMap;
import java.util.Map;
import lombok.Data;

/**
 * Defines the console information for the managed service.
 */
@Data
public class Console {

    private String backend;
    @SuppressFBWarnings("EI_EXPOSE_REP")
    private Map<String, Object> properties = new HashMap<>();

}
