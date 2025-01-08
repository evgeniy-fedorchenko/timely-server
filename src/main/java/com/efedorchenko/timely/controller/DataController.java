package com.efedorchenko.timely.controller;

import com.efedorchenko.timely.configuration.ApplicationProperties;
import com.efedorchenko.timely.logging.Level;
import com.efedorchenko.timely.logging.Log;
import com.efedorchenko.timely.model.data.DataRangeRequest;
import com.efedorchenko.timely.model.data.UserDataDto;
import com.efedorchenko.timely.model.data.UserDataModifyDto;
import com.efedorchenko.timely.model.data.UserDataType;
import com.efedorchenko.timely.service.UserDataService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.Collection;
import java.util.UUID;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Log(Level.DEBUG)
@Validated
@AllArgsConstructor
@RestController
@ResponseStatus(HttpStatus.ACCEPTED)
@RequestMapping(path = DataController.DATA_ENDPOINT)
public class DataController {

    static final String DATA_ENDPOINT = ApplicationProperties.BASE_PATH + "/data";

    private final UserDataService<UserDataDto, DataRangeRequest> userDataService;

    @ResponseStatus(HttpStatus.OK)
    @PostMapping(consumes = APPLICATION_JSON_VALUE)
    public UserDataDto addData(@AuthenticationPrincipal UUID userId, @RequestBody @Valid UserDataDto userDataDto) {
        if (userDataDto.getToUserId() == null) {
            return userDataService.addData(userId, userDataDto);
        } else {
            return userDataService.addDataToOtherUser(userId, userDataDto);
        }
    }

    @DeleteMapping(path = "/{clearableUserId}/{dataType}/{dataId}")
    public void deleteData(@AuthenticationPrincipal UUID userId,
                           @PathVariable UUID clearableUserId,
                           @PathVariable UserDataType dataType,
                           @PathVariable Long dataId) {
        userDataService.deleteData(userId, clearableUserId, dataType, dataId);
    }

    @ResponseStatus(HttpStatus.OK)
    @PostMapping(path = "/{dataType}", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public Collection<UserDataDto> getRange(@RequestBody @Valid DataRangeRequest dataRangeRequest,
                                            @PathVariable UserDataType dataType) {
        return userDataService.getRange(dataRangeRequest, dataType);
    }

    @GetMapping(path = "/{dataType}", produces = APPLICATION_JSON_VALUE)
    public Collection<UserDataDto> getUpdates(
            @AuthenticationPrincipal UUID userId,
            @PathVariable UserDataType dataType,
            @RequestParam(required = false) UUID targetUserId,
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) @RequestParam Instant since) {
        return targetUserId == null
                ? userDataService.getUpdates(userId, dataType, since)
                : userDataService.getUpdates(targetUserId, dataType, since);
    }

    @PatchMapping(consumes = APPLICATION_JSON_VALUE)
    public void editData(@AuthenticationPrincipal UUID userId, @RequestBody @Valid UserDataModifyDto newData) {
        userDataService.changeData(userId, newData);
    }
    // TODO 05.01.2025 00:07: ResponseEntity?
}
