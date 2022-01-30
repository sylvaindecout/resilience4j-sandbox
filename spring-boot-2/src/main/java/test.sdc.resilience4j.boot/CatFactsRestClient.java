package test.sdc.resilience4j.boot;

import io.netty.handler.timeout.ReadTimeoutException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.codec.CodecException;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import test.sdc.resilience4j.CatFactServiceAccessException;

import static org.springframework.web.util.UriComponentsBuilder.fromUriString;

public class CatFactsRestClient {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final WebClient client;

    CatFactsRestClient(@Qualifier("catFactsApiClient") WebClient client) {
        this.client = client;
    }

    public Mono<CatFactDto> getRandomCatFact() {
        return client.get()
                .uri(fromUriString("/facts/random")
                        .queryParam("animal_type", "cat")
                        .queryParam("amount", 1)
                        .buildAndExpand()
                        .toUriString())
                .retrieve()
                .bodyToMono(CatFactDto.class)
                .onErrorMap(ReadTimeoutException.class, ex -> mapTimeoutError())
                .onErrorMap(WebClientResponseException.class, CatFactsRestClient::mapUnhandledResponseStatus)
                .onErrorMap(CodecException.class, CatFactsRestClient::mapResponseFormatMappingError)
                .onErrorMap(WebClientRequestException.class, CatFactsRestClient::mapRequestError)
                .doOnError(thr -> log.error("Random cat fact generation failed", thr))
                .doOnSuccess(dto -> log.info("Received response: {}", dto));
    }

    public Flux<CatFactDto> getRandomCatFacts(int amount) {
        if (amount < 2) {
            throw new IllegalArgumentException("Invalid amount: " + amount);
        }
        return client.get()
                .uri(fromUriString("/facts/random")
                        .queryParam("animal_type", "cat")
                        .queryParam("amount", amount)
                        .buildAndExpand()
                        .toUriString())
                .retrieve()
                .bodyToFlux(CatFactDto.class)
                .onErrorMap(ReadTimeoutException.class, ex -> mapTimeoutError())
                .onErrorMap(WebClientResponseException.class, CatFactsRestClient::mapUnhandledResponseStatus)
                .onErrorMap(CodecException.class, CatFactsRestClient::mapResponseFormatMappingError)
                .onErrorMap(WebClientRequestException.class, CatFactsRestClient::mapRequestError)
                .doOnError(thr -> log.error("Random cat fact generation failed for amount " + amount, thr));
    }

    private static CatFactServiceAccessException mapTimeoutError() {
        return new CatFactServiceAccessException("Read timeout");
    }

    private static CatFactServiceAccessException mapResponseFormatMappingError(CodecException ex) {
        return new CatFactServiceAccessException("Unexpected response format - " + ex.getMessage());
    }

    private static CatFactServiceAccessException mapRequestError(WebClientRequestException ex) {
        return new CatFactServiceAccessException(ex.getMessage());
    }

    private static CatFactServiceAccessException mapUnhandledResponseStatus(WebClientResponseException ex) {
        return new CatFactServiceAccessException("Server responsed with status " + ex.getRawStatusCode());
    }
}
