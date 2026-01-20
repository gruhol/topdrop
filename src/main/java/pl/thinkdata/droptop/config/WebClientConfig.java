package pl.thinkdata.droptop.config;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;
import reactor.netty.resources.ConnectionProvider;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Configuration
public class WebClientConfig {

    @Bean
    public WebClient webClient() {
        ConnectionProvider connectionProvider = ConnectionProvider.builder("custom-pool")
                .maxConnections(200)
                .pendingAcquireMaxCount(500)
                .pendingAcquireTimeout(Duration.ofSeconds(60))
                .maxIdleTime(Duration.ofSeconds(30))
                .maxLifeTime(Duration.ofMinutes(5))
                .evictInBackground(Duration.ofSeconds(120))
                .build();

        HttpClient httpClient = HttpClient.create(connectionProvider)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 30_000)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .responseTimeout(Duration.ofMinutes(3))
                .doOnConnected(conn -> conn
                        .addHandlerLast(new ReadTimeoutHandler(180, TimeUnit.SECONDS))
                        .addHandlerLast(new WriteTimeoutHandler(180, TimeUnit.SECONDS))
                );

        ExchangeStrategies strategies = ExchangeStrategies.builder()
                .codecs(configurer ->
                        configurer.defaultCodecs().maxInMemorySize(20 * 1024 * 1024)
                )
                .build();

        return WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .exchangeStrategies(strategies)
                .build();
    }
}