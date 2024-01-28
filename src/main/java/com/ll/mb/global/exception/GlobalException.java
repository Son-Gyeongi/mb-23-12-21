package com.ll.mb.global.exception;

import com.ll.mb.global.rsData.RsData;
import lombok.Getter;

public class GlobalException extends RuntimeException {
    // GlobalException 클래스는 RuntimeException을 확장하여 전역 예외를 정의

    @Getter
    private RsData<?> rsData;

    public GlobalException(String resultCode, String msg) {
        super(resultCode + ": " + msg);
        // 부모 클래스인 RuntimeException의 생성자를 호출하면서 예외 메시지를 설정
        // 예외 메시지는 resultCode와 msg를 조합하여 설정

        rsData = RsData.of(resultCode, msg);
        // RsData.of(resultCode, msg)를 사용하여 RsData 객체를 생성하고, 이를 rsData 필드에 할당
        // RsData는 결과 코드와 메시지를 포함하는 데이터 객체입니다. 이 객체는 예외를 처리하고 클라이언트에게 응답을 반환할 때 사용
    }
}
