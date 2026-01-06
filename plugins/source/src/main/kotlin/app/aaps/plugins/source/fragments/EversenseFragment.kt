package app.aaps.plugins.source.fragments

import android.os.Bundle
import android.widget.Toast
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import app.aaps.plugins.source.R
import com.nightscout.eversense.EversenseCGMPlugin
import com.nightscout.eversense.callbacks.EversenseScanCallback
import com.nightscout.eversense.callbacks.EversenseWatcher
import com.nightscout.eversense.enums.EversenseType
import com.nightscout.eversense.models.EversenseCGMResult
import com.nightscout.eversense.models.EversenseScanResult
import com.nightscout.eversense.models.EversenseState
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class EversenseFragment : PreferenceFragmentCompat(), EversenseWatcher {

    private var connectedPreference: Preference? = null
    private var batteryPreference: Preference? = null
    private var insertionPreference: Preference? = null
    private var lastSyncPreference: Preference? = null

    private val dateFormatter = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.eversense_preferences, rootKey)
        findPreferences()
        setupListeners()

        EversenseCGMPlugin.instance.addWatcher(this)
        EversenseCGMPlugin.instance.getCurrentState()?.let { onStateChanged(it) }
        onConnectionChanged(EversenseCGMPlugin.instance.isConnected())
    }

    private fun findPreferences() {
        connectedPreference = findPreference("eversense_information_connected")
        batteryPreference = findPreference("eversense_information_battery")
        insertionPreference = findPreference("eversense_information_insertion_date")
        lastSyncPreference = findPreference("eversense_information_last_sync")
    }

    private fun setupListeners() {
        findPreference<Preference>("eversense_calibrate")?.setOnPreferenceClickListener {
            // Replaced ToastUtils with standard Toast to fix error
            Toast.makeText(context, "Opening Calibration Interface...", Toast.LENGTH_SHORT).show()
            true
        }

        findPreference<Preference>("eversense_connect")?.setOnPreferenceClickListener {
            Toast.makeText(context, "Scanning for Smart Transmitter...", Toast.LENGTH_SHORT).show()

            // Try reconnect without scanning
            if (EversenseCGMPlugin.instance.connect(null)) {
                return@setOnPreferenceClickListener true
            }

            EversenseCGMPlugin.instance.startScan(object : EversenseScanCallback {
                override fun onResult(var0: EversenseScanResult) {
                    if (var0.name.startsWith("DEMO")) {
                        EversenseCGMPlugin.instance.connect(var0.device)
                    }
                }
            })
            true
        }
    }

    override fun onCGMRead(type: EversenseType, readings: List<EversenseCGMResult>) {}

    override fun onStateChanged(state: EversenseState) {
        batteryPreference?.let { it.summary = state.batteryPercentage.toString() + "%" }
        insertionPreference?.let { it.summary = dateFormatter.format(Date(state.insertionDate)) }
        lastSyncPreference?.let { it.summary = dateFormatter.format(Date(state.lastSync)) }
    }

    override fun onConnectionChanged(connected: Boolean) {
        // TODO: Find something better than this...
        connectedPreference?.let { it.summary = if (connected) "✅" else "❌" }
    }
}