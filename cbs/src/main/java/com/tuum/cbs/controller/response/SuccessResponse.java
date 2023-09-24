package com.tuum.cbs.controller.response;

public class SuccessResponse<T> {
    private T data;
    private String message;

    public SuccessResponse() {
    }

    public SuccessResponse(T data, String message) {
        this.data = data;
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return "SuccessResponse{" +
                "data=" + data +
                ", message='" + message + '\'' +
                '}';
    }
}
