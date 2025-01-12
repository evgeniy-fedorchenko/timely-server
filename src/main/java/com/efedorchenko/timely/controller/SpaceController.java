package com.efedorchenko.timely.controller;

import com.efedorchenko.timely.configuration.ApplicationProperties;
import com.efedorchenko.timely.logging.Level;
import com.efedorchenko.timely.logging.Log;
import com.efedorchenko.timely.model.GetMembersResponse;
import com.efedorchenko.timely.model.SpaceConnectResponse;
import com.efedorchenko.timely.service.SpaceService;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.UUID;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Log(Level.DEBUG)
@Validated
@AllArgsConstructor
@RestController
@RequestMapping(path = SpaceController.SPACES_ENDPOINT, produces = APPLICATION_JSON_VALUE)
public class SpaceController {

    static final String SPACES_ENDPOINT = ApplicationProperties.BASE_PATH + "/spaces";

    private final SpaceService spaceService;

    @GetMapping
    public GetMembersResponse getMembers(
            @AuthenticationPrincipal UUID userId,
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) @RequestParam(required = false) Instant since) {
        return spaceService.getMembers(userId, since);
    }

    @GetMapping(path = "/kick")
    public boolean kick(@AuthenticationPrincipal UUID userId, @RequestParam(required = false) UUID kickedUserId) {
        return kickedUserId == null
                ? spaceService.leaveSpace(userId)
                : spaceService.detachUser(kickedUserId);
    }

    @PatchMapping
    public SpaceConnectResponse connectToSpace(@AuthenticationPrincipal UUID userId, @NotBlank String spaceKey) {
        return spaceService.connectToSpace(userId, spaceKey);
    }
}
