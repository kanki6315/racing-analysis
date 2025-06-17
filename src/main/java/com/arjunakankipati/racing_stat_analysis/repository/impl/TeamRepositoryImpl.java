package com.arjunakankipati.racing_stat_analysis.repository.impl;

import com.arjunakankipati.racing_stat_analysis.model.Team;
import com.arjunakankipati.racing_stat_analysis.repository.TeamRepository;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static org.jooq.impl.DSL.field;

/**
 * Implementation of the TeamRepository interface using JOOQ.
 * Extends BaseRepositoryImpl to inherit common CRUD operations.
 */
@Repository
public class TeamRepositoryImpl extends BaseRepositoryImpl<Team, Long> implements TeamRepository {

    /**
     * Constructor with DSLContext dependency injection.
     *
     * @param dsl the JOOQ DSL context
     */
    @Autowired
    public TeamRepositoryImpl(DSLContext dsl) {
        super(dsl, "teams", "id", Long.class);
    }

    @Override
    protected Team mapToEntity(Record record) {
        if (record == null) {
            return null;
        }

        return new Team(
                record.get(field("id", Long.class)),
                record.get(field("name", String.class)),
                record.get(field("description", String.class))
        );
    }

    @Override
    protected Team insert(Team team) {
        Record record = dsl.insertInto(table)
                .columns(
                        field("name"),
                        field("description")
                )
                .values(
                        team.getName(),
                        team.getDescription()
                )
                .returningResult(
                        field("id"),
                        field("name"),
                        field("description")
                )
                .fetchOne();

        return mapToEntity(record);
    }

    @Override
    protected void update(Team team) {
        dsl.update(table)
                .set(field("name"), team.getName())
                .set(field("description"), team.getDescription())
                .where(idField.eq(team.getId()))
                .execute();
    }

    @Override
    public Optional<Team> findByName(String name) {
        Record record = dsl.select()
                .from(table)
                .where(field("name").eq(name))
                .fetchOne();

        return Optional.ofNullable(record)
                .map(this::mapToEntity);
    }

    @Override
    public List<Team> findByNameContaining(String nameContains) {
        return dsl.select()
                .from(table)
                .where(field("name").like("%" + nameContains + "%"))
                .fetch()
                .map(this::mapToEntity);
    }
}