package org.eclipse.osc.modules.ocl.loader.data.models;

import java.util.ArrayList;
import java.util.List;
import lombok.Data;

/**
    List of OclResources.
 **/
@Data
public class OclResources {

    String state = "inactive";
    List<OclResource> resources = new ArrayList<>();

}
