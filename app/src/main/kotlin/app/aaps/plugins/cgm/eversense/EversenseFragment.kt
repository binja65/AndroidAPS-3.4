package app.aaps.plugins.cgm.eversense

import app.aaps.common.utils.ToastUtils
import android.os.Bundle
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import app.aaps.R

class EversenseFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        // Load the main menu XML
        setPreferencesFromResource(R.xml.eversense_preferences, rootKey)

        setupListeners()
    }

    private fun setupListeners() {
        // Handle "Calibrate" click
        findPreference<Preference>("eversense_calibrate")?.setOnPreferenceClickListener {
            ToastUtils.showToastInMainThread(context, "Opening Calibration Interface...")
            true
        }

        // Handle "Connect" click
        findPreference<Preference>("eversense_connect")?.setOnPreferenceClickListener {
            ToastUtils.showToastInMainThread(context, "Scanning for Smart Transmitter...")
            true
        }
    }
}