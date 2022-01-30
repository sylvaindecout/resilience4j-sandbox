package test.sdc.resilience4j.reactor;

import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.client.RestTemplate;
import test.sdc.resilience4j.CatFactAccess;

@Profile("basic")
@Configuration
@EnableConfigurationProperties(CircuitBreakersConfig.class)
public class CatFactAccessAdapterConfig {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder.build();
    }

    @Bean
    public CatFactsRestClient catFactsRestClient(RestTemplate restTemplate,
                                                 @Value("${cat-facts.api.base-url}") String baseUrl,
                                                 CircuitBreakerRegistry circuitBreakerRegistry,
                                                 CircuitBreakersConfig circuitBreakersConfig) {
        log.info("Loading client for mode==basic");
        return new CatFactsRestClient(restTemplate, baseUrl, circuitBreakerRegistry, circuitBreakersConfig);
    }

    @Bean
    public CatFactAccess catFactAccess(CatFactsRestClient client) {
        return new CatFactAccessAdapter(client);
    }

}
