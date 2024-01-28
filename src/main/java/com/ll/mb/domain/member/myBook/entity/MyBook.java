package com.ll.mb.domain.member.myBook.entity;

import com.ll.mb.domain.book.entity.Book;
import com.ll.mb.domain.member.member.entity.Member;
import com.ll.mb.global.jpa.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import lombok.*;

@Entity
@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Setter
@Getter
@ToString(callSuper = true)
public class MyBook extends BaseEntity {
    @ManyToOne
    private Member owner; // 책 주인
    @ManyToOne
    private Book book; // 결제한 책
}
