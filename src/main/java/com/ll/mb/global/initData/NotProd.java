package com.ll.mb.global.initData;

import com.ll.mb.domain.member.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.transaction.annotation.Transactional;

@Configuration
@RequiredArgsConstructor
public class NotProd {
    @Autowired
    @Lazy
    private NotProd self;
    private final MemberService memberService;

    @Bean
    ApplicationRunner initNotProd() {
        return args -> {
            self.work1();
        };
    }

    @Transactional
    public void work1() {
        // admin이 이미 있으면 아래 회원 생성이 만들어진 거라고 인식해서 또 실행되어도 만들어지지 않는다.
        if (memberService.findByUsername("admin").isPresent()) return;

        // 회원 생성
        memberService.join("admin", "1234");
        memberService.join("user1", "1234");
        memberService.join("user2", "1234");
    }
}
