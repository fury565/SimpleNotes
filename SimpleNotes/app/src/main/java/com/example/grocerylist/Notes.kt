package com.example.grocerylist

import android.content.ContentValues.TAG
import android.content.Context
import android.nfc.Tag
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class Notes : Fragment(),AdapterView.OnItemSelectedListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_notes, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE)
        val options=view.findViewById<Spinner>(R.id.spinnerNotes)
        options.adapter= context?.let { ArrayAdapter<String>(it,android.R.layout.simple_spinner_dropdown_item,sharedPref?.getStringSet("CategoryList", setOf())?.toMutableList()!!) }
        options.onItemSelectedListener=this
        arguments?.getInt("Category",0)?.let { options.setSelection(it) }
        if(sharedPref?.getString("Category",null)=="All") {
            getQuery()
        }
        else
            getQueryWithCategory()

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
    override fun onItemSelected(parent: AdapterView<*>, view: View?, pos: Int, id: Long) {
        val selected=parent.getItemAtPosition(pos)
        //(parent.getChildAt(0) as TextView).setTextColor(0xFFFF0000.toInt())
        val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE)
        val count=sharedPref?.getInt("Reloaded",-1)!!
        with(sharedPref.edit()){
            this?.putString("Category", selected.toString())
            this?.putInt("Reloaded", count+1)
        }?.apply()
        if(count >0) {
            with(sharedPref.edit()){
                this?.putInt("Reloaded", 0)
            }?.apply()
            reloadNotes(findItemPos(selected.toString()))
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>) {
        parent.getItemAtPosition(0)
    }
    private fun findItemPos(value:String?):Int{
        val noteCategories=activity?.getPreferences(Context.MODE_PRIVATE)?.getStringSet("CategoryList", setOf())?.toMutableList()!!
        var counter=0
        noteCategories.forEach {
            if(it==value)
                return counter
            counter++
        }
        return 0
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
    private fun reloadNotes(category:Int){
        val nextFragment = Notes()
        val bundle = Bundle()
        bundle.putInt("Category",category)
        nextFragment.arguments = bundle
        val fragmentTransaction: FragmentTransaction? =
            activity?.supportFragmentManager?.beginTransaction()
        fragmentTransaction?.replace(R.id.fragContainer, nextFragment)
        fragmentTransaction?.commit()
    }
    private fun getQuery(){
        val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE)
        val db = Firebase.firestore
        sharedPref?.getString("ID", "null")?.let {
            db.collection("User").document(it).collection("Note").orderBy("Title")
                .addSnapshotListener { value, error ->
                    if (error == null) {
                        val values: MutableList<Note> =
                            value!!.toObjects(Note::class.java)
                        view?.findViewById<RecyclerView>(R.id.Note_holder)?.apply {
                            layoutManager =
                                LinearLayoutManager(activity)
                            adapter = NoteAdapter(values, context)
                        }
                    } else {
                        Log.e("FIRESTORE ERROR", error.message.toString())
                    }
                }
        }
    }
    private fun getQueryWithCategory(){
        val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE)
        val db = Firebase.firestore
        sharedPref?.getString("ID", "null")?.let {
            db.collection("User").document(it).collection("Note").orderBy("Title").whereEqualTo("Category",
                sharedPref.getString("Category","Home")!!
            )
                .addSnapshotListener { value, error ->
                    if (error == null) {
                        val values: MutableList<Note> =
                            value!!.toObjects(Note::class.java)
                        view?.findViewById<RecyclerView>(R.id.Note_holder)?.apply {
                            layoutManager =
                                LinearLayoutManager(activity)
                            adapter = NoteAdapter(values, context)
                        }
                    } else {
                        Log.e("FIRESTORE ERROR", error.message.toString())
                    }
                }
        }
    }

}