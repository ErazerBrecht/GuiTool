package com.ap.brecht.guitool;

import android.os.Bundle;
import android.preference.PreferenceFragment;

/**
 * Created by hannelore on 29/04/2015.
 */
public class SettingsFragment extends PreferenceFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);
    }

}
