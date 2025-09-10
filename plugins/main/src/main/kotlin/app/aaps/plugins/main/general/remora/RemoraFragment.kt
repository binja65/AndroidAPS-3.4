package app.aaps.plugins.main.general.remora

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import app.aaps.plugins.main.R
import dagger.android.support.DaggerFragment
import de.tebbeubben.remora.lib.ui.RemoraLibActivity

class RemoraFragment : DaggerFragment() {

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.remora_fragment, container, false)
        val launchActivityButton: Button = view.findViewById(R.id.launch_activity_button)
        launchActivityButton.setOnClickListener {
            val intent = Intent(activity, RemoraLibActivity::class.java)
            startActivity(intent)
        }
        return view
    }
}