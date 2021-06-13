package com.example.cestavirtual

import android.app.Dialog
import android.graphics.Color
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.Menu
import android.view.MenuItem
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavOptions
import androidx.navigation.Navigation

class MainActivity : AppCompatActivity() {
    lateinit var menu:Menu

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        actionBar?.title = "Cesta Virtual"

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
       val menuInflater=menuInflater
        menuInflater.inflate(R.menu.menu_mainactivity,menu)
        this.menu= menu!!
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.logout_menu_item -> {
              createLogoutDialog()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun createLogoutDialog(){
        val customDialog=Dialog(this)
        customDialog.setContentView(R.layout.custom_dialog_logout)
        customDialog.window?.setBackgroundDrawable(getDrawable(R.drawable.background_dialog_logout))
        customDialog.window?.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT)
        customDialog.findViewById<Button>(R.id.btn_dialog_no).setOnClickListener {
            customDialog.dismiss()
        }
        customDialog.findViewById<Button>(R.id.bt_dialog_yes).setOnClickListener {
            backLogin()
            customDialog.dismiss()
        }
        customDialog.show()
    }

    private fun backLogin() {
        val bundle = Bundle()
        bundle.putBoolean("leerPreferencias", false)
        val navOptions = NavOptions.Builder()
            .setPopUpTo(Navigation
                .findNavController(this, R.id.fragmentContainerView).currentDestination!!.id, true)
            .build();

        Navigation.findNavController(this, R.id.fragmentContainerView)
            .navigate(R.id.login, bundle, navOptions)
    }

    private fun ActionBar.setTitleColor(color: Int) {
        val text = SpannableString(title ?: "")
        text.setSpan(ForegroundColorSpan(color),0,text.length, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
        title = text
    }
}