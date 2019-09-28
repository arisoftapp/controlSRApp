package com.example.arisoft.controlsr;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.MenuItem;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.widget.TextView;
import android.widget.Toast;

import com.example.arisoft.controlsr.Tools.Database;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,FragmentAlmacenes.OnFragmentInteractionListener, FragmentInicial.OnFragmentInteractionListener {

    Context contexto=this;
    TextView tv_empresa,tv_usuario;
    String URL,mensajeGlobal;
    //fragment
    FragmentAlmacenes fragment_almacenes;
    FragmentInicial fragment_inicial;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        View header = ((NavigationView)findViewById(R.id.nav_view)).getHeaderView(0);

        tv_empresa=(TextView)header.findViewById(R.id.tv_empresa);
        tv_usuario=(TextView)header.findViewById(R.id.tv_usuario);

        //inciando fragment
        fragment_almacenes=new FragmentAlmacenes();
        fragment_inicial=new FragmentInicial();
        getSupportFragmentManager().beginTransaction().add(R.id.contenedorFragment,fragment_inicial).commit();
        getDomain();

        if(tablaVacia("almacenes","cod_alm")==true)
        {
            new cargarAlmacenesWS().execute();
        }


    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            // Handle the camera action
            FragmentTransaction transaction=getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.contenedorFragment,fragment_inicial);
            transaction.commit();
        } else if (id == R.id.nav_almacenes) {
            FragmentTransaction transaction=getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.contenedorFragment,fragment_almacenes);
            transaction.commit();

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_tools) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_cerrar) {
            eliminarTabla("login");
            eliminarTabla("almacenes");
            Intent i=new Intent(contexto,Login.class);
            startActivity(i);
            finish();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    class cargarAlmacenesWS extends AsyncTask<String,Integer,String>
    {
        String validar;
        private ProgressDialog progreso;

        @Override
        protected void onPreExecute()
        {
            progreso = new ProgressDialog(contexto);
            progreso.setMessage("Iniciando");
            progreso.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progreso.setCancelable(false);
            progreso.setMax(100);
            progreso.setProgress(0);
            progreso.show();
            super.onPreExecute();
        }
        @Override
        protected String doInBackground(String... params)
        {

            try {
                HttpClient cliente = new DefaultHttpClient();
                HttpGet htpoget = new HttpGet(URL+"control_almacenes");
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
                    JSONArray jArray = jObject.getJSONArray("almacenes");
                    progreso.setMax(jArray.length());
                    for (int i=0; i < jArray.length(); i++) //Miramos en todos los objetos del array de objetos results
                    {

                        try {
                            JSONObject objeto = jArray.getJSONObject(i); //Obtenemos cada uno de los objetos del array results
                            String id_almacen=objeto.getString("idalmacen");
                            String almacen=objeto.getString("almacen");
                            try{
                                publishProgress(i+1);
                                Database admin=new Database(contexto,null,1);
                                SQLiteDatabase db = admin.getWritableDatabase();
                                ContentValues r = new ContentValues();
                                r.put("cod_alm",id_almacen);
                                r.put("almacen",almacen);
                                db.insert("almacenes",null,r);
                                db.close();

                            }catch (SQLiteException e)
                            {
                                mensajeGlobal="error al insertar almacenes:"+e.getMessage();
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
            consultatabla();
            if(s.equalsIgnoreCase("OK"))
            {
                mensajes("Se agregaron almacenes con exito");
            }
            else
            {
                eliminarTabla("almacenes");
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

    public void consultatabla()
    {
        try{
            Database admin = new Database(contexto,null,1);
            SQLiteDatabase db = admin.getWritableDatabase();
            Cursor fila = db.rawQuery("SELECT * FROM almacenes",null);
            if(fila.moveToFirst())
            {
                do{
                    Log.i("consultaAlmacenes"," | "+fila.getString(0)+
                            " | "+fila.getString(1)

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

    public void eliminarTabla(String tabla)
    {
        try{
            Database admin=new Database(this,null,1);
            SQLiteDatabase db = admin.getWritableDatabase();
            //db.execSQL("DROP TABLE IF EXISTS "+tabla);
            db.execSQL("DELETE FROM " + tabla);
            db.close();

        }catch (SQLiteException e)
        {
            mensajes("Error al eliminar tabla "+tabla+":"+e.getMessage());
        }

    }
    public void mensajes(String mensaje) {
        Toast.makeText(getApplicationContext(),mensaje,Toast.LENGTH_SHORT).show();
    }

    public void getDomain(){

        try {
            Database admin = new Database(contexto,null,1);
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

    public Boolean tablaVacia(String nomTabla, String columna)
    {
        Boolean vacio = true;
        Database admin = new Database(this, null,1);
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


    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
