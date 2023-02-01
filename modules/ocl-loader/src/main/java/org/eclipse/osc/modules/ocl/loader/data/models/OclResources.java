package org.eclipse.osc.modules.ocl.loader.data.models;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;

/**
    List of OclResources.
 **/
@Data
public class OclResources {

    String state = "inactive";
    @SuppressFBWarnings("EI_EXPOSE_REP")
    List<OclResource> resources = new ArrayList<>();

}
