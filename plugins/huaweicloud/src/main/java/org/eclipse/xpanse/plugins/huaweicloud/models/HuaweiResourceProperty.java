/*
 * SPDX-License-Identifier: Apache-2.0
 * SPDX-FileCopyrightText: Huawei Inc.
 *
 */

package org.eclipse.xpanse.plugins.huaweicloud.models;

import java.util.HashMap;
import java.util.Map;
import org.eclipse.xpanse.modules.models.service.deploy.enums.DeployResourceKind;


/**
 * Enum for DeployResourceKind and Huawei Resource Property.
 */
public enum HuaweiResourceProperty {
    HUAWEI_VM_PROPERTY(DeployResourceKind.VM, new HuaweiVmProperty()),
    HUAWEI_VOLUME_PROPERTY(DeployResourceKind.VOLUME, new HuaweiVolumeProperty()),
    HUAWEI_VPC_PROPERTY(DeployResourceKind.VPC, new HuaweiVpcProperty()),
    HUAWEI_PUBLIC_IP_PROPERTY(DeployResourceKind.PUBLIC_IP, new HuaweiPublicIpProperty());

    private static final String IP_PROPERTY_NAME = "ip";
    private static final String IMAGE_NAME_PROPERTY_NAME = "image_name";
    private static final String IMAGE_ID_PROPERTY_NAME = "image_id";
    private static final String REGION_PROPERTY_NAME = "region";
    private static final String VPC_PROPERTY_NAME = "vpc";
    private static final String SUBNET_PROPERTY_NAME = "subnet";
    private static final String SIZE_PROPERTY_NAME = "size";
    private static final String TYPE_PROPERTY_NAME = "type";

    private final DeployResourceKind resourceKind;
    private final Map<String, String> properties;

    HuaweiResourceProperty(DeployResourceKind resourceKind,
            Map<String, String> resourceProperties) {
        this.resourceKind = resourceKind;
        this.properties = resourceProperties;
    }

    /**
     * get property by resourceKind.
     *
     * @param resourceKind deployResourceKind
     * @return property map
     */
    public static Map<String, String> getProperties(DeployResourceKind resourceKind) {
        for (HuaweiResourceProperty property : values()) {
            if (property.resourceKind.equals(resourceKind)) {
                return property.properties;
            }
        }
        return new HashMap<>();
    }

    /**
     * Huawei cloud vm property.
     */
    static class HuaweiVmProperty extends HashMap<String, String> {

        /**
         * Init method to put property key and value.
         */
        HuaweiVmProperty() {
            this.put(IP_PROPERTY_NAME, "access_ip_v4");
            this.put(IMAGE_ID_PROPERTY_NAME, IMAGE_ID_PROPERTY_NAME);
            this.put(IMAGE_NAME_PROPERTY_NAME, IMAGE_NAME_PROPERTY_NAME);
            this.put(REGION_PROPERTY_NAME, REGION_PROPERTY_NAME);
        }
    }

    /**
     * Huawei cloud publicIp property.
     */
    static class HuaweiPublicIpProperty extends HashMap<String, String> {

        /**
         * Init method to put property key and value.
         */
        HuaweiPublicIpProperty() {
            this.put(IP_PROPERTY_NAME, "address");
        }
    }

    /**
     * Huawei cloud volume property.
     */
    static class HuaweiVolumeProperty extends HashMap<String, String> {

        /**
         * Init method to put property key and value.
         */
        HuaweiVolumeProperty() {
            this.put(SIZE_PROPERTY_NAME, SIZE_PROPERTY_NAME);
            this.put(TYPE_PROPERTY_NAME, "volume_type");
        }
    }

    /**
     * Huawei cloud vpc property.
     */
    static class HuaweiVpcProperty extends HashMap<String, String> {

        /**
         * Init method to put property key and value.
         */
        HuaweiVpcProperty() {
            this.put(VPC_PROPERTY_NAME, "vpc_id");
            this.put(SUBNET_PROPERTY_NAME, "subnet_id");
        }
    }
}
