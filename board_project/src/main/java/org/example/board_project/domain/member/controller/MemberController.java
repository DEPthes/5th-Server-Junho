package org.example.board_project.domain.member.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.board_project.domain.member.dto.MemberCreateRequest;
import org.example.board_project.domain.member.dto.MemberLoginRequest;
import org.example.board_project.domain.member.service.MemberService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class MemberController {

    private static final String LOGIN_MEMBER_ID = "loginMemberId";

    private final MemberService memberService;

    @GetMapping("/")
    public String home() {
        return "index";
    }

    @GetMapping("/members/new")
    public String createForm(Model model) {
        model.addAttribute("memberCreateRequest", new MemberCreateRequest());
        return "members/new";
    }

    @PostMapping("/members")
    public String create(
            @Valid @ModelAttribute MemberCreateRequest memberCreateRequest,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes
    ) {
        if (bindingResult.hasErrors()) {
            return "members/new";
        }

        try {
            memberService.join(memberCreateRequest);
        } catch (IllegalArgumentException exception) {
            bindingResult.reject("joinFailed", exception.getMessage());
            return "members/new";
        }

        redirectAttributes.addFlashAttribute("joined", true);
        return "redirect:/";
    }

    @GetMapping("/members/login")
    public String loginForm(Model model) {
        model.addAttribute("memberLoginRequest", new MemberLoginRequest());
        return "members/login";
    }

    @PostMapping("/members/login")
    public String login(
            @Valid @ModelAttribute MemberLoginRequest memberLoginRequest,
            BindingResult bindingResult,
            HttpServletRequest request,
            RedirectAttributes redirectAttributes
    ) {
        if (bindingResult.hasErrors()) {
            return "members/login";
        }

        try {
            Long memberId = memberService.login(memberLoginRequest);
            HttpSession session = request.getSession();
            session.setAttribute(LOGIN_MEMBER_ID, memberId);
        } catch (IllegalArgumentException exception) {
            bindingResult.reject("loginFailed", exception.getMessage());
            return "members/login";
        }

        redirectAttributes.addFlashAttribute("loggedIn", true);
        return "redirect:/main";
    }

    @PostMapping("/members/logout")
    public String logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);

        if (session != null) {
            session.invalidate();
        }

        return "redirect:/members/login";
    }
}
