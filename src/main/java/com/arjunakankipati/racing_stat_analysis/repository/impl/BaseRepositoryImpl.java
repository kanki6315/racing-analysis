package com.arjunakankipati.racing_stat_analysis.repository.impl;

import com.arjunakankipati.racing_stat_analysis.model.BaseEntity;
import com.arjunakankipati.racing_stat_analysis.repository.BaseRepository;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.Table;

import java.util.List;
import java.util.Optional;

import static org.jooq.impl.DSL.field;
import static org.jooq.impl.DSL.table;

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
    protected final Class<ID> idType;

    /**
     * Constructor with DSLContext dependency injection.
     *
     * @param dsl the JOOQ DSL context
     * @param tableName the name of the database table
     * @param idFieldName the name of the ID field
     * @param idType the class of the ID type
     */
    protected BaseRepositoryImpl(DSLContext dsl, String tableName, String idFieldName, Class<ID> idType) {
        this.dsl = dsl;
        this.table = table(tableName);
        this.idField = field(idFieldName, idType);
        this.idType = idType;
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
