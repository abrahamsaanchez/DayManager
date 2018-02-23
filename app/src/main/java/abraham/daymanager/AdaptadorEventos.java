package abraham.daymanager;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class AdaptadorEventos extends RecyclerView.Adapter<AdaptadorEventos.AdaptadorViewHolder> {

    private ArrayList<EventosPOJO> items;
    private MainActivity main;
    private boolean modoSeleccion;
    private ArrayList<EventosPOJO> seleccionados = new ArrayList<>();

    public AdaptadorEventos(ArrayList<EventosPOJO> items, MainActivity main) {
        super();
        this.items = items;
        this.main = main;
    }

    @Override
    public void onBindViewHolder(AdaptadorViewHolder holder, int position) {
        EventosPOJO registro = items.get(position);
        holder.bindView(registro);
    }

    @Override
    public AdaptadorViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_main, parent, false);
        AdaptadorViewHolder avh = new AdaptadorViewHolder(itemView);
        return avh;
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void cambiarSeleccion(Boolean valor){
        this.modoSeleccion = valor;
    }

    public class AdaptadorViewHolder extends RecyclerView.ViewHolder {

        private TextView tvTitulo;
        private View vItem;

        public AdaptadorViewHolder(View itemView) {
            super(itemView);
            this.vItem = itemView;
            tvTitulo = (TextView) itemView.findViewById(R.id.tvTituloEvento);
        }

        public void bindView(final EventosPOJO item) {
            // Rellenar datos
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            DecimalFormat df = new DecimalFormat("00");
            String fecha = day+"-"+df.format(month+1)+"-"+year;
            if(item.getFecha().equals(fecha))
                tvTitulo.setText(item.getHora()+" - "+item.getTitulo());
            else
                tvTitulo.setText(item.getFecha()+" - "+item.getTitulo());
            vItem.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    if (!modoSeleccion) {
                        modoSeleccion = true;
                        view.setSelected(true);
                        seleccionados.add(items.get(getAdapterPosition()));
                        main.seleccionOk();
                        main.meterSeleccionados(seleccionados);
                    }
                    return true;
                }
            });
            vItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (modoSeleccion) {
                        if (!view.isSelected()) {
                            view.setSelected(true);
                            seleccionados.add(items.get(getAdapterPosition()));
                            main.meterSeleccionados(seleccionados);
                        } else {
                            view.setSelected(false);
                            seleccionados.remove(items.get(getAdapterPosition()));
                            main.meterSeleccionados(seleccionados);
                            if (!haySeleccionados()) {
                                modoSeleccion = false;
                                main.seleccionFuera();
                            }
                        }
                    } else {
                        EventosPOJO reg = items.get(getAdapterPosition());
                        main.modificarEvento(reg);
                    }
                }
            });
        }

        public boolean haySeleccionados() {
            if(seleccionados.size()!=0) {
                return true;
            }
            return false;
        }
    }
}