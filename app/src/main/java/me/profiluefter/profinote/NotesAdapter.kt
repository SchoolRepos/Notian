package me.profiluefter.profinote

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import me.profiluefter.profinote.models.Note
import me.profiluefter.profinote.models.date
import me.profiluefter.profinote.models.overdue
import me.profiluefter.profinote.models.time

class NotesAdapter(notes: List<Note>, private val context: MainActivity) :
    RecyclerView.Adapter<NotesAdapter.ViewHolder>() {
    var notes: List<Note> = notes
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.recycler_view_item, parent, false),
        context
    )

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val note = notes[position]

        holder.title.text = note.title

        val warning = if (note.overdue) R.drawable.outline_error_outline_24 else 0
        holder.title.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, warning, 0)

        holder.date.text = note.date
        holder.time.text = note.time

        holder.description.text = note.description
    }

    override fun getItemCount() = notes.size

    class ViewHolder(view: View, context: MainActivity) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.itemTitle)
        val date: TextView = view.findViewById(R.id.itemDate)
        val time: TextView = view.findViewById(R.id.itemTime)
        val description: TextView = view.findViewById(R.id.itemDescription)

        init {
            view.setOnClickListener {
                context.onShowNoteDetails(this.adapterPosition)
            }

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