package com.shop.shop.controller;

import com.shop.shop.dto.MemberFormDto;
import com.shop.shop.entity.Member;
import com.shop.shop.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/members")
@Controller
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;
    private final PasswordEncoder passwordEncoder;



    //회원가입
    @PostMapping(value = "/new")
    public String memberForm(@Valid MemberFormDto memberFormDto, BindingResult bindingResult, Model model){
        if(bindingResult.hasErrors()){
            return "member/memberForm";// 에러 있으면 다시 회원가입 폼으로
        }
        try {
            //Member 객체생성
            Member member = Member.createMember(memberFormDto, passwordEncoder);
            //데이터베이스 저장
            memberService.saveMember(member);
        }catch (IllegalStateException e){
            model.addAttribute("errorMessage", e.getMessage());
            return "member/memberForm";
        }
        return "redirect:/";
    }
/// //////////////////////////////////////////getMapping//////////////////////////////////////////////////////////////////////

    //회원가입 폼ㄱㄱ
    @GetMapping(value = "/new")
    public String memberForm(Model model){ //ui가 사용할 모델 -spring에서 제공
        model.addAttribute("memberFormDto", new MemberFormDto());
    return "member/memberForm";
}

    //로그인
    @GetMapping(value = "/login")
    public String loginMember(){
        return "member/memberLoginForm";
    }

    //로그인에러
    @GetMapping(value = "/login/error")
    public String loginError(Model model){
        model.addAttribute("loginErrorMsg", "아이디 또는 비밀번호를 확인해주세요.");
        return "member/memberLoginForm";
    }
}
