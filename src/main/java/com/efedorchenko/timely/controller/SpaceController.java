package com.efedorchenko.timely.controller;

import com.efedorchenko.timely.configuration.properties.ApplicationProperties;
import com.efedorchenko.timely.data.model.AcceptMember;
import com.efedorchenko.timely.data.model.MemberOpResult;
import com.efedorchenko.timely.data.model.MembersResponse;
import com.efedorchenko.timely.data.model.Since;
import com.efedorchenko.timely.data.model.SpaceConnectResponse;
import com.efedorchenko.timely.middleware.logging.Level;
import com.efedorchenko.timely.middleware.logging.Log;
import com.efedorchenko.timely.service.SpaceService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Log(Level.DEBUG)
@Validated
@AllArgsConstructor
@RestController
@RequestMapping(path = SpaceController.SPACES_ENDPOINT)
public class SpaceController {

    static final String SPACES_ENDPOINT = ApplicationProperties.BASE_PATH + "/spaces";

    private final SpaceService spaceService;

    @GetMapping(produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<MembersResponse> getMembers(
            @AuthenticationPrincipal UUID userId,
            @Since @RequestParam(required = false) Instant since,
            @RequestParam(required = false, defaultValue = "false") boolean withJoinRequests) {
        return ResponseEntity.ok(spaceService.getMembers(userId, since, withJoinRequests));
    }

//    also leave space
    @PatchMapping(path = "/kick")
    public ResponseEntity<Boolean> kick(@AuthenticationPrincipal UUID userId,
                                        @RequestParam(required = false) UUID targetUserId) {
        Boolean isKicked = Optional.ofNullable(targetUserId)
                .map(kicked -> spaceService.detachUser(userId, kicked))
                .orElseGet(() -> spaceService.leaveSpace(userId));
        return ResponseEntity.ok(isKicked);
    }

    @PatchMapping(produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<SpaceConnectResponse> requestConnectToSpace(
            @AuthenticationPrincipal UUID userId,
            @RequestParam(name = "key") @NotBlank String spaceKey) {
        return ResponseEntity.accepted().body(spaceService.requestConnectToSpace(userId, spaceKey));
    }

    @PostMapping(consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity<MemberOpResult> acceptMember(@AuthenticationPrincipal UUID userId,
                                                       @RequestBody @Valid AcceptMember acceptMember) {
        return ResponseEntity.ok(spaceService.acceptMember(userId, acceptMember));
    }
}
