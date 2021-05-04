package com.sayurbox.kale.common.client;

public class ErrorResponse {

    private ErrorCode error;

    public void setError(ErrorCode error) {
        this.error = error;
    }

    public ErrorCode getError() {
        return error;
    }

}
