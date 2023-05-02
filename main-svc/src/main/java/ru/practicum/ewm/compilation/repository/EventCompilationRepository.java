package ru.practicum.ewm.compilation.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class EventCompilationRepository {

    private final JdbcTemplate jdbcTemplate;

    public List<Long> getEventIdsByCompId(Long compId) {
        String sql = "SELECT event_id FROM events_compilations WHERE compilation_id = ?";

        return jdbcTemplate.queryForList(sql, compId)
                .stream()
                .map(row -> (Long) row.get("event_id"))
                .collect(Collectors.toList());
    }

    public void update(List<Long> eventIds, Long compId) {
        StringBuilder sql = new StringBuilder("DELETE FROM events_compilations WHERE compilation_id = ?; ");
        if (!eventIds.isEmpty()) {
            sql.append("MERGE INTO events_compilations (event_id, compilation_id) VALUES ");
            addValuesToMergeStatement(eventIds, compId, sql);
        }
        jdbcTemplate.update(sql.toString(), compId);
    }

    private void addValuesToMergeStatement(List<Long> eventIds, Long compId, StringBuilder sql) {
        for (int i = 0; i < eventIds.size(); i++) {
            sql.append("(").append(eventIds.get(i)).append(", ").append(compId).append(")");
            if (i != eventIds.size() - 1) {
                sql.append(", ");
            }
        }
    }
}
