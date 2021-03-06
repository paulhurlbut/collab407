package com.DPAC.collabormate;

import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import java.util.List;

public class Preferences extends PreferenceActivity {
    @Override
    public void onBuildHeaders(List<Header> target) {
        loadHeadersFromResource(R.xml.pref_headers, target);
    }

    @Override
    protected boolean isValidFragment(String fragmentName) {
        if (Display.class.getName().equals(fragmentName)) {
            return(true);
        }
        return(false);
    }

    public static class Display extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_display);
            //addPreferencesFromResource(R.xml.pref_general);
            //addPreferencesFromResource(R.xml.pref_notification);
        }
    }
}