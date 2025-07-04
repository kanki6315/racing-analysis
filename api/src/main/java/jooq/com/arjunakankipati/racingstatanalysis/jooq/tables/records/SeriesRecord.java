/*
 * This file is generated by jOOQ.
 */
package com.arjunakankipati.racingstatanalysis.jooq.tables.records;


import com.arjunakankipati.racingstatanalysis.jooq.tables.Series;

import org.jooq.Record1;
import org.jooq.impl.UpdatableRecordImpl;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes", "this-escape" })
public class SeriesRecord extends UpdatableRecordImpl<SeriesRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * Setter for <code>public.series.id</code>.
     */
    public void setId(Long value) {
        set(0, value);
    }

    /**
     * Getter for <code>public.series.id</code>.
     */
    public Long getId() {
        return (Long) get(0);
    }

    /**
     * Setter for <code>public.series.name</code>.
     */
    public void setName(String value) {
        set(1, value);
    }

    /**
     * Getter for <code>public.series.name</code>.
     */
    public String getName() {
        return (String) get(1);
    }

    /**
     * Setter for <code>public.series.description</code>.
     */
    public void setDescription(String value) {
        set(2, value);
    }

    /**
     * Getter for <code>public.series.description</code>.
     */
    public String getDescription() {
        return (String) get(2);
    }

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    @Override
    public Record1<Long> key() {
        return (Record1) super.key();
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached SeriesRecord
     */
    public SeriesRecord() {
        super(Series.SERIES);
    }

    /**
     * Create a detached, initialised SeriesRecord
     */
    public SeriesRecord(Long id, String name, String description) {
        super(Series.SERIES);

        setId(id);
        setName(name);
        setDescription(description);
        resetChangedOnNotNull();
    }
}
