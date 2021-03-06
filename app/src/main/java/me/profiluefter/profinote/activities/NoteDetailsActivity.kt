package me.profiluefter.profinote.activities

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import me.profiluefter.profinote.R
import me.profiluefter.profinote.databinding.ActivityNoteDetailsBinding
import me.profiluefter.profinote.models.Note

class NoteDetailsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding: ActivityNoteDetailsBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_note_details)

        binding.lifecycleOwner = this
        binding.note = intent.extras!!["note"] as Note
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}