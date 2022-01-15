package com.example.grocerylist

import android.content.ContentValues.TAG
import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import android.view.View
import androidx.appcompat.app.ActionBar
import androidx.appcompat.widget.Toolbar
import java.util.*


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val sharedPref = this.getPreferences(Context.MODE_PRIVATE)
        if(sharedPref.getString("ID","null")=="null")
            with(sharedPref.edit()){
                putString("ID",UUID.randomUUID().toString())
            }.apply()

        sharedPref.getString("ID","nema")?.let { Log.d(TAG, it) }

        val mode=applicationContext.resources.configuration.uiMode.and(Configuration.UI_MODE_NIGHT_MASK)
        when(mode){
            Configuration.UI_MODE_NIGHT_YES->{supportActionBar!!.setBackgroundDrawable(ColorDrawable(Color.parseColor("#b37700")))}
            Configuration.UI_MODE_NIGHT_NO->{supportActionBar!!.setBackgroundDrawable(ColorDrawable(Color.parseColor("#F6CA9F")))}
        }
    }

    override fun onBackPressed() {
        val count=supportFragmentManager.backStackEntryCount
        if(count==0){
            super.onBackPressed()
        }
        else{
            supportFragmentManager.popBackStack()
        }
    }
}