package com.ll.mb.domain.book.book.repository;

import com.ll.mb.domain.book.book.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRepository extends JpaRepository<Book, Long> {
}
