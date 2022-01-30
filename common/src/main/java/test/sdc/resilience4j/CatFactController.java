package test.sdc.resilience4j;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.HttpStatus.BAD_GATEWAY;
import static org.springframework.http.MediaType.TEXT_PLAIN_VALUE;

@RestController
public class CatFactController {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final CatFactAccess access;

    CatFactController(CatFactAccess access) {
        this.access = access;
    }

    @GetMapping(path = "random", produces = TEXT_PLAIN_VALUE)
    public String getRandomCatFact() {
        log.info("GET /random");
        return access.getRandomCatFact().orElse(null);
    }

    @ExceptionHandler(CatFactServiceAccessException.class)
    @ResponseStatus(BAD_GATEWAY)
    public ResponseEntity<String> handleCatFactServiceAccessException(CatFactServiceAccessException ex) {
        log.error("Call failed: {}", ex.getMessage());
        return ResponseEntity
                .status(BAD_GATEWAY)
                .body(ex.getMessage());
    }

}
