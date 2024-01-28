package com.ll.mb.domain.cash.cash.entity;

import com.ll.mb.domain.member.member.entity.Member;
import com.ll.mb.global.jpa.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.ManyToOne;
import lombok.*;

@Entity
@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Setter
@Getter
@ToString(callSuper = true)
public class CashLog extends BaseEntity {
    @Enumerated(EnumType.STRING)
    private EventType eventType;
    private String relTypeCode; // rel(related) 관련된
    private Long relId;
    @ManyToOne
    private Member member;
    private long price;

    public enum EventType { // EventType는 그냥 String이라고 생각하면 된다. : String의 경우 자유도가 높은데 자유도를 제한할 때 사용
        충전__무통장입금,
        충전__토스페이먼츠,
        출금__통장입금,
        사용__토스페이먼츠_주문결제,
        사용__예치금_주문결제,
        환불__예치금_주문결제,
        작가정산__예치금;
    }
}
