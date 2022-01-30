package test.sdc.resilience4j;

import java.util.Optional;
import java.util.stream.Stream;

public interface CatFactAccess {

    Optional<String> getRandomCatFact();

    Stream<String> getRandomCatFacts(int amount);

}
