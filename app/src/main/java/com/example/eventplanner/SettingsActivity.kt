package com.example.eventplanner

import android.content.res.ColorStateList
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Switch
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.example.eventplanner.setupBottomNavigation

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

        val themeSection = container.findViewById<LinearLayout>(R.id.themeSection)
        val colorSection = container.findViewById<LinearLayout>(R.id.colorSection)

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

            val accentColor = UserPreferences.getAccentColor(this)
            val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
            (themeSection.background as? GradientDrawable)?.setStroke(2, accentColor)
            (colorSection.background as? GradientDrawable)?.setStroke(2, accentColor)
            bottomNav?.itemIconTintList = ColorStateList.valueOf(accentColor)
            bottomNav?.itemTextColor = ColorStateList.valueOf(accentColor)

            recreate()
        }

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        val accentColor = UserPreferences.getAccentColor(this)

        bottomNav?.itemIconTintList = ColorStateList.valueOf(accentColor)
        bottomNav?.itemTextColor = ColorStateList.valueOf(accentColor)

        val drawable1 = themeSection.background.mutate()
        val drawable2 = colorSection.background.mutate()

        (drawable1 as? GradientDrawable)?.setStroke(2, accentColor)
        (drawable2 as? GradientDrawable)?.setStroke(2, accentColor)

    }
}

