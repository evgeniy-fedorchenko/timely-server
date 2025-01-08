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
@RequestMapping(path = DataController.DATA_ENDPOINT)
public class DataController {

    static final String DATA_ENDPOINT = ApplicationProperties.BASE_PATH + "/data";

    private final UserDataService<UserDataDto, DataRangeRequest> userDataService;

    @PostMapping(consumes = APPLICATION_JSON_VALUE)
    public UserDataDto addData(@AuthenticationPrincipal UUID userId, @RequestBody @Valid UserDataDto userDataDto) {
        return userDataDto.getToUserId() == null
                ? userDataService.addData(userId, userDataDto)
                : userDataService.addDataToOtherUser(userId, userDataDto);
    }

    @ResponseStatus(HttpStatus.ACCEPTED)
    @DeleteMapping(path = "/{dataType}")
    public void deleteData(@AuthenticationPrincipal UUID userId,
                           @PathVariable UserDataType dataType,
                           @RequestParam UUID targetUserId,
                           @RequestParam Long dataId) {
        userDataService.deleteData(userId, targetUserId, dataType, dataId);
    }

    // TODO 08.01.2025 19:53: Принимать даты в параметрах, userId сделать nullable и если что брать из principal
    @PostMapping(path = "/{dataType}", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public Collection<UserDataDto> getRange(@RequestBody @Valid DataRangeRequest dataRangeRequest,
                                            @PathVariable UserDataType dataType) {
        return userDataService.getRange(dataRangeRequest, dataType);
    }

    @ResponseStatus(HttpStatus.OK)
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

    @ResponseStatus(HttpStatus.ACCEPTED)
    @PatchMapping(consumes = APPLICATION_JSON_VALUE)
    public void editData(@AuthenticationPrincipal UUID userId, @RequestBody @Valid UserDataModifyDto newData) {
        userDataService.changeData(userId, newData);
    }
    // TODO 05.01.2025 00:07: ResponseEntity?
}
