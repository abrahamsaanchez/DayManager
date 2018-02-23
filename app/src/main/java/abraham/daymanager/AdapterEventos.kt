package abraham.daymanager

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import java.text.DecimalFormat
import java.util.*

class AdapterEventos(private val items: ArrayList<EventosPOJO>, private val main: MainActivity) : RecyclerView.Adapter<AdapterEventos.AdaptadorViewHolder>() {
    private var modoSeleccion: Boolean = false
    private val seleccionados = ArrayList<EventosPOJO>()

    override fun onBindViewHolder(holder: AdaptadorViewHolder, position: Int) {
        val registro = items[position]
        holder.bindView(registro)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdaptadorViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.recycler_main, parent, false)
        return AdaptadorViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun cambiarSeleccion(valor: Boolean?) {
        this.modoSeleccion = valor!!
    }

    inner class AdaptadorViewHolder(private val vItem: View) : RecyclerView.ViewHolder(vItem) {

        private val tvTitulo: TextView

        init {
            tvTitulo = vItem.findViewById<View>(R.id.tvTituloEvento) as TextView
        }

        fun bindView(item: EventosPOJO) {
            // Rellenar datos
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)
            val df = DecimalFormat("00")
            val fecha = day.toString() + "-" + df.format((month + 1).toLong()) + "-" + year
            if (item.fecha == fecha)
                tvTitulo.text = item.hora + " - " + item.titulo
            else
                tvTitulo.text = item.fecha + " - " + item.titulo
            vItem.setOnLongClickListener { view ->
                if (!modoSeleccion) {
                    modoSeleccion = true
                    view.isSelected = true
                    seleccionados.add(items[adapterPosition])
                    main.seleccionOk()
                    main.meterSeleccionados(seleccionados)
                }
                true
            }
            vItem.setOnClickListener { view ->
                if (modoSeleccion) {
                    if (!view.isSelected) {
                        view.isSelected = true
                        seleccionados.add(items[adapterPosition])
                        main.meterSeleccionados(seleccionados)
                    } else {
                        view.isSelected = false
                        seleccionados.remove(items[adapterPosition])
                        main.meterSeleccionados(seleccionados)
                        if (!haySeleccionados()) {
                            modoSeleccion = false
                            main.seleccionFuera()
                        }
                    }
                } else {
                    val reg = items[adapterPosition]
                    main.modificarEvento(reg)
                }
            }
        }

        fun haySeleccionados(): Boolean {
            return if (seleccionados.size != 0) {
                true
            } else false
        }
    }
}