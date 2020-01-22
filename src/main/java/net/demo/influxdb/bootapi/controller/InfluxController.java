package net.demo.influxdb.bootapi.controller;

import net.demo.influxdb.bootapi.reqeust.DemoInfluxRequestBody;
import net.demo.influxdb.bootapi.response.CommonResponse;
import net.demo.influxdb.bootapi.service.InfluxService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
public class InfluxController {

    @Autowired
    private InfluxService influxService;


    @PostMapping(value = "/insert", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResponse insertRecord(@Valid @RequestBody DemoInfluxRequestBody requestBody) {

        // todo params checking

        return influxService.insert(requestBody);
    }

}
