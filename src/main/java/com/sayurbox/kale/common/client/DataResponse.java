package com.sayurbox.kale.common.client;

public class DataResponse<T> {

    private T data;

    public void setData(T data) {
        this.data = data;
    }

    public T getData() {
        return data;
    }
}
