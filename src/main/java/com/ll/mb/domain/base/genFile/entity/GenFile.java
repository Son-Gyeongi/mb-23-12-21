package com.ll.mb.domain.base.genFile.entity;

import com.ll.mb.global.app.AppConfig;
import com.ll.mb.global.jpa.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.*;

import static lombok.AccessLevel.PROTECTED;

@Entity
@Builder
@AllArgsConstructor(access = PROTECTED)
@NoArgsConstructor(access = PROTECTED)
@Setter
@Getter
@ToString(callSuper = true)
@Table(
        // Unique인덱스로 걸겠다. 이런걸 복합키라고 한다.
        //이 5개의 조합은 같은게 있어서는 안된다. 조합이 유니크, DB 검색속도 빠르게 하기 위해서
        uniqueConstraints = @UniqueConstraint(
                columnNames = {
                        "relId", "relTypeCode", "typeCode", "type2Code", "fileNo"
                }
        ),
        // 일반 인덱스
        //인덱스이름이 프로그램 전체에서 유니크해야해서 앞에 접두어로 GenFile을 붙였다.
        indexes = {
                // 특정 그룹의 데이터들을 불러올 때
                @Index(name = "GenFile_idx2", columnList = "relTypeCode, typeCode, type2Code")
        }
)
public class GenFile extends BaseEntity {
    // relTypeCode, relId 로만 봤을 때는 범용적인 테이블이다.
    private String relTypeCode;
    private long relId;
    private String typeCode; // 파일 용도
    private String type2Code; // 파일 용도
    private String fileExtTypeCode; // 파일 코드 종류들
    private String fileExtType2Code; // 파일 코드 종류들
    private long fileSize; // 파일 용량
    private long fileNo;
    private String fileExt; // 파일 확장자
    private String fileDir; // 파일 저장되는 경로
    private String originFileName; // 원래 파일명

    public String getFileName() {
        return getId() + "." + getFileExt();
    }

    public String getUrl() {
        return "/gen/" + getFileDir() + "/" + getFileName();
    }

    public String getDownloadUrl() {
        return "/domain/genFile/download/" + getId();
    }

    public String getFilePath() {
        return AppConfig.getGenFileDirPath() + "/" + getFileDir() + "/" + getFileName();
    }
}
