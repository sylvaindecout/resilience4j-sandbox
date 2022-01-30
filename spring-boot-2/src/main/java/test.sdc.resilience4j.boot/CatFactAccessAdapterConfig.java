package test.sdc.resilience4j.boot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.reactive.function.client.WebClient;
import test.sdc.resilience4j.CatFactAccess;

@Profile("spring-boot-2")
@Configuration
@EnableConfigurationProperties(WebClientProperties.class)
public class CatFactAccessAdapterConfig {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Bean
    public WebClient catFactsApiClient(WebClient.Builder webClientBuilder,
                                       @Value("${cat-facts.api.base-url}") String baseUrl) {
        return webClientBuilder.baseUrl(baseUrl).build();
    }

    @Bean
    public CatFactsRestClient catFactsRestClient(WebClient client) {
        log.info("Loading client for mode==boot");
        return new CatFactsRestClient(client);
    }

    @Bean
    public WebClientPropertiesCustomizer webClientPropertiesCustomizer(WebClientProperties properties) {
        return new WebClientPropertiesCustomizer(properties);
    }

    @Bean
    public CatFactAccess catFactAccess(CatFactsRestClient client) {
        return new CatFactAccessAdapter(client);
    }

}
