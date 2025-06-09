package com.shop.shop.service;

import com.shop.shop.entity.Member;
import com.shop.shop.repository.MemberRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor//final @Nonnull 변수에 붙으면 자동주입(autowired)을 해준다.
public class MemberService {
    private final MemberRepository memberRepository; //자동주입됨

    public Member saveMember(Member member){
        validateDuplicateMember(member);
        return memberRepository.save(member);
    }

    private void validateDuplicateMember(Member member) {
        Member findMember = memberRepository.findByEmail(member.getEmail());
        if(findMember !=null){
            throw new IllegalStateException("이미 가입된 회원입니다.");
        }
    }

}
