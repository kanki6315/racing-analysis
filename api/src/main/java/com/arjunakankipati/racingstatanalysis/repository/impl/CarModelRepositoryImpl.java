package com.arjunakankipati.racingstatanalysis.repository.impl;

import com.arjunakankipati.racingstatanalysis.jooq.Tables;
import com.arjunakankipati.racingstatanalysis.model.CarModel;
import com.arjunakankipati.racingstatanalysis.repository.CarModelRepository;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Implementation of CarModelRepository using JOOQ.
 */
@Repository
public class CarModelRepositoryImpl extends BaseRepositoryImpl<CarModel, Long> implements CarModelRepository {

    @Autowired
    public CarModelRepositoryImpl(DSLContext dsl) {
        super(dsl, Tables.CAR_MODELS, Tables.CAR_MODELS.ID);
    }

    @Override
    protected CarModel mapToEntity(Record record) {
        if (record == null) {
            return null;
        }

        var carModelRec = record.into(Tables.CAR_MODELS);
        return new CarModel(
                carModelRec.getId(),
                carModelRec.getManufacturerId(),
                carModelRec.getName(),
                carModelRec.getFullName(),
                carModelRec.getYearModel(),
                carModelRec.getDescription()
        );
    }

    @Override
    protected CarModel insert(CarModel carModel) {
        Record record = dsl.insertInto(table)
                .columns(
                        Tables.CAR_MODELS.MANUFACTURER_ID,
                        Tables.CAR_MODELS.NAME,
                        Tables.CAR_MODELS.FULL_NAME,
                        Tables.CAR_MODELS.YEAR_MODEL,
                        Tables.CAR_MODELS.DESCRIPTION
                )
                .values(
                        carModel.getManufacturerId(),
                        carModel.getName(),
                        carModel.getFullName(),
                        carModel.getYearModel(),
                        carModel.getDescription()
                )
                .returning()
                .fetchOne();

        return mapToEntity(record);
    }

    @Override
    protected void update(CarModel carModel) {
        dsl.update(table)
                .set(Tables.CAR_MODELS.MANUFACTURER_ID, carModel.getManufacturerId())
                .set(Tables.CAR_MODELS.NAME, carModel.getName())
                .set(Tables.CAR_MODELS.FULL_NAME, carModel.getFullName())
                .set(Tables.CAR_MODELS.YEAR_MODEL, carModel.getYearModel())
                .set(Tables.CAR_MODELS.DESCRIPTION, carModel.getDescription())
                .where(idField.eq(carModel.getId()))
                .execute();
    }

    @Override
    public Optional<CarModel> findByManufacturerIdAndName(Long manufacturerId, String name) {
        Record record = dsl.select()
                .from(table)
                .where(Tables.CAR_MODELS.MANUFACTURER_ID.eq(manufacturerId))
                .and(Tables.CAR_MODELS.NAME.eq(name))
                .fetchOne();

        return Optional.ofNullable(record)
                .map(this::mapToEntity);
    }
} 