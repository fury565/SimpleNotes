package com.example.simplenotes

import android.content.Context
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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
        if(sharedPref.getString("Category","null")=="null")
            with(sharedPref.edit()){
                putString("Category","All")
            }.apply()
        if(sharedPref.getStringSet("CategoryList",null)==null)
            with(sharedPref.edit()){
                putStringSet("CategoryList", setOf("All","Home","Work","Errand","Other"))
            }.apply()
        with(sharedPref.edit()){
            putInt("Reloaded",0)
        }.apply()
        val mode=applicationContext.resources.configuration.uiMode.and(Configuration.UI_MODE_NIGHT_MASK)
        when(mode){
            Configuration.UI_MODE_NIGHT_YES->{supportActionBar!!.setBackgroundDrawable(ColorDrawable(resources.getColor(R.color.darkerTheme)))}
            Configuration.UI_MODE_NIGHT_NO->{supportActionBar!!.setBackgroundDrawable(ColorDrawable(resources.getColor(R.color.theme)))}
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

