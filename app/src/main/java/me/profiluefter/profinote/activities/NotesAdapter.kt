package me.profiluefter.profinote.activities

import android.annotation.SuppressLint
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.PopupMenu
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import me.profiluefter.profinote.R
import me.profiluefter.profinote.data.entities.Note
import me.profiluefter.profinote.databinding.RecyclerViewItemBinding

class NotesAdapter(notes: List<Note>, private val activity: MainActivity) :
    RecyclerView.Adapter<NotesAdapter.ViewHolder>() {
    var notes: List<Note> = notes
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(
        RecyclerViewItemBinding.inflate(LayoutInflater.from(parent.context), parent, false),
        activity
    )

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.position = position
        holder.binding.note = notes[position]
    }

    override fun getItemCount() = notes.size

    class ViewHolder(val binding: RecyclerViewItemBinding, activity: MainActivity) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            val view = binding.root
            view.setOnLongClickListener {
                val menu = PopupMenu(activity, view)
                menu.inflate(R.menu.note_action_menu)
                menu.setOnMenuItemClickListener {
                    when(it.itemId) {
                        R.id.note_action_edit -> activity.onEditNote(this.adapterPosition)
                        R.id.note_action_details -> activity.onShowNoteDetails(this.adapterPosition)
                        R.id.note_action_delete -> activity.onDeleteNote(this.adapterPosition, view)
                    }
                    true
                }
                menu.show()
                true
            }
            view.findViewById<CheckBox>(R.id.itemDone).setOnCheckedChangeListener { _, checked ->
                activity.setNoteChecked(this.adapterPosition, checked)
            }
        }
    }
}

@BindingAdapter("drawableEndCompat")
fun setDrawableEndCompat(view: TextView, drawable: Drawable?) {
    val (start, top, _, bottom) = view.compoundDrawables
    view.setCompoundDrawablesRelativeWithIntrinsicBounds(start, top, drawable, bottom)
}