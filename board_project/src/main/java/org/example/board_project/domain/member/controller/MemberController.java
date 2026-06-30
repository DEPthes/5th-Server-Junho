package org.example.board_project.domain.member.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.board_project.domain.member.dto.MemberCreateRequest;
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

    private final MemberService memberService;

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
        return "redirect:/members/new";
    }
}
