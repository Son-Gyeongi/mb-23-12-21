package com.ll.mb.domain.product.cart.entity;

import com.ll.mb.domain.member.member.entity.Member;
import com.ll.mb.domain.product.product.entity.Product;
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
public class CartItem extends BaseEntity { // 장바구니
    @ManyToOne
    private Member buyer; // 장바구니의 주인
    @ManyToOne // 다른 사용자들이 같은 상품을 장바구니에 담을 수 있다.
    private Product product; // 상품
}
