package com.ll.mb.domain.product.order.service;

import com.ll.mb.domain.cash.cash.entity.CashLog;
import com.ll.mb.domain.member.member.entity.Member;
import com.ll.mb.domain.member.member.service.MemberService;
import com.ll.mb.domain.product.cart.entity.CartItem;
import com.ll.mb.domain.product.cart.service.CartService;
import com.ll.mb.domain.product.order.entity.Order;
import com.ll.mb.domain.product.order.repository.OrderRepository;
import com.ll.mb.global.exception.GlobalException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

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

    @Transactional // 서비스 안에서 Select 외에 다른거를 한다면 붙여주기
    public void payByCashOnly(Order order) {
        Member buyer = order.getBuyer();
        long restCash = buyer.getRestCash();
        long payPrice = order.calcPayPrice();

        if (payPrice > restCash) {
            // RuntimeException을 해도 되지만
            //조금 더 체계적으로 관리하려면 GlobalException 클래스를 만들어서 사용하는 게 좋다.
            throw new GlobalException("400-1", "예치금이 부족합니다.");
        }

        memberService.addCash(buyer, payPrice * -1, CashLog.EventType.사용__예치금_주문결제, order);

        payDone(order);
    }

    @Transactional
    public void payByTossPayments(Order order, long pgPayPrice) {
        // 이제 이거를 토스페이먼츠에 붙이면 된다.

        Member buyer = order.getBuyer();
        long restCash = buyer.getRestCash();
        long payPrice = order.calcPayPrice();

        // 토스페이먼츠 결제 후에 나머지 결제할 금액을 캐시에서 지불한다.
        long userRestCash = payPrice - pgPayPrice;

        // 먼저 토스페이먼츠에서 선금액 충전
        memberService.addCash(buyer, pgPayPrice, CashLog.EventType.충전__토스페이먼츠, order);
        // 사용
        memberService.addCash(buyer, pgPayPrice * -1, CashLog.EventType.사용__토스페이먼츠_주문결제, order);

        // 토스페이먼츠만으로 결제가 안될 때 추가 결제를 예치금으로 한다.
        if (userRestCash > 0) {
            if (userRestCash > restCash) {
                throw new RuntimeException("예치금이 부족합니다.");
                // @Transactional이 있어서 위에 토스페이먼츠 충전, 사용한 부분은 에러가 나면 롤백이 된다.
                // 하나의 트랜잭션으로 묶인 작업은 에러가 나면 다 롤백이 된다.
            }

            memberService.addCash(buyer, userRestCash * -1, CashLog.EventType.사용__예치금_주문결제, order);
        }

        payDone(order);
    }

    // 주문 완료 처리
    private void payDone(Order order) {
        order.setPaymentDone();
    }

    // 환불
    @Transactional
    public void refund(Order order) {
        long payPrice = order.calcPayPrice();

        // 돈 환불
        memberService.addCash(order.getBuyer(), payPrice, CashLog.EventType.환불__예치금_주문결제, order);

        // 환불 완료 - 주문 취소 날짜, 환불 날짜
        order.setCancelDone();
        order.setRefundDone();
    }

    // 주문자의 결제캐시랑 클라이언트측에서 준 결제캐시랑 같은지 확인
    public void checkCanPay(String orderCode, long pgPayPrice) {
        // 주문 찾기
        Order order = findByCode(orderCode).orElse(null);

        if (order == null)
            throw new GlobalException("400-1", "존재하지 않는 주문입니다.");

        checkCanPay(order, pgPayPrice);
    }

    // 돈을 지불할 수 있는지 체크하겠다.
    public void checkCanPay(Order order, long pgPayPrice) {
        if (!canPay(order, pgPayPrice))
            throw new GlobalException("400-2", "PG결제 금액 혹은 예치금이 부족하여 결제할 수 없습니다.");
    }

    public boolean canPay(Order order, long pgPayPrice) { // pgPayPrice : 토스페이먼츠로 결제되는 금액. pg(paygate)
        // canPay 메서드에 order.isPayable 를 추가해서 서버사이드에서도 올바르지 않은 결제요청을 막는다.
        if (!order.isPayable()) return false; // 결제일, 취소일이 null이 아닌 경우 이미 주문을 했다.

        long restCash = order.getBuyer().getRestCash();

        // 내가 가지고 있는 캐시(restCash)와 토스페이먼츠를 합했을 때(pgPayPrice)
        // order.calcPayPrice 이 주문의 결제 가격보다 restCash + pgPayPrice 이게 크면 돈을 지불할 수 있다.
        return order.calcPayPrice() <= restCash + pgPayPrice;
    }

    public Optional<Order> findById(long id) {
        return orderRepository.findById(id);
    }

    // 주문 상세페이지는 구매자만 볼 수 있습니다.
    public boolean actorCanSee(Member actor, Order order) {
        return order.getBuyer().equals(actor);
    }

    public Optional<Order> findByCode(String code) { // code : 2024-01-29__4
        long id = Long.parseLong(code.split("__", 2)[1]);

        return findById(id);
    }

    public void payDone(String code) {
        Order order = findByCode(code).orElse(null);

        if (order == null)
            throw new GlobalException("400-1", "존재하지 않는 주문입니다.");

        payDone(order);
    }
}
