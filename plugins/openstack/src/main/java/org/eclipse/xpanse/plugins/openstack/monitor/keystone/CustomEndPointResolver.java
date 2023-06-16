/*
 * SPDX-License-Identifier: Apache-2.0
 * SPDX-FileCopyrightText: Huawei Inc.
 */

package org.eclipse.xpanse.plugins.openstack.monitor.keystone;

import com.google.common.collect.SortedSetMultimap;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.Optional;
import java.util.SortedSet;
import java.util.concurrent.ConcurrentHashMap;
import org.openstack4j.api.exceptions.RegionEndpointNotFoundException;
import org.openstack4j.api.identity.EndpointURLResolver;
import org.openstack4j.api.types.Facing;
import org.openstack4j.api.types.ServiceType;
import org.openstack4j.model.identity.URLResolverParams;
import org.openstack4j.model.identity.v2.Access;
import org.openstack4j.model.identity.v2.Endpoint;
import org.openstack4j.model.identity.v3.Service;
import org.openstack4j.model.identity.v3.Token;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Overrides the DefaultEndpointResolver from OpenStack4j.
 * This is required because the default implementation of OpenStack4j assumes that the metric
 * measures are available via Ceilometer API. But this is already not available in the recent
 * Openstack releases, and we must use Gnocchi API.
 */
public class CustomEndPointResolver implements EndpointURLResolver {
    private static final Logger LOG = LoggerFactory.getLogger(CustomEndPointResolver.class);

    private static final int HASH_CODE = 31;
    private static final Map<Key, String>
            CACHE = new ConcurrentHashMap<>();
    private static final boolean LEGACY_EP_HANDLING = Boolean.getBoolean(LEGACY_EP_RESOLVING_PROP);
    private String publicHostIp;

    @Override
    public String findURLV2(URLResolverParams urlResolverParams) {
        if (urlResolverParams.type == null) {
            return urlResolverParams.access.getEndpoint();
        }

        Key
                key = Key.of(urlResolverParams.access.getCacheIdentifier(), urlResolverParams.type,
                urlResolverParams.perspective, urlResolverParams.region);
        String url = CACHE.get(key);

        if (url != null) {
            return url;
        }

        url = resolveV2(urlResolverParams);

        if (url != null) {
            return url;
        } else if (urlResolverParams.region != null) {
            throw RegionEndpointNotFoundException.create(urlResolverParams.region,
                    urlResolverParams.type.getServiceName());
        }

        return urlResolverParams.access.getEndpoint();
    }

    @Override
    public String findURLV3(URLResolverParams urlResolverParams) {

        if (urlResolverParams.type == null) {
            return urlResolverParams.token.getEndpoint();
        }

        Key
                key = Key.of(urlResolverParams.token.getCacheIdentifier(), urlResolverParams.type,
                urlResolverParams.perspective, urlResolverParams.region);

        String url = CACHE.get(key);

        if (url != null) {
            return url;
        }

        url = resolveV3(urlResolverParams);

        if (url != null) {
            CACHE.put(key, url);
            return url;
        } else if (urlResolverParams.region != null) {
            throw RegionEndpointNotFoundException.create(urlResolverParams.region,
                    urlResolverParams.type.getServiceName());
        }

        return urlResolverParams.token.getEndpoint();
    }

    private String resolveV2(URLResolverParams urlResolverParams) {
        SortedSetMultimap<String, ? extends Access.Service> catalog =
                urlResolverParams.access.getAggregatedCatalog();
        SortedSet<? extends Access.Service> services =
                catalog.get(urlResolverParams.type.getServiceName());

        if (services.isEmpty()) {
            services = catalog.get(urlResolverParams.type.getType());
        }

        if (!services.isEmpty()) {
            Access.Service service =
                    urlResolverParams.getV2Resolver().resolveV2(urlResolverParams.type, services);
            for (Endpoint endpoint : service.getEndpoints()) {
                if (urlResolverParams.region != null
                        && !urlResolverParams.region.equalsIgnoreCase(endpoint.getRegion())) {
                    continue;
                }

                if (service.getServiceType() == ServiceType.NETWORK) {
                    service.getEndpoints().get(0).toBuilder().type(service.getServiceType().name());
                }

                if (urlResolverParams.perspective == null) {
                    return getEndpointUrl(urlResolverParams.access, endpoint);
                }

                return switch (urlResolverParams.perspective) {
                    case ADMIN -> endpoint.getAdminURL().toString();
                    case INTERNAL -> endpoint.getInternalURL().toString();
                    default -> endpoint.getPublicURL().toString();
                };
            }
        } else {
            //if no catalog returned, if is identity service, just return endpoint
            if (ServiceType.IDENTITY.equals(urlResolverParams.type)) {
                return urlResolverParams.access.getEndpoint();
            }
        }
        return null;
    }

