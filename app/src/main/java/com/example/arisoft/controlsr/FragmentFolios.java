package com.example.arisoft.controlsr;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.arisoft.controlsr.Tools.Database;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FragmentFolios.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FragmentFolios#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentFolios extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    //variables
    TextView tv_folio_oc;
    EditText et_folio_oc,et_folio_oc_number;
    Button btn_guardar_oc;
    String mensajeGlobal,URL;
    HttpParams httpParameters = new BasicHttpParams();
    int timeoutConnection = 5000;
    int timeoutSocket = 5000;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public FragmentFolios() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragmentFolios.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentFolios newInstance(String param1, String param2) {
        FragmentFolios fragment = new FragmentFolios();
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
        View v = inflater.inflate(R.layout.fragment_fragment_folios, container, false);
        getDomain();
        et_folio_oc=(EditText)v.findViewById(R.id.et_folio_oc);
        et_folio_oc_number=(EditText)v.findViewById(R.id.et_folio_oc_number);
        tv_folio_oc=(TextView) v.findViewById(R.id.tv_folio_oc);
        btn_guardar_oc=(Button) v.findViewById(R.id.btn_guardar_folio);

            if(folioSeleccionado().equalsIgnoreCase(""))
            {
                tv_folio_oc.setText("----");
            }
            else
            {
                //et_folio_oc.setText(folioSeleccionado());
                tv_folio_oc.setText(folioSeleccionado());
            }





        //click btn guardar
        btn_guardar_oc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //mensajes("click");
                String numero,serie,folioOC;
                numero=et_folio_oc_number.getText().toString();
                serie=et_folio_oc.getText().toString();
                folioOC=serie+numero;
                if(folioOC.equalsIgnoreCase(folioSeleccionado()))
                {
                    mensajes("folio igual al seleccionado");
                }
                else
                {
                    if(numero.equalsIgnoreCase(""))
                    {
                        mensajes("Introducir numero");
                    }
                    else
                    {
                        if(serie.equalsIgnoreCase(""))
                        {
                            mensajes("Introducir serie");
                        }
                        else
                        {
                            new consultaFolio().execute(folioOC,almacenSeleccionado());
                        }

                    }

                }


            }
        });

        // Inflate the layout for this fragment
        return v;
    }

    public void getDomain(){

        try {
            Database admin = new Database(getContext(),null,1);
            SQLiteDatabase db = admin.getWritableDatabase();
            Cursor fila = db.rawQuery("SELECT dominio FROM login",null);
            if(fila.moveToFirst())
            {
                URL = "http://wsar.homelinux.com:" + (fila.getString(0) + "/");
            }
            db.close();
        }catch (SQLiteException sql){
            mensajes(sql.getMessage());
        }
    }

    public void mensajes(String mensaje) {
        Toast.makeText(getContext(),mensaje,Toast.LENGTH_SHORT).show();
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
    public String folioSeleccionado()
    {
        String alm="";
        try{
            Database admin = new Database(getContext(),null,1);
            SQLiteDatabase db = admin.getWritableDatabase();
            Cursor fila = db.rawQuery("SELECT folioOC FROM login",null);
            if(fila.moveToFirst())
            {
                do{
                    Log.i("consultaFolio"," | "+fila.getString(0)
                    );
                    alm=fila.getString(0);
                }while (fila.moveToNext());
            }
            else
            {
                alm="sin folio seleccionado";
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
    class consultaFolio extends AsyncTask<String,Integer,String>
    {
        String validar;
        private ProgressDialog progreso;

        @Override
        protected void onPreExecute()
        {
            progreso = new ProgressDialog(getContext());
            progreso.setMessage("Consultando folios");
            progreso.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progreso.setCancelable(false);
            // progreso.setMax(100);
            // progreso.setProgress(0);
            progreso.show();
            super.onPreExecute();
        }
        @Override
        protected String doInBackground(String... params)
        {
            String folio=params[0],almacen=params[1];
            try {
                HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
                HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
                HttpClient cliente = new DefaultHttpClient(httpParameters);
                //HttpGet htpoget = new HttpGet(URL+"consulta_previo/"+folio+"/"+almacen);
                HttpGet httpGet=new HttpGet(URL+"foliosOC/"+folio+"/"+almacen);
                org.apache.http.HttpResponse resx = cliente.execute(httpGet);
                BufferedReader bfr = new BufferedReader(new InputStreamReader(resx.getEntity().getContent()));
                StringBuffer stb = new StringBuffer("");
                String linea="";
                StringBuffer res = new StringBuffer();
                while ((linea =bfr.readLine())!=null)
                {
                    res.append(linea);
                    validar="OK";
                }
                String finalJSON = res.toString();
                JSONObject jObject = new JSONObject(finalJSON); //Obtenemos el JSON global
                if(jObject.getBoolean("success")==true)
                {
                    validar="OK";
                    mensajeGlobal=""+jObject.getString("message");
                }
                else
                {
                    validar="false";
                    mensajeGlobal=jObject.getString("message");
                }
                bfr.close();
            }
            catch (Exception e)
            {
                validar=e.getMessage();
                mensajeGlobal="Error:"+e.getMessage();
            }
            return validar;

        }
        protected void onProgressUpdate(Integer... i)
        {
            progreso.setProgress(i[0]);
        }
        protected void onPostExecute(String s)
        {
            progreso.dismiss();
            if(s.equalsIgnoreCase("OK"))
            {
                //consultatabla();
                guardarFolio();
                tv_folio_oc.setText(folioSeleccionado());
                mensajes(mensajeGlobal);

            }
            else
            {
                if(s.equalsIgnoreCase("false"))
                {
                    mensajes(mensajeGlobal);
                }
                else
                {
                    mensajes(mensajeGlobal);
                }
            }
            super.onPostExecute(s);
        }
    }

    public void guardarFolio()
    {
        String folioOC=et_folio_oc.getText().toString()+et_folio_oc_number.getText().toString();
        try{
            Database admin=new Database(getContext(),null,1);
            SQLiteDatabase db = admin.getWritableDatabase();
            ContentValues r = new ContentValues();
            r.put("folioOC",folioOC);
            r.put("serieOC",et_folio_oc.getText().toString());
            r.put("numeroOC",et_folio_oc_number.getText().toString());
            db.update("login",r, "usuario='"+ getUsuario() +"'",null);
            db.close();
        }catch (SQLiteException e)
        {
            Toast.makeText(getContext(), "Error:"+e.getMessage(), Toast.LENGTH_SHORT).show();
        }
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
