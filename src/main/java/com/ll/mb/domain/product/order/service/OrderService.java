package com.ll.mb.domain.product.order.service;

import com.ll.mb.domain.cash.cash.entity.CashLog;
import com.ll.mb.domain.member.member.entity.Member;
import com.ll.mb.domain.member.member.service.MemberService;
import com.ll.mb.domain.product.cart.entity.CartItem;
import com.ll.mb.domain.product.cart.service.CartService;
import com.ll.mb.domain.product.order.entity.Order;
import com.ll.mb.domain.product.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderService {
    private final OrderRepository orderRepository;
    private final CartService cartService;
    private final MemberService memberService;

    @Transactional
    public Order createFromCart(Member buyer) {
        // 구매자의 장바구니 탐색
        List<CartItem> cartItems = cartService.findItemsByBuyer(buyer);

        Order order = Order.builder()
                .buyer(buyer)
                .build();

        // Order 주문 테이블에 상품 한개씩 추가하기 cartItem -> order.addItem(cartItem)
        cartItems.stream().forEach(order::addItem);

        orderRepository.save(order);

        // 장바구니 비우기
        cartItems.stream().forEach(cartService::delete);

        return order;
    }

    public void payByCashOnly(Order order) {
        Member buyer = order.getBuyer();
        long restCash = buyer.getRestCash();
        long payPrice = order.calcPayPrice();

        if (payPrice > restCash) {
            throw new RuntimeException("예치금이 부족합니다.");
        }

        memberService.addCash(buyer, payPrice * -1, CashLog.EventType.사용__예치금_주문결제, order);

        payDone(order);
    }

    // 주문 완료 처리
    private void payDone(Order order) {
        order.setPaymentDone();
    }
}
