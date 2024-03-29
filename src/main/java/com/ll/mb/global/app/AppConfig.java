package com.ll.mb.global.app;

import jakarta.persistence.EntityManager;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class AppConfig {
    // AppConfig 클래스는 Spring의 설정을 담당하는 클래스

    private static String activeProfile;

    @Value("${spring.profiles.active}")
    public void setActiveProfile(String value) {
        activeProfile = value;
    }

    public static boolean isNotProd() {
        return !isProd();
    }

    public static boolean isProd() {
        return activeProfile.equals("prod");
    }

    @Getter
    private static String siteName;

    @Value("${custom.site.name}")
    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    @Getter // @Getter 어노테이션은 Lombok 라이브러리를 사용하여 해당 필드의 getter 메서드를 자동으로 생성
    private static EntityManager entityManager;

    @Autowired // @Autowired 어노테이션은 Spring에게 해당 메서드가 자동으로 호출될 때 이 메서드의 매개변수에 맞는 빈을 주입해야 함
    public void setEntityManager(EntityManager entityManager) {
        // setEntityManager 메서드는 EntityManager 빈을 주입받아서 AppConfig 클래스의 entityManager 필드에 할당
        this.entityManager = entityManager;
    }

    @Getter
    private static String tempDirPath;

    @Value("${custom.temp.dirPath}")
    public void setTempDirPath(String tempDirPath) {
        this.tempDirPath = tempDirPath;
    }

    @Getter
    private static String genFileDirPath;

    @Value("${custom.genFile.dirPath}")
    public void setGenFileDirPath(String genFileDirPath) {
        this.genFileDirPath = genFileDirPath;
    }

    @Getter
    private static String tossPaymentsWidgetSecretKey;

    @Value("${custom.tossPayments.widget.secretKey}")
    public void setTossPaymentsWidgetSecretKey(String tossPaymentsWidgetSecretKey) {
        this.tossPaymentsWidgetSecretKey = tossPaymentsWidgetSecretKey;
    }
}
