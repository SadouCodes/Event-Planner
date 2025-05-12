package com.example.eventplanner

import android.os.Bundle
import android.widget.FrameLayout
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Switch
import androidx.appcompat.app.AppCompatActivity

class SettingsActivity : AppCompatActivity() {

    private lateinit var themeSwitch: Switch
    private lateinit var colorGroup: RadioGroup



    override fun onCreate(savedInstanceState: Bundle?) {
        if (UserPreferences.getThemeMode(this)) {
            setTheme(R.style.Theme_EventPlanner_Dark)
        } else {
            setTheme(R.style.Theme_EventPlanner_Light)
        }

        super.onCreate(savedInstanceState)
        setContentView(R.layout.base_with_nav)
        setupBottomNavigation(this)

        val container = findViewById<FrameLayout>(R.id.container)
        val formView = layoutInflater.inflate(R.layout.activity_settings, container, true)

        themeSwitch = formView.findViewById(R.id.themeSwitch)
        colorGroup = formView.findViewById(R.id.colorGroup)

        themeSwitch.isChecked = UserPreferences.getThemeMode(this)
        when (UserPreferences.getPrimaryColor(this)) {
            "blue" -> colorGroup.check(R.id.colorBlue)
            "green" -> colorGroup.check(R.id.colorGreen)
            "red" -> colorGroup.check(R.id.colorRed)
        }

        themeSwitch.setOnCheckedChangeListener { _, isChecked ->
            UserPreferences.saveThemeMode(this, isChecked)
            UserPreferences.applyThemeMode(isChecked)
            recreate()
        }

        colorGroup.setOnCheckedChangeListener { _, checkedId ->
            val color = findViewById<RadioButton>(checkedId).text.toString().lowercase()
            UserPreferences.savePrimaryColor(this, color)
        }
    }
}
