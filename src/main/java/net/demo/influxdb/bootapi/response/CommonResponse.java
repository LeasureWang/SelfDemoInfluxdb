package net.demo.influxdb.bootapi.response;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

import java.io.Serializable;


@Data
@NoArgsConstructor
public class CommonResponse implements Serializable {

    private static final long serialVersionUID = -149199074184302523L;

    private final int CODE = HttpStatus.OK.value();
    private final String MESSAGE = HttpStatus.OK.getReasonPhrase();

    public static CommonResponse setSuccess() {
        return new CommonResponse();
    }

    public static void main(String[] args) {
        System.out.println(CommonResponse.setSuccess());
    }
}
