package com.grape.grape.enums;

public enum ScenarioTypeEnum {
    NORMAL("normal", "正常场景"),
    EXCEPTION("exception", "异常场景"),
    REVERSE("reverse", "反向场景"),
    MONKEY("monkey", "猴子场景");

    private final String value;
    private final String description;

    ScenarioTypeEnum(String value, String description) {
        this.value = value;
        this.description = description;
    }

    public String getValue() {
        return value;
    }

    public String getDescription() {
        return description;
    }

    public static ScenarioTypeEnum fromValue(String value) {
        for (ScenarioTypeEnum type : ScenarioTypeEnum.values()) {
            if (type.value.equals(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown scenario type: " + value);
    }

    public static boolean isValid(String value) {
        for (ScenarioTypeEnum type : ScenarioTypeEnum.values()) {
            if (type.value.equals(value)) {
                return true;
            }
        }
        return false;
    }
}