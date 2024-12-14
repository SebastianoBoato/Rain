package com.example.rain.utils;

public interface OneElementCallback<T> {
    void onSuccess(T element);
    void onError(String error);
}
