package com.axiora.spotgo.iam.infrastructure.persistence.jpa.converters;

import com.axiora.spotgo.iam.domain.model.valueobjects.UserRole;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class UserRoleConverter implements AttributeConverter<UserRole, String> {

    @Override
    public String convertToDatabaseColumn(UserRole role) {
        return role == null ? null : role.name();
    }

    @Override
    public UserRole convertToEntityAttribute(String dbData) {
        return dbData == null ? null : UserRole.valueOf(dbData.trim().toUpperCase());
    }
}
