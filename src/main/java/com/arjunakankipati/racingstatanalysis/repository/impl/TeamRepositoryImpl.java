package com.arjunakankipati.racingstatanalysis.repository.impl;

import com.arjunakankipati.racingstatanalysis.jooq.Tables;
import com.arjunakankipati.racingstatanalysis.model.Team;
import com.arjunakankipati.racingstatanalysis.repository.TeamRepository;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

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
        super(dsl, Tables.TEAMS, Tables.TEAMS.ID);
    }

    @Override
    protected Team mapToEntity(Record record) {
        if (record == null) {
            return null;
        }

        var teamRec = record.into(Tables.TEAMS);
        return new Team(
                teamRec.getId(),
                teamRec.getName(),
                teamRec.getDescription()
        );
    }

    @Override
    protected Team insert(Team team) {
        Record record = dsl.insertInto(table)
                .columns(
                        Tables.TEAMS.NAME,
                        Tables.TEAMS.DESCRIPTION
                )
                .values(
                        team.getName(),
                        team.getDescription()
                )
                .returning()
                .fetchOne();
        return mapToEntity(record);
    }

    @Override
    protected void update(Team team) {
        dsl.update(table)
                .set(Tables.TEAMS.NAME, team.getName())
                .set(Tables.TEAMS.DESCRIPTION, team.getDescription())
                .where(idField.eq(team.getId()))
                .execute();
    }

    @Override
    public Optional<Team> findByName(String name) {
        Record record = dsl.select()
                .from(table)
                .where(Tables.TEAMS.NAME.eq(name))
                .fetchOne();

        return Optional.ofNullable(record)
                .map(this::mapToEntity);
    }

    @Override
    public List<Team> findByNameContaining(String nameContains) {
        return dsl.select()
                .from(table)
                .where(Tables.TEAMS.NAME.like("%" + nameContains + "%"))
                .fetch()
                .map(this::mapToEntity);
    }
}