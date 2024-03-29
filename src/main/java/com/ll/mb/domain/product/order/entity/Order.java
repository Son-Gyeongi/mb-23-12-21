package com.ll.mb.domain.product.order.entity;

import com.ll.mb.domain.member.member.entity.Member;
import com.ll.mb.domain.product.cart.entity.CartItem;
import com.ll.mb.global.app.AppConfig;
import com.ll.mb.global.exception.GlobalException;
import com.ll.mb.global.jpa.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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

    @Builder.Default // 빌더(Builder) 패턴을 사용할 때 필드의 기본값을 설정하는 데 사용
    // 여러 개의 OrderItem 엔티티가 하나의 Order 엔티티에 속한다
    // mappedBy = "order" OrderItem 엔티티 클래스에서 order라는 이름의 필드를 사용하여 Order 엔티티와 연관
    // CascadeType.ALL은 Order 엔티티가 저장, 수정, 삭제될 때 연관된 모든 OrderItem 엔티티에도 동일한 작업을 적용한다는 것을 의미
    // orphanRemoval = true: 이 옵션은 부모 엔티티와 연관이 끊어진 자식 엔티티를 자동으로 삭제하는 옵션
    // 즉, Order 엔티티에서 제거된 OrderItem 엔티티는 데이터베이스에서도 삭제
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> orderItems = new ArrayList<>(); // Order 엔티티에는 OrderItem 엔티티를 여러 개 가질 수 있음

    private LocalDateTime payDate; // 결제일, null이면 결제하지 않은거다.
    private LocalDateTime cancelDate; // 취소일
    private LocalDateTime refundDate; // 환불일

    public void addItem(CartItem cartItem) {
        // 주문 상품 추가할 때 상품을 구매한 적이 있는지 확인
        if (buyer.has(cartItem.getProduct()))
            throw new GlobalException("400-1", "이미 구매한 상품입니다.");

        // CartItem로부터 OrderItem을 만들어서 Order에 넣는다.
        OrderItem orderItem = OrderItem.builder()
                .order(this)
                .product(cartItem.getProduct())
                .build();

        orderItems.add(orderItem);
    }

    public long calcPayPrice() {
        return orderItems.stream()
                .mapToLong(OrderItem::getPayPrice)
                .sum();
    }

    public void setPaymentDone() {
        payDate = LocalDateTime.now();

        orderItems.stream().forEach(OrderItem::setPaymentDone);
    }

    public void setCancelDone() {
        cancelDate = LocalDateTime.now();

        orderItems.stream().forEach(OrderItem::setCancelDone);
    }

    public void setRefundDone() {
        refundDate = LocalDateTime.now();

        orderItems.stream().forEach(OrderItem::setRefundDone);
    }

    // 상품 주문 요약 - 책 제목 3 외 2 건
    public String getName() {
        String name = orderItems.get(0).getProduct().getName();

        if (orderItems.size() > 1) {
            name += " 외 %d건".formatted(orderItems.size() - 1);
        }

        return name;
    }

    // 주문 번호
    public String getCode() {
        // yyyy-MM-dd 형식의 DateTimeFormatter 생성
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        // LocalDateTime 객체를 문자열로 변환
        // 테스트 모드, 개발 모드일 때 주문코드가 주문시도 마다 유니크 하게 생성
        return getCreateDate().format(formatter)
                + (AppConfig.isNotProd() ? "-test-" + UUID.randomUUID().toString() : "")
                + "__" + getId();
    }

    // 결제 가능한 상태 확인
    public boolean isPayable() {
        if (payDate != null) return false; // 결제일이 null이 아닐경우 false
        if (cancelDate != null) return false; // 취소일이 null이 아닐경우 false

        return true; // 결제가 가능하다.
    }
}
