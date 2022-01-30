package test.sdc.resilience4j.reactor;

import test.sdc.resilience4j.CatFactAccess;

import java.util.Optional;
import java.util.stream.Stream;

public class CatFactAccessAdapter implements CatFactAccess {

    private final CatFactsRestClient client;

    CatFactAccessAdapter(CatFactsRestClient client) {
        this.client = client;
    }

    @Override
    public Optional<String> getRandomCatFact() {
        return Optional.ofNullable(client.getRandomCatFact())
                .map(CatFactDto::text);
    }

    @Override
    public Stream<String> getRandomCatFacts(int amount) {
        return client.getRandomCatFacts(amount)
                .map(CatFactDto::text);
    }

}
