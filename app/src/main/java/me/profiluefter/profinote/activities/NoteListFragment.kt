package me.profiluefter.profinote.activities

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.view.menu.MenuBuilder
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.DividerItemDecoration
import me.profiluefter.profinote.R
import me.profiluefter.profinote.databinding.FragmentNoteListBinding
import me.profiluefter.profinote.models.MainViewModel

class NoteListFragment : Fragment() {
    private val viewModel: MainViewModel by activityViewModels()

    private val logTag = "NoteListFragment"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = DataBindingUtil.inflate<FragmentNoteListBinding>(
        inflater,
        R.layout.fragment_note_list,
        container,
        false
    ).apply {
        lifecycleOwner = this@NoteListFragment
        layoutViewModel = this@NoteListFragment.viewModel

        val preferences = PreferenceManager.getDefaultSharedPreferences(requireContext())
        if(!preferences.contains("username") || !preferences.contains("password")) {
            Log.i(logTag, "User not authenticated. Redirecting to login...")
            findNavController().navigate(NoteListFragmentDirections.startLogin())
        }

        val adapter = NotesAdapter(viewModel.sortedList.value?.notes ?: emptyList(), requireActivity() as MainActivity)
        viewModel.sortedList.observe(viewLifecycleOwner) {
            adapter.notes = it?.notes ?: emptyList()
        }

        this.notes.adapter = adapter
        this.notes.addItemDecoration(
            DividerItemDecoration(
                this.notes.context,
                DividerItemDecoration.VERTICAL
            )
        )

        setHasOptionsMenu(true)
    }.root

    @SuppressLint("RestrictedApi") // https://stackoverflow.com/q/48607853
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main_menu, menu)

        if (menu is MenuBuilder) {
            menu.setOptionalIconsVisible(true)
        }

        super.onCreateOptionsMenu(menu, inflater)
    }
}