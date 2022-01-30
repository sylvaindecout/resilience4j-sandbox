package test.sdc.resilience4j.boot;

import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import test.sdc.resilience4j.CatFactAccess;
import test.sdc.resilience4j.CatFactServiceAccessException;

import java.util.Optional;
import java.util.stream.Stream;

public class CatFactAccessAdapter implements CatFactAccess {

    private final CatFactsRestClient client;

    CatFactAccessAdapter(CatFactsRestClient client) {
        this.client = client;
    }

    @Override
    public Optional<String> getRandomCatFact() {
        return client.getRandomCatFact()
                .onErrorMap(CallNotPermittedException.class, CatFactAccessAdapter::mapOpenCircuitError)
                .map(CatFactDto::text)
                .blockOptional();
    }

    @Override
    public Stream<String> getRandomCatFacts(int amount) {
        return client.getRandomCatFacts(amount)
                .onErrorMap(CallNotPermittedException.class, CatFactAccessAdapter::mapOpenCircuitError)
                .map(CatFactDto::text)
                .toStream();
    }

    private static Throwable mapOpenCircuitError( CallNotPermittedException ex) {
        return new CatFactServiceAccessException("Calls to API are currently suspended");
    }
}