    private String resolveV3(URLResolverParams urlResolverParams) {
        Token token = urlResolverParams.token;

        //in v3 api, if user has no default project, and token is unscoped,
        // no catalog will be returned
        //then if service is Identity service, should directly return the endpoint back
        if (token.getCatalog() == null) {
            if (ServiceType.IDENTITY.equals(urlResolverParams.type)) {
                return token.getEndpoint();
            }
            return null;
        }

        for (Service service : token.getCatalog()) {
            // Special handling for metric to get the correct end point.
            if (urlResolverParams.type == ServiceType.TELEMETRY
                    && service.getType().equals("metric")
                    || urlResolverParams.type == ServiceType.forName(service.getType())
                    || urlResolverParams.type == ServiceType.forName(service.getName())) {
                if (urlResolverParams.perspective == null) {
                    urlResolverParams.perspective = Facing.PUBLIC;
                }
                for (org.openstack4j.model.identity.v3.Endpoint endpoint : service.getEndpoints()) {

                    if (matches(endpoint, urlResolverParams)) {
                        return endpoint.getUrl().toString();
                    }
                }
            }
        }
        return null;
    }

    /**
     * Returns <code>true</code> for any endpoint that matches a given
     * {@link URLResolverParams}.
     *
     * @param endpoint          Endpoint to be resolved
     * @param urlResolverParams URL parameters
     * @return returns if endpoint matches
     */
    private boolean matches(org.openstack4j.model.identity.v3.Endpoint endpoint,
                            URLResolverParams urlResolverParams) {
        boolean matches = endpoint.getIface() == urlResolverParams.perspective;
        if (Optional.ofNullable(urlResolverParams.region).isPresent()) {
            matches &= endpoint.getRegion().equals(urlResolverParams.region);
        }
        return matches;
    }

    /**
     * Gets the endpoint url.
     *
     * @param access   the current access data source
     * @param endpoint the endpoint
     * @return the endpoint url
     */
    private String getEndpointUrl(Access access, Endpoint endpoint) {
        if (LEGACY_EP_HANDLING) {
            if (endpoint.getAdminURL() != null) {
                if (getPublicIp(access) != null
                        && !getPublicIp(access).equals(endpoint.getAdminURL().getHost())) {
                    return endpoint.getAdminURL().toString()
                            .replaceAll(endpoint.getAdminURL().getHost(),
                                    getPublicIp(access));
                }
                return endpoint.getAdminURL().toString();
            }
        }
        return endpoint.getPublicURL().toString();
    }

    private String getPublicIp(Access access) {
        if (publicHostIp == null) {
            try {
                publicHostIp = new URI(access.getEndpoint()).getHost();
            } catch (URISyntaxException uriSyntaxException) {
                LOG.error(uriSyntaxException.getMessage(), uriSyntaxException);
            }
        }
        return publicHostIp;
    }

    private record Key(String uid, ServiceType type, Facing perspective) {

        static Key of(String uid, ServiceType type, Facing perspective, String region) {
            return new Key((region == null) ? uid : uid + region, type, perspective);
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            Key other = (Key) obj;
            if (perspective != other.perspective) {
                return false;
            }
            if (type != other.type) {
                return false;
            }
            if (uid == null) {
                return other.uid == null;
            } else {
                return uid.equals(other.uid);
            }
        }

        @Override
        public int hashCode() {
            int result = 1;
            result = HASH_CODE * result
                    + (this.perspective == null ? 0 : this.perspective.hashCode());
            result = HASH_CODE * result + (this.type == null ? 0 : this.type.hashCode());
            result = HASH_CODE * result + (this.uid == null ? 0 : this.uid.hashCode());
            return result;
        }
    }
}
