package com.ll.mb.domain.member.member.service;

import com.ll.mb.domain.base.genFile.entity.GenFile;
import com.ll.mb.domain.base.genFile.service.GenFileService;
import com.ll.mb.domain.cash.cash.entity.CashLog;
import com.ll.mb.domain.cash.cash.service.CashService;
import com.ll.mb.domain.member.member.entity.Member;
import com.ll.mb.domain.member.member.repository.MemberRepository;
import com.ll.mb.global.app.AppConfig;
import com.ll.mb.global.jpa.BaseEntity;
import com.ll.mb.global.rsData.RsData;
import com.ll.mb.standard.util.Ut;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final CashService cashService;
    private final GenFileService genFileService;

    @Transactional
    public RsData<Member> join(String username, String password, String nickname) {
        return join(username, password, nickname, "");
    }

    @Transactional
    public RsData<Member> join(String username, String password, String nickname, MultipartFile profileImg) {
        // 업로드된 MultipartFile을 일반 파일로 바꿔준다.
        String profileImgFilePath = Ut.file.toFile(profileImg, AppConfig.getTempDirPath());
        return join(username, password, nickname, profileImgFilePath);
    }

    @Transactional
    public RsData<Member> join(String username, String password, String nickname, String profileImgFilePath) {
        if (findByUsername(username).isPresent()) {
            return RsData.of("400-2", "이미 존재하는 회원입니다.");
        }

        Member member = Member.builder()
                .username(username)
                .password(passwordEncoder.encode(password))
                .nickname(nickname)
                .build();

        memberRepository.save(member);

        if (Ut.str.hasLength(profileImgFilePath)) {
            saveProfileImg(member, profileImgFilePath);
        }

        return RsData.of("200",
                "%s님 환영합니다. 회원가입이 완료되었습니다. 로그인 후 이용해주세요.".formatted(member.getUsername())
                , member);
    }

    private void saveProfileImg(Member member, String profileImgFilePath) {
        // fileNo는 자동으로 증가한다.
        genFileService.save(member.getModelName(), member.getId(), "common", "profileImg", 1, profileImgFilePath);
    }

    public Optional<Member> findByUsername(String username) {
        return memberRepository.findByUsername(username);
    }

    @Transactional
    public void addCash(Member member, long price, CashLog.EventType eventType, BaseEntity relEntity) {
        CashLog cashLog = cashService.addCash(member, price, eventType, relEntity);

        long newRestCash = member.getRestCash() + cashLog.getPrice();
        member.setRestCash(newRestCash);
    }

    // 소셜로그인이 실행되었을 때 실행된다.
    @Transactional
    public RsData<Member> whenSocialLogin(String providerTypeCode, String username, String nickname, String profileImgUrl) {
        Optional<Member> opMember = findByUsername(username);

        // 이미 있는 소셜로그인인 경우 기존 회원을 리턴
        if (opMember.isPresent()) return RsData.of("200", "이미 존재합니다.", opMember.get());

        String filePath = Ut.str.hasLength(profileImgUrl) ?
                Ut.file.downloadFileByHttp(profileImgUrl, AppConfig.getTempDirPath()) : "";

        // 소셜로그인으로 첫번째 가입일 때 가입을 한다.
        return join(username, "", nickname, filePath);
    }

    public String getProfileImgUrl(Member member) {
        // member가 null이 아니면 이미지 가져온다.
        return Optional.ofNullable(member)
                .flatMap(this::findProfileImgUrl)
                .orElse("https://placehold.co/30x30?text=UU");
    }

    private Optional<String> findProfileImgUrl(Member member) {
        return genFileService.findBy(
                        member.getModelName(), member.getId(), "common", "profileImg", 1
                )
                .map(GenFile::getUrl);
    }
}
