/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 * <p>
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */

package org.openmrs.mobile.activities.formdisplay;

import android.widget.LinearLayout;

import org.openmrs.mobile.activities.BasePresenter;
import org.openmrs.mobile.bundle.FormFieldsWrapper;
import org.openmrs.mobile.models.Page;
import org.openmrs.mobile.models.Question;
import org.openmrs.mobile.models.Section;
import org.openmrs.mobile.utilities.InputField;
import org.openmrs.mobile.utilities.NameParcelable;
import org.openmrs.mobile.utilities.SelectManyFields;
import org.openmrs.mobile.utilities.SelectOneField;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class FormDisplayPagePresenter extends BasePresenter implements FormDisplayContract.Presenter.PagePresenter {

    private FormDisplayContract.View.PageView mFormDisplayPageView;
    private Page mPage;
    private ArrayList<FormFieldsWrapper> mFormFieldsWrapper = null;
    ;
    private List<Page> pageList = new LinkedList<>();
    public List<List<InputField>> mInputFields = new LinkedList<>();
    public List<List<SelectOneField>> mSelectOneField = new LinkedList<>();
    public List<List<SelectManyFields>> mSelectManyFields = new LinkedList<>();
    private String encounterDate = null;
    private String personNames = null;

    public FormDisplayPagePresenter(FormDisplayContract.View.PageView mFormPageView, Page page) {
        this.mFormDisplayPageView = mFormPageView;
        this.mPage = page;
        this.mFormDisplayPageView.setPresenter(this);

    }
    public FormDisplayPagePresenter(FormDisplayContract.View.PageView mFormPageView, Page page, String personNames) {
        this.mFormDisplayPageView = mFormPageView;
        this.mPage = page;
        this.mFormDisplayPageView.setPresenter(this);
        this.personNames = personNames;
    }
    public FormDisplayPagePresenter(FormDisplayContract.View.PageView mFormPageView, Page page, FormFieldsWrapper formFieldsWrapper) {
        this.mFormDisplayPageView = mFormPageView;
        this.mPage = page;
        this.mFormDisplayPageView.setPresenter(this);
        setViewFields(formFieldsWrapper);
    }

    public FormDisplayPagePresenter(FormDisplayContract.View.PageView mFormPageView, Page page, ArrayList<FormFieldsWrapper> formFieldsWrapper, List<Page> pageList, String encounterDate) {
        this.mFormDisplayPageView = mFormPageView;
        this.pageList = pageList;
        this.mPage = page;
        this.mFormDisplayPageView.setPresenter(this);
        this.mFormFieldsWrapper = formFieldsWrapper;
        this.encounterDate = encounterDate;
    }

    private void setViewFields(FormFieldsWrapper formFieldsWrapper) {
        if (formFieldsWrapper != null) {
            mFormDisplayPageView.setInputFields(formFieldsWrapper.getInputFields());
            mFormDisplayPageView.setSelectOneFields(formFieldsWrapper.getSelectOneFields());
            mFormDisplayPageView.setSelectManyFields(formFieldsWrapper.getSelectManyFields());
//            mInputFields = formFieldsWrapper.getInputFields();
//            mSelectOneField = formFieldsWrapper.getSelectOneFields();
        }
    }

    @Override
    public void subscribe() {
        if (this.mFormFieldsWrapper == null) {
            //For follow up
            List<Section> sectionList = mPage.getSections();
            for (Section section : sectionList) {
                addSection(section);
            }
        } else {
            for (Page page : this.pageList) {
                mInputFields.add(this.mFormFieldsWrapper.get(pageList.indexOf(page)).getInputFields());
                mSelectOneField.add(this.mFormFieldsWrapper.get(pageList.indexOf(page)).getSelectOneFields());
                mSelectManyFields.add(this.mFormFieldsWrapper.get(pageList.indexOf(page)).getSelectManyFields());

            }
            List<Section> sectionList = mPage.getSections();
            for (Section section : sectionList) {
                addSection(section);
            }
        }


    }

    private void addSection(Section section) {
        LinearLayout sectionLinearLayout = mFormDisplayPageView.createSectionLayout(section.getLabel());
        if(personNames != null && section.getLabel().equals("Child Follow up Information")){
            mFormDisplayPageView.createSpecialSelectQuestionDropdown(sectionLinearLayout, personNames);
        }
        mFormDisplayPageView.attachSectionToView(sectionLinearLayout);
        if ((mInputFields == null || mInputFields.isEmpty()) && (mSelectOneField == null || mSelectOneField.isEmpty()) && (mSelectManyFields == null || mSelectManyFields.isEmpty())) {
            for (Question question : section.getQuestions()) {
                addQuestion(question, sectionLinearLayout);
            }
        } else {
            for (Question question : section.getQuestions()) {
                addQuestionEdit(question, sectionLinearLayout);
            }
        }
    }


    private void addQuestion(Question question, LinearLayout sectionLinearLayout) {
        if (question.getQuestionOptions().getRendering().equals("group")) {
            LinearLayout questionLinearLayout = mFormDisplayPageView.createQuestionGroupLayout(question.getLabel());
            mFormDisplayPageView.attachQuestionToSection(sectionLinearLayout, questionLinearLayout);

            for (Question subquestion : question.getQuestions()) {
                addQuestion(subquestion, questionLinearLayout);
            }
        }

        if (question.getQuestionOptions().getRendering().equals("number")) {
            mFormDisplayPageView.createAndAttachNumericQuestionEditText(question, sectionLinearLayout);
        }
        if (question.getQuestionOptions().getRendering().equals("date")) {
            mFormDisplayPageView.createAndAttachDateQuestionEditText(question, sectionLinearLayout);
        }
        if (question.getQuestionOptions().getRendering().equals("text") | question.getQuestionOptions().getRendering().equals("textarea")) {
            mFormDisplayPageView.createAndAttachTextQuestionEditText(question, sectionLinearLayout);
        }
        if (question.getQuestionOptions().getRendering().equals("select")) {
//            if(personNames != null){
                mFormDisplayPageView.createAndAttachSelectQuestionDropdown(question, sectionLinearLayout);
//            }else
//            {
//                mFormDisplayPageView.createAndAttachSelectQuestionDropdown(question, sectionLinearLayout);
//            }
        }
        if (question.getQuestionOptions().getRendering().equals("check")) {
            mFormDisplayPageView.createAndAttachSelectQuestionCheckBox(question, sectionLinearLayout);
        }
        if (question.getQuestionOptions().getRendering().equals("radio")) {
            mFormDisplayPageView.createAndAttachSelectQuestionRadioButton(question, sectionLinearLayout);
        }
    }

    private void addQuestionEdit(Question question, LinearLayout sectionLinearLayout) {
        if (question.getQuestionOptions().getRendering().equals("group")) {
            LinearLayout questionLinearLayout = mFormDisplayPageView.createQuestionGroupLayout(question.getLabel());
            mFormDisplayPageView.attachQuestionToSection(sectionLinearLayout, questionLinearLayout);

            for (Question subquestion : question.getQuestions()) {
                addQuestionEdit(subquestion, questionLinearLayout);
            }
        }

        if (question.getQuestionOptions().getRendering().equals("number")) {
            for (List<InputField> inputFields : mInputFields) {
                for (InputField inputField : inputFields) {
                    if (question.getQuestionOptions().getConcept().equals(inputField.getConcept())) {
                        mFormDisplayPageView.editAndAttachNumericQuestionEditText(question, sectionLinearLayout, inputField.getValueAll());
                    }
                }
            }

        }
        if (question.getQuestionOptions().getRendering().equals("date")) {
            for (List<InputField> inputFields : mInputFields) {
                for (InputField inputField : inputFields) {
                    if (question.getQuestionOptions().getConcept().equals(inputField.getConcept())) {
                        if (question.getType().equals("encounterDate")) {
                            mFormDisplayPageView.editAndAttachDateQuestionEditText(question, sectionLinearLayout, this.encounterDate);
                        } else {
                            mFormDisplayPageView.editAndAttachDateQuestionEditText(question, sectionLinearLayout, inputField.getValueAll());
                        }
                    }
                }
            }

        }
//        if (question.getQuestionOptions().getRendering().equals("date") && question.getType().equals("encounterDate")) {
//            for (List<InputField> inputFields : mInputFields) {
//                for (InputField inputField : inputFields) {
//                    if (question.getQuestionOptions().getConcept().equals(inputField.getConcept())) {
//                        mFormDisplayPageView.editAndAttachDateQuestionEditText(question, sectionLinearLayout, this.encounterDate);
//                    }
//                }
//            }
//
//        }
        if (question.getQuestionOptions().getRendering().equals("text")) {
            for (List<InputField> inputFields : mInputFields) {
                for (InputField inputField : inputFields) {
                    if (question.getQuestionOptions().getConcept().equals(inputField.getConcept())) {
                        mFormDisplayPageView.editAndAttachTextQuestionEditText(question, sectionLinearLayout, inputField.getValueAll());
                    }
                }
            }

        }
        if (question.getQuestionOptions().getRendering().equals("select")) {
            for (List<SelectOneField> selectOneFields : mSelectOneField) {
                for (SelectOneField selectOneField : selectOneFields) {
                    if (question.getQuestionOptions().getConcept().equals(selectOneField.getConcept())) {

                        mFormDisplayPageView.editAndAttachSelectQuestionDropdown(question, sectionLinearLayout, selectOneField);
                    }
                }
            }

        }
        if (question.getQuestionOptions().getRendering().equals("check")) {

            for (List<SelectManyFields> selectManyFields : mSelectManyFields) {
                for (SelectManyFields selectManyField : selectManyFields) {
                    if (question.getQuestionOptions().getConcept().equals(selectManyField.getConcept())) {
                        mFormDisplayPageView.editAndAttachSelectQuestionCheckBox(question, sectionLinearLayout, selectManyField);
                    }
                }
            }
        }
        if (question.getQuestionOptions().getRendering().equals("radio")) {
            for (List<SelectOneField> selectOneFields : mSelectOneField) {
                for (SelectOneField selectOneField : selectOneFields) {
                    if (question.getQuestionOptions().getConcept().equals(selectOneField.getConcept())) {
                        mFormDisplayPageView.editAndAttachSelectQuestionRadioButton(question, sectionLinearLayout, selectOneField);
                    }
                }
            }
        }
    }

}
