/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.mobile.activities.formdisplay;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.SparseArray;

import org.joda.time.LocalDateTime;
import org.openmrs.mobile.activities.BasePresenter;
import org.openmrs.mobile.api.EncounterService;
import org.openmrs.mobile.dao.EncounterDAO;
import org.openmrs.mobile.dao.PatientDAO;
import org.openmrs.mobile.listeners.retrofit.DefaultResponseCallbackListener;
import org.openmrs.mobile.models.Answer;
import org.openmrs.mobile.models.Encounter;
import org.openmrs.mobile.models.EncounterType;
import org.openmrs.mobile.models.Encountercreate;
import org.openmrs.mobile.models.Form;
import org.openmrs.mobile.models.FormResource;
import org.openmrs.mobile.models.Obscreate;
import org.openmrs.mobile.models.ObscreateLocal;
import org.openmrs.mobile.models.Patient;
import org.openmrs.mobile.models.Resource;
import org.openmrs.mobile.models.Visit;
import org.openmrs.mobile.utilities.ApplicationConstants;
import org.openmrs.mobile.utilities.InputField;
import org.openmrs.mobile.utilities.SelectManyFields;
import org.openmrs.mobile.utilities.SelectOneField;
import org.openmrs.mobile.utilities.ToastUtil;

import java.util.ArrayList;
import java.util.List;

import static org.openmrs.mobile.utilities.FormService.getFormResourceByName;

public class FormDisplayMainPresenter extends BasePresenter implements FormDisplayContract.Presenter.MainPresenter {

    private final long mPatientID;
    private final long mEntryID;
    private final String mEncountertype;
    private final String mFormname;
    private final String mFormnameRaw;
    private FormDisplayContract.View.MainView mFormDisplayView;
    private Patient mPatient;
    private FormPageAdapter mPageAdapter;
    private String mEncounterDate = null;
    private String mLocation = null;
    private String mProvider = null;
    private String mProviderUUID = null;
    private String infantName = null;

    public FormDisplayMainPresenter(FormDisplayContract.View.MainView mFormDisplayView, Bundle bundle, FormPageAdapter mPageAdapter) {
        this.mFormDisplayView = mFormDisplayView;
        this.mPatientID =(long) bundle.get(ApplicationConstants.BundleKeys.PATIENT_ID_BUNDLE);
        this.mPatient =new PatientDAO().findPatientByID(Long.toString(mPatientID));
        this.mEncountertype =(String)bundle.get(ApplicationConstants.BundleKeys.ENCOUNTERTYPE);
        this.mFormname = (String) bundle.get(ApplicationConstants.BundleKeys.FORM_NAME);
        this.mFormnameRaw = (String) bundle.get(ApplicationConstants.BundleKeys.FORM_NAME_RAW);
        this.mPageAdapter = mPageAdapter;
        this.mEntryID =(long) bundle.get(ApplicationConstants.BundleKeys.ENTRIES_ID);
        mFormDisplayView.setPresenter(this);
    }

    @Override
    public void subscribe() {
        // This method is intentionally empty
    }

