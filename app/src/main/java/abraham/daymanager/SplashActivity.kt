package abraham.daymanager

import android.content.Intent
import android.content.SharedPreferences
import android.preference.PreferenceManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.AppCompatDelegate

class SplashActivity : AppCompatActivity() {

    private var preferencias: SharedPreferences? = null

    private var nigthMode = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Compruebo las preferencias para comprobar el estado del modo noche
        preferencias = PreferenceManager.getDefaultSharedPreferences(this)
        nigthMode = preferencias!!.getBoolean("modoNoche", false)
        if (nigthMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        }

        // Iniciamos la actividad principal
        val i = Intent(this, MainActivity::class.java)
        startActivity(i)
        finish()
    }
}
