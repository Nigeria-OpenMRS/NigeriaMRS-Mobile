package org.openmrs.mobile.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.activeandroid.Cache;
import com.activeandroid.query.Select;

//import net.sqlcipher.Cursor;
import net.sqlcipher.database.SQLiteOpenHelper;

import org.openmrs.mobile.databases.DBOpenHelper;
import org.openmrs.mobile.databases.OpenMRSDBOpenHelper;
import org.openmrs.mobile.databases.tables.EncounterCreateTable;
import org.openmrs.mobile.databases.tables.EncounterTable;
import org.openmrs.mobile.databases.tables.ObservationTable;
import org.openmrs.mobile.models.Encounter;
import org.openmrs.mobile.models.EncounterType;
import org.openmrs.mobile.models.Encountercreate;
import org.openmrs.mobile.models.Observation;
import org.openmrs.mobile.utilities.DateUtils;
import org.openmrs.mobile.utilities.FormService;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.schedulers.Schedulers;

import static org.openmrs.mobile.databases.DBOpenHelper.createObservableIO;

/**
 * Created by Arinze on 10/12/2018.
 */


public class EncounterCreateDAO {

    public List<Encountercreate> getEncountersByPatientID(long patientId) {
        return  new Select()
                .from(Encountercreate.class)
                .where("patientid = ?", patientId)
                .execute();
    }

    public List<Encountercreate> getEncounterTypesByFormName(String formname) {
        return new Select()
                .from(Encountercreate.class)
                .where("formname = ?", formname)
                .execute();
    }


    public Observable<List<Encountercreate>> getEncounterByPatientID(final Long patientID) {
        return createObservableIO(() -> {
            List<Encountercreate> encountercreates = new ArrayList<>();
            DBOpenHelper helper = OpenMRSDBOpenHelper.getInstance().getDBOpenHelper();
//            DBOpenHelper helper = new SQLiteOpenHelper(context, "", null ,"");
            final SQLiteDatabase db = Cache.openDatabase();

            String where = String.format("%s = ?", EncounterCreateTable.Column.PATIENT_ID);
            String[] whereArgs = new String[]{patientID.toString()};
            String orderBy = null; //EncounterCreateTable.Column.START_DATE + " DESC";


            final Cursor cursor = db.query(EncounterCreateTable.TABLE_NAME, null, where, whereArgs, null, null, orderBy);
            if (null != cursor) {
                try {
                    while (cursor.moveToNext()) {
                        int id_CI = cursor.getColumnIndex(EncounterCreateTable.Column.ID);
                        int encounter_CI = cursor.getColumnIndex(EncounterCreateTable.Column.ENCOUNTER_DATETIME);
                        int patient_CI = cursor.getColumnIndex(EncounterCreateTable.Column.PATIENT_ID);
                        int form_CI = cursor.getColumnIndex(EncounterCreateTable.Column.FORM_NAME);
                        int form_raw_CI = cursor.getColumnIndex(EncounterCreateTable.Column.FORM_NAME_RAW);
                        int obs_CI = cursor.getColumnIndex(EncounterCreateTable.Column.OBS_LIST);
                        int encounterType_CI = cursor.getColumnIndex(EncounterCreateTable.Column.ENCOUNTER_TYPE);
                        Encountercreate encountercreate = new Encountercreate();
                        encountercreate.setEncounterDatetime(cursor.getString(encounter_CI));
                        encountercreate.setFormname(cursor.getString(form_CI));
                        encountercreate.setPatientId(cursor.getLong(patient_CI));
                        encountercreate.setObslistLocals(cursor.getString(obs_CI));
                        encountercreate.setEncounterType(cursor.getString(encounterType_CI));
                        encountercreate.setFormnameRaw(cursor.getString(form_raw_CI));
                        encountercreate.pullObslistLocal();
                        encountercreate.setId(cursor.getLong(id_CI));
                        encountercreates.add(encountercreate);
                    }
                } finally {
                    cursor.close();
                }
            }
            return encountercreates;
        });
    }

}
