package com.hryzx.submissiongithubuser3.fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings;

import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreference;

import com.hryzx.submissiongithubuser3.AlarmReceiver;
import com.hryzx.submissiongithubuser3.R;

import java.util.Locale;

public class MyPreferenceFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener {
    private final String defaultLang = Locale.getDefault().getDisplayLanguage();
    private String reminder;
    private String language;

    private SwitchPreference reminderPreference;
    private Preference languagePreference;

    private AlarmReceiver alarmReceiver;

    @Override
    public void onCreatePreferences(Bundle bundle, String s) {
        addPreferencesFromResource(R.xml.preferences);

        System.out.println(defaultLang);

        reminder = getString(R.string.key_reminder);
        language = getString(R.string.key_language);

        reminderPreference = findPreference(reminder);
        languagePreference = findPreference(language);

        SharedPreferences preferences = getPreferenceManager().getSharedPreferences();
        reminderPreference.setChecked(preferences.getBoolean(reminder, false));
        languagePreference.setSummary(preferences.getString(language, defaultLang));

        reminderPreference.setOnPreferenceChangeListener((preference, newValue) -> {
            if (reminderPreference.isChecked()) {
                alarmReceiver.cancelAlarm(requireContext());
                reminderPreference.setChecked(false);
            } else {
                alarmReceiver.setRepeatingAlarm(getContext(), "09:00", AlarmReceiver.EXTRA_MESSAGE);
                reminderPreference.setChecked(true);
            }
            return false;
        });

        languagePreference.setOnPreferenceClickListener(preference -> {
            Intent intent = new Intent(Settings.ACTION_LOCALE_SETTINGS);
            startActivity(intent);
            return false;
        });

        alarmReceiver = new AlarmReceiver();
    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(reminder))
            reminderPreference.setChecked(sharedPreferences.getBoolean(reminder, false));
        if (key.equals(language))
            languagePreference.setSummary(sharedPreferences.getString(language, defaultLang));
    }
}
