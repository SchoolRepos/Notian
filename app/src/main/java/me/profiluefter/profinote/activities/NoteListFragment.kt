package me.profiluefter.profinote.activities

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.*
import androidx.appcompat.view.menu.MenuBuilder
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import me.profiluefter.profinote.R
import me.profiluefter.profinote.databinding.FragmentNoteListBinding
import me.profiluefter.profinote.models.MainViewModel

class NoteListFragment : Fragment() {
    private val viewModel: MainViewModel by activityViewModels()

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
        viewModel = this@NoteListFragment.viewModel
    }.root.also { root ->
        val adapter = NotesAdapter(viewModel.notes.value!!, requireActivity() as MainActivity)
        viewModel.notes.observe(viewLifecycleOwner) {
            adapter.notes = it
        }

        val recyclerView = root.findViewById<RecyclerView>(R.id.notes)
        recyclerView.adapter = adapter
        recyclerView.addItemDecoration(
            DividerItemDecoration(
                recyclerView.context,
                DividerItemDecoration.VERTICAL
            )
        )

        setHasOptionsMenu(true)
    }

    @SuppressLint("RestrictedApi") // https://stackoverflow.com/q/48607853
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main_menu, menu)

        if (menu is MenuBuilder) {
            menu.setOptionalIconsVisible(true)
        }

        super.onCreateOptionsMenu(menu, inflater)
    }
}