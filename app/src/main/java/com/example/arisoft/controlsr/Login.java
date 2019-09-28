package com.example.arisoft.controlsr;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.arisoft.controlsr.Tools.Database;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class Login extends AppCompatActivity {
    private Button btn_accesar;
    private EditText et_usuario,et_contra;
    private static final String URL = "http://wsar.homelinux.com:3005/";
    private String mensajeGlobal="";
    Context contexto=this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if(consultarLogin()==true)
        {
            Intent i=new Intent(contexto,MainActivity.class);
            startActivity(i);
            finish();
        }

        btn_accesar=(Button)findViewById(R.id.btn_accesar);
        et_usuario=(EditText)findViewById(R.id.et_usuario);
        et_contra=(EditText)findViewById(R.id.et_contra);

        btn_accesar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(et_usuario.getText().toString().isEmpty())
                {
                    mensajes("usuario en blanco");
                }
                else {
                    if(et_contra.getText().toString().isEmpty())
                    {
                        mensajes("contraseña en blanco");
                    }
                    else {
                        new cargarUsuariosWS().execute(et_usuario.getText().toString(),et_contra.getText().toString());
                    }
                }


            }
        });




    }

    class cargarUsuariosWS extends AsyncTask<String,Integer,String>
    {
        String validar;
        private ProgressDialog progreso;

        @Override
        protected void onPreExecute()
        {
            progreso = new ProgressDialog(contexto);
            progreso.setMessage("Iniciando");
            progreso.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progreso.setIndeterminate(true);
            progreso.show();
            super.onPreExecute();
        }
        @Override
        protected String doInBackground(String... params)
        {
            String usuario=params[0],contra=params[1];
            try {
                HttpClient cliente = new DefaultHttpClient();
                HttpGet htpoget = new HttpGet(URL+"login/"+usuario+"/"+contra);
                org.apache.http.HttpResponse resx = cliente.execute(htpoget);
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
                    mensajeGlobal="";
                    String success=jObject.getString("success");
                    JSONArray jArray = jObject.getJSONArray("usuario");
                    //Log.i("login",jArray.getString(0));
                    for (int i=0; i < jArray.length(); i++) //Miramos en todos los objetos del array de objetos results
                    {
                        try {
                            JSONObject objeto = jArray.getJSONObject(i); //Obtenemos cada uno de los objetos del array results
                            String empresa=objeto.getString("empresa");
                            String dominio=objeto.getString("dominio");
                            usuario=objeto.getString("usuario");
                            String id_empresa=objeto.getString("id_empresa");
                            Log.i("cargausuario",empresa+" "+dominio+" "+usuario+" "+id_empresa+" "+success);
                            try{

                                Database admin=new Database(contexto,null,1);
                                SQLiteDatabase db = admin.getWritableDatabase();
                                ContentValues r = new ContentValues();
                                r.put("success",success);
                                r.put("nomEmpresa",empresa);
                                r.put("dominio",dominio);
                                r.put("usuario",usuario);
                                r.put("id_empresa",id_empresa);
                                r.put("almacen","");
                                db.insert("login",null,r);
                                db.close();

                            }catch (SQLiteException e)
                            {
                                Log.e("cargausuario",e.getMessage());
                                mensajeGlobal="error al insertar login:"+e.getMessage();
                                validar="false";
                            }
                        } catch (JSONException e) {
                            mensajeGlobal="error al hacer peticion: "+e.getMessage();
                            validar="false";
                            Log.e("cargausuario",e.getMessage());
                        }
                    }
                }
                else
                {
                    validar="false";
                    mensajeGlobal=jObject.getString("mensaje");
                }
                bfr.close();
            }
            catch (Exception e)
            {
                validar=e.getMessage();
                mensajeGlobal="Error:"+e.getMessage();
                Log.e("cargarusuario",""+e.getMessage());
            }
            return validar;

        }

        protected void onProgressUpdate(Integer... i)
        {
            //progreso.setProgress(i[0]);
        }
        protected void onPostExecute(String s)
        {
            progreso.dismiss();
            if(s.equalsIgnoreCase("OK"))
            {

                Intent i=new Intent(contexto,MainActivity.class);
                startActivity(i);
                finish();
                consultatabla();
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

    public void mensajes(String mensaje) {
        Toast.makeText(getApplicationContext(),mensaje,Toast.LENGTH_SHORT).show();
    }
    public void consultatabla()
    {
        try{
            Database admin = new Database(contexto,null,1);
            SQLiteDatabase db = admin.getWritableDatabase();
            Cursor fila = db.rawQuery("SELECT * FROM login",null);
            if(fila.moveToFirst())
            {
                do{
                    Log.i("consultaLogin"," | "+fila.getString(0)+
                            " | "+fila.getString(1)+
                            " | "+fila.getString(2)+
                            " | "+fila.getString(3)+
                            " | "+fila.getString(4)+
                            " | "+fila.getString(5)
                            );
                }while (fila.moveToNext());
            }
            db.close();
        }catch (Exception e)
        {
            Log.e("Error:",""+e.getMessage());
            //mensajes("Error al validar login:"+e.getMessage());
        }
    }

    public boolean consultarLogin()
    {
        boolean validarLogin=false;
        try{
            Database admin = new Database(contexto,null,1);
            SQLiteDatabase db = admin.getWritableDatabase();
            Cursor fila = db.rawQuery("SELECT success FROM login",null);
            if(fila.moveToFirst())
            {
                do{
                    Log.i("consultaLogin"," | "+fila.getString(0));
                    if(fila.getString(0).equalsIgnoreCase("true"))
                    {
                        validarLogin=true;
                    }
                }while (fila.moveToNext());
            }
            db.close();
        }catch (Exception e)
        {
            validarLogin=false;
            Log.e("Error:",""+e.getMessage());
            //mensajes("Error al validar login:"+e.getMessage());
        }
        return validarLogin;
    }


}
