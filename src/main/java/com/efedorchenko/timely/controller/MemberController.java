package com.efedorchenko.timely.controller;

import com.efedorchenko.timely.configuration.ApplicationProperties;
import com.efedorchenko.timely.logging.Level;
import com.efedorchenko.timely.logging.Log;
import com.efedorchenko.timely.model.SpaceMember;
import com.efedorchenko.timely.service.MemberService;
import lombok.AllArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Log(Level.DEBUG)
@Validated
@AllArgsConstructor
@RestController
@RequestMapping(path = MemberController.MEMBERS_ENDPOINT, produces = APPLICATION_JSON_VALUE)
public class MemberController {

    static final String MEMBERS_ENDPOINT = ApplicationProperties.BASE_PATH + "/members";

    private final MemberService memberService;
    @GetMapping
    public List<SpaceMember> getMembers(@AuthenticationPrincipal UUID userId) {
        return memberService.getMembers(userId);
    }
}
