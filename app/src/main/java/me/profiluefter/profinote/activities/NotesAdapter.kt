package me.profiluefter.profinote.activities

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.recyclerview.widget.RecyclerView
import me.profiluefter.profinote.data.entities.Note
import me.profiluefter.profinote.databinding.RecyclerViewItemBinding

class NotesAdapter(
    notes: List<Note>,
    private val showDetailsFactory: (Note) -> View.OnClickListener,
    private val menuListenerFactory: (Note) -> View.OnLongClickListener,
    private val checkedChangeListener: (Note) -> CompoundButton.OnCheckedChangeListener
) :
    RecyclerView.Adapter<NotesAdapter.ViewHolder>() {
    var notes: List<Note> = notes
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(
        RecyclerViewItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.note = notes[position]
    }

    override fun getItemCount() = notes.size

    inner class ViewHolder(
        val binding: RecyclerViewItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        init {
            val view = binding.root
            view.setOnClickListener { showDetailsFactory(binding.note!!).onClick(it) }
            view.setOnLongClickListener {
                menuListenerFactory(binding.note!!).onLongClick(it)
            }
            binding.itemDone.setOnCheckedChangeListener { button, checked ->
                checkedChangeListener(binding.note!!).onCheckedChanged(button, checked)
            }
        }
    }
}
