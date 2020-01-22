package net.demo.influxdb.bootapi.controller;

import com.alibaba.fastjson.JSONObject;
import net.demo.influxdb.bootapi.reqeust.DemoInfluxRequestBody;
import net.demo.influxdb.bootapi.response.CommonResponse;
import net.demo.influxdb.bootapi.service.InfluxService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
public class InfluxController {

    @Autowired
    private InfluxService influxService;

    @PostMapping(value = "/insert", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResponse insert(@Valid @RequestBody DemoInfluxRequestBody requestBody,
                                 BindingResult result) {

        // todo params checking

        return influxService.insert(requestBody);
    }


    @PostMapping(value = "/insertBatchList", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResponse insertBatchList(@Valid @RequestBody List<DemoInfluxRequestBody> request,
                                          BindingResult result) {

        // todo params checking

        return influxService.insertBatchList(request);

    }

    @GetMapping(value = "/query/{time}", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<JSONObject> query(@PathVariable long time) {

        // todo params checking

        return influxService.query(time);
    }
}
