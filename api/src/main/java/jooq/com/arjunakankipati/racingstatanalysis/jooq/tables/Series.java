/*
 * This file is generated by jOOQ.
 */
package com.arjunakankipati.racingstatanalysis.jooq.tables;


import com.arjunakankipati.racingstatanalysis.jooq.Keys;
import com.arjunakankipati.racingstatanalysis.jooq.Public;
import com.arjunakankipati.racingstatanalysis.jooq.tables.Classes.ClassesPath;
import com.arjunakankipati.racingstatanalysis.jooq.tables.Events.EventsPath;
import com.arjunakankipati.racingstatanalysis.jooq.tables.records.SeriesRecord;

import java.util.Collection;

import org.jooq.Condition;
import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Identity;
import org.jooq.InverseForeignKey;
import org.jooq.Name;
import org.jooq.Path;
import org.jooq.PlainSQL;
import org.jooq.QueryPart;
import org.jooq.Record;
import org.jooq.SQL;
import org.jooq.Schema;
import org.jooq.Select;
import org.jooq.Stringly;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.TableOptions;
import org.jooq.UniqueKey;
import org.jooq.impl.DSL;
import org.jooq.impl.SQLDataType;
import org.jooq.impl.TableImpl;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes", "this-escape" })
public class Series extends TableImpl<SeriesRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>public.series</code>
     */
    public static final Series SERIES = new Series();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<SeriesRecord> getRecordType() {
        return SeriesRecord.class;
    }

    /**
     * The column <code>public.series.id</code>.
     */
    public final TableField<SeriesRecord, Long> ID = createField(DSL.name("id"), SQLDataType.BIGINT.nullable(false).identity(true), this, "");

    /**
     * The column <code>public.series.name</code>.
     */
    public final TableField<SeriesRecord, String> NAME = createField(DSL.name("name"), SQLDataType.VARCHAR(255).nullable(false), this, "");

    /**
     * The column <code>public.series.description</code>.
     */
    public final TableField<SeriesRecord, String> DESCRIPTION = createField(DSL.name("description"), SQLDataType.CLOB, this, "");

    private Series(Name alias, Table<SeriesRecord> aliased) {
        this(alias, aliased, (Field<?>[]) null, null);
    }

    private Series(Name alias, Table<SeriesRecord> aliased, Field<?>[] parameters, Condition where) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.table(), where);
    }

    /**
     * Create an aliased <code>public.series</code> table reference
     */
    public Series(String alias) {
        this(DSL.name(alias), SERIES);
    }

    /**
     * Create an aliased <code>public.series</code> table reference
     */
    public Series(Name alias) {
        this(alias, SERIES);
    }

    /**
     * Create a <code>public.series</code> table reference
     */
    public Series() {
        this(DSL.name("series"), null);
    }

    public <O extends Record> Series(Table<O> path, ForeignKey<O, SeriesRecord> childPath, InverseForeignKey<O, SeriesRecord> parentPath) {
        super(path, childPath, parentPath, SERIES);
    }

    /**
     * A subtype implementing {@link Path} for simplified path-based joins.
     */
    public static class SeriesPath extends Series implements Path<SeriesRecord> {

        private static final long serialVersionUID = 1L;
        public <O extends Record> SeriesPath(Table<O> path, ForeignKey<O, SeriesRecord> childPath, InverseForeignKey<O, SeriesRecord> parentPath) {
            super(path, childPath, parentPath);
        }
        private SeriesPath(Name alias, Table<SeriesRecord> aliased) {
            super(alias, aliased);
        }

        @Override
        public SeriesPath as(String alias) {
            return new SeriesPath(DSL.name(alias), this);
        }

        @Override
        public SeriesPath as(Name alias) {
            return new SeriesPath(alias, this);
        }

        @Override
        public SeriesPath as(Table<?> alias) {
            return new SeriesPath(alias.getQualifiedName(), this);
        }
    }

    @Override
    public Schema getSchema() {
        return aliased() ? null : Public.PUBLIC;
    }

    @Override
    public Identity<SeriesRecord, Long> getIdentity() {
        return (Identity<SeriesRecord, Long>) super.getIdentity();
    }

    @Override
    public UniqueKey<SeriesRecord> getPrimaryKey() {
        return Keys.SERIES_PKEY;
    }

    private transient ClassesPath _classes;

    /**
     * Get the implicit to-many join path to the <code>public.classes</code>
     * table
     */
    public ClassesPath classes() {
        if (_classes == null)
            _classes = new ClassesPath(this, null, Keys.CLASSES__CLASSES_SERIES_ID_FKEY.getInverseKey());

        return _classes;
    }

    private transient EventsPath _events;

    /**
     * Get the implicit to-many join path to the <code>public.events</code>
     * table
     */
    public EventsPath events() {
        if (_events == null)
            _events = new EventsPath(this, null, Keys.EVENTS__EVENTS_SERIES_ID_FKEY.getInverseKey());

        return _events;
    }

    @Override
    public Series as(String alias) {
        return new Series(DSL.name(alias), this);
    }

    @Override
    public Series as(Name alias) {
        return new Series(alias, this);
    }

    @Override
    public Series as(Table<?> alias) {
        return new Series(alias.getQualifiedName(), this);
    }

    /**
     * Rename this table
     */
    @Override
    public Series rename(String name) {
        return new Series(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public Series rename(Name name) {
        return new Series(name, null);
    }

    /**
     * Rename this table
     */
    @Override
    public Series rename(Table<?> name) {
        return new Series(name.getQualifiedName(), null);
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    public Series where(Condition condition) {
        return new Series(getQualifiedName(), aliased() ? this : null, null, condition);
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    public Series where(Collection<? extends Condition> conditions) {
        return where(DSL.and(conditions));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    public Series where(Condition... conditions) {
        return where(DSL.and(conditions));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    public Series where(Field<Boolean> condition) {
        return where(DSL.condition(condition));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    @PlainSQL
    public Series where(SQL condition) {
        return where(DSL.condition(condition));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    @PlainSQL
    public Series where(@Stringly.SQL String condition) {
        return where(DSL.condition(condition));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    @PlainSQL
    public Series where(@Stringly.SQL String condition, Object... binds) {
        return where(DSL.condition(condition, binds));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    @PlainSQL
    public Series where(@Stringly.SQL String condition, QueryPart... parts) {
        return where(DSL.condition(condition, parts));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    public Series whereExists(Select<?> select) {
        return where(DSL.exists(select));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    public Series whereNotExists(Select<?> select) {
        return where(DSL.notExists(select));
    }
}
