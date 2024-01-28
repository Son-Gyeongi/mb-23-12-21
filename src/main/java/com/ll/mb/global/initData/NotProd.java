package com.ll.mb.global.initData;

import com.ll.mb.domain.book.book.service.BookService;
import com.ll.mb.domain.book.entity.Book;
import com.ll.mb.domain.cash.cash.entity.CashLog;
import com.ll.mb.domain.member.member.entity.Member;
import com.ll.mb.domain.member.member.service.MemberService;
import com.ll.mb.domain.product.cart.service.CartService;
import com.ll.mb.domain.product.order.entity.Order;
import com.ll.mb.domain.product.order.service.OrderService;
import com.ll.mb.domain.product.product.entity.Product;
import com.ll.mb.domain.product.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.transaction.annotation.Transactional;

@Configuration
@RequiredArgsConstructor
public class NotProd {
    @Autowired
    @Lazy
    private NotProd self;
    private final MemberService memberService;
    private final BookService bookService;
    private final ProductService productService;
    private final CartService cartService;
    private final OrderService orderService;

    @Bean
    ApplicationRunner initNotProd() {
        return args -> {
            self.work1();
            self.work2();
            // work1, work2 로 트랜잭션을 나눠나서 work1은 성공하고 work2는 실패한다.
        };
    }

    @Transactional
    public void work1() {
        // admin이 이미 있으면 아래 회원 생성이 만들어진 거라고 인식해서 또 실행되어도 만들어지지 않는다.
        if (memberService.findByUsername("admin").isPresent()) return;

        // 회원 생성
        Member memberAdmin = memberService.join("admin", "1234", "관리자").getData();
        Member memberUser1 = memberService.join("user1", "1234", "유저1").getData();
        Member memberUser2 = memberService.join("user2", "1234", "유저2").getData();
        Member memberUser3 = memberService.join("user3", "1234", "유저3").getData();

        // Book 생성
        Book book1 = bookService.createBook(memberUser1, "책 제목 1", "책 내용 1", 10_000);
        Book book2 = bookService.createBook(memberUser2, "책 제목 2", "책 내용 2", 20_000);
        Book book3 = bookService.createBook(memberUser2, "책 제목 3", "책 내용 3", 30_000);
        Book book4 = bookService.createBook(memberUser3, "책 제목 4", "책 내용 4", 40_000);
        Book book5 = bookService.createBook(memberUser3, "책 제목 5", "책 내용 5", 15_000);
        Book book6 = bookService.createBook(memberUser3, "책 제목 6", "책 내용 6", 20_000);

        // 상품화 (Book, Post 등 상품화 될 수 있다.)
        Product product1 = productService.createProduct(book3); // 책 3번이 상품화 된거다.
        Product product2 = productService.createProduct(book4);
        Product product3 = productService.createProduct(book5);
        Product product4 = productService.createProduct(book5); // 같은 상품을 두번 상품화 할 수 없다.

        // 장바구니 - book을 바로 장바구니에 담는게 아닌 상품을 담아야 한다.
        cartService.addItem(memberUser1, product1); // memberUser1이 product1을 장바구니에 담았다.
        cartService.addItem(memberUser1, product2);
        cartService.addItem(memberUser1, product3);

        cartService.addItem(memberUser2, product1); // memberUser2이 product1을 장바구니에 담았다.
        cartService.addItem(memberUser2, product2);
        cartService.addItem(memberUser2, product3);

        cartService.addItem(memberUser3, product1); // memberUser3이 product1을 장바구니에 담았다.
        cartService.addItem(memberUser3, product2);
        cartService.addItem(memberUser3, product3);

        // 캐시 사용에 대한 기록, 자세할수록 좋다.
        memberService.addCash(memberUser1, 150_000, CashLog.EventType.충전__무통장입금, memberUser1);
        memberService.addCash(memberUser1, -20_000, CashLog.EventType.출금__통장입금, memberUser1);

        // 주문 - 장바구니 아이템들을 기반으로 주문을 생성 / 주문을 하면 장바구니는 비워지고 주문 1개가 생긴다.
        // 캐시만으로 1번 주문 결제처리
        Order order1 = orderService.createFromCart(memberUser1);
        long order1PayPrice = order1.calcPayPrice();
        orderService.payByCashOnly(order1); // 85,000원

        // 2번 주문을 결제처리 후 환불처리
        // 회원 3번에 충전을 하고
        memberService.addCash(memberUser3,150_000, CashLog.EventType.충전__무통장입금, memberUser3);
        // 회원 3번이 주문을 한다.
        Order order2 = orderService.createFromCart(memberUser3);
        // 주문을 가지고 결제
        orderService.payByCashOnly(order2); // 85,000원
        // 환불
        orderService.refund(order2);

        /*// memberUser2가 장바구니에 있는 걸 다 주문한다.
        Order order3 = orderService.createFromCart(memberUser2);
        orderService.checkPayPrice(order3, 85_000); // 주문금액(85,000원), 주문과 금액이 일치하지 않으면 예외발생하는 함수*/

        // 단순히 checkPayPrice() 하는거 보다는 아래 처럼 해야 한다.
        memberService.addCash(memberUser2, 150_000, CashLog.EventType.충전__무통장입금,memberUser2);
        Order order3 = orderService.createFromCart(memberUser2);
        orderService.checkCanPay(order3, 55_000); // 토스페이먼츠로 계산하는 금액 55,000원

        // 토스 페이먼츠로 결제하는 기능 구현, 부족한 금액은 자동으로 예치금에서 차감하도록
        // 85_000원 결제 중에 토스페이먼츠로 55_000원으로 결제하겠다. 나머지 차액은 예치금에서 결제하면 된다.
        orderService.payByTossPayments(order3, 55_000);
    }

    @Transactional
    public void work2() {
        // 똑같은 상품을 장바구니에 담을 경우 에러 확인용 테스트
//        Member memberUser1 = memberService.findByUsername("user1").get();
//        Product product1 = productService.findById(1L).get();
//
//        // 장바구니 담기에서 "400-1: 이미 구매한 상품입니다." 오류가 나야한다.
//        cartService.addItem(memberUser1, product1);
    }
}

