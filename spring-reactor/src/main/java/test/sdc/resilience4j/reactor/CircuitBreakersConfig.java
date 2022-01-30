package test.sdc.resilience4j.reactor;

import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import test.sdc.resilience4j.CatFactServiceAccessException;

import java.util.Map;
import java.util.Optional;

import static io.github.resilience4j.circuitbreaker.CircuitBreakerConfig.SlidingWindowType.TIME_BASED;

@ConstructorBinding
@ConfigurationProperties(prefix = "resilience4j.circuitbreaker")
public record CircuitBreakersConfig(Map<String, CircuitBreakerProperties> instances) {

    public Optional<CircuitBreakerProperties> find(String name) {
        return Optional.ofNullable(instances.get(name));
    }

    public record CircuitBreakerProperties(
            Integer slidingWindowSize,
            Integer failureRateThreshold,
            Integer permittedNumberOfCallsInHalfOpenState,
            Integer minimumNumberOfCalls
    ) {

        private static final int DEFAULT_SLIDING_WINDOW_SIZE = 100;
        private static final int DEFAULT_FAILURE_RATE_THRESHOLD = 50;
        private static final int DEFAULT_MIN_NB_CALLS = 100;
        private static final int DEFAULT_PERMITTED_NB_CALLS_IN_HALF_OPEN_STATE = 10;

        public CircuitBreakerConfig toCircuitBreakerConfig() {
            return CircuitBreakerConfig.custom()
                    .slidingWindowSize(slidingWindowSize == null
                            ? DEFAULT_SLIDING_WINDOW_SIZE
                            : slidingWindowSize)
                    .failureRateThreshold(failureRateThreshold == null
                            ? DEFAULT_FAILURE_RATE_THRESHOLD
                            : failureRateThreshold)
                    .permittedNumberOfCallsInHalfOpenState(permittedNumberOfCallsInHalfOpenState == null
                            ? DEFAULT_PERMITTED_NB_CALLS_IN_HALF_OPEN_STATE
                            : permittedNumberOfCallsInHalfOpenState)
                    .minimumNumberOfCalls(minimumNumberOfCalls == null
                            ? DEFAULT_MIN_NB_CALLS
                            : minimumNumberOfCalls)
                    .slidingWindowType(TIME_BASED)
                    .recordExceptions(CatFactServiceAccessException.class)
                    .build();
        }
    }

}
