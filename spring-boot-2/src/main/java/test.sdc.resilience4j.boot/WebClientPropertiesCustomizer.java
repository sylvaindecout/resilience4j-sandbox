package test.sdc.resilience4j.boot;

import io.netty.handler.timeout.ReadTimeoutHandler;
import org.springframework.boot.web.reactive.function.client.WebClientCustomizer;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import static io.netty.channel.ChannelOption.CONNECT_TIMEOUT_MILLIS;
import static java.util.concurrent.TimeUnit.MILLISECONDS;

public class WebClientPropertiesCustomizer implements WebClientCustomizer {

    private final WebClientProperties properties;

    WebClientPropertiesCustomizer(WebClientProperties properties) {
        this.properties = properties;
    }

    @Override
    public void customize(WebClient.Builder webClientBuilder) {
        final var httpClient = HttpClient.create()
                .option(CONNECT_TIMEOUT_MILLIS, properties.connectionTimeOutMillis())
                .doOnConnected(connection -> connection.addHandlerLast(new ReadTimeoutHandler(properties.readTimeOutMillis(), MILLISECONDS)));
        webClientBuilder.clientConnector(new ReactorClientHttpConnector(httpClient));
    }

}
