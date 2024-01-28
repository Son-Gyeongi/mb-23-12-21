package com.ll.mb.global.initData;

import com.ll.mb.global.app.AppConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.annotation.Order;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;

@Configuration
@RequiredArgsConstructor
public class All {
    @Autowired
    @Lazy
    private All self;

    @Bean
    @Order(2)
        // init 데이터 우선순위로 먼저 실행하기 위해서, 모든게 세팅이 된 다음에 실행되기를 바라서 순서를 1이 아닌 2부터 시작했다.
    ApplicationRunner initAll() {
        return args -> {
            self.work1();
        };
    }

    @Transactional
    public void work1() {
        // 개발환경이든 아니든 해당 폴더는 필요하다.
        // c:/temp/mb/temp에서 temp 또는 mb 또는 temp 폴더가 없으면 만든다.
        // mkdirs() : getTempDirPath() 경로에 해당하는 폴더가 생길 떄까지 폴더를 만든다.
        new File(AppConfig.getTempDirPath()).mkdirs();
    }
}
