/*
 * SPDX-License-Identifier: Apache-2.0
 * SPDX-FileCopyrightText: Huawei Inc.
 */

package org.eclipse.xpanse.api.security;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.OAuthFlow;
import io.swagger.v3.oas.annotations.security.OAuthFlows;
import io.swagger.v3.oas.annotations.security.OAuthScope;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

@SecurityScheme(name = "security_auth", type = SecuritySchemeType.OAUTH2,
        in = SecuritySchemeIn.HEADER,
        scheme = "bearer",
        flows = @OAuthFlows(authorizationCode = @OAuthFlow(
                authorizationUrl = "${springdoc.oAuthFlow.authorizationUrl}"
                , tokenUrl = "${springdoc.oAuthFlow.tokenUrl}", scopes = {
                @OAuthScope(name = "openid"),
                @OAuthScope(name = "email"),
                @OAuthScope(name = "profile"),
                @OAuthScope(name = "csp"),
                @OAuthScope(name = "admin")
        })))
@Configuration
public class OpenApiConfig {
}