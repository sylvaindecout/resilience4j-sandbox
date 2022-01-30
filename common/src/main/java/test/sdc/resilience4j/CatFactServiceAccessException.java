package test.sdc.resilience4j;

import org.springframework.web.bind.annotation.ResponseStatus;

import static org.springframework.http.HttpStatus.BAD_GATEWAY;

@ResponseStatus(BAD_GATEWAY)
public class CatFactServiceAccessException extends RuntimeException {
    public CatFactServiceAccessException(String message) {
        super(message);
    }
}
