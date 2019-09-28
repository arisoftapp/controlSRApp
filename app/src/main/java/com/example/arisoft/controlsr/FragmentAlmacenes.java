package com.example.arisoft.controlsr;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.arisoft.controlsr.Tools.Database;

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
    ArrayAdapter<String>listaAlmacenes;
    Vector<String> codigo = new Vector<String>();
    Vector<String> almacen = new Vector<String>();

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
        spinner2=(Spinner) v.findViewById(R.id.spinner2);
        spinner2.setAdapter(getAlmacenes());
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_fragment_almacenes, container, false);
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
                }while (fila.moveToNext());
                listaAlmacenes = new ArrayAdapter<>(getContext(),R.layout.support_simple_spinner_dropdown_item, almacen);
                spinner2.setAdapter(listaAlmacenes);
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
