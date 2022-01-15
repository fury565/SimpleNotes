package com.example.grocerylist

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView

class Settings : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE)
        val view= inflater.inflate(R.layout.fragment_settings, container, false)
        val idDisplay=view.findViewById<TextView>(R.id.tvID)
        val idInput=view.findViewById<EditText>(R.id.id_input)
        val idSetter=view.findViewById<Button>(R.id.setId)
        idDisplay.setText(sharedPref?.getString("ID","null"))
        idSetter.setOnClickListener {
            with(sharedPref?.edit()){
                this?.putString("ID", idInput.text.toString())
            }?.apply()
            idDisplay.setText(sharedPref?.getString("ID","null"))
        }
        return view
    }
}