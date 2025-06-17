package com.arjunakankipati.racingstatanalysis.repository.impl;

import com.arjunakankipati.racingstatanalysis.jooq.Tables;
import com.arjunakankipati.racingstatanalysis.model.Driver;
import com.arjunakankipati.racingstatanalysis.repository.DriverRepository;
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
        super(dsl, Tables.DRIVERS, Tables.DRIVERS.ID);
    }

    @Override
    protected Driver mapToEntity(Record record) {
        if (record == null) {
            return null;
        }

        var driverRec = record.into(Tables.DRIVERS);
        return new Driver(
                driverRec.getId(),
                driverRec.getFirstName(),
                driverRec.getLastName(),
                driverRec.getNationality(),
                driverRec.getHometown(),
                driverRec.getLicenseType(),
                driverRec.getExternalId()
        );
    }

    @Override
    protected Driver insert(Driver driver) {
        Record record = dsl.insertInto(table)
                .columns(
                        Tables.DRIVERS.FIRST_NAME,
                        Tables.DRIVERS.LAST_NAME,
                        Tables.DRIVERS.NATIONALITY,
                        Tables.DRIVERS.HOMETOWN,
                        Tables.DRIVERS.LICENSE_TYPE,
                        Tables.DRIVERS.EXTERNAL_ID
                )
                .values(
                        driver.getFirstName(),
                        driver.getLastName(),
                        driver.getNationality(),
                        driver.getHometown(),
                        driver.getLicenseType(),
                        driver.getExternalId()
                )
                .returning()
                .fetchOne();

        return mapToEntity(record);
    }

    @Override
    protected void update(Driver driver) {
        dsl.update(table)
                .set(Tables.DRIVERS.FIRST_NAME, driver.getFirstName())
                .set(Tables.DRIVERS.LAST_NAME, driver.getLastName())
                .set(Tables.DRIVERS.NATIONALITY, driver.getNationality())
                .set(Tables.DRIVERS.HOMETOWN, driver.getHometown())
                .set(Tables.DRIVERS.LICENSE_TYPE, driver.getLicenseType())
                .set(Tables.DRIVERS.EXTERNAL_ID, driver.getExternalId())
                .where(idField.eq(driver.getId()))
                .execute();
    }

    @Override
    public Optional<Driver> findByFirstNameAndLastName(String firstName, String lastName) {
        Record record = dsl.select()
                .from(table)
                .where(Tables.DRIVERS.FIRST_NAME.eq(firstName))
                .and(Tables.DRIVERS.LAST_NAME.eq(lastName))
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
