package org.eclipse.osc.modules.ocl.loader.data.models;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import jakarta.annotation.Nonnull;
import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Object which describes the Artifacts needed for provisioning the service.
 */
@Data
@EqualsAndHashCode(callSuper = true)
@SuppressFBWarnings("EI_EXPOSE_REP")
public class Artifact extends RuntimeBase {

    @Nonnull
    private String name;
    private String base;
    private String type;
    private List<String> provisioners;

}
