package com.efedorchenko.timely.controller;

import com.efedorchenko.timely.configuration.properties.ApplicationProperties;
import com.efedorchenko.timely.data.model.Since;
import com.efedorchenko.timely.data.model.UserDataDto;
import com.efedorchenko.timely.data.model.UserDataModifyDto;
import com.efedorchenko.timely.data.model.UserDataType;
import com.efedorchenko.timely.middleware.logging.Level;
import com.efedorchenko.timely.middleware.logging.Log;
import com.efedorchenko.timely.service.UserDataService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.time.YearMonth;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Log(Level.DEBUG)
@Validated
@AllArgsConstructor
@RestController
@RequestMapping(path = DataController.DATA_ENDPOINT)
public class DataController {

    static final String DATA_ENDPOINT = ApplicationProperties.BASE_PATH + "/data";

    private final UserDataService<UserDataDto> userDataService;

    @PostMapping(produces = APPLICATION_JSON_VALUE, consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity<UserDataDto> addData(@AuthenticationPrincipal UUID userId,
                                               @RequestBody @Valid UserDataDto userDataDto) {
        UserDataDto responseDto = Optional.ofNullable(userDataDto.getOwner())
                .map(toUserId -> userDataService.addDataToOtherUser(userId, userDataDto))
                .orElseGet(() -> userDataService.addData(userId, userDataDto));
        return ResponseEntity.ok(responseDto);
    }

    @ResponseStatus(HttpStatus.ACCEPTED)
    @DeleteMapping(path = "/{type}")
    public ResponseEntity<Void> deleteData(@AuthenticationPrincipal UUID userId,
                                           @PathVariable UserDataType type,
                                           @RequestParam UUID targetUserId,
                                           @RequestParam Long dataId) {
        userDataService.deleteData(userId, targetUserId, type, dataId);
        return ResponseEntity.accepted().build();
    }

    @GetMapping(path = "{type}/range", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Collection<UserDataDto>> getRange(@AuthenticationPrincipal UUID userId,
                                                            @RequestParam(required = false) UUID requestedUserId,
                                                            @RequestParam YearMonth start,
                                                            @RequestParam YearMonth end,
                                                            @PathVariable UserDataType type) {
        UUID targetUserId = Optional.ofNullable(requestedUserId).orElse(userId);
        return ResponseEntity.ok(userDataService.getRange(targetUserId, start, end, type));
    }

    @GetMapping(path = "/{type}/updates", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Collection<UserDataDto>> getUpdates(@AuthenticationPrincipal UUID userId,
                                                              @RequestParam(required = false) UUID targetUserId,
                                                              @Since @RequestParam(required = false) Instant since,
                                                              @PathVariable UserDataType type) {
        List<UserDataDto> responseDto = Optional.ofNullable(targetUserId)
                .map(t -> userDataService.getUpdates(t, type, since))
                .orElseGet(() -> userDataService.getUpdates(userId, type, since));
        return ResponseEntity.ok(responseDto);
    }

    @ResponseStatus(HttpStatus.ACCEPTED)
    @PatchMapping(consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> editData(@AuthenticationPrincipal UUID userId,
                                         @RequestBody @Valid UserDataModifyDto newData) {
        userDataService.changeData(userId, newData);
        return ResponseEntity.accepted().build();
    }
}
