package com.ll.mb.domain.product.cart.entity;

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
public class CartItem extends BaseEntity { // 장바구니
    @ManyToOne
    private Member buyer; // 장바구니의 주인
    @OneToOne // 장바구니 하나에 상품 하나 - 똑같은 책 2개 사는 거 허용 안됨
    private Product product; // 상품
}
