package com.tuum.cbs.controller.response;

import lombok.*;

import java.time.Instant;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    @Builder
    public class ErrorResponse {
        private String code;
        private String message;
        private Instant timestamp;
}
