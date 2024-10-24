package com.efedorchenko.timely.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@RestController
@RequestMapping(
        path = DataController.DATA_ENDPOINT
)
public class DataController {

    public static final String DATA_ENDPOINT = "/data";

    @PostMapping(path = "/some")
    public Mono<Void> data() {
        System.err.println("inside");
        return Mono.empty();
    }
}
