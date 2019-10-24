package com.example.arisoft.controlsr.Modelo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.arisoft.controlsr.R;

import java.util.ArrayList;

public class RecibidoAdapter extends BaseAdapter {
    private ArrayList<Recibido> listItem;
    private Context context;

    public RecibidoAdapter(ArrayList<Recibido> listItem, Context context) {
        this.listItem = listItem;
        this.context = context;
    }

    @Override
    public int getCount() {
        return listItem.size();
    }

    @Override
    public Object getItem(int position) {
        return listItem.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Recibido item=(Recibido) getItem(position);


        convertView= LayoutInflater.from(context).inflate(R.layout.lista_recibido_item,null);
        TextView tv_cod=convertView.findViewById(R.id.tv_cod);
        TextView tv_desc=convertView.findViewById(R.id.tv_desc);
        TextView tv_cant=convertView.findViewById(R.id.tv_cant);
        TextView tv_recibido=convertView.findViewById(R.id.tv_recibido);
        TextView tv_dif=convertView.findViewById(R.id.tv_dif);
        TextView tv_fac=convertView.findViewById(R.id.tv_fac);


        tv_cod.setText(item.getCodigo());
        tv_desc.setText(item.getDescripcion());
        tv_cant.setText(item.getCantidad().toString());
        tv_recibido.setText(item.surtidas.toString());
        tv_dif.setText(item.por_surtir.toString());
        tv_fac.setText(item.getFolio());
        /*
        if(tv_fac.getText().toString().equalsIgnoreCase(""))
        {
            tv_fac.setVisibility(View.GONE);
        }
        else
        {
            tv_fac.setVisibility(View.VISIBLE);
        }
        */
        return convertView;
    }
}
