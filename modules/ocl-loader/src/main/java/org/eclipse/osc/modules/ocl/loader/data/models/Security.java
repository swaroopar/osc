package org.eclipse.osc.modules.ocl.loader.data.models;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Base class which holds the security rule information.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class Security extends RuntimeBase {

    private String name;
    @SuppressFBWarnings("EI_EXPOSE_REP")
    private List<SecurityRule> rules;

}
