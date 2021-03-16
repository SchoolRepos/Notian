package me.profiluefter.profinote.activities

import android.annotation.SuppressLint
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import me.profiluefter.profinote.R
import me.profiluefter.profinote.databinding.RecyclerViewItemBinding
import me.profiluefter.profinote.data.Note

class NotesAdapter(notes: List<Note>, private val context: MainActivity) :
    RecyclerView.Adapter<NotesAdapter.ViewHolder>() {
    var notes: List<Note> = notes
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(
        RecyclerViewItemBinding.inflate(LayoutInflater.from(parent.context), parent, false),
        context
    )

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.position = position
        holder.binding.note = notes[position]
    }

    override fun getItemCount() = notes.size

    class ViewHolder(val binding: RecyclerViewItemBinding, context: MainActivity) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            val view = binding.root
            view.setOnLongClickListener {
                val menu = PopupMenu(context, view)
                menu.inflate(R.menu.note_action_menu)
                menu.setOnMenuItemClickListener {
                    when(it.itemId) {
                        R.id.note_action_edit -> context.onEditNote(this.adapterPosition)
                        R.id.note_action_details -> context.onShowNoteDetails(this.adapterPosition)
                        R.id.note_action_delete -> context.onDeleteNote(this.adapterPosition, view)
                    }
                    true
                }
                menu.show()
                true
            }
        }
    }
}

@BindingAdapter("drawableEndCompat")
fun setDrawableEndCompat(view: TextView, drawable: Drawable?) {
    val (start, top, _, bottom) = view.compoundDrawables
    view.setCompoundDrawablesRelativeWithIntrinsicBounds(start, top, drawable, bottom)
}