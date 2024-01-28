package com.ll.mb.domain.cash.cash.repository;

import com.ll.mb.domain.cash.cash.entity.CashLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CashLogRepository extends JpaRepository<CashLog, Long> {
}
