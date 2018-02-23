package abraham.daymanager

import android.app.ProgressDialog
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.RecyclerView
import android.view.ActionMode
import android.view.View
import com.getbase.floatingactionbutton.FloatingActionsMenu
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.util.ArrayList
import android.support.design.widget.Snackbar
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.ValueEventListener
import android.support.v7.widget.LinearLayoutManager
import android.app.AlertDialog
import android.view.Menu
import android.view.MenuItem
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.preference.PreferenceManager
import android.support.v7.app.AppCompatDelegate
import com.getbase.floatingactionbutton.FloatingActionButton


class MainActivity : AppCompatActivity() {

    private lateinit var refreshLayout : SwipeRefreshLayout
    private lateinit var recycler : RecyclerView

    private lateinit var manager : RecyclerView.LayoutManager
    private lateinit var adaptador : AdapterEventos

    private lateinit var fam : FloatingActionsMenu
    private lateinit var fabNight : FloatingActionButton
    private lateinit var pDialog : ProgressDialog

    private var array : ArrayList<EventosPOJO> = java.util.ArrayList<EventosPOJO>()
    private var seleccionados : ArrayList<EventosPOJO> = java.util.ArrayList<EventosPOJO>()
    private lateinit var cab : ActionBarCallBack
    private lateinit var actionMode : ActionMode

    private lateinit var database : FirebaseDatabase
    private lateinit var referencia : DatabaseReference

    private var preferencias: SharedPreferences? = null

    private var nigthMode = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Inicializar variables
        fam = findViewById(R.id.famEventos)
        fabNight = findViewById(R.id.fabNight)
        refreshLayout = findViewById(R.id.swipeMain)
        recycler = findViewById(R.id.recyclerMain)

        // Colores y listener del refresh Layout
        refreshLayout.setColorSchemeResources(
                R.color.colorPrimaryDark,
                R.color.colorPrimary,
                R.color.azulClaro)
        refreshLayout.setOnRefreshListener { rellenarArray() }

        // Conexión a DB
        database = FirebaseDatabase.getInstance()

        // Relleno el array
        rellenarArray()

