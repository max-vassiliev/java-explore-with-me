package ru.practicum.ewm.rating.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;
import ru.practicum.ewm.event.model.params.EventPublicSearchParams;
import ru.practicum.ewm.rating.model.utils.EventInitiatorRatingFull;
import ru.practicum.ewm.rating.model.utils.EventRating;
import ru.practicum.ewm.rating.model.utils.EventInitiatorRating;
import ru.practicum.ewm.rating.model.utils.InitiatorRating;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class EventRatingRepository {

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public List<EventRating> getEventRatings(List<Long> eventIds) {
        String sql = "SELECT event_id, " +
                "COUNT(CASE WHEN reaction = 'LIKE' THEN 1 END) AS likes, " +
                "COUNT(CASE WHEN reaction = 'DISLIKE' THEN 1 END) AS dislikes, " +
                "(CAST(COUNT(CASE WHEN reaction = 'LIKE' THEN 1 END) AS float)) / " +
                "NULLIF((COUNT(CASE WHEN reaction = 'LIKE' THEN 1 END) + " +
                "COUNT(CASE WHEN reaction = 'DISLIKE' THEN 1 END)), 0) * 5 AS rating " +
                "FROM ratings " +
                "WHERE event_id IN (:eventIds) " +
                "GROUP BY event_id";

        SqlParameterSource parameters = new MapSqlParameterSource("eventIds", eventIds);
        return namedParameterJdbcTemplate.query(sql, parameters, EventRatingRepository::makeEventRating);
    }

    public List<EventRating> getEventRatings(EventPublicSearchParams searchParams) {
        StringBuilder sqlBuilder = new StringBuilder("SELECT r.event_id, " +
                "COUNT(CASE WHEN r.reaction = 'LIKE' THEN 1 ELSE NULL END) AS likes, " +
                "COUNT(CASE WHEN r.reaction = 'DISLIKE' THEN 1 ELSE NULL END) AS dislikes, " +
                "(CAST(COUNT(CASE WHEN r.reaction = 'LIKE' THEN 1 ELSE NULL END) AS float)) / " +
                "NULLIF((COUNT(CASE WHEN r.reaction = 'LIKE' THEN 1 ELSE NULL END) + " +
                "COUNT(CASE WHEN r.reaction = 'DISLIKE' THEN 1 ELSE NULL END)), 0) * 5 AS rating " +
                "FROM ratings r " +
                "LEFT JOIN events e on e.id = r.event_id " +
                "LEFT JOIN categories c ON e.category_id = c.id " +
                "WHERE 1=1 ");

        MapSqlParameterSource sqlParams = new MapSqlParameterSource();
        addQueryParams(sqlBuilder, sqlParams, searchParams);

        sqlBuilder.append("GROUP BY r.event_id " +
                "ORDER BY rating DESC NULLS LAST " +
                "LIMIT :size " +
                "OFFSET :offset");
        sqlParams.addValue("size", searchParams.getSize())
                .addValue("offset", searchParams.getPage().getOffset());

        return namedParameterJdbcTemplate.query(sqlBuilder.toString(), sqlParams,
                EventRatingRepository::makeEventRating);
    }

    public List<EventInitiatorRating> getEventInitiatorRatings(EventPublicSearchParams searchParams) {
        StringBuilder sqlBuilder = new StringBuilder("SELECT e.id as event_id, " +
                "i.initiator_id as initiator_id, " +
                "i.rating as rating " +
                "FROM events e " +
                "JOIN (SELECT initiator_id, " +
                "(CAST(COUNT(CASE WHEN r.reaction = 'LIKE' THEN 1 ELSE NULL END) AS float)) / " +
                "NULLIF((COUNT(CASE WHEN r.reaction = 'LIKE' THEN 1 ELSE NULL END) + " +
                "COUNT(CASE WHEN r.reaction = 'DISLIKE' THEN 1 ELSE NULL END)), 0) * 5 AS rating " +
                "FROM events " +
                "LEFT JOIN ratings r ON events.id = r.event_id " +
                "GROUP BY initiator_id " +
                ") AS i ON e.initiator_id = i.initiator_id " +
                "LEFT JOIN categories c ON e.category_id = c.id " +
                "WHERE 1=1 ");

        MapSqlParameterSource sqlParams = new MapSqlParameterSource();
        addQueryParams(sqlBuilder, sqlParams, searchParams);

        sqlBuilder.append("ORDER BY i.rating DESC NULLS LAST " +
                "LIMIT :size " +
                "OFFSET :offset");
        sqlParams.addValue("size", searchParams.getSize())
                .addValue("offset", searchParams.getPage().getOffset());

        return namedParameterJdbcTemplate.query(sqlBuilder.toString(), sqlParams,
                EventRatingRepository::makeEventInitiatorRating);
    }

    public List<InitiatorRating> getInitiatorRatings(List<Long> initiatorIds) {
        String sql = "SELECT initiator_id, " +
                "(CAST(COUNT(CASE WHEN r.reaction = 'LIKE' THEN 1 END) AS float)) " +
                "/ NULLIF((COUNT(CASE WHEN r.reaction = 'LIKE' THEN 1 END) + " +
                "COUNT(CASE WHEN r.reaction = 'DISLIKE' THEN 1 END)), 0) * 5 AS rating " +
                "FROM events " +
                "LEFT JOIN ratings r ON events.id = r.event_id " +
                "WHERE initiator_id IN (:initiatorIds) " +
                "GROUP BY initiator_id";

        MapSqlParameterSource sqlParams = new MapSqlParameterSource("initiatorIds", initiatorIds);
        return namedParameterJdbcTemplate.query(sql, sqlParams,
                EventRatingRepository::makeInitiatorRating);
    }

    public Optional<EventInitiatorRatingFull> getFullRatingsByEventId(Long eventId) {
        String sql = "SELECT events.id AS event_id, " +
                "events.initiator_id AS initiator_id, " +
                "COUNT(ratings.reaction = 'LIKE' OR NULL) AS event_likes, " +
                "COUNT(ratings.reaction = 'DISLIKE' OR NULL) AS event_dislikes, " +
                "CAST(COUNT(ratings.reaction = 'LIKE' OR NULL) AS float) / " +
                "NULLIF(COUNT(ratings.reaction IN ('LIKE', 'DISLIKE') OR NULL), 0) * 5 AS event_rating, " +
                "(SELECT CAST(COUNT(r2.reaction = 'LIKE' OR NULL) AS float) / " +
                "NULLIF(COUNT(r2.reaction IN ('LIKE', 'DISLIKE') OR NULL), 0) * 5 " +
                "FROM ratings r2 " +
                "LEFT JOIN events e2 ON r2.event_id = e2.id " +
                "WHERE e2.initiator_id = events.initiator_id) AS initiator_rating " +
                "FROM events " +
                "LEFT JOIN ratings ON events.id = ratings.event_id " +
                "WHERE events.id = :eventId " +
                "GROUP BY events.id, events.initiator_id";

        MapSqlParameterSource sqlParams = new MapSqlParameterSource("eventId", eventId);
        List<EventInitiatorRatingFull> result = namedParameterJdbcTemplate.query(sql, sqlParams,
                EventRatingRepository::makeEventInitiatorRatingFull);
        return Optional.ofNullable(result.get(0));
    }

    // ----------------------
    // Вспомогательные методы
    // ----------------------

    private void addQueryParams(StringBuilder sqlBuilder,
                                MapSqlParameterSource sqlParams,
                                EventPublicSearchParams searchParams) {
        if (searchParams.hasText()) {
            sqlBuilder.append("AND (e.annotation ILIKE :text OR e.description ILIKE :text) ");
            sqlParams.addValue("text", "%" + searchParams.getText() + "%");
        }
        if (searchParams.hasCategories()) {
            sqlBuilder.append("AND e.category_id IN (:categoryIds) ");
            sqlParams.addValue("categoryIds", searchParams.getCategories());
        }
        if (searchParams.hasPaid()) {
            sqlBuilder.append("AND e.paid = :paid ");
            sqlParams.addValue("paid", searchParams.getPaid());
        }
        if (searchParams.isOnlyAvailable()) {
            sqlBuilder.append("AND (participant_limit = 0 OR e.confirmed_requests < participant_limit) ");
        }
        if (searchParams.hasRangeStart() && searchParams.hasRangeEnd()) {
            sqlBuilder.append("AND (e.created_on BETWEEN :rangeStart AND :rangeEnd) ");
            sqlParams.addValue("rangeStart", searchParams.getRangeStart());
            sqlParams.addValue("rangeEnd", searchParams.getRangeEnd());
        }
        if (searchParams.hasRangeStart()) {
            sqlBuilder.append("AND e.created_on >= :rangeStart ");
            sqlParams.addValue("rangeStart", searchParams.getRangeStart());
        }
        if (searchParams.hasRangeEnd()) {
            sqlBuilder.append("AND e.created_on < :rangeEnd ");
        }
    }

    static EventRating makeEventRating(ResultSet rs, int rowNum) throws SQLException {
        Long eventId = rs.getLong("event_id");
        Long likes = rs.getLong("likes");
        Long dislikes = rs.getLong("dislikes");
        Float rating = rs.getFloat("rating");
        return new EventRating(eventId, likes, dislikes, rating);
    }

    static EventInitiatorRating makeEventInitiatorRating(ResultSet rs, int rowNum) throws SQLException {
        Long eventId = rs.getLong("event_id");
        Long initiatorId = rs.getLong("initiator_id");
        Float rating = rs.getFloat("rating");
        return new EventInitiatorRating(eventId, initiatorId, rating);
    }

    static InitiatorRating makeInitiatorRating(ResultSet rs, int rowNum) throws SQLException {
        Long initiatorId = rs.getLong("initiator_id");
        Float rating = rs.getFloat("rating");
        return new InitiatorRating(initiatorId, rating);
    }

    static EventInitiatorRatingFull makeEventInitiatorRatingFull(ResultSet rs, int rowNum) throws SQLException {
        Long eventId = rs.getLong("event_id");
        Long initiatorId = rs.getLong("initiator_id");
        Long eventLikes = rs.getLong("event_likes");
        Long eventDislikes = rs.getLong("event_dislikes");
        Float eventRating = rs.getFloat("event_rating");
        Float initiatorRating = rs.getFloat("initiator_rating");
        return new EventInitiatorRatingFull(eventId, initiatorId,
                eventLikes, eventDislikes, eventRating, initiatorRating);
    }
}
