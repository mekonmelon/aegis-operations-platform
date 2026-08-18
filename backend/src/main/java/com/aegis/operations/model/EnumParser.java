package com.aegis.operations.model;

final class EnumParser {
    private EnumParser() {
    }

    static <T extends Enum<T> & JsonEnum> T parse(Class<T> enumType, String value) {
        for (T candidate : enumType.getEnumConstants()) {
            if (candidate.jsonValue().equals(value)) {
                return candidate;
            }
        }

        throw new IllegalArgumentException("Unsupported " + enumType.getSimpleName() + " value: " + value);
    }
}
