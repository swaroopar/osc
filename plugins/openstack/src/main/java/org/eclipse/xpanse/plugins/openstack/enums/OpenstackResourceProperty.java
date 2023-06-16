/*
 * SPDX-License-Identifier: Apache-2.0
 * SPDX-FileCopyrightText: Openstack Inc.
 *
 */

package org.eclipse.xpanse.plugins.openstack.enums;

import java.util.HashMap;
import java.util.Map;
import org.eclipse.xpanse.modules.models.service.deploy.enums.DeployResourceKind;


/**
 * Enum for DeployResourceKind and Openstack Resource Property.
 */
public enum OpenstackResourceProperty {
    OPENSTACK_VM_PROPERTY(DeployResourceKind.VM, new OpenstackVmProperty()),
    OPENSTACK_VOLUME_PROPERTY(DeployResourceKind.VOLUME, new OpenstackVolumeProperty()),
    OPENSTACK_VPC_PROPERTY(DeployResourceKind.VPC, new OpenstackVpcProperty()),
    OPENSTACK_PUBLIC_IP_PROPERTY(DeployResourceKind.PUBLIC_IP, new OpenstackPublicIpProperty());

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

    OpenstackResourceProperty(DeployResourceKind resourceKind,
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
        for (OpenstackResourceProperty property : values()) {
            if (property.resourceKind.equals(resourceKind)) {
                return property.properties;
            }
        }
        return new HashMap<>();
    }

    /**
     * Openstack cloud vm property.
     */
    static class OpenstackVmProperty extends HashMap<String, String> {

        /**
         * Init method to put property key and value.
         */
        OpenstackVmProperty() {
            this.put(IP_PROPERTY_NAME, "access_ip_v4");
            this.put(IMAGE_ID_PROPERTY_NAME, IMAGE_ID_PROPERTY_NAME);
            this.put(IMAGE_NAME_PROPERTY_NAME, IMAGE_NAME_PROPERTY_NAME);
            this.put(REGION_PROPERTY_NAME, REGION_PROPERTY_NAME);
        }
    }

    /**
     * Openstack cloud publicIp property.
     */
    static class OpenstackPublicIpProperty extends HashMap<String, String> {

        /**
         * Init method to put property key and value.
         */
        OpenstackPublicIpProperty() {
            this.put(IP_PROPERTY_NAME, "address");
        }
    }

    /**
     * Openstack cloud volume property.
     */
    static class OpenstackVolumeProperty extends HashMap<String, String> {

        /**
         * Init method to put property key and value.
         */
        OpenstackVolumeProperty() {
            this.put(SIZE_PROPERTY_NAME, SIZE_PROPERTY_NAME);
            this.put(TYPE_PROPERTY_NAME, "volume_type");
        }
    }

    /**
     * Openstack cloud vpc property.
     */
    static class OpenstackVpcProperty extends HashMap<String, String> {

        /**
         * Init method to put property key and value.
         */
        OpenstackVpcProperty() {
            this.put(VPC_PROPERTY_NAME, "network_id");
            this.put(SUBNET_PROPERTY_NAME, "subnetpool_id");
        }
    }
}
