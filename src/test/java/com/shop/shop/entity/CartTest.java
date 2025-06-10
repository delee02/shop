package com.shop.shop.entity;

import com.shop.shop.dto.MemberFormDto;
import com.shop.shop.repository.CartRepository;
import com.shop.shop.repository.MemberRepository;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@TestPropertySource(locations = "classpath:application-test.properties")
class CartTest {
    @Autowired
    CartRepository cartRepository;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @PersistenceContext
    EntityManager em;

    public Member createMember(){
        MemberFormDto memberFormDto = new MemberFormDto();
        memberFormDto.setEmail("test@email.com");
        memberFormDto.setName("홍길동");
        memberFormDto.setPassword("1234");
        memberFormDto.setAddress("마포구");
        return Member.createMember(memberFormDto,passwordEncoder);
    }

    @Test
    @DisplayName("장바구니 회원 엔티티 매핑 조회 테스트")
    public void findCartAndMemberTest(){
        Member member = createMember();
        memberRepository.save(member);

        Cart cart = new Cart();
        cart.setMember(member);
        cartRepository.save(cart);

        em.flush();//밀어넣음 entityManager
        em.clear();
        //cartRepository를 통해 cartid를 넣어서 객체추출 단 객체가 없으면 exception
        Cart savedCart = cartRepository.findById(cart.getId()).orElseThrow(EntityExistsException::new);

        //DB에 저장된 member에 id와 위에서 객체 생성 된 member id를 비교해서 같으면 성공 아님 실퓨ㅐ
        assertEquals(savedCart.getMember().getId(), member.getId());
    }
}