package org.openmrs.mobile.activities.patientdashboard.entries;

import org.openmrs.mobile.activities.patientdashboard.PatientDashboardContract;
import org.openmrs.mobile.activities.patientdashboard.PatientDashboardMainPresenterImpl;
import org.openmrs.mobile.api.retrofit.VisitApi;
import org.openmrs.mobile.dao.EncounterCreateDAO;
import org.openmrs.mobile.dao.PatientDAO;
import org.openmrs.mobile.dao.VisitDAO;
import org.openmrs.mobile.listeners.retrofit.DefaultResponseCallbackListener;
import org.openmrs.mobile.listeners.retrofit.StartVisitResponseListenerCallback;
import org.openmrs.mobile.models.Encountercreate;
import org.openmrs.mobile.models.Patient;
import org.openmrs.mobile.utilities.NetworkUtils;

import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by Arinze on 10/12/2018.
 */
import static org.openmrs.mobile.utilities.FormService.getFormResourceByName;
public class PatientDashboardEntriesPresenter extends PatientDashboardMainPresenterImpl implements PatientDashboardContract.PatientEntriesPresenter {

    private PatientDashboardContract.ViewPatientEntries mPatientEntriesView;
    private VisitDAO visitDAO;
    private EncounterCreateDAO encounterCreateDAO;
    private VisitApi visitApi;

    public PatientDashboardEntriesPresenter(String id, PatientDashboardContract.ViewPatientEntries mPatientEntriesView) {
        this.mPatient = new PatientDAO().findPatientByID(id);
        this.mPatientEntriesView = mPatientEntriesView;
        this.mPatientEntriesView.setPresenter(this);
        this.encounterCreateDAO = new EncounterCreateDAO();

    }

    public PatientDashboardEntriesPresenter(Patient patient,
                                           PatientDashboardContract.ViewPatientEntries mPatientEntriesView,
                                           EncounterCreateDAO encounterCreateDAO,
                                           VisitApi visitApi) {
        this.mPatient = patient;
        this.mPatientEntriesView = mPatientEntriesView;
        this.visitApi = visitApi;
        this.encounterCreateDAO = encounterCreateDAO;
        this.mPatientEntriesView.setPresenter(this);
    }

    @Override
    public void subscribe() {
        addSubscription(encounterCreateDAO.getEncounterByPatientID(mPatient.getId())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(patientEncounters -> {
                    if (patientEncounters !=null && patientEncounters.isEmpty()) {
                        mPatientEntriesView.setEmptyListVisibility(false);
                    }
                    else {
                        mPatientEntriesView.setEmptyListVisibility(true);
                        mPatientEntriesView.updateList(patientEncounters);
                    }
                }));
    }




    @Override
    public void startFormDisplayActivityWithEncounter(Encountercreate encounter) {
        mPatientEntriesView.startFormDisplayActivity(encounter);
    }}
