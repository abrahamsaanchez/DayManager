package abraham.daymanager

import android.app.AlertDialog
import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.TextInputLayout
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.widget.TextView
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.text.DecimalFormat
import java.util.*

class EventosMod : AppCompatActivity() {

    private lateinit var toolbar : Toolbar

    private lateinit var valTitulo : TextInputLayout
    private lateinit var valDesc : TextInputLayout
    private lateinit var valFecha : TextInputLayout
    private lateinit var valHora : TextInputLayout

    private lateinit var etTitulo : TextView
    private lateinit var etDesc : TextView
    private lateinit var etFecha : TextView
    private lateinit var etHora : TextView
    private lateinit var etBoton : TextView

    private lateinit var titulo : String
    private lateinit var desc : String
    private lateinit var fecha : String
    private lateinit var hora : String

    private lateinit var database : FirebaseDatabase
    private lateinit var referencia : DatabaseReference

    private lateinit var evento : EventosPOJO

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_eventos_mod)

        // Iniciar toolbar
        toolbar = findViewById(R.id.toolbarEventos)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        toolbar.bringToFront()

        // Iniciar variables
        valTitulo = findViewById(R.id.validarTitulo)
        valDesc = findViewById(R.id.validarDescripcion)
        valFecha = findViewById(R.id.validarFecha)
        valHora = findViewById(R.id.validarHora)

        etTitulo = findViewById(R.id.tituloEvento)
        etDesc = findViewById(R.id.descripcionEvento)
        etFecha = findViewById(R.id.fechaEvento)
        etHora = findViewById(R.id.horaEvento)
        etBoton = findViewById(R.id.eventoButton)

        // Conexión a DB
        database = FirebaseDatabase.getInstance()

        // Intento obtener el evento para editarlo
        try {
            evento = intent.extras!!.getSerializable("evento") as EventosPOJO
            rellenarDatos(evento)
            etBoton.text = resources.getString(R.string.main_mod)
        } catch (e: NullPointerException) {}

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.getItemId()) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
        }
        return true
    }

    fun rellenarDatos(reg : EventosPOJO){
        etTitulo.text = reg.titulo
        etDesc.text = reg.descripcion
        etFecha.text = reg.fecha
        etHora.text = reg.hora
    }

    fun ocultarTeclado(v: View) {
        val manager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        manager!!.hideSoftInputFromWindow(valTitulo.getWindowToken(), 0)
    }

    fun mostrarFecha(v: View){
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)
        val dpd = DatePickerDialog(this, AlertDialog.THEME_HOLO_DARK, DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
            val df = DecimalFormat("00")
            etFecha!!.text = dayOfMonth.toString()+"-"+df.format(monthOfYear+1).toString()+"-"+year.toString()
        }, year, month, day)
        dpd.show()
    }

    fun mostrarHora(v: View){
        val c = Calendar.getInstance()
        val hour = c.get(Calendar.HOUR_OF_DAY)
        val minute = c.get(Calendar.MINUTE)
        val tpd = TimePickerDialog(this, AlertDialog.THEME_HOLO_DARK, TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->
            val df = DecimalFormat("00")
            etHora!!.text = df.format(hourOfDay)+":"+df.format(minute)
        }, hour, minute, true)
        tpd.show()
    }

    fun agregarEvento (v: View){
        if(!validar()) return

        val reg = EventosPOJO(titulo,desc,fecha,hora)
        referencia = database.getReference().child("eventos")
        referencia.child(reg.fecha+"-"+reg.hora).setValue(reg)
        finish()
    }

    private fun validar(): Boolean {
        val error: String

        // Obtengo los datos introducidos
        titulo = etTitulo.getText().toString().trim()
        desc = etDesc.getText().toString().trim()
        fecha = etFecha.getText().toString().trim()
        hora = etHora.getText().toString().trim()

        // Comprobación de titulo
        if (titulo.isEmpty()) {
            etTitulo.requestFocus()
            error = getString(R.string.error_vacio)
            mostrarErrores(valTitulo, error)
            return false
        } else {
            mostrarErrores(valTitulo, null)
        }

        // Comprobación de descripcion
        if (desc.isEmpty()) {
            etDesc.requestFocus()
            error = getString(R.string.error_vacio)
            mostrarErrores(valDesc, error)
            return false
        } else {
            mostrarErrores(valDesc, null)
        }

        // Comprobación de fecha
        if (fecha.isEmpty()) {
            etFecha.requestFocus()
            error = getString(R.string.error_vacio)
            mostrarErrores(valFecha, error)
            return false
        } else {
            mostrarErrores(valFecha, null)
        }

        // Comprobación de hora

        if (hora.isEmpty()) {
            etHora.requestFocus()
            error = getString(R.string.error_vacio)
            mostrarErrores(valHora, error)
            return false
        } else {
            mostrarErrores(valHora, null)
        }
        return true
    }

    private fun mostrarErrores(textInputLayout: TextInputLayout, error: String?) {
        textInputLayout.error = error
        if (error == null) {
            textInputLayout.isErrorEnabled = false
        } else {
            textInputLayout.isErrorEnabled = true
        }
    }
}
