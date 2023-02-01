package org.eclipse.osc.modules.ocl.loader.data.models;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.List;
import lombok.Data;

/**
 * Defines the network details on which the managed service is deployed.
 */
@SuppressFBWarnings("EI_EXPOSE_REP")
@Data
public class Network {

    private String id;
    private List<Vpc> vpc;
    private List<Subnet> subnet;
    private List<Security> security;

}
