package com.tuum.cbs.models;

public enum TransactionType {
    IN("+"),OUT("-");

    public final String mime;
    TransactionType(String mimeType) {
        this.mime = mimeType;
    }
}
