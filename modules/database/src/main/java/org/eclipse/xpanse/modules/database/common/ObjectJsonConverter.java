/*
 * SPDX-License-Identifier: Apache-2.0
 * SPDX-FileCopyrightText: Huawei Inc.
 *
 */

package org.eclipse.xpanse.modules.database.common;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.util.Objects;
import org.apache.commons.lang3.StringUtils;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.SerializationFeature;
import tools.jackson.databind.cfg.DateTimeFeature;
import tools.jackson.databind.json.JsonMapper;

/**
 * Converter to handle object data type and string automatic conversion between database and the
 * entity.
 */
@Converter
public class ObjectJsonConverter implements AttributeConverter<Object, String> {

    private final JsonMapper jsonMapper =
            JsonMapper.builder()
                    .enable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                    .enable(SerializationFeature.FAIL_ON_EMPTY_BEANS)
                    .enable(DateTimeFeature.WRITE_DATES_AS_TIMESTAMPS)
                    .build();

    @Override
    public String convertToDatabaseColumn(Object object) {
        if (Objects.isNull(object)) {
            return null;
        }
        try {
            return jsonMapper.writeValueAsString(object);
        } catch (JacksonException ex) {
            throw new IllegalStateException("Serialising object to string failed.", ex);
        }
    }

    @Override
    public Object convertToEntityAttribute(String dataJson) {
        if (StringUtils.isEmpty(dataJson)) {
            return null;
        }
        try {
            return jsonMapper.readValue(dataJson, Object.class);
        } catch (JacksonException ex) {
            throw new IllegalStateException("Serialising string to object failed.", ex);
        }
    }
}
