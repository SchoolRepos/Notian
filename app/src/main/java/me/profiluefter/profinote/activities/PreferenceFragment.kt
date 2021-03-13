package me.profiluefter.profinote.activities

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import me.profiluefter.profinote.R

class PreferenceFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)
    }
}