package org.eclipse.osc.modules.ocl.loader.data.models;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.List;
import lombok.Data;

/**
 * Defines compute services required to run the managed service.
 */
@Data
public class Compute {

    @SuppressFBWarnings("EI_EXPOSE_REP")
    private List<Vm> vm;

}
