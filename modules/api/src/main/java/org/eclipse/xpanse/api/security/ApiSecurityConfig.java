/*
 * SPDX-License-Identifier: Apache-2.0
 * SPDX-FileCopyrightText: Huawei Inc.
 */

package org.eclipse.xpanse.api.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
class ApiSecurityConfig {

    @Bean
    public SecurityFilterChain securityChain(HttpSecurity httpSecurity) throws Exception {

        httpSecurity.sessionManagement(
                it -> it.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        httpSecurity.authorizeRequests(it -> {
            it.requestMatchers("/xpanse/**").hasRole("admin");
            it.requestMatchers("/xpanse/**").authenticated();
            // add additional routes
        });
        httpSecurity.oauth2ResourceServer().opaqueToken();
        return httpSecurity.build();
    }

}
