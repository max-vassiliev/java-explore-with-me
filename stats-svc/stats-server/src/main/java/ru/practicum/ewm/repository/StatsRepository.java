package ru.practicum.ewm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.ewm.model.EndpointHit;
import ru.practicum.ewm.model.ViewStats;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface StatsRepository extends JpaRepository<EndpointHit, Long> {

    @Query("select new ru.practicum.ewm.model.ViewStats(h.app, h.uri, count(h.id)) " +
            "from EndpointHit h " +
            "where h.uri in :uris " +
            "and h.timestamp between :starts and :ends " +
            "group by h.app, h.uri " +
            "order by count(h.id) desc")
    List<ViewStats> getViewStats(@Param("uris") String[] uris,
                                 @Param("starts") LocalDateTime starts,
                                 @Param("ends") LocalDateTime ends);

    @Query("select new ru.practicum.ewm.model.ViewStats(h.app, h.uri, count(h.id)) " +
            "from EndpointHit h " +
            "where h.timestamp between :starts and :ends " +
            "group by h.app, h.uri " +
            "order by count(h.id) desc")
    List<ViewStats> getViewStatsWithoutUris(@Param("starts") LocalDateTime starts,
                                            @Param("ends") LocalDateTime ends);

    @Query("select new ru.practicum.ewm.model.ViewStats(h.app, h.uri, count(distinct h.ip)) " +
            "from EndpointHit h " +
            "where h.uri in :uris " +
            "and h.timestamp between :starts and :ends " +
            "group by h.app, h.uri " +
            "order by count(distinct h.ip) desc")
    List<ViewStats> getViewStatsWithUniqueIp(@Param("uris") String[] uris,
                                             @Param("starts") LocalDateTime starts,
                                             @Param("ends") LocalDateTime ends);

    @Query("select new ru.practicum.ewm.model.ViewStats(h.app, h.uri, count(distinct h.ip)) " +
            "from EndpointHit h " +
            "where h.timestamp between :starts and :ends " +
            "group by h.app, h.uri " +
            "order by count(distinct h.ip) desc")
    List<ViewStats> getViewStatsWithUniqueIpWithoutUris(@Param("starts") LocalDateTime starts,
                                                        @Param("ends") LocalDateTime ends);
}
