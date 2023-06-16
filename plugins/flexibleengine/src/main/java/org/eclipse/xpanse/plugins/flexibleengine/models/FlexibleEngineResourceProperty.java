/*
 * SPDX-License-Identifier: Apache-2.0
 * SPDX-FileCopyrightText: FlexibleEngine Inc.
 *
 */

package org.eclipse.xpanse.plugins.flexibleengine.models;

import java.util.HashMap;
import java.util.Map;
import org.eclipse.xpanse.modules.models.service.deploy.enums.DeployResourceKind;


/**
 * Enum for DeployResourceKind and FlexibleEngine Resource Property.
 */
public enum FlexibleEngineResourceProperty {
    FLEXIBLE_ENGINE_VM_PROPERTY(DeployResourceKind.VM, new FlexibleEngineVmProperty()),
    FLEXIBLE_ENGINE_VOLUME_PROPERTY(DeployResourceKind.VOLUME, new FlexibleEngineVolumeProperty()),
    FLEXIBLE_ENGINE_VPC_PROPERTY(DeployResourceKind.VPC, new FlexibleEngineVpcProperty()),
    FLEXIBLE_ENGINE_PUBLIC_IP_PROPERTY(DeployResourceKind.PUBLIC_IP,
            new FlexibleEnginePublicIpProperty());

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

    FlexibleEngineResourceProperty(DeployResourceKind resourceKind,
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
        for (FlexibleEngineResourceProperty property : values()) {
            if (property.resourceKind.equals(resourceKind)) {
                return property.properties;
            }
        }
        return new HashMap<>();
    }

    /**
     * FlexibleEngine cloud vm property.
     */
    static class FlexibleEngineVmProperty extends HashMap<String, String> {

        /**
         * Init method to put property key and value.
         */
        FlexibleEngineVmProperty() {
            this.put(IP_PROPERTY_NAME, "access_ip_v4");
            this.put(IMAGE_ID_PROPERTY_NAME, IMAGE_ID_PROPERTY_NAME);
            this.put(IMAGE_NAME_PROPERTY_NAME, IMAGE_NAME_PROPERTY_NAME);
            this.put(REGION_PROPERTY_NAME, REGION_PROPERTY_NAME);
            this.put("project_id", "owner");
        }
    }

    /**
     * FlexibleEngine cloud publicIp property.
     */
    static class FlexibleEnginePublicIpProperty extends HashMap<String, String> {

        /**
         * Init method to put property key and value.
         */
        FlexibleEnginePublicIpProperty() {
            this.put(IP_PROPERTY_NAME, "address");
        }
    }

    /**
     * FlexibleEngine cloud volume property.
     */
    static class FlexibleEngineVolumeProperty extends HashMap<String, String> {

        /**
         * Init method to put property key and value.
         */
        FlexibleEngineVolumeProperty() {
            this.put(SIZE_PROPERTY_NAME, SIZE_PROPERTY_NAME);
            this.put(TYPE_PROPERTY_NAME, "volume_type");
        }
    }

    /**
     * FlexibleEngine cloud vpc property.
     */
    static class FlexibleEngineVpcProperty extends HashMap<String, String> {

        /**
         * Init method to put property key and value.
         */
        FlexibleEngineVpcProperty() {
            this.put(VPC_PROPERTY_NAME, "vpc_id");
            this.put(SUBNET_PROPERTY_NAME, "subnet_id");
        }
    }
}
