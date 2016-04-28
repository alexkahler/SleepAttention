package dk.aau.student.b211.sleepattention;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;


public class SettingsActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();

    }

    public static class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {
        private SharedPreferences sharedPreferences;
        private AlarmReceiver alarmReceiver = new AlarmReceiver();

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            addPreferencesFromResource(R.xml.pref_general);
            sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());

        }

        @Override
        public void onResume() {
            super.onResume();
            sharedPreferences.registerOnSharedPreferenceChangeListener(this);
            getActivity().registerReceiver(alarmReceiver, new IntentFilter(getString(R.string.ACTION_SET_NOTIFICATIONS)));
        }

        @Override
        public void onPause() {
            super.onPause();
            sharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
            getActivity().unregisterReceiver(alarmReceiver);
        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            Preference preference = findPreference(key);
            if (preference instanceof CheckBoxPreference) {
                Intent alarmReceiverIntent = new Intent(getString(R.string.ACTION_SET_NOTIFICATIONS));
                getActivity().getApplicationContext().sendBroadcast(alarmReceiverIntent);
            } else if (preference instanceof TimePreference) {
                Intent alarmReceiverIntent = new Intent(getString(R.string.ACTION_SET_NOTIFICATIONS));
                getActivity().getApplicationContext().sendBroadcast(alarmReceiverIntent);
            }
        }
    }
}
