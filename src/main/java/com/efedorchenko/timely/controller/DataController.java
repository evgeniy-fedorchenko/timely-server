package com.efedorchenko.timely.controller;

import com.efedorchenko.timely.entity.UserData;
import com.efedorchenko.timely.service.UserDataService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping(path = DataController.DATA_ENDPOINT,
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE
)
public class DataController {

    public static final String DATA_ENDPOINT = "/data";

    private final UserDataService<UserData> userDataService;

//    @Log
//    @PostMapping
//    public Mono<ResponseEntity<Void>> addData(@AuthenticationPrincipal UUID userId,
//                                              @RequestBody @Valid UserData userData) {
//        return userDataService.addData(userId, userData)
//                .thenReturn(ResponseEntity.accepted().build());
//    }
//
//    @Log
//    @DeleteMapping
//    public Mono<ResponseEntity<Void>> removeData(@AuthenticationPrincipal UUID userId,
//                                                 @RequestBody @Valid UserData userData) {
//        return userDataService.removeData(userId, userData)
//                .thenReturn(ResponseEntity.accepted().build());
//    }
//
//    @Log
//    @PostMapping(path = "/range")
//    public Flux<?> getRange(@AuthenticationPrincipal UUID userId,
//                            @RequestParam UserDataType type,
//                            @RequestBody @Valid DataRangeRequest dataRangeRequest) {
//        return userDataService.getRange(userId, dataRangeRequest, type)
//                .map(r -> ResponseEntity.ok().body(r));
//    }
//
//    @Log
//    @PostMapping(path = "/range-all")
//    public Mono<ResponseEntity<EventsAndFines>> getRangeAllTypes(
//            @AuthenticationPrincipal UUID userId,
//            @RequestBody @Valid DataRangeRequest dataRangeRequest) {
//        return userDataService.getRange(userId, dataRangeRequest)
//                .map(r -> ResponseEntity.ok().body(r));
//    }
}
