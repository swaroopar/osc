package org.eclipse.osc.orchestrator.plugin.huaweicloud.builders.packer;

import lombok.Data;

/**
 * Packer variables.
 */
@Data
public class PackerVars {

    private String vpcId = "";
    private String subnetId = "";
    private String secGroupId = "";
    private String secGroupName = "";
}
