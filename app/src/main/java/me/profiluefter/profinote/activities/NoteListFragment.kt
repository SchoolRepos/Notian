package me.profiluefter.profinote.activities

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.CompoundButton
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.menu.MenuBuilder
import androidx.appcompat.widget.PopupMenu
import androidx.core.content.edit
import androidx.core.view.doOnPreDraw
import androidx.core.widget.doOnTextChanged
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.DividerItemDecoration
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.android.material.transition.MaterialElevationScale
import com.google.android.material.transition.MaterialFadeThrough
import me.profiluefter.profinote.R
import me.profiluefter.profinote.data.entities.Note
import me.profiluefter.profinote.databinding.FragmentNoteListBinding
import me.profiluefter.profinote.models.MainViewModel

class NoteListFragment : Fragment() {
    private val viewModel: MainViewModel by activityViewModels()
    private val args: NoteListFragmentArgs by navArgs()

    private val preferences by lazy { PreferenceManager.getDefaultSharedPreferences(requireContext()) }

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

        if (!preferences.contains("username") || !preferences.contains("password")) {
            Log.i(logTag, "User not authenticated. Redirecting to login...")
            findNavController().navigate(NoteListFragmentDirections.startLogin())
        }

        val lastListID = preferences.getInt("listID", -1)
        if (lastListID != -1)
            viewModel.selectList(lastListID)

        floatingActionButton.setOnClickListener {
            this@NoteListFragment.apply {
                exitTransition = MaterialElevationScale(true).apply {
                    duration = resources.getInteger(R.integer.notian_animation_time).toLong()
                }
                reenterTransition = MaterialElevationScale(true).apply {
                    duration = resources.getInteger(R.integer.notian_animation_time).toLong()
                }
            }
            findNavController().navigate(NoteListFragmentDirections.openEditor(Note()))
        }

        val adapter = NotesAdapter(
            viewModel.sortedList.value?.notes ?: emptyList(),
            ::showNoteDetails,
            ::createPopupMenu,
            ::checkedChange
        )
        this.notes.adapter = adapter
        this.notes.addItemDecoration(
            DividerItemDecoration(
                requireContext(),
                DividerItemDecoration.VERTICAL
            )
        )

        (requireActivity() as AppCompatActivity).supportActionBar?.title =
            viewModel.sortedList.value?.name ?: getString(R.string.app_name)

        viewModel.sortedList.observe(viewLifecycleOwner) {
            adapter.notes = it?.notes ?: emptyList()

            @Suppress("USELESS_ELVIS") // `it` is nullable!
            (requireActivity() as AppCompatActivity).supportActionBar?.title =
                it.name ?: getString(R.string.app_name)
        }

        if (args.listID != -1) {
            viewModel.selectList(args.listID)
            preferences.edit {
                putInt("listID", args.listID)
            }
        }

        setHasOptionsMenu(true)

        enterTransition = MaterialFadeThrough().apply {
            duration = resources.getInteger(R.integer.notian_animation_time).toLong()
        }
        postponeEnterTransition()
        this.root.doOnPreDraw { startPostponedEnterTransition() }
    }.root

    private fun showNoteDetails(note: Note) = View.OnClickListener {
        exitTransition = MaterialElevationScale(false).apply {
            duration = resources.getInteger(R.integer.notian_animation_time).toLong()
        }
        reenterTransition = MaterialElevationScale(true).apply {
            duration = resources.getInteger(R.integer.notian_animation_time).toLong()
        }
        findNavController().navigate(
            NoteListFragmentDirections.showDetails(viewModel.refreshNote(note)),
            FragmentNavigatorExtras(it to "fragment_note_details_transition_name")
        )
    }

    private fun checkedChange(note: Note) = CompoundButton.OnCheckedChangeListener { _, checked ->
        if (note.done == checked) return@OnCheckedChangeListener
        viewModel.setNoteChecked(note, checked)
    }

    private fun createPopupMenu(note: Note) = View.OnLongClickListener { view ->
        fun deleteNote() {
            viewModel.deleteNote(note)

            Snackbar.make(requireView(), R.string.note_deleted, Snackbar.LENGTH_SHORT).apply {
                animationMode = Snackbar.ANIMATION_MODE_SLIDE
                setAction(R.string.undo) { viewModel.addNote(note) }
                show()
            }
        }

        val menu = PopupMenu(requireContext(), view)
        menu.inflate(R.menu.note_action_menu)
        menu.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.note_action_edit -> findNavController().navigate(
                    NoteListFragmentDirections.openEditor(viewModel.refreshNote(note))
                )
                R.id.note_action_details -> showNoteDetails(note).onClick(view)
                R.id.note_action_delete -> deleteNote()
            }
            true
        }
        menu.show()
        true
    }

    @SuppressLint("RestrictedApi") // https://stackoverflow.com/q/48607853
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main_menu, menu)

        if (menu is MenuBuilder) {
            menu.setOptionalIconsVisible(true)
        }

        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val nav = findNavController()
        when (item.itemId) {
            R.id.menu_new_task -> nav.navigate(NoteListFragmentDirections.openEditor(Note()))
            R.id.menu_synchronize -> viewModel.synchronize()
            R.id.menu_new_list -> createNewTaskList()
            R.id.menu_preferences -> nav.navigate(NoteListFragmentDirections.openSettings())
            else -> return false
        }
        return true
    }

    private fun createNewTaskList() {
        val container = TextInputLayout(requireContext()).apply {
            setStartIconDrawable(R.drawable.baseline_new_label_24)
            setHint(R.string.list_name)
        }
        val editText = TextInputEditText(container.context)
        container.addView(editText)

        val dialog = MaterialAlertDialogBuilder(requireContext())
            .setView(container)
            .setNegativeButton(R.string.cancel) { dialog, _ ->
                dialog.cancel()
            }
            .setPositiveButton(R.string.create) { dialog, _ ->
                val listName = editText.text.toString()
                viewModel.addList(listName, preferences)
                dialog.dismiss()
            }
            .show()

        val positiveButton = dialog.getButton(DialogInterface.BUTTON_POSITIVE)
        positiveButton.isEnabled = false
        editText.doOnTextChanged { text, _, _, _ ->
            positiveButton.isEnabled = text?.isNotBlank() ?: false
        }

        editText.requestFocus()
    }
}