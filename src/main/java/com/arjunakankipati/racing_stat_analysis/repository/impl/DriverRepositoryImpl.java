package com.arjunakankipati.racing_stat_analysis.repository.impl;

import com.arjunakankipati.racing_stat_analysis.model.Driver;
import com.arjunakankipati.racing_stat_analysis.repository.DriverRepository;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static org.jooq.impl.DSL.field;

/**
 * Implementation of the DriverRepository interface using JOOQ.
 * Extends BaseRepositoryImpl to inherit common CRUD operations.
 */
@Repository
public class DriverRepositoryImpl extends BaseRepositoryImpl<Driver, Long> implements DriverRepository {

    /**
     * Constructor with DSLContext dependency injection.
     *
     * @param dsl the JOOQ DSL context
     */
    @Autowired
    public DriverRepositoryImpl(DSLContext dsl) {
        super(dsl, "drivers", "id", Long.class);
    }

    @Override
    protected Driver mapToEntity(Record record) {
        if (record == null) {
            return null;
        }

        return new Driver(
                record.get(field("id", Long.class)),
                record.get(field("first_name", String.class)),
                record.get(field("last_name", String.class)),
                record.get(field("nationality", String.class)),
                record.get(field("hometown", String.class)),
                record.get(field("license_type", String.class)),
                record.get(field("external_id", String.class))
        );
    }

    @Override
    protected Driver insert(Driver driver) {
        Record record = dsl.insertInto(table)
                .columns(
                        field("first_name"),
                        field("last_name"),
                        field("nationality"),
                        field("hometown"),
                        field("license_type"),
                        field("external_id")
                )
                .values(
                        driver.getFirstName(),
                        driver.getLastName(),
                        driver.getNationality(),
                        driver.getHometown(),
                        driver.getLicenseType(),
                        driver.getExternalId()
                )
                .returningResult(
                        field("id"),
                        field("first_name"),
                        field("last_name"),
                        field("nationality"),
                        field("hometown"),
                        field("license_type"),
                        field("external_id")
                )
                .fetchOne();

        return mapToEntity(record);
    }

    @Override
    protected void update(Driver driver) {
        dsl.update(table)
                .set(field("first_name"), driver.getFirstName())
                .set(field("last_name"), driver.getLastName())
                .set(field("nationality"), driver.getNationality())
                .set(field("hometown"), driver.getHometown())
                .set(field("license_type"), driver.getLicenseType())
                .set(field("external_id"), driver.getExternalId())
                .where(idField.eq(driver.getId()))
                .execute();
    }

    @Override
    public Optional<Driver> findByFirstNameAndLastName(String firstName, String lastName) {
        Record record = dsl.select()
                .from(table)
                .where(field("first_name").eq(firstName))
                .and(field("last_name").eq(lastName))
                .fetchOne();

        return Optional.ofNullable(record)
                .map(this::mapToEntity);
    }

    @Override
    public List<Driver> findByNationality(String nationality) {
        return dsl.select()
                .from(table)
                .where(field("nationality").eq(nationality))
                .fetch()
                .map(this::mapToEntity);
    }

    @Override
    public List<Driver> findByLicenseType(String licenseType) {
        return dsl.select()
                .from(table)
                .where(field("license_type").eq(licenseType))
                .fetch()
                .map(this::mapToEntity);
    }

    @Override
    public Optional<Driver> findByExternalId(String externalId) {
        Record record = dsl.select()
                .from(table)
                .where(field("external_id").eq(externalId))
                .fetchOne();

        return Optional.ofNullable(record)
                .map(this::mapToEntity);
    }

    @Override
    public List<Driver> findByFirstNameContainingOrLastNameContaining(String nameContains, String nameContains2) {
        return dsl.select()
                .from(table)
                .where(field("first_name").like("%" + nameContains + "%"))
                .or(field("last_name").like("%" + nameContains2 + "%"))
                .fetch()
                .map(this::mapToEntity);
    }

    @Override
    public List<Driver> findByHometownContaining(String hometownContains) {
        return dsl.select()
                .from(table)
                .where(field("hometown").like("%" + hometownContains + "%"))
                .fetch()
                .map(this::mapToEntity);
    }
}