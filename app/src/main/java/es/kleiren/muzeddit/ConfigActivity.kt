package es.kleiren.muzeddit

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.Window
import android.widget.CheckBox
import android.widget.Toast
import es.kleiren.muzeddit.RedditFetcherService.Companion.subReddit
import org.jetbrains.anko.defaultSharedPreferences


class ConfigActivity : AppCompatActivity() {

    private lateinit var cbExposure: CheckBox
    private lateinit var cbEarth: CheckBox
    private lateinit var cbSpace: CheckBox

    private val clickListener = View.OnClickListener {
        subReddit = ""
        if (cbExposure.isChecked) subReddit += "ExposurePorn"
        if (cbEarth.isChecked) subReddit += "+EarthPorn"
        if (cbSpace.isChecked) subReddit += "+SpacePorn"
        if (!cbSpace.isChecked && !cbEarth.isChecked && !cbExposure.isChecked) {
            cbSpace.isChecked = true
            subReddit = "SpacePorn"
            Toast.makeText(this, "Some SubReddit must be enabled!", Toast.LENGTH_LONG).show()
        }
        defaultSharedPreferences.edit().putString("subReddit", subReddit).apply()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.activity_config)


        if (defaultSharedPreferences.getString("subReddit", "") == "")
            defaultSharedPreferences.edit().putString("subReddit", "SpacePorn+EarthPorn+ExposurePorn").apply()
        else
            subReddit = defaultSharedPreferences.getString("subReddit", "")

        cbExposure = findViewById(R.id.cbExposure)
        cbEarth = findViewById(R.id.cbEarth)
        cbSpace = findViewById(R.id.cbSpace)

        cbExposure.isChecked = (subReddit.contains("Exposure"))
        cbEarth.isChecked = (subReddit.contains("EarthPorn"))
        cbSpace.isChecked = (subReddit.contains("SpacePorn"))

        cbExposure.setOnClickListener(clickListener)
        cbEarth.setOnClickListener(clickListener)
        cbSpace.setOnClickListener(clickListener)
    }

}
