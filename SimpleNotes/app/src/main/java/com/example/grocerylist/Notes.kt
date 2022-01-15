package com.example.grocerylist

import android.content.ContentValues.TAG
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class Notes : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view=inflater.inflate(R.layout.fragment_notes, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE)
        val db = Firebase.firestore
        sharedPref?.getString("ID","null")?.let {
            db.collection("User").document(it).collection("Note").orderBy("Title")
                .addSnapshotListener { value, error ->
                    if(error == null) {
                        val values: MutableList<Note> =
                            value!!.toObjects(Note::class.java)
                        view.findViewById<RecyclerView>(R.id.Note_holder).apply {
                            layoutManager =
                                LinearLayoutManager(activity)
                            adapter = NoteAdapter(values,context)
                        }
                    } else {
                        Log.e("FIRESTORE ERROR", error.message.toString())
                    }
                }
        }
    }
    override fun onCreateOptionsMenu(menu: Menu,inflater: MenuInflater){
        inflater.inflate(R.menu.notes,menu)
        return super.onCreateOptionsMenu(menu,inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId==R.id.add){
            goToEditor(true)
        }
        else if(item.itemId==R.id.settings){
            goToSettings()
        }
        return super.onOptionsItemSelected(item)
    }
    private fun goToEditor(isNew:Boolean){
        val nextFragment = NoteEditor()
        val bundle = Bundle()
        bundle.putBoolean("New",isNew)
        nextFragment.arguments = bundle
        val fragmentTransaction: FragmentTransaction? =
            activity?.supportFragmentManager?.beginTransaction()
        fragmentTransaction?.replace(R.id.fragContainer, nextFragment)
        fragmentTransaction?.addToBackStack(null)?.commit()
    }
    private fun goToSettings(){
        val nextFragment = Settings()
        val fragmentTransaction: FragmentTransaction? =
            activity?.supportFragmentManager?.beginTransaction()
        fragmentTransaction?.replace(R.id.fragContainer, nextFragment)
        fragmentTransaction?.addToBackStack(null)?.commit()
    }
}