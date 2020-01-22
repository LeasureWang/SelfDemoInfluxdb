package net.demo.influxdb.bootapi.entity;

import lombok.Data;
import lombok.experimental.Accessors;
import org.influxdb.annotation.Column;
import org.influxdb.annotation.Measurement;

import java.io.Serializable;

@Data
@Accessors(chain = true)
@Measurement(name = "entityMs")
public class DemoInfluxEntity implements Serializable {

    private static final long serialVersionUID = -5300101655229272427L;

    @Column(name = "indexId", tag = true)
    private String id;
    @Column(name = "refNumber", tag = true)
    private String refNumber;
    @Column(name = "location")
    private String location;
    @Column(name = "status")
    private String status;
    @Column(name = "message")
    private String message;
}
