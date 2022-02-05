package com.example.simplenotes

import android.content.ContentValues.TAG
import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.util.Log
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.*
import android.widget.TextView


class NoteEditor : Fragment(),AdapterView.OnItemSelectedListener {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val sharedPref=activity?.getPreferences(Context.MODE_PRIVATE)
        val view=inflater.inflate(R.layout.fragment_note_editor, container, false)
        val doneButton=view.findViewById<Button>(R.id.Done_button)
        val title=view.findViewById<EditText>(R.id.Note_Title)
        val content=view.findViewById<EditText>(R.id.Note_Content)
        val cancelButton=view.findViewById<Button>(R.id.Cancel_button)
        val isNew=arguments?.getBoolean("New")
        val oldTitleText=arguments?.getString("Title")
        val oldContentText=arguments?.getString("Content")
        val oldCategoryID=arguments?.getInt("Category")
        val now=getCurrentDate()
        val noteCategories=sharedPref?.getStringSet("CategoryList", setOf())?.toMutableList()!!
        noteCategories.remove("All")
        val options=view.findViewById<Spinner>(R.id.spinnerEditor)
        options.adapter= context?.let { ArrayAdapter<String>(it,android.R.layout.simple_spinner_dropdown_item,noteCategories) }
        options.onItemSelectedListener=this

        if(oldTitleText!=null)
            title.text= SpannableStringBuilder(oldTitleText)
        if(oldContentText!=null)
            content.text=SpannableStringBuilder(oldContentText)
        if(oldCategoryID!=null)
            options.setSelection(oldCategoryID)
        doneButton.setOnClickListener {
            if(isNew == true){
                addNoteToDb(title.text.toString() ,content.text.toString(),options.selectedItem.toString(), now)
            }
            else if (isNew==false){
                val newCategory=options.selectedItem
                editNoteInDb(title.text.toString(),oldTitleText,content.text.toString(),oldContentText,newCategory.toString(), now)
            }
            goToNotes()
        }
        cancelButton.setOnClickListener {
            goToNotes()
        }
        val mode=context?.resources?.configuration?.uiMode?.and(Configuration.UI_MODE_NIGHT_MASK)
        when(mode){
            Configuration.UI_MODE_NIGHT_YES->{cancelButton.setBackgroundColor(resources.getColor(R.color.darkerTheme))
                doneButton.setBackgroundColor(resources.getColor(R.color.darkerTheme))}
            Configuration.UI_MODE_NIGHT_NO->{cancelButton.setBackgroundColor(resources.getColor(R.color.theme))
                doneButton.setBackgroundColor(resources.getColor(R.color.theme))}
        }
        return view
    }
    override fun onItemSelected(parent: AdapterView<*>, view: View?, pos: Int, id: Long) {
        parent.getItemAtPosition(pos)
    }

    override fun onNothingSelected(parent: AdapterView<*>) {
        parent.getItemAtPosition(0)
    }
    private fun goToNotes(){
        activity?.supportFragmentManager?.popBackStackImmediate()
    }
    private fun addNoteToDb(title:String?,content:String?,category:String?,modified:String?){
        val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE)
        val db=Firebase.firestore
        val note = hashMapOf(
            "Content" to content,
            "Title" to title,
            "Category" to category,
            "LastModified" to modified
        )
        sharedPref?.getString("ID","null")?.let {
            db.collection("User").document(it).collection("Note")
                .add(note)
                .addOnSuccessListener { documentReference ->
            }
                .addOnFailureListener { e ->
                    Log.w(TAG, "Error adding document", e)
                }
        }
    }
    private fun editNoteInDb(title:String?,oldTitle:String?,content:String?,oldContent:String?,category:String?,modified:String?){
        val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE)
        val db=Firebase.firestore
        val noteQuery = sharedPref?.getString("ID","null")
            ?.let { db.collection("User").document(it).collection("Note").whereEqualTo("Title",oldTitle).whereEqualTo("Content",oldContent) }
        noteQuery?.get()?.addOnCompleteListener {
            it.result?.documents?.forEach {
                it.reference.update("Title",title,"Content",content,"Category",category,"LastModified",modified)
            }
        }
    }
    private fun getCurrentDate():String?{
        val dateFormat=SimpleDateFormat("HH:mm dd-MM-yyyy")
        val currentTime=Date()
        return dateFormat.format(currentTime)
    }
}