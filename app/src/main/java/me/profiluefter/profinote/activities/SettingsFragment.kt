package me.profiluefter.profinote.activities

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import me.profiluefter.profinote.R

class SettingsFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)
    }
}