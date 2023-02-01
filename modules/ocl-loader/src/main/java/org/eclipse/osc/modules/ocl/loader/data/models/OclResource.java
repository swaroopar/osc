package org.eclipse.osc.modules.ocl.loader.data.models;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.HashMap;
import java.util.Map;
import lombok.Data;

/**
 * Describes the list of resources that are deployed as part of managed service deployment.
 */
@SuppressFBWarnings("EI_EXPOSE_REP")
@Data
public class OclResource {

    String state = "inactive";
    String id = "";
    String type = "";
    String name = "";

    Map<String, String> properties = new HashMap<>();
}
