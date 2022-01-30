package test.sdc.resilience4j.boot;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

@ConstructorBinding
@ConfigurationProperties(prefix = "cat-facts.api")
public record WebClientProperties(
        Integer connectionTimeOutMillis,
        Integer readTimeOutMillis
) {
}
