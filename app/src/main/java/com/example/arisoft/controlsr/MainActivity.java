package com.example.arisoft.controlsr;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.arisoft.controlsr.Tools.Database;

import org.apache.http.HttpException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        FragmentAlmacenes.OnFragmentInteractionListener,
        FragmentInicial.OnFragmentInteractionListener,
        FragmentFolios.OnFragmentInteractionListener {

    Context contexto=this;
    TextView tv_empresa,tv_usuario;
    String URL,mensajeGlobal,fragmentActivo;
    LinearLayout ll_cargarAlm;
    //fragment
    FragmentAlmacenes fragment_almacenes;
    FragmentInicial fragment_inicial;
    FragmentFolios fragment_folios;
    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;
    HttpParams httpParameters = new BasicHttpParams();
    int timeoutConnection = 3000;
    int timeoutSocket = 5000;



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
        tv_usuario.setText(getUsuario());
        tv_empresa.setText(getEmpresa());
        ll_cargarAlm=(LinearLayout)findViewById(R.id.ll_cargarAlm);
        checkCameraPermission();



    }
    private void checkCameraPermission() {
        int permissionCheck = ContextCompat.checkSelfPermission(
                this, Manifest.permission.CAMERA);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            Log.i("Mensaje", "No se tiene permiso para la camara!.");
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 225);
        } else {
            Log.i("Mensaje", "Tienes permiso para usar la camara.");
        }
        int permissionCheckInternet = ContextCompat.checkSelfPermission(
                this, Manifest.permission.INTERNET);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            Log.i("Mensaje", "No se tiene permiso para la conexion a internet!.");
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.INTERNET}, 225);
        } else {
            Log.i("Mensaje", "Tienes permiso para la conexion a internet.");
        }
    }
    protected void onStart() {
        Log.i("fragment","onStart");
        //inciando fragment
        fragment_almacenes=new FragmentAlmacenes();
        fragment_inicial=new FragmentInicial();
        fragment_folios=new FragmentFolios();
        Log.i("fragment","onCreate");

        getDomain();
        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        //iniciando control de fragment
        //fragmentActivo="";

        if(tablaVacia("almacenes","cod_alm")==true)
        {
            new cargarAlmacenesWS().execute();
        }
        else
        {
            String camara="";
            camara=""+getIntent().getStringExtra("barcode");
            if(camara.equalsIgnoreCase("null"))
            {
                Log.i("camara", "null");
                if(almacenSeleccionado().equalsIgnoreCase(""))
                {
                    //getSupportFragmentManager().beginTransaction().add(R.id.contenedorFragment,fragment_almacenes).commit();
                    fragmentTransaction.add(R.id.contenedorFragment,fragment_almacenes,"ALM").commit();
                    fragmentActivo="ALM";
                }
                else
                {

                    //getSupportFragmentManager().beginTransaction().add(R.id.contenedorFragment,fragment_inicial).commit();
                    fragmentTransaction.add(R.id.contenedorFragment,fragment_inicial,"INI").commit();
                    fragmentActivo="INI";

                }
            }
            else
            {
                Bundle args = new Bundle();
                args.putString("codigo", ""+camara);
                fragment_inicial.setArguments(args);
                fragmentTransaction.add(R.id.contenedorFragment,fragment_inicial,"INI").commit();
                fragmentActivo="INI";
                Log.i("camara", "datos:"+camara);
            }


        }
        super.onStart();
    }
    protected void onResume() {
        Log.i("fragment","onResume");
        super.onResume();
    }
    protected void onPause() {
        Log.i("fragment","onPause");
        super.onPause();
    }
    protected void onStop() {
        Log.i("fragment","onStop");
        super.onStop();
    }
    protected void onDestroy() {
        Log.i("fragment","onDestroy");


        super.onDestroy();
    }
    protected void onRestart() {
        Log.i("fragment","onRestart");
        super.onRestart();
    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        //No call for super(). Bug on API Level > 11.
        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        switch (fragmentActivo)
        {
            case "INI":
                /*
                FragmentTransaction transaction=getSupportFragmentManager().beginTransaction();
                transaction.remove(fragment_inicial);
                transaction.commit();
                */

                fragmentTransaction.remove(fragment_inicial).commit();
                break;
            case "ALM":
                fragmentTransaction.remove(fragment_almacenes).commit();
                break;
            case "FOL":
                fragmentTransaction.remove(fragment_folios).commit();
                break;
        }
        Log.i("fragment","onSaveInstanceState");
        super.onSaveInstanceState(outState);
    }
    public void cargaAlmacenes(View view)
    {
        new cargarAlmacenesWS().execute();
    }


    public String getEmpresa()
    {
        String alm="";
        try{
            Database admin = new Database(contexto,null,1);
            SQLiteDatabase db = admin.getWritableDatabase();
            Cursor fila = db.rawQuery("SELECT nomEmpresa FROM login",null);
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
    public String getIdEmpresa()
    {
        String alm="";
        try{
            Database admin = new Database(contexto,null,1);
            SQLiteDatabase db = admin.getWritableDatabase();
            Cursor fila = db.rawQuery("SELECT id_empresa FROM login",null);
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
            Database admin = new Database(contexto,null,1);
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
    public String almacenSeleccionado()
    {
        String alm="";
        try{
            Database admin = new Database(this,null,1);
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
        Log.i("fragment","onCreateOptionsMenu");
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
            //reenviando aclaracion


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
            if(ll_cargarAlm.getVisibility()==View.GONE)
            {
                if(almacenSeleccionado().equalsIgnoreCase(""))
                {
                    mensajes("seleccionar almacen");
                }
                else
                {
                    FragmentTransaction transaction=getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.contenedorFragment,fragment_inicial);
                    transaction.commit();
                    fragmentActivo="INI";
                }
            }


        } else if (id == R.id.nav_almacenes) {
            if(ll_cargarAlm.getVisibility()==View.GONE)
            {
                FragmentTransaction transaction=getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.contenedorFragment,fragment_almacenes);
                transaction.commit();
                fragmentActivo="ALM";
            }



        } else if (id == R.id.nav_slideshow) {
            if(ll_cargarAlm.getVisibility()==View.GONE)
            {
                if(almacenSeleccionado().equalsIgnoreCase(""))
                {
                    mensajes("seleccionar almacen");
                }
                else
                {
                    FragmentTransaction transaction=getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.contenedorFragment,fragment_folios);
                    transaction.commit();
                    fragmentActivo="FOL";
                }
            }



        } else if (id == R.id.nav_tools) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_cerrar) {
            guardarFolio();

        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    public void guardarFolio()
    {
            JSONObject obj = new JSONObject();
            try {
                obj.put("usuario", getUsuario());
                obj.put("id_empresa", getIdEmpresa());
                obj.put("serie", getSerie());
                obj.put("numero", getNumero());
                Log.i("json",obj.toString());
                new actualizarFolio().execute(obj);
            }
            catch (JSONException e)
            {
                Log.e("error",e.getMessage());
                mensajes(e.getMessage());
            }
    }
    class actualizarFolio extends AsyncTask<JSONObject,Integer,String>
    {
        String validar;
        private ProgressDialog progreso;

        @Override
        protected void onPreExecute()
        {
            progreso = new ProgressDialog(contexto);
            progreso.setMessage("Guardando folio");
            progreso.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progreso.setCancelable(false);
            // progreso.setMax(100);
            // progreso.setProgress(0);
            //progreso.show();
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(JSONObject... params)
        {

            JSONObject obj=params[0];
             try {
                 java.net.URL url = new URL("http://wsar.homelinux.com:3006/actualizar_folio"); //in the real code, there is an ip and a port
                 HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                 conn.setRequestMethod("PUT");
                 conn.setRequestProperty("Content-Type", "application/json");
                 conn.setRequestProperty("Accept", "application/json");
                 conn.setConnectTimeout(10000);//10 segundos espera
                 conn.setDoOutput(true);
                 conn.setDoInput(true);
                 conn.connect();
                 DataOutputStream os = new DataOutputStream(conn.getOutputStream());
                 os.writeBytes(obj.toString());
                 os.flush();
                 os.close();
                 BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                 StringBuilder sb = new StringBuilder();
                 String line;
                 while ((line = br.readLine()) != null) {
                     sb.append(line + "\n");
                 }
                 int status = conn.getResponseCode();
                 if (status < 400) {
                     validar = "OK";
                     String finalJSON = sb.toString();
                     JSONObject jObject = new JSONObject(finalJSON); //Obtenemos el JSON global
                     if (jObject.getBoolean("success") == true) {
                         validar = "OK";
                         mensajeGlobal = "" + jObject.getString("message");
                     } else {
                         validar = "false";
                         mensajeGlobal = jObject.getString("message");
                     }
                     br.close();

                 } else {
                     validar = "false";
                     mensajeGlobal = conn.getResponseMessage();
                 }
                 Log.i("STATUS", String.valueOf(conn.getResponseCode()));
                 Log.i("MSG", conn.getResponseMessage());
                 conn.disconnect();
             }
             catch (Exception e)
             {
                 Log.e("error",e.getMessage());
                 validar="false";
             }
            return validar;

        }
        protected void onProgressUpdate(Integer... i)
        {
            progreso.setProgress(i[0]);
        }
        protected void onPostExecute(String s)
        {
            //progreso.dismiss();
            if(s.equalsIgnoreCase("OK"))
            {
                mensajes(mensajeGlobal);
                eliminarTabla("login");
                eliminarTabla("almacenes");
                eliminarTabla("documento");
                eliminarTabla("articulos");
                eliminarTabla("comentarios");
                Intent i=new Intent(contexto,Login.class);
                startActivity(i);
                finish();
            }
            else
            {
                if(s.equalsIgnoreCase("false"))
                {
                    mensajes(mensajeGlobal);
                    eliminarTabla("login");
                    eliminarTabla("almacenes");
                    eliminarTabla("documento");
                    eliminarTabla("articulos");
                    Intent i=new Intent(contexto,Login.class);
                    startActivity(i);
                    finish();
                }
                else
                {
                    mensajes(mensajeGlobal);
                    eliminarTabla("login");
                    eliminarTabla("almacenes");
                    eliminarTabla("documento");
                    eliminarTabla("articulos");
                    Intent i=new Intent(contexto,Login.class);
                    startActivity(i);
                    finish();
                }
            }
            super.onPostExecute(s);
        }
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

                HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
                HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
                HttpClient cliente = new DefaultHttpClient(httpParameters);
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
            //consultatabla();
            if(s.equalsIgnoreCase("OK"))
            {

                Log.i("consultaAlmacenes"," | "+ll_cargarAlm.getVisibility());
                if(ll_cargarAlm.getVisibility()==View.VISIBLE)
                {
                    ll_cargarAlm.setVisibility(View.GONE);
                }

                mensajes("Se agregaron almacenes con exito");
                if(almacenSeleccionado().equalsIgnoreCase(""))
                {
                    actualizarAlmacen();
                    getSupportFragmentManager().beginTransaction().add(R.id.contenedorFragment,fragment_almacenes).commit();
                    fragmentActivo="ALM";
                }

            }
            else
            {
                eliminarTabla("almacenes");
                ll_cargarAlm.setVisibility(View.VISIBLE);
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
    public void actualizarAlmacen()
    {
        String almaux="";
        Database admin = new Database(contexto,null,1);
        SQLiteDatabase db = admin.getWritableDatabase();
        try{

            Cursor fila = db.rawQuery("SELECT almacenaux FROM login",null);
            if(fila.moveToFirst())
            {
                almaux=fila.getString(0);
                    Log.i("consultaAlmacenes"," | "+fila.getString(0));
            }

        }catch (SQLiteException e)
        {
            Log.e("Error:",""+e.getMessage());
            //mensajes("Error al validar login:"+e.getMessage());
        }
        try{

            ContentValues r = new ContentValues();
            r.put("almacen",almaux);
            db.update("login",r,"usuario='"+ getUsuario() +"'",null);
            db.close();

        }catch (SQLiteException e)
        {
            Log.e("update almacen",e.getMessage());
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
    public String getSerie()
    {
        String alm="";
        try{
            Database admin = new Database(contexto,null,1);
            SQLiteDatabase db = admin.getWritableDatabase();
            Cursor fila = db.rawQuery("SELECT serieOC FROM login",null);
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
    public String getNumero()
    {
        String alm="";
        try{
            Database admin = new Database(contexto,null,1);
            SQLiteDatabase db = admin.getWritableDatabase();
            Cursor fila = db.rawQuery("SELECT numeroOC FROM login",null);
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
