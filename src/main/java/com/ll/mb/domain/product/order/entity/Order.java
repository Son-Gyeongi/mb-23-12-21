package com.ll.mb.domain.product.order.entity;

import com.ll.mb.domain.member.member.entity.Member;
import com.ll.mb.domain.product.cart.entity.CartItem;
import com.ll.mb.global.jpa.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Setter
@Getter
@ToString(callSuper = true)
@Table(name = "order_") // 테이블에 Order를 쓰면 안된다. SQL의 예약어이다. JPA에서 허용하지 않는다. 명시적으로 만들어주기
public class Order extends BaseEntity { // 주문 1개
    @ManyToOne
    private Member buyer;

    @Builder.Default
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> orderItems = new ArrayList<>();

    private boolean isPaid; // 결제 여부
    private boolean isCanceled; // 취소 여부
    private boolean isRefunded; // 환불 여부

    public void addItem(CartItem cartItem) {
        // CartItem로부터 OrderItem을 만들어서 Order에 넣는다.
        OrderItem orderItem = OrderItem.builder()
                .order(this)
                .product(cartItem.getProduct())
                .build();

        orderItems.add(orderItem);
    }
}
