/*
 * SPDX-License-Identifier: Apache-2.0
 * SPDX-FileCopyrightText: Huawei Inc.
 *
 */

package org.eclipse.xpanse.modules.models.service.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.xpanse.modules.models.servicetemplate.DeployVariable;
import org.eclipse.xpanse.modules.models.servicetemplate.utils.JsonObjectSchema;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

/**
 * The Class is used to generate a JSONSchema for service variables.
 */
@Slf4j
@Component
public class ServiceVariablesJsonSchemaGenerator {

    private static final String VARIABLE_TYPE_KEY = "type";
    private static final String VARIABLE_DESCRIPTION_KEY = "description";
    private static final String VARIABLE_EXAMPLE_KEY = "example";

    /**
     * Generate JsonSchema objects to describe validation rules and metadata information for a set
     * of deployment variables.
     *
     * @param deployVariables list of deployVariables in registered service
     * @return JsonObjectSchema
     */
    public JsonObjectSchema buildJsonObjectSchema(List<DeployVariable> deployVariables) {
        JsonObjectSchema jsonObjectSchema = new JsonObjectSchema();
        Map<String, Map<String, Object>> jsonObjectSchemaProperties = new HashMap<>();
        List<String> requiredList = new ArrayList<>();
        for (DeployVariable variable : deployVariables) {
            Map<String, Object> validationProperties = new HashMap<>();

            if (variable.getMandatory()) {
                requiredList.add(variable.getName());
            }

            if (!CollectionUtils.isEmpty(variable.getValueSchema())) {
                variable.getValueSchema().forEach((validator, value) ->
                        validationProperties.put(validator.toValue(), value)
                );
            }

            if (Objects.nonNull(variable.getDataType())) {
                validationProperties.put(VARIABLE_TYPE_KEY, variable.getDataType().toValue());
            }

            if (Objects.nonNull(variable.getDescription())) {
                validationProperties.put(VARIABLE_DESCRIPTION_KEY, variable.getDescription());
            }

            if (Objects.nonNull(variable.getExample())) {
                validationProperties.put(VARIABLE_EXAMPLE_KEY, variable.getExample());
            }

            if (!validationProperties.isEmpty()) {
                jsonObjectSchemaProperties.put(variable.getName(), validationProperties);
            }
        }

        jsonObjectSchema.setRequired(requiredList);
        jsonObjectSchema.setProperties(jsonObjectSchemaProperties);
        return jsonObjectSchema;
    }

}
