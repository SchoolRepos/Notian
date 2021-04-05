package me.profiluefter.profinote.activities

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.navArgs
import me.profiluefter.profinote.R
import me.profiluefter.profinote.databinding.FragmentNoteDetailsBinding
import me.profiluefter.profinote.models.MainViewModel

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
        note = this@NoteDetailsFragment.viewModel.getNote(args.index)
    }.root
}