package ru.practicum.ewm;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.practicum.ewm.dto.EndpointHitDto;
import ru.practicum.ewm.dto.ViewStatsDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static ru.practicum.ewm.common.StatsConstants.EWM_DATE_TIME_FORMAT;

@Service
@Slf4j
public class StatsClient {

    private final WebClient webClient;

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(EWM_DATE_TIME_FORMAT);

    public StatsClient(@Value("${stats-server.url}") String baseUrl) {
        this.webClient = WebClient.builder()
                .baseUrl(baseUrl)
                .build();
    }

    public ResponseEntity<Object> postEndpointHit(EndpointHitDto dto) {
        return webClient.post()
                .uri("/hit")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(dto))
                .exchangeToMono(response -> {
                    if (response.statusCode().equals(HttpStatus.CREATED)) {
                        return response.bodyToMono(Object.class)
                                .map(body -> ResponseEntity.status(HttpStatus.CREATED).body(body));
                    } else {
                        return response.createException()
                                .flatMap(Mono::error);
                    }
                })
                .block();
    }

    public Flux<ViewStatsDto> getViewStats(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {
        String uri = buildGetViewStatsUri(start, end, uris, unique);

        return webClient.get()
                .uri(uri)
                .exchangeToFlux(response -> {
                    if (response.statusCode().is2xxSuccessful()) {
                        return response.bodyToFlux(ViewStatsDto.class);
                    } else {
                        throw new StatsClientException(String.format("Error retrieving stats for parameters: " +
                                "start=%s, end=%s, uris=%s, unique=%s", start, end, uris, unique));
                    }
                });
    }

    private String buildGetViewStatsUri(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {
        StringBuilder uriBuilder = new StringBuilder("/stats");
        uriBuilder.append("?start=").append(start.format(formatter))
                .append("&end=").append(end.format(formatter));
        if (uris != null) {
            for (String uri : uris) {
                uriBuilder.append("&uris=").append(uri);
            }
        }
        if (unique != null) {
            uriBuilder.append("&unique=").append(unique);
        }
        return uriBuilder.toString();
    }
}
