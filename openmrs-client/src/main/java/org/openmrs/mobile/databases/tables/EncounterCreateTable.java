package org.openmrs.mobile.databases.tables;

import org.openmrs.mobile.databases.DBOpenHelper;
import org.openmrs.mobile.databases.OpenMRSDBOpenHelper;
import org.openmrs.mobile.models.Encountercreate;

/**
 * Created by Arinze on 10/12/2018.
 */

public class EncounterCreateTable extends Table<Encountercreate> {
    public static final String TABLE_NAME = "encountercreate";

    /**
     * Number of columns without ID column
     * use as a param to
     *
     * @see org.openmrs.mobile.databases.tables.Table#values(int)
     */
    private static final int INSERT_COLUMNS_COUNT = 7;

    @Override
    public String createTableDefinition() {
        return CREATE_TABLE + TABLE_NAME + "("
                + Column.ID + PRIMARY_KEY
                + Column.ENCOUNTER_PROVIDERS + Column.Type.DATE_TYPE_WITH_COMMA
                + Column.ENCOUNTER_DATETIME + Column.Type.DATE_TYPE_NOT_NULL
                + Column.ENCOUNTER_TYPE + Column.Type.DATE_TYPE_WITH_COMMA
                + Column.PATIENT_ID + Column.Type.INT_TYPE_WITH_COMMA
                + Column.OBS + Column.Type.TEXT_TYPE_WITH_COMMA
                + Column.OBS_LIST + Column.Type.TEXT_TYPE_WITH_COMMA
                + Column.LOCATION + Column.Type.TEXT_TYPE_WITH_COMMA
                + Column.PATIENT + Column.Type.TEXT_TYPE_WITH_COMMA
                + Column.FORM_NAME + Column.Type.TEXT_TYPE_WITH_COMMA
                + Column.FORM_NAME_RAW + Column.Type.TEXT_TYPE_WITH_COMMA
                + Column.INFANT_NAME + Column.Type.TEXT_TYPE_WITH_COMMA
                + Column.VISIT + Column.Type.TEXT_TYPE_WITH_COMMA
                + Column.SYNCED + Column.Type.INT_TYPE
                + ");";
    }

    @Override
    public String insertIntoTableDefinition() {
        return INSERT_INTO + TABLE_NAME + "("
                + Column.ENCOUNTER_PROVIDERS + Column.COMMA
                + Column.ENCOUNTER_DATETIME + Column.COMMA
                + Column.ENCOUNTER_TYPE + Column.COMMA
                + Column.PATIENT_ID + Column.COMMA
                + Column.OBS + Column.COMMA
                + Column.OBS_LIST + Column.COMMA
                + Column.LOCATION + Column.COMMA
                + Column.PATIENT + Column.COMMA
                + Column.FORM_NAME + Column.COMMA
                + Column.FORM_NAME_RAW + Column.COMMA
                + Column.INFANT_NAME + Column.COMMA
                + Column.VISIT + Column.COMMA
                + Column.SYNCED + ")"
                + values(INSERT_COLUMNS_COUNT);
    }

    @Override
    public String dropTableDefinition() {
        return DROP_TABLE_IF_EXISTS + TABLE_NAME;
    }

    @Override
    public Long insert(Encountercreate tableObject) {
        DBOpenHelper helper = OpenMRSDBOpenHelper.getInstance().getDBOpenHelper();
        return helper.insertEncounterCreate(helper.getWritableDatabase(), tableObject);
    }

    @Override
    public int update(long tableObjectID, Encountercreate tableObject) {
        DBOpenHelper helper = OpenMRSDBOpenHelper.getInstance().getDBOpenHelper();
        return helper.updateEncounterCreate(helper.getWritableDatabase(), tableObjectID, tableObject);
    }

    @Override
    public void delete(long tableObjectID) {
        DBOpenHelper openHelper = OpenMRSDBOpenHelper.getInstance().getDBOpenHelper();
        openHelper.getWritableDatabase().delete(TABLE_NAME, MasterColumn.ID + MasterColumn.EQUALS + tableObjectID, null);
    }

    public class Column extends MasterColumn {
        public static final String ENCOUNTER_PROVIDERS = "encounterProviders";
        public static final String ENCOUNTER_DATETIME = "encounterDatetime";
        public static final String ENCOUNTER_TYPE = "encounterType";
        public static final String PATIENT_ID = "patientid";
        public static final String FORM_NAME = "formname";
        public static final String FORM_NAME_RAW = "formnameRaw";
        public static final String INFANT_NAME = "infantName";
        public static final String LOCATION = "location";
        public static final String OBS = "obs";
        public static final String OBS_LIST = "obslist";
        public static final String PATIENT = "patient";
        public static final String SYNCED = "synced";
        public static final String VISIT = "visit";
        public static final String ID = "Id";

    }

    @Override
    public String toString() {
        return TABLE_NAME + createTableDefinition();
    }
}
