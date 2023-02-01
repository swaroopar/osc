package org.eclipse.osc.orchestrator.plugin.huaweicloud.builders.terraform;

import lombok.Data;
import lombok.Getter;

@Data
class TfProperty {

    @Getter
    String ocl;

    @Getter
    String tf;

    TfProperty(String ocl, String tf) {
        this.ocl = ocl;
        this.tf = tf;
    }

    TfProperty(String property) {
        this.ocl = property;
        this.tf = property;
    }
}
