package com.ll.mb.domain.member.member.entity;

import com.ll.mb.domain.book.entity.Book;
import com.ll.mb.domain.member.myBook.entity.MyBook;
import com.ll.mb.domain.product.product.entity.Product;
import com.ll.mb.global.jpa.BaseEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Transient;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Entity
@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Setter
@Getter
@ToString(callSuper = true)
public class Member extends BaseEntity {
    private String username;
    private String password;
    private String nickname;
    private long restCash; // 회원 캐시

    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<MyBook> myBooks = new ArrayList<>();

    public void addMyBook(Book book) {
        MyBook myBook = MyBook.builder()
                .owner(this)
                .book(book)
                .build();

        myBooks.add(myBook);
    }

    public void removeMyBook(Book book) {
        myBooks.removeIf(myBook -> myBook.getBook().equals(book));
    }

    public boolean hasBook(Book book) {
        return myBooks
                .stream()
                .anyMatch(myBook -> myBook.getBook().equals(book));
    }

    public boolean has(Product product) {
        return switch (product.getRelTypeCode()) {
            case "book" -> hasBook(product.getBook());
            default -> false;
        };
    }

    // @Transient 어노테이션은 해당 필드 또는 메서드가 영속성 컨텍스트에 저장되지 않음을 나타냅니다.
    // 여기서는 JPA의 영속성 컨텍스트에 저장되는 속성이 아님을 나타내기 위해 사용됩니다.
    @Transient
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // getAuthorities() 메서드는 현재 사용자의 권한 목록을 반환합니다.

        // 권한 목록을 담을 ArrayList를 생성합니다.
        List<GrantedAuthority> authorities = new ArrayList<>();

        // 기본적으로 사용자는 "ROLE_MEMBER" 권한을 가지고 있습니다.
        authorities.add(new SimpleGrantedAuthority("ROLE_MEMBER"));

        if (List.of("system", "admin").contains(username)) {
            // username이 "system" 또는 "admin"인 경우에는 추가 권한인 "ROLE_ADMIN"을 부여합니다.
            authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
        }

        // 권한 목록을 반환합니다.
        return authorities;
    }

    // isAdmin() 메서드는 현재 사용자가 관리자인지 여부를 확인합니다.
    public boolean isAdmin() {
        return getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        // getAuthorities() 메서드를 통해 사용자의 권한 목록을 가져온 후,
        // "ROLE_ADMIN" 권한을 가진 권한이 존재하는지 확인하여 관리자 여부를 반환합니다.
    }
}
