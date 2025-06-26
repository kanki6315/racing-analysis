package com.arjunakankipati.racingstatanalysis.repository.impl;

import com.arjunakankipati.racingstatanalysis.dto.CarDTO;
import com.arjunakankipati.racingstatanalysis.dto.CarModelDTO;
import com.arjunakankipati.racingstatanalysis.dto.DriverDTO;
import com.arjunakankipati.racingstatanalysis.dto.TeamDTO;
import com.arjunakankipati.racingstatanalysis.jooq.Tables;
import com.arjunakankipati.racingstatanalysis.model.Team;
import com.arjunakankipati.racingstatanalysis.repository.TeamRepository;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;
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
    public Map<Long, TeamDTO> findTeamsWithCarsAndDriversByEventId(Long eventId) {
        // Map to store unique teams
        Map<Long, TeamDTO> teamMap = new HashMap<>();
        Map<Long, CarDTO> carMap = new HashMap<>();

        // Fetch all teams, cars, and drivers for the event in a single query
        Result<Record> records = dsl.select()
                .from(Tables.TEAMS)
                .join(Tables.CAR_ENTRIES).on(Tables.CAR_ENTRIES.TEAM_ID.eq(Tables.TEAMS.ID))
                .join(Tables.SESSIONS).on(Tables.CAR_ENTRIES.SESSION_ID.eq(Tables.SESSIONS.ID))
                .join(Tables.CAR_MODELS).on(Tables.CAR_MODELS.ID.eq(Tables.CAR_ENTRIES.CAR_MODEL_ID))
                .join(Tables.CAR_DRIVERS).on(Tables.CAR_DRIVERS.CAR_ID.eq(Tables.CAR_ENTRIES.ID))
                .join(Tables.DRIVERS).on(Tables.DRIVERS.ID.eq(Tables.CAR_DRIVERS.DRIVER_ID))
                .where(Tables.SESSIONS.EVENT_ID.eq(eventId))
                .fetch();

        // Process the records
        for (Record record : records) {
            // Extract team data
            Long teamId = record.get(Tables.TEAMS.ID);
            String teamName = record.get(Tables.TEAMS.NAME);
            String teamDescription = record.get(Tables.TEAMS.DESCRIPTION);

            // Create or get the team DTO
            TeamDTO teamDTO = teamMap.computeIfAbsent(teamId,
                    id -> new TeamDTO(teamId, teamName, teamDescription));

            // Extract car data
            Long carId = record.get(Tables.CAR_ENTRIES.ID);
            String carNumber = record.get(Tables.CAR_ENTRIES.NUMBER);
            String carModelName = record.get(Tables.CAR_MODELS.NAME);
            String carTireSupplier = record.get(Tables.CAR_ENTRIES.TIRE_SUPPLIER);
            Long carClassId = record.get(Tables.CAR_ENTRIES.CLASS_ID);
            Long carModelId = record.get(Tables.CAR_MODELS.ID);
            Long carManufacturerId = record.get(Tables.CAR_MODELS.MANUFACTURER_ID);

            // Create or get the car DTO
            CarDTO carDTO = carMap.computeIfAbsent(carId,
                    id -> {
                        // Create CarModelDTO
                        CarModelDTO carModelDTO = new CarModelDTO(
                                carModelId,
                                carManufacturerId,
                                carModelName,
                                carModelName, // For now, use name as fullName
                                null, // yearModel not available
                                null  // description not available
                        );
                        
                        CarDTO car = new CarDTO(
                                carId,
                                carNumber,
                                carModelDTO,
                                carTireSupplier,
                                carClassId,
                                teamId
                        );
                        teamDTO.addCar(car);
                        return car;
                    });

            // Extract driver data
            Long driverId = record.get(Tables.DRIVERS.ID);
            String driverFirstName = record.get(Tables.DRIVERS.FIRST_NAME);
            String driverLastName = record.get(Tables.DRIVERS.LAST_NAME);
            String driverNationality = record.get(Tables.DRIVERS.NATIONALITY);
            String driverHometown = record.get(Tables.DRIVERS.HOMETOWN);
            String driverLicenseType = record.get(Tables.DRIVERS.LICENSE_TYPE);
            Integer driverNumber = record.get(Tables.CAR_DRIVERS.DRIVER_NUMBER);

            // Create driver DTO
            DriverDTO driverDTO = new DriverDTO(
                    driverId,
                    driverFirstName,
                    driverLastName,
                    driverNationality,
                    driverHometown,
                    driverLicenseType,
                    driverNumber
            );

            // Add driver to car
            carDTO.addDriver(driverDTO);
        }

        return teamMap;
    }
}
