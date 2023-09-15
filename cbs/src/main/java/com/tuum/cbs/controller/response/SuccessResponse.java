package com.tuum.cbs.controller.response;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class SuccessResponse<T> {
    private T data;
    private String message;
}
