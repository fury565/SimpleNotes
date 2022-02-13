package com.example.simplenotes

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.FragmentTransaction
import kotlinx.android.synthetic.main.fragment_settings.*

class Settings : Fragment(),AdapterView.OnItemSelectedListener {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE)
        val view= inflater.inflate(R.layout.fragment_settings, container, false)
        val categoryInput=view.findViewById<EditText>(R.id.categoryName)
        val categoryAdder=view.findViewById<Button>(R.id.addCategory)
        val categoryRemover=view.findViewById<Button>(R.id.removeCategory)
        val idDisplay=view.findViewById<TextView>(R.id.tvID)
        val idInput=view.findViewById<EditText>(R.id.id_input)
        val idSetter=view.findViewById<Button>(R.id.setId)
        val categoryOption=view.findViewById<Spinner>(R.id.spinnerSettings)

        val displayCategories=sharedPref?.getStringSet("CategoryList", setOf())?.toMutableList()!!
        displayCategories.remove("All")
        displayCategories.remove("Other")
        categoryOption.adapter= context?.let { ArrayAdapter<String>(it,android.R.layout.simple_spinner_dropdown_item,displayCategories) }
        categoryOption.onItemSelectedListener=this

        idDisplay.setText(sharedPref.getString("ID","null"))
        idSetter.setOnClickListener {
            if(id_input.text.toString()!="") {
                with(sharedPref.edit()) {
                    this?.putString("ID", idInput.text.toString())
                }?.apply()
                idDisplay.setText(sharedPref.getString("ID", "null"))
            }
        }
        val noteCategories=sharedPref.getStringSet("CategoryList", setOf())?.toMutableList()!!
        categoryAdder.setOnClickListener {
            val newCategory=categoryInput.text.toString()
            if(newCategory!="") {
                noteCategories.add(newCategory)
                with(sharedPref.edit()){
                    this?.putStringSet("CategoryList", noteCategories.toMutableSet())
                }?.apply()
            }
            reloadSettings()
        }
        categoryRemover.setOnClickListener {
            if(categoryOption.selectedItem!=null){
                noteCategories.remove(categoryOption.selectedItem.toString())
                with(sharedPref.edit()){
                    this?.putStringSet("CategoryList", noteCategories.toMutableSet())
                }?.apply()
                reloadSettings()
            }
        }
        return view
    }
    override fun onItemSelected(parent: AdapterView<*>, view: View?, pos: Int, id: Long) {
        parent.getItemAtPosition(pos)
        (parent.getChildAt(0) as TextView).setTextColor(0xFFFF0000.toInt())
    }

    override fun onNothingSelected(parent: AdapterView<*>) {
        parent.getItemAtPosition(0)
        (parent.getChildAt(0) as TextView).setTextColor(0xFFFF0000.toInt())
    }
    private fun reloadSettings(){
        val nextFragment = Settings()
        val fragmentTransaction: FragmentTransaction? =
            activity?.supportFragmentManager?.beginTransaction()
        fragmentTransaction?.replace(R.id.fragContainer, nextFragment)
        fragmentTransaction?.commit()
    }
}