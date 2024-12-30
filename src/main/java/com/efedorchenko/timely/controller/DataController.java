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
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

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

    @PostMapping(consumes = APPLICATION_JSON_VALUE)
    public void addData(@AuthenticationPrincipal UUID userId, @RequestBody @Valid UserDataDto userDataDto) {
        userDataService.addData(userId, userDataDto);
    }

    @DeleteMapping(path = "/{dataType}/{dataId}")
    public void removeData(@AuthenticationPrincipal UUID userId,
                           @PathVariable UserDataType dataType,
                           @PathVariable Long dataId) {
        userDataService.deleteData(userId, dataType, dataId);
    }

    @ResponseStatus(HttpStatus.OK)
    @PostMapping(path = "/{dataType}", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public CompletableFuture<Collection<UserDataDto>> getRange(@AuthenticationPrincipal UUID userId,
                                                               @RequestBody @Valid DataRangeRequest dataRangeRequest,
                                                               @PathVariable UserDataType dataType) {
        return userDataService.getRange(userId, dataRangeRequest, dataType);
    }

    @PatchMapping(consumes = APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyAuthority('BOSS', 'CREATOR', 'MODERATOR')")
    public void editData(@AuthenticationPrincipal UUID userId, @RequestBody @Valid UserDataModifyDto newData) {
        userDataService.changeData(userId, newData);
    }
}

