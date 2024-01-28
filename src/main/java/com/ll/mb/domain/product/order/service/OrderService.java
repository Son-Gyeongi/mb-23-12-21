package com.ll.mb.domain.product.order.service;

import com.ll.mb.domain.member.member.entity.Member;
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
}
