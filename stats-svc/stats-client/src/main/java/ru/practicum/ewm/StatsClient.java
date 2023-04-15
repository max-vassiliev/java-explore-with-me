package ru.practicum.ewm;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.practicum.ewm.dto.EndpointHitDto;
import ru.practicum.ewm.dto.ViewStatsDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static ru.practicum.ewm.common.StatsConstants.EWM_DATE_TIME_FORMAT;

@Service
public class StatsClient {

    private final WebClient webClient;

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(EWM_DATE_TIME_FORMAT);

    public StatsClient(@Value("${stats-server.url}") String baseUrl) {
        this.webClient = WebClient.builder()
                .baseUrl(baseUrl)
                .build();
    }

    public Mono<EndpointHitDto> postEndpointHit(EndpointHitDto dto) {
        return webClient.post()
                .uri("/hit")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(dto)
                .retrieve()
                .bodyToMono(EndpointHitDto.class);
    }

    public Flux<ViewStatsDto> getViewStats(LocalDateTime start, LocalDateTime end, String[] uris, Boolean unique) {
        String uri = buildGetViewStatsUri(start, end, uris, unique);

        return webClient.get()
                .uri(uri)
                .retrieve()
                .bodyToFlux(ViewStatsDto.class);
    }

    private String buildGetViewStatsUri(LocalDateTime start, LocalDateTime end, String[] uris, Boolean unique) {
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
