package com.ll.mb.global.rsData;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class RsData<T> {
    private final String resultCode;
    private final String msg;
    private final T data;
    private final int statusCode;

    public static <T> RsData<T> of(String resultCode, String msg, T data) {
        int statusCode = Integer.parseInt(resultCode.split("-", 2)[0]); // resultCode 400-1, 0번째는 400

        return new RsData<>(resultCode, msg, data, statusCode);
    }

    public static RsData<?> of(String resultCode, String msg) {
        return of(resultCode, msg, null);
    }

    public <T> RsData<T> of(T data) {
        return RsData.of(resultCode, msg, data);
    }

    public boolean isSuccess() {
        return 200 <= statusCode && statusCode < 400;
    }

    public boolean isFail() {
        return !isSuccess();
    }
}
