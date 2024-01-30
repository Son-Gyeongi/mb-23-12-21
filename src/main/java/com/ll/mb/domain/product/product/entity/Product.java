package com.ll.mb.domain.product.product.entity;

import com.ll.mb.domain.book.book.entity.Book;
import com.ll.mb.domain.member.member.entity.Member;
import com.ll.mb.global.app.AppConfig;
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
public class Product extends BaseEntity { // 상품화
    @ManyToOne
    private Member maker;
    private String relTypeCode; // 상품 종류 book || post ...
    private long relId; // 1 || 2 || 3 ...
    private String name;
    private int price;

    // Product에서 relId를 기준으로 Book 엔티티를 얻을 수 있다.
    // 이 메서드는 관련된 도서 엔티티를 반환하는데, 필요한 경우에만 실제로 데이터를 가져오도록 Lazy Loading을 사용
    public Book getBook() {
        // getBook() 메서드는 상품의 관련 도서(Book) 엔티티를 반환
        // AppConfig.getEntityManager()를 통해 EntityManager를 가져옵니다. 이는 JPA를 사용하여 데이터베이스와 상호 작용할 수 있는 인스턴스
        // .getReference(Book.class, relId)를 호출하여 Book 엔티티의 참조를 가져온다. 여기서 relId는 상품(Product) 엔티티의 관련 도서의 ID를 나타낸다.
        // 이 메서드는 데이터베이스에서 실제로 엔티티를 로드하지 않고, 엔티티의 프록시(Proxy)를 반환 
        // 프록시는 실제 엔티티에 대한 참조를 갖고 있지만, 실제로 엔티티 데이터를 로드하지 않고, 필요한 경우에만 로드 됨
        return AppConfig.getEntityManager().getReference(Book.class, relId);
    }
}
