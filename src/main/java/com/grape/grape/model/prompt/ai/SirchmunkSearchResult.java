package com.grape.grape.model.prompt.ai;

import lombok.Data;

@Data
public class SirchmunkSearchResult {
    private Boolean success;
    private SirchmunkSearchData data;
    private String error;

    @Data
    public static class SirchmunkSearchData {
        private String type;
        private String summary;
        private Object extra;
    }
}
