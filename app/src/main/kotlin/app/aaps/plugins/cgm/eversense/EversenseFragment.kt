package app.aaps.plugins.cgm.eversense

import android.os.Bundle
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import app.aaps.R
import app.aaps.utils.ToastUtils

class EversenseFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.eversense_preferences, rootKey)
        setupListeners()
    }

    private fun setupListeners() {
        findPreference<Preference>("eversense_calibrate")?.setOnPreferenceClickListener {
            ToastUtils.showToastInMainThread(context, "Opening Calibration Interface...")
            true
        }

        findPreference<Preference>("eversense_connect")?.setOnPreferenceClickListener {
            ToastUtils.showToastInMainThread(context, "Scanning for Smart Transmitter...")
            true
        }
    }
}