        // Compruebo el modo noche
        preferencias = PreferenceManager.getDefaultSharedPreferences(this)
        nigthMode = preferencias!!.getBoolean("modoNoche", false)
        if (nigthMode) {
            fabNight.setIcon(R.drawable.ic_sun)
        }else{
            fabNight.setIcon(R.drawable.ic_moon)
        }
    }

    fun agregarEvento(v : View){
        val i = Intent(this, EventosMod::class.java)
        startActivity(i)
        fam.collapse()
    }

    fun modificarEvento(reg : EventosPOJO){
        val i = Intent(this, EventosMod::class.java)
        i.putExtra("evento",reg)
        startActivity(i)
    }

    fun eliminarEventos(v : View){
        seleccionOk()
    }

    fun modoNoche(v : View){
        preferencias = PreferenceManager.getDefaultSharedPreferences(this)
        nigthMode = preferencias!!.getBoolean("modoNoche", false)
        if (nigthMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            preferencias!!.edit().putBoolean("modoNoche",false).apply()
            recreate()
        }else{
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            preferencias!!.edit().putBoolean("modoNoche",true).apply()
            recreate()
        }
    }

    private fun rellenarArray() {
        // Muestro un diálogo indicando que vamos a cargar los datos a no ser que se esté mostrando el refresh
        if (!refreshLayout.isRefreshing) {
            pDialog = ProgressDialog(this)
            pDialog.setMessage(resources.getString(R.string.gen_cargando))
            pDialog.isIndeterminate = false
            pDialog.setCancelable(false)
            pDialog.show()
        }

        // Conecto a la DB
        referencia = database.reference.child("eventos")
        referencia.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                array.clear()
                for (dsp in dataSnapshot.children) {
                    val reg = dsp.getValue<EventosPOJO>(EventosPOJO::class.java)
                    array.add(reg!!)
                }
                rellenarAdaptador()
            }

            override fun onCancelled(error: DatabaseError) {
                Snackbar.make(findViewById<View>(android.R.id.content), R.string.error_conexion, Snackbar.LENGTH_LONG).show()
                println(error.message)
                if (pDialog != null && pDialog.isShowing) {
                    pDialog.dismiss()
                }
                if (refreshLayout.isRefreshing) {
                    refreshLayout.isRefreshing = false
                }
                return
            }
        })
    }

    private fun rellenarAdaptador() {
        if (array.size === 0) {
            Snackbar.make(findViewById<View>(android.R.id.content), R.string.main_vacio, Snackbar.LENGTH_LONG).show()
            if (pDialog != null && pDialog.isShowing) {
                pDialog.dismiss()
            }
            if (refreshLayout.isRefreshing) {
                refreshLayout.isRefreshing = false
            }
            return
        }
        manager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        adaptador = AdapterEventos(array, this)
        recycler.layoutManager = manager
        recycler.adapter = adaptador
        if (pDialog != null && pDialog.isShowing) {
            pDialog.dismiss()
        }
        if (refreshLayout.isRefreshing) {
            refreshLayout.isRefreshing = false
        }
    }

    fun seleccionOk() {
        fam.visibility = View.INVISIBLE
        fam.collapse()
        cab = ActionBarCallBack()
        actionMode = startActionMode(cab)
        adaptador.cambiarSeleccion(true)
    }

    fun seleccionFuera() {
        fam.visibility = View.VISIBLE
    }

    fun meterSeleccionados(array: ArrayList<EventosPOJO>) {
        seleccionados = array
        if (seleccionados.size === 0) {
            actionMode.finish()
            return
        }
        if (seleccionados.size === 1) {
            var str = seleccionados.size.toString() + " "
            str += resources.getString(R.string.gen_selec)
            actionMode.setTitle(str)
        } else {
            var str = seleccionados.size.toString() + " "
            str += resources.getString(R.string.gen_selecs)
            actionMode.setTitle(str)
        }

    }

    private fun borrarMultiples() {
        val comprobacion = AlertDialog.Builder(this)
        comprobacion.setTitle(R.string.main_dels)
        comprobacion.setIcon(R.drawable.ic_trash)
        comprobacion.setMessage(R.string.main_del_inf)

        comprobacion.setPositiveButton(R.string.gen_eliminar, DialogInterface.OnClickListener { dialogInterface, i ->
            for (reg in seleccionados) {
                referencia = database.reference.child("eventos")
                referencia.child(reg.fecha+"-"+reg.hora).removeValue()
            }
            if (seleccionados.size === 1) {
                Snackbar.make(findViewById<View>(android.R.id.content), R.string.main_del_ok, Snackbar.LENGTH_LONG).show()
            } else {
                Snackbar.make(findViewById<View>(android.R.id.content), R.string.main_del_oks, Snackbar.LENGTH_LONG).show()
            }
            actionMode.finish()
            rellenarArray()
        })

        comprobacion.setNegativeButton(R.string.gen_cancelar, DialogInterface.OnClickListener { dialogInterface, i -> })

        val alertDialog = comprobacion.create()
        alertDialog.show()
    }

    internal inner class ActionBarCallBack : ActionMode.Callback {
        override fun onCreateActionMode(mode: ActionMode, menu: Menu): Boolean {
            mode.menuInflater.inflate(R.menu.menu_main, menu)
            return true
        }

        override fun onPrepareActionMode(mode: ActionMode, menu: Menu): Boolean {
            var str = seleccionados.size.toString() + " "
            str += resources.getString(R.string.gen_selec)
            mode.setTitle(str)
            return false
        }

        override fun onActionItemClicked(mode: ActionMode, item: MenuItem): Boolean {
            when (item.getItemId()) {
                R.id.miem_eliminar -> {
                    borrarMultiples()
                    return true
                }
                else -> return false
            }
        }

        override fun onDestroyActionMode(mode: ActionMode) {
            seleccionFuera()
        }
    }
}
