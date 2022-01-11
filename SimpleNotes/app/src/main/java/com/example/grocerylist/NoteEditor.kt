package com.example.grocerylist

import android.content.ContentValues.TAG
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.SpannableStringBuilder
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.FragmentTransaction
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class NoteEditor : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view=inflater.inflate(R.layout.fragment_note_editor, container, false)
        val doneButton=view.findViewById<Button>(R.id.Done_button)
        val title=view.findViewById<EditText>(R.id.Note_Title)
        val content=view.findViewById<EditText>(R.id.Note_Content)
        val cancelButton=view.findViewById<Button>(R.id.Cancel_button)
        val isNew=arguments?.getBoolean("New")
        val oldTitleText=arguments?.getString("Title")
        val oldContentText=arguments?.getString("Content")

        if(oldTitleText!=null)
            title.text= SpannableStringBuilder(oldTitleText)
        if(oldContentText!=null)
            content.text=SpannableStringBuilder(oldContentText)
        doneButton.setOnClickListener {
            if(isNew == true){
                addNoteToDb(title.text.toString() ,content.text.toString())
                Log.d(TAG,"Added note")
            }
            else if (isNew==false){
                editNoteInDb(title.text.toString(),oldTitleText,content.text.toString(),oldContentText)
                Log.d(TAG,"Edited note")
            }
            goToNotes()
        }
        cancelButton.setOnClickListener {
            goToNotes()
        }
        return view;
    }
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater){
        inflater.inflate(R.menu.note_editor,menu)
        return super.onCreateOptionsMenu(menu,inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId==R.id.bullet){
            Log.d(TAG,"ajde implementiraj vise")
        }
        return super.onOptionsItemSelected(item)
    }
    private fun goToNotes(){
        activity?.supportFragmentManager?.popBackStackImmediate()
    }
    private fun addNoteToDb(title:String?,content:String?){
        val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE)
        val db=Firebase.firestore
        val note = hashMapOf(
            "Content" to content,
            "Title" to title
        )
        sharedPref?.getString("ID","null")?.let {
            db.collection("User").document(it).collection("Note")
                .add(note)
                .addOnSuccessListener { documentReference ->
                Log.d(TAG, "DocumentSnapshot added with ID: ${documentReference.id}")
            }
                .addOnFailureListener { e ->
                    Log.w(TAG, "Error adding document", e)
                }
        }
    }
    private fun editNoteInDb(title:String?,oldTitle:String?,content:String?,oldContent:String?){
        val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE)
        val db=Firebase.firestore
        var noteQuery = sharedPref?.getString("ID","null")
            ?.let { db.collection("User").document(it).collection("Note").whereEqualTo("Title",oldTitle).whereEqualTo("Content",oldContent) }
        noteQuery?.get()?.addOnCompleteListener {
            it.result?.documents?.forEach {
                it.reference.update("Title",title,"Content",content)
            }
        }
    }
}