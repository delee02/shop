package com.shop.shop.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name="cart")
@Getter
@Setter
@ToString
public class Cart {
    @Id
    @Column(name="cart_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="member_id") //매핑할 외리키를 지정한다. 외래키 이름설정
    private Member member;

    public static Cart createCart(Member member){//바로 메모리 올라가서 멤버 매칭 싹함
        Cart cart = new Cart();
        cart.setMember(member);
        return cart;
    }
}
