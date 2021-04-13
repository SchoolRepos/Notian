package me.profiluefter.profinote.activities

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.navArgs
import com.google.android.material.transition.MaterialContainerTransform
import me.profiluefter.profinote.R
import me.profiluefter.profinote.databinding.FragmentNoteDetailsBinding
import me.profiluefter.profinote.models.MainViewModel
import me.profiluefter.profinote.themeColor

class NoteDetailsFragment : Fragment() {
    private val args by navArgs<NoteDetailsFragmentArgs>()
    private val viewModel by activityViewModels<MainViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = DataBindingUtil.inflate<FragmentNoteDetailsBinding>(
        inflater,
        R.layout.fragment_note_details,
        container,
        false
    ).apply {
        lifecycleOwner = this@NoteDetailsFragment
        layoutViewModel = viewModel
        note = args.note
    }.also { binding ->
        binding.noteDetailsDone.setOnCheckedChangeListener { _, checked ->
            viewModel.setNoteChecked(binding.note!!, checked)
        }

        sharedElementEnterTransition = MaterialContainerTransform().apply {
            drawingViewId = R.id.nav_host_fragment
            duration = resources.getInteger(R.integer.notian_animation_time).toLong()
            scrimColor = Color.TRANSPARENT
            setAllContainerColors(requireContext().themeColor(R.attr.colorSurface))
        }
    }.root
}