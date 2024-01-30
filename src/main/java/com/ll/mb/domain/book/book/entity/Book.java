package com.ll.mb.domain.book.book.entity;

import com.ll.mb.domain.member.member.entity.Member;
import com.ll.mb.domain.product.product.entity.Product;
import com.ll.mb.global.jpa.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import lombok.*;

@Entity
@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Setter
@Getter
@ToString(callSuper = true)
public class Book extends BaseEntity {
    @ManyToOne
    private Member author;
    @OneToOne // Book 하나에 상품 하나
    private Product product; // null이면 상품화 되지 않았다.
    private String title;
    private String body;
    private int price;
}
