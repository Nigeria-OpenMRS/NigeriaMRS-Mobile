/*
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */

package org.openmrs.mobile.activities.ancpatientlist;

import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import org.openmrs.mobile.R;
import org.openmrs.mobile.activities.ACBaseActivity;
import org.openmrs.mobile.activities.formentrypatientlist.FormEntryPatientListContract;
import org.openmrs.mobile.activities.formentrypatientlist.FormEntryPatientListFragment;
import org.openmrs.mobile.activities.formentrypatientlist.FormEntryPatientListPresenter;

public class ancPatientListActivity extends ACBaseActivity {

    private ancPatientListContract.Presenter mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_form_entry_patient_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Create fragment
        ancPatientListFragment ancPatientListFragment =
                (ancPatientListFragment) getSupportFragmentManager().findFragmentById(R.id.formEntryPatientListContentFrame);
        if (ancPatientListFragment == null) {
            ancPatientListFragment = ancPatientListFragment.newInstance();
        }
        if (!ancPatientListFragment.isActive()) {
            addFragmentToActivity(getSupportFragmentManager(),
                    ancPatientListFragment, R.id.formEntryPatientListContentFrame);
        }

        // Create the presenter
        mPresenter = new ancPatientListPresenter (ancPatientListFragment);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.form_entry_patient_list_menu, menu);
        final SearchView findPatientView;
        MenuItem mFindPatientMenuItem = menu.findItem(R.id.actionSearchRemoteFormEntry);
        findPatientView = (SearchView) mFindPatientMenuItem.getActionView();

        // Search function
        findPatientView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                findPatientView.clearFocus();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                mPresenter.setQuery(query);
                mPresenter.updatePatientsList();
                return true;
            }
        });
        return true;
    }

}
