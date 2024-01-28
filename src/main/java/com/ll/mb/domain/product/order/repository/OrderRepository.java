package com.ll.mb.domain.product.order.repository;

import com.ll.mb.domain.product.order.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {

}
