package test.sdc.resilience4j.reactor;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import test.sdc.resilience4j.CatFactServiceAccessException;

import java.util.stream.Stream;

import static java.util.Arrays.stream;
import static org.springframework.web.util.UriComponentsBuilder.fromUriString;

public class CatFactsRestClient {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final RestTemplate restTemplate;
    private final String baseUrl;
    private final CircuitBreakerRegistry circuitBreakerRegistry;
    private final CircuitBreakersConfig circuitBreakerConfig;

    CatFactsRestClient(RestTemplate restTemplate,
                       String baseUrl,
                       CircuitBreakerRegistry circuitBreakerRegistry,
                       CircuitBreakersConfig circuitBreakerConfig) {
        this.restTemplate = restTemplate;
        this.baseUrl = baseUrl;
        this.circuitBreakerRegistry = circuitBreakerRegistry;
        this.circuitBreakerConfig = circuitBreakerConfig;
    }

    public CatFactDto getRandomCatFact() {
        var circuitBreaker = circuitBreaker("random-cat-fact");
        return circuitBreaker.executeSupplier(() -> {
            var uri = fromUriString(baseUrl)
                    .path("/facts/random")
                    .queryParam("animal_type", "cat")
                    .queryParam("amount", 1)
                    .buildAndExpand()
                    .toUriString();
            try {
                var response = restTemplate.getForEntity(uri, CatFactDto.class);
                if (response.getStatusCode().isError()) {
                    throw new CatFactServiceAccessException("Server responded with status " + response.getStatusCode());
                }
                var result = response.getBody();
                log.info("Received response: {}", result);
                return result;
            } catch (HttpClientErrorException ex) {
                throw new CatFactServiceAccessException("Server responded with status " + ex.getStatusCode());
            }
        });
    }

    public Stream<CatFactDto> getRandomCatFacts(int amount) {
        if (amount < 2) {
            throw new IllegalArgumentException("Invalid amount: " + amount);
        }
        var uri = fromUriString(baseUrl)
                .path("/facts/random")
                .queryParam("animal_type", "cat")
                .queryParam("amount", amount)
                .buildAndExpand()
                .toUriString();
        try {
            var response = restTemplate.getForEntity(uri, CatFactDto[].class);
            if (response.getStatusCode().isError()) {
                throw new CatFactServiceAccessException("Server responded with status " + response.getStatusCode());
            }
            var result = response.getBody() == null
                    ? Stream.<CatFactDto>empty()
                    : stream(response.getBody());
            log.info("Received response: {}", result);
            return result;
        } catch (HttpClientErrorException ex) {
            throw new CatFactServiceAccessException("Server responded with status " + ex.getStatusCode());
        }
    }

    private CircuitBreaker circuitBreaker(String name) {
        return circuitBreakerRegistry.circuitBreaker(name, circuitBreakerConfig.find(name)
                .map(CircuitBreakersConfig.CircuitBreakerProperties::toCircuitBreakerConfig)
                .orElseGet(CircuitBreakerConfig::ofDefaults));
    }

}
