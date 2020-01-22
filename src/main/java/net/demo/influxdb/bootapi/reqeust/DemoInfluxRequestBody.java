package net.demo.influxdb.bootapi.reqeust;

import com.alibaba.fastjson.JSONObject;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DemoInfluxRequestBody implements Serializable {

    private static final long serialVersionUID = -1251743182770125921L;

    private long id;
    private String location;
    private int refNumber;
    private boolean status;
    private JSONObject message;
}
