package com.ll.mb.domain.product.order.entity;

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
public class OrderItem extends BaseEntity { // 주문 아이템, 한번의 주문으로 여러 상품 살때
    @ManyToOne
    private Order order;
    @ManyToOne
    private Product product;

    public long getPayPrice() {
        return product.getPrice();
    }
}
