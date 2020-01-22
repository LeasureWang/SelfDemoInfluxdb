package net.demo.influxdb.bootapi.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SimpleObjectEntity implements Serializable {

    private static final long serialVersionUID = -3992027231729682813L;

    private long id;
    private String location;
    private int refNumber;
    private boolean status;
}
