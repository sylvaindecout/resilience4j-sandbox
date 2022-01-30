package test.sdc.resilience4j.reactor;

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
        try {
            return Optional.ofNullable(client.getRandomCatFact())
                    .map(CatFactDto::text);
        } catch (CallNotPermittedException ex) {
            throw openCircuitError();
        }
    }

    @Override
    public Stream<String> getRandomCatFacts(int amount) {
        try {
            return client.getRandomCatFacts(amount)
                    .map(CatFactDto::text);
        } catch (CallNotPermittedException ex) {
            throw openCircuitError();
        }
    }

    private static CatFactServiceAccessException openCircuitError() {
        return new CatFactServiceAccessException("Calls to API are currently suspended");
    }
}
