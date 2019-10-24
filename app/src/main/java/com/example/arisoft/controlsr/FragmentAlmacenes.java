package com.example.arisoft.controlsr;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.arisoft.controlsr.Tools.Database;

import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;

import java.util.ArrayList;
import java.util.Vector;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FragmentAlmacenes.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FragmentAlmacenes#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentAlmacenes extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    Spinner spinner2;
    Button btn_guardar;
    TextView tv_almacen;
    ArrayAdapter<String>listaAlmacenes;
    Vector<String> codigo = new Vector<String>();
    Vector<String> almacen = new Vector<String>();
    String dominio;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public FragmentAlmacenes() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragmentAlmacenes.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentAlmacenes newInstance(String param1, String param2) {
        FragmentAlmacenes fragment = new FragmentAlmacenes();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }



    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_fragment_almacenes, container, false);
        getDomain();
        spinner2=(Spinner) v.findViewById(R.id.spinner2);
        spinner2.setAdapter(getAlmacenes());
        btn_guardar=(Button)v.findViewById(R.id.btn_guardar);
        tv_almacen=(TextView)v.findViewById(R.id.tv_almacen);
        tv_almacen.setText(almacenSeleccionado());
        btn_guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(tablaVacia("articulos","codigo")==false)
                {
                    mensajes("Ya tiene seleccionado un previo de compra");
                }
                else
                {
                    int size = spinner2.getAdapter().getCount();
                    if (size>0){
                        int pos = spinner2.getSelectedItemPosition();
                        guardar(codigo.get(pos).toString());
                        //Log.i("guardaralm",codigo.get(pos).toString());
                    }
                }


            }
        });
        // Inflate the layout for this fragment
        return v;
    }
    public void mensajes(String mensaje) {
        Toast.makeText(getContext(),mensaje,Toast.LENGTH_SHORT).show();
    }
    public Boolean tablaVacia(String nomTabla, String columna)
    {
        Boolean vacio = true;
        Database admin = new Database(getContext(), null,1);
        SQLiteDatabase db = admin.getWritableDatabase();
        try {
            Cursor fila = db.rawQuery("SELECT " + columna + " FROM "+ nomTabla,null);
            if(fila.moveToFirst())
            {
                vacio=false;
            }

        }catch (SQLiteException sql){
            vacio = true;
            Log.e("error",sql.getMessage());
        }
        db.close();
        return vacio;
    }
    public String almacenSeleccionado()
    {
        String alm="";
        try{
            Database admin = new Database(getContext(),null,1);
            SQLiteDatabase db = admin.getWritableDatabase();
            Cursor fila = db.rawQuery("SELECT almacen FROM login",null);
            if(fila.moveToFirst())
            {
                do{
                    Log.i("consultaAlmacen"," | "+fila.getString(0)
                    );
                    alm=fila.getString(0);
                }while (fila.moveToNext());
            }
            else
            {
                alm="sin almacen seleccionado";
            }
            db.close();
        }catch (Exception e)
        {
            Log.e("Error:",""+e.getMessage());
            alm="";
            //mensajes("Error al validar login:"+e.getMessage());
        }

        return alm;
    }
    public String getUsuario()
    {
        String alm="";
        try{
            Database admin = new Database(getContext(),null,1);
            SQLiteDatabase db = admin.getWritableDatabase();
            Cursor fila = db.rawQuery("SELECT usuario FROM login",null);
            if(fila.moveToFirst())
            {
                do{
                    Log.i("consultaAlmacen"," | "+fila.getString(0)
                    );
                    alm=fila.getString(0);
                }while (fila.moveToNext());
            }
            else
            {
                alm="sin almacen seleccionado";
            }
            db.close();
        }catch (Exception e)
        {
            Log.e("Error:",""+e.getMessage());
            alm="";
            //mensajes("Error al validar login:"+e.getMessage());
        }

        return alm;
    }
    public void guardar(String codigo)
    {
        try{

            Database admin=new Database(getActivity().getApplicationContext(),null,1);
            SQLiteDatabase db = admin.getWritableDatabase();
            ContentValues r = new ContentValues();
            r.put("almacen",codigo);
            db.update("login",r, "usuario='"+ getUsuario() +"'",null);
            db.close();
            tv_almacen.setText(almacenSeleccionado());


        }catch (SQLiteException e)
        {
            Log.e("actualizarbd",e.getMessage());
        }
    }
    public void getDomain(){

        try {
            Database admin = new Database(getActivity().getApplicationContext(),null,1);
            SQLiteDatabase db = admin.getWritableDatabase();
            Cursor fila = db.rawQuery("SELECT dominio FROM login",null);
            if(fila.moveToFirst())
            {
                dominio = fila.getString(0);
            }
            db.close();
        }catch (SQLiteException sql){
            //mensajes(sql.getMessage());
            Log.e("obtenerdominio",sql.getMessage());
        }
    }

    public ArrayAdapter<String> getAlmacenes (){

        try{
            Database admin = new Database(getContext(),null,1);
            SQLiteDatabase db = admin.getWritableDatabase();
            Cursor fila = db.rawQuery("SELECT * FROM almacenes",null);
            if(fila.moveToFirst())
            {
                do{
                    codigo.add(fila.getString(0));
                    almacen.add(fila.getString(0)+" - "+fila.getString(1));
                    Log.i("consultaALM",fila.getString(0)+" - "+fila.getString(1));
                }while (fila.moveToNext());
                listaAlmacenes = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_spinner_item, almacen);
                listaAlmacenes.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
            }
            else
            {
                Toast.makeText(getContext(), "No existen almacenes", Toast.LENGTH_SHORT).show();
            }
            db.close();

        }catch (SQLiteException e)
        {
            Toast.makeText(getContext(), "Error al consultar almacenes: "+e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        return listaAlmacenes;
    }







    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
