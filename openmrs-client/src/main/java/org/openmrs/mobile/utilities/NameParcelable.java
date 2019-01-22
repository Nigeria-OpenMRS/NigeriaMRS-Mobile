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

package org.openmrs.mobile.utilities;

import android.os.Parcel;
import android.os.Parcelable;

import org.openmrs.mobile.models.Answer;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class NameParcelable implements Serializable, Parcelable {
    private String name;

    public NameParcelable(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
    }

    protected NameParcelable(Parcel in) {
        this.name = in.readString();
    }

    public static final Parcelable.Creator<InputField> CREATOR = new Parcelable.Creator<InputField>() {
        @Override
        public InputField createFromParcel(Parcel source) {
            return new InputField(source);
        }

        @Override
        public InputField[] newArray(int size) {
            return new InputField[size];
        }
    };
    public static ArrayList<NameParcelable> create(ArrayList<String> names) {
        ArrayList<NameParcelable> nameList = new ArrayList<>();
        for(String name: names){
            nameList.add(new NameParcelable(name));
        }
        return nameList;
    }
}
