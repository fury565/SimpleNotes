package com.example.grocerylist

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.internal.ContextUtils.getActivity
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_main.view.*
import kotlinx.android.synthetic.main.note_holder.view.*

class NoteAdapter(private val items: MutableList<Note>,private val context: Context):
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):
            RecyclerView.ViewHolder {
        return NoteViewHolder(

            LayoutInflater.from(parent.context).inflate(R.layout.note_holder, parent,
                false),context
        )
    }
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder,
                                  position: Int) {
        when(holder) {
            is NoteViewHolder -> {
                holder.bind(items[position])
            }
        }
        holder.itemView.apply {
            RemoveButton.setOnClickListener{
                deleteFromDb(position)
                deleteItem(position)
            }
        }
    }
    private fun deleteFromDb(pos:Int){
        val sharedPref = (context as FragmentActivity).getPreferences(Context.MODE_PRIVATE)
        val db=Firebase.firestore
        var noteQuery = sharedPref.getString("ID","null")?.let {
            db.collection("User").document(it).collection("Note").whereEqualTo("Title",items[pos].Title).whereEqualTo("Content",items[pos].Content) }
        noteQuery?.get()?.addOnCompleteListener {
            it.result?.documents?.forEach {
                it.reference.delete()
            }
        }
    }
    private fun deleteItem(pos:Int){
        items.removeAt(pos)
        notifyDataSetChanged()
    }
    override fun getItemCount(): Int {
        return items.size
    }
    class NoteViewHolder constructor(
        itemView: View,context: Context
    ): RecyclerView.ViewHolder(itemView) {
        private val title: TextView =
            itemView.findViewById(R.id.tvNoteTitle)
        private val content: TextView=
            itemView.findViewById(R.id.tvNoteContent)
        private val activity:FragmentActivity= context as FragmentActivity
        fun bind(notes: Note) {
            title.text=notes.Title
            content.text=notes.Content
            title.setOnClickListener {
                goToEditor(notes)
            }
            content.setOnClickListener {
                goToEditor(notes)
            }
        }
        fun goToEditor(notes:Note){
            val nextFragment = NoteEditor()
            val bundle = Bundle()
            bundle.putBoolean("New",false)
            bundle.putString("Title",notes.Title)
            bundle.putString("Content",notes.Content)
            nextFragment.arguments = bundle
            val fragmentTransaction: FragmentTransaction? =
                activity.supportFragmentManager?.beginTransaction()
            fragmentTransaction?.replace(R.id.fragContainer, nextFragment)
            fragmentTransaction?.addToBackStack(null)?.commit()
        }
    }
}