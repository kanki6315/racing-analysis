package com.arjunakankipati.racingstatanalysis.repository.impl;

import com.arjunakankipati.racingstatanalysis.model.BaseEntity;
import com.arjunakankipati.racingstatanalysis.repository.BaseRepository;
import org.jooq.*;
import org.jooq.Record;

import java.util.List;
import java.util.Optional;

/**
 * Base implementation of the BaseRepository interface using JOOQ.
 * Provides common CRUD operations for all entities.
 *
 * @param <T> the entity type
 * @param <ID> the type of the entity's ID
 */
public abstract class BaseRepositoryImpl<T extends BaseEntity<ID>, ID> implements BaseRepository<T, ID> {

    protected final DSLContext dsl;
    protected final Table<?> table;
    protected final Field<ID> idField;

    protected BaseRepositoryImpl(DSLContext dsl, Table<?> table, Field<ID> idField) {
        this.dsl = dsl;
        this.table = table;
        this.idField = idField;
    }

    @Override
    public List<T> findAll() {
        Result<Record> result = dsl.select()
                .from(table)
                .fetch();

        return result.map(this::mapToEntity);
    }

    @Override
    public Optional<T> findById(ID id) {
        Record record = dsl.select()
                .from(table)
                .where(idField.eq(id))
                .fetchOne();

        return Optional.ofNullable(record)
                .map(this::mapToEntity);
    }

    @Override
    public T save(T entity) {
        if (entity.getId() == null) {
            // Insert new entity
            return insert(entity);
        } else {
            // Update existing entity
            update(entity);
            return entity;
        }
    }

    @Override
    public boolean deleteById(ID id) {
        int count = dsl.deleteFrom(table)
                .where(idField.eq(id))
                .execute();

        return count > 0;
    }

    @Override
    public long count() {
        return dsl.selectCount()
                .from(table)
                .fetchOne(0, Long.class);
    }

    /**
     * Maps a JOOQ Record to an entity.
     *
     * @param record the JOOQ Record
     * @return the entity
     */
    protected abstract T mapToEntity(Record record);

    /**
     * Inserts a new entity into the database.
     *
     * @param entity the entity to insert
     * @return the inserted entity with its ID populated
     */
    protected abstract T insert(T entity);

    /**
     * Updates an existing entity in the database.
     *
     * @param entity the entity to update
     */
    protected abstract void update(T entity);
}
