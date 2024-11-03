package com.efedorchenko.timely.controller;

import com.efedorchenko.timely.entity.UserData;
import com.efedorchenko.timely.model.DataRangeRequest;
import com.efedorchenko.timely.model.EventsAndFines;
import com.efedorchenko.timely.model.UserDataType;
import com.efedorchenko.timely.service.UserDataService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping(path = DataController.DATA_ENDPOINT,
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE
)
public class DataController {

    public static final String DATA_ENDPOINT = "/data";

    private final UserDataService<UserData> userDataService;

    @PostMapping
    public Mono<Void> addData(@AuthenticationPrincipal UUID userId, @RequestBody @Valid UserData userData) {
        return userDataService.addData(userId, userData);
    }

    @DeleteMapping
    public Mono<Void> removeData(@AuthenticationPrincipal UUID userId, @RequestBody @Valid UserData userData) {
        return userDataService.removeData(userId, userData);
    }

    @PostMapping(path = "/range")
    public Flux<?> getRange(@AuthenticationPrincipal UUID userId,
                            @RequestParam UserDataType type,
                            @RequestBody @Valid DataRangeRequest dataRangeRequest) {
        return userDataService.getRange(userId, dataRangeRequest, type);
    }

    @PostMapping(path = "/range-all")
    public Mono<EventsAndFines> getRangeAllTypes(@AuthenticationPrincipal UUID userId,
                                                 @RequestBody @Valid DataRangeRequest dataRangeRequest) {
        return userDataService.getRange(userId, dataRangeRequest);
    }
}
