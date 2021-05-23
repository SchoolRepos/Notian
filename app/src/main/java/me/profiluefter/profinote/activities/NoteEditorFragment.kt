package me.profiluefter.profinote.activities

import android.Manifest
import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Criteria
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.getSystemService
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.transition.Slide
import com.google.android.material.transition.MaterialContainerTransform
import dagger.hilt.android.AndroidEntryPoint
import me.profiluefter.profinote.R
import me.profiluefter.profinote.databinding.FragmentNoteEditorBinding
import me.profiluefter.profinote.models.MainViewModel
import me.profiluefter.profinote.models.NoteEditorViewModel
import me.profiluefter.profinote.themeColor
import java.util.*

@AndroidEntryPoint
class NoteEditorFragment : Fragment() {
    private val args by navArgs<NoteEditorFragmentArgs>()

    private val viewModel: MainViewModel by activityViewModels()
    private val editor: NoteEditorViewModel by viewModels()

    private val gpsRequestCode = 1337

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View = DataBindingUtil.inflate<FragmentNoteEditorBinding>(
            inflater,
            R.layout.fragment_note_editor,
            container,
            false
    ).apply {
        editor.setInitialNote(args.note)

        lifecycleOwner = this@NoteEditorFragment
        fragment = this@NoteEditorFragment
        layoutViewModel = this@NoteEditorFragment.editor

        enterTransition = MaterialContainerTransform().apply {
            startView = requireActivity().findViewById(R.id.floatingActionButton)
            endView = root
            duration = resources.getInteger(R.integer.notian_animation_time).toLong()
            scrimColor = Color.TRANSPARENT
            containerColor = requireContext().themeColor(R.attr.colorSurface)
            startContainerColor = requireContext().themeColor(R.attr.colorSecondary)
            endContainerColor = requireContext().themeColor(R.attr.colorSurface)
        }
        returnTransition = Slide().apply {
            duration = resources.getInteger(R.integer.notian_animation_time).toLong()
            addTarget(root)
        }

        noteEditorLocationContainer.setEndIconOnClickListener {
            val permission = requireContext().checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)

            if (permission != PackageManager.PERMISSION_GRANTED) {
                requireActivity().requestPermissions(
                        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                        gpsRequestCode
                )
            } else {
                searchGPS()
            }
        }
    }.root

    @Suppress("JoinDeclarationAndAssignment") // IntelliJ Bug
    @SuppressLint("MissingPermission") // Only called if permission is granted
    private fun searchGPS() {
        val locationManager = requireContext().getSystemService<LocationManager>()!!

        lateinit var locationListener: LocationListener
        locationListener = LocationListener {
            Log.i("NoteEditorFragment", "Received location $it")
            editor.receivedGPS(it.latitude, it.longitude)
            locationManager.removeUpdates(locationListener)
        }

        val provider = locationManager.getBestProvider(Criteria().apply {
            this.isCostAllowed = false
            this.accuracy = Criteria.ACCURACY_FINE
        }, true)!!
        Log.i("NoteEditorFragment", "Using provider $provider")
        locationManager.requestLocationUpdates(provider, 0L, 0.0f, locationListener)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode != gpsRequestCode) return

        if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            searchGPS()
    }

    fun saveNote() {
        if (editor.localID == 0)
            viewModel.addNote(editor.note)
        else
            viewModel.setNote(editor.note)
        findNavController().navigateUp()
    }

    fun openTimePicker(view: View) {
        val calendar = Calendar.getInstance()
        val dialog = TimePickerDialog(
                requireContext(),
                { _, hour, minute -> editor.setTime(hour, minute) },
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                false
        )
        dialog.show()
    }

    fun openDatePicker(view: View) {
        val calendar = Calendar.getInstance()
        val dialog = DatePickerDialog(
                requireContext(),
                { _, year, month, day -> editor.setDate(day, month + 1, year) },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        )
        dialog.show()
    }
}