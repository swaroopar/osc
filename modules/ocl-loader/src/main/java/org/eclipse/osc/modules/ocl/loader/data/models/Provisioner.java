package org.eclipse.osc.modules.ocl.loader.data.models;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.List;
import lombok.Data;

/**
 * Defines all provisioning steps needed to fully deploy the managed service.
 */
@Data
@SuppressFBWarnings("EI_EXPOSE_REP")
public class Provisioner {

    private String name;
    private String type;
    private List<String> environments;
    private List<String> inline;

}