    @Override
    public void createEncounter() {
        List<InputField> inputFields = new ArrayList<>();
        List<SelectOneField> radioGroupFields = new ArrayList<>();
        List<SelectManyFields> selectManyFields = new ArrayList<>();

        mFormDisplayView.enableSubmitButton(false);

        Encountercreate encountercreate=new Encountercreate();
        if (mEntryID != 0){
            Encountercreate.delete(Encountercreate.class,mEntryID);
        }
        encountercreate.setPatient(mPatient.getUuid());
        encountercreate.setEncounterType(mEncountertype);

        Encounter encounter = new Encounter();


        List<Obscreate> observations=new ArrayList<>();
        List<ObscreateLocal> observationsLocal =new ArrayList<>();
        SparseArray<Fragment> activefrag = mPageAdapter.getRegisteredFragments();
        boolean valid=true;
        for (int i = 0;i < activefrag.size();i++) {
            FormDisplayPageFragment formPageFragment=(FormDisplayPageFragment)activefrag.get(i);
            if(!formPageFragment.checkInputFields()) {
                valid=false;
                break;
            }

            inputFields.addAll(formPageFragment.getInputFields());
            radioGroupFields.addAll(formPageFragment.getSelectOneFields());
            selectManyFields.addAll(formPageFragment.getSelectManyFields());
        }

        if(valid) {
            for (InputField input: inputFields) {
                if(!input.getValueAll().isEmpty() && !input.getValueAll().equals("") && input.getObs().equals("obs")) {
                    Obscreate obscreate = new Obscreate();
                    ObscreateLocal obscreateLocal = new ObscreateLocal();
                    obscreate.setConcept(input.getConcept());
                    obscreateLocal.setConcept(input.getConcept());
                    obscreateLocal.setQuestionLabel(input.getQuestionLabel());
                    obscreate.setValue(String.valueOf(input.getValueAll()));
                    obscreateLocal.setValue(String.valueOf(input.getValueAll()));
                    obscreateLocal.setAnswerLabel(String.valueOf(input.getValueAll()));
                    LocalDateTime localDateTime = new LocalDateTime();
                    obscreate.setObsDatetime(localDateTime.toString());
                    obscreateLocal.setObsDatetime(localDateTime.toString());
                    obscreate.setPerson(mPatient.getUuid());
                    obscreateLocal.setPerson(mPatient.getUuid());
                    observations.add(obscreate);
                    observationsLocal.add(obscreateLocal);
                }
                if (input.getObs().equals("encounterDate")){
                    this.mEncounterDate = input.getValueAll();
                }
            }

            for (SelectOneField radioGroupField : radioGroupFields) {
                if (radioGroupField.getChosenAnswer() != null && radioGroupField.getObs().equals("obs")) {
                    Obscreate obscreate = new Obscreate();
                    ObscreateLocal obscreateLocal = new ObscreateLocal();
                    obscreate.setConcept(radioGroupField.getConcept());
                    obscreateLocal.setConcept(radioGroupField.getConcept());
                    obscreateLocal.setQuestionLabel(radioGroupField.getQuestionLabel());
                    obscreate.setValue(radioGroupField.getChosenAnswer().getConcept());
                    obscreateLocal.setValue(radioGroupField.getChosenAnswer().getConcept());
                    obscreateLocal.setAnswerLabel(radioGroupField.getChosenAnswer().getLabel());
                    LocalDateTime localDateTime = new LocalDateTime();
                    obscreate.setObsDatetime(localDateTime.toString());
                    obscreateLocal.setObsDatetime(localDateTime.toString());
                    obscreate.setPerson(mPatient.getUuid());
                    obscreateLocal.setPerson(mPatient.getUuid());
                    observations.add(obscreate);
                    observationsLocal.add(obscreateLocal);
                }
                if (radioGroupField.getObs().equals("location")){
                    this.mProviderUUID = radioGroupField.getChosenAnswer().getUuid();
                    this.mProvider = radioGroupField.getChosenAnswer().getLabel();
                }
            }

            for (SelectManyFields selectManyField : selectManyFields) {
                if (selectManyField.getChosenAnswerList().size() > 0 && selectManyField.getObs().equals("obs")) {
                    for (Answer answer:selectManyField.getChosenAnswerList()){
                        Obscreate obscreate = new Obscreate();
                        ObscreateLocal obscreateLocal = new ObscreateLocal();
                        obscreate.setConcept(selectManyField.getConcept());
                        obscreateLocal.setConcept(selectManyField.getConcept());
                        obscreateLocal.setQuestionLabel(selectManyField.getQuestionLabel());
                        obscreate.setValue(answer.getConcept());
                        obscreateLocal.setValue(answer.getConcept());
                        LocalDateTime localDateTime = new LocalDateTime();
                        obscreateLocal.setAnswerLabel(answer.getLabel());
                        obscreate.setObsDatetime(localDateTime.toString());
                        obscreateLocal.setObsDatetime(localDateTime.toString());
                        obscreate.setPerson(mPatient.getUuid());
                        obscreateLocal.setPerson(mPatient.getUuid());
                        observations.add(obscreate);
                        observationsLocal.add(obscreateLocal);
                    }

                }
            }
            encounter.setPatient(mPatient);
            EncounterType encounterType = new EncounterType();
            encounterType.setDisplay(mFormname);
            encounterType.setUuid(mEncountertype);
            encounter.setEncounterType(encounterType);
            List<Resource> resources = new ArrayList<>();
            Resource resource = new Resource();
            resource.setDisplay(mProvider);
            resource.setUuid(mProviderUUID);
            resources.add(resource);

            Form form = new Form();
            form.setDisplay(mFormname);
            FormResource formResource = getFormResourceByName(mFormnameRaw);
            form.setUuid(getFormResourceByName(mFormnameRaw).getUuid());



            encounter.setEncounterProviders(resources);
            encounter.setPatientUUID(mPatient.getUuid());


            encountercreate.setObservations(observations);
            encountercreate.setObservationsLocal(observationsLocal);
            encountercreate.setFormname(mFormname);
            encountercreate.setFormnameRaw(mFormnameRaw);
            encountercreate.setPatientId(mPatientID);
            encountercreate.setEncounterDatetime(mEncounterDate+" 23:00:00");
            encountercreate.setLocation(null);
            encountercreate.setFormUuid(getFormResourceByName(mFormnameRaw).getUuid());
            encountercreate.setObslist();
            encountercreate.setVisitDate(mEncounterDate+" 00:00:00");
            encountercreate.setObslistLocal();
            if (mFormname.equals("Child Birth Registration")){
                encountercreate.setInfantName(observations.get(0).getValue());
            }
            encountercreate.save();



            if(!mPatient.isSynced()) {
                mPatient.addEncounters(encountercreate.getId());
//                new PatientDAO().updatePatient(mPatient.getId(),mPatient);
//                new EncounterDAO().saveGeneralEncounter(encounter);
                ToastUtil.error("Patient not yet registered. Form data is saved locally " +
                        "and will sync when internet connection is restored. ");
                mFormDisplayView.enableSubmitButton(true);
            }
            else {
                new EncounterService().addEncounter(encountercreate, new DefaultResponseCallbackListener() {
                    @Override
                    public void onResponse() {
                        mFormDisplayView.enableSubmitButton(true);
                    }
                    @Override
                    public void onErrorResponse(String errorMessage) {
                        mFormDisplayView.showToast(errorMessage);
                        mFormDisplayView.enableSubmitButton(true);
                    }
                });
                mFormDisplayView.quitFormEntry();
            }
        }
        else {
            mFormDisplayView.enableSubmitButton(true);
        }
    }
}
