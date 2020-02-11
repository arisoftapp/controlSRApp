package com.example.arisoft.controlsr;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.arisoft.controlsr.Modelo.Calculos;
import com.example.arisoft.controlsr.Modelo.ConsultasBD;
import com.example.arisoft.controlsr.Modelo.CreaJson;
import com.example.arisoft.controlsr.Modelo.Recibido;
import com.example.arisoft.controlsr.Modelo.RecibidoAdapter;
import com.example.arisoft.controlsr.Tools.Database;

import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.channels.FileLock;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;


public class FragmentInicial extends Fragment {


    //variables
    EditText et_folio_factura,et_codigo;
    TextView tv_almacen,tv_folio,tv_prov,tv_fol_dialog;
    Button btn_cancelar,btn_guardar,btn_camara,btn_reenviar,btn_continuar;
    String mensajeGlobal,URL;
    ImageView iv_mp1,iv_mb,iv_mp2,iv_co1,iv_co2,iv_comen;
    ArrayList<Recibido> Recibido_list;
    private RecibidoAdapter Recibido_adap;
    ListView lvItems;
    Float cantidad_gbl,surtido_gbl,por_surtir_gbl;
    String codigo1_gbl,folio_gbl,folio_OC_gbl,serie_OC_gbl,numero_OC_gbl,codigo_camara="",posicion_gbl;
    Boolean aclacarion=false;
    HttpParams httpParameters = new BasicHttpParams();
    int timeoutConnection = 10000;
    int timeoutSocket = 10000;
    Activity actividad = getActivity();
    Context contexto;
    ConsultasBD consultasBD;
    CreaJson crearJson;
    Calculos calculos;




    private OnFragmentInteractionListener mListener;

    public FragmentInicial() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            codigo_camara = getArguments().getString("codigo");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_fragment_inicial, container, false);
        // Inflate the layout for this fragment
        et_folio_factura=(EditText)v.findViewById(R.id.et_folio_factura);
        et_codigo=(EditText)v.findViewById(R.id.et_codigo);
        tv_almacen=(TextView) v.findViewById(R.id.tv_almacen);
        tv_folio=(TextView) v.findViewById(R.id.tv_folio);
        tv_prov=(TextView) v.findViewById(R.id.tv_prov);
        btn_cancelar=(Button)v.findViewById(R.id.btn_cancelar);
        btn_guardar=(Button)v.findViewById(R.id.btn_guardar);
        btn_camara=(Button)v.findViewById(R.id.btn_camara);
        lvItems=(ListView)v.findViewById(R.id.lvItems);
        getDomain();
        contexto=getContext();
        consultasBD=new ConsultasBD();
        crearJson=new CreaJson();
        calculos=new Calculos();

        if(tablaVacia("articulos","codigo")==false)
        {
            //eliminarTabla("articulos");
            actualizarLista();
            if(et_folio_factura.getVisibility()==View.VISIBLE)
            {
                et_folio_factura.setVisibility(View.GONE);
                et_codigo.setText("");
                et_codigo.setVisibility(View.VISIBLE);
                tv_folio.setText(folio_gbl);
                tv_prov.setText(consultaDato("proveedor","documento"));
            }
        }

        Log.i("camara fragment",""+codigo_camara);
        if(codigo_camara.equalsIgnoreCase(""))
        {

        }
        else
        {
            if(et_folio_factura.getVisibility()==View.VISIBLE )
            {
                //mensajes("folio previo visible "+et_folio_factura.getVisibility()+"|"+et_codigo.getVisibility());

                et_folio_factura.setText(""+codigo_camara);
                if(almacenSeleccionado().equalsIgnoreCase(""))
                {
                    mensajes("Seleccionar Almacen");
                }
                else
                {
                    new cargarFactura().execute(et_folio_factura.getText().toString().trim(),almacenSeleccionado());
                }

            }
            else
            {
                if(et_codigo.getVisibility()==View.VISIBLE)
                {
                    //mensajes("codigo visible");
                    contarSurtido();
                }
            }
        }

        tv_almacen.setText(almacenSeleccionado());
        //botno camara
        btn_camara.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //mensajes("camara");
                Intent i=new Intent(getContext(),barcode.class);
                startActivity(i);
                //actividad.onBackPressed();
            }
        });
        //boton guardar
        btn_guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //calcularDescuento();

                //mensajes("iva "+calculos.getIva(contexto));
                //mensajes("subtotal "+calculos.getSumatotal(contexto));
                //mensajes("descuento "+calculos.calcularDescuento(contexto));
                //mensajes("total "+calculos.getTotal(contexto));


                //mensajes("guardar");

                if(existenCambios()==false)
                {
                    mensajes("Sin cambios");
                }
                else
                {
                    AlertDialog dialog = new AlertDialog.Builder(FragmentInicial.this.getContext())
                            .setTitle("Finalizar Recibido")
                            .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    //trabajando
                                    //new modificarBackorderJson().execute();
                                    //new soloConsultaFolio().execute(getFolioOC(),almacenSeleccionado());
                                    if(consultasBD.getCrear_comren(contexto)==false)
                                    {
                                        new soloConsultaFolio().execute(getFolioOC(),almacenSeleccionado());
                                    }
                                    else
                                    {
                                        procesandoOrden();
                                    }

                                }
                            })
                            .setNegativeButton("Cancelar", null)
                            .create();
                    dialog.show();
                }


            }
        });

        //btn cancelar
        btn_cancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {



                if(tablaVacia("articulos","codigo")==false)
                {
                    AlertDialog dialog = new AlertDialog.Builder(FragmentInicial.this.getContext())
                            .setTitle("Eliminar")
                            .setMessage("¿Desea eliminar el previo de compra sin guardar?")
                            .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    quitarDatos();

                                }
                            })
                            .setNegativeButton("Cancelar", null)
                            .create();
                    dialog.show();

                }
                else
                {
                    mensajes("No tiene ningun previo de compra seleccionado");
                }



            }
        });


        //enter en codigo
        et_codigo.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                contarSurtido();

                return false;
            }
        });

        //enter en folio factura

        et_folio_factura.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if(almacenSeleccionado().equalsIgnoreCase(""))
                {
                    mensajes("Seleccionar Almacen");
                }
                else
                {
                    new cargarFactura().execute(et_folio_factura.getText().toString().trim(),almacenSeleccionado());
                }

                return false;
            }
        });
        //click en lista
        lvItems.setLongClickable(true);
        lvItems.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int position, long l) {
                LayoutInflater inflater = FragmentInicial.this.getLayoutInflater();
                View v = inflater.inflate(R.layout.dialog_editar_surtidas, null);

                final EditText et_nuevo_surtido = v.findViewById(R.id.editTextConteo);
                final TextView tvcodigo = v.findViewById(R.id.tv_cod);
                final TextView tvdesc = v.findViewById(R.id.tv_descr);
                final TextView tvexi= v.findViewById(R.id.tv_exiact);
                final ImageView menos = (ImageView) v.findViewById(R.id.imageView5);
                ImageView mas = (ImageView) v.findViewById(R.id.imageView4);
                et_nuevo_surtido.setText(Recibido_list.get(position).getSurtidas().toString());
                tvcodigo.setText(Recibido_list.get(position).getCodigo());
                tvdesc.setText(Recibido_list.get(position).getDescripcion());
                final Float recibida_aux=surtidaAux(tvcodigo.getText().toString());
                final String pos=Recibido_list.get(position).getPosicion();
                Log.i("posicion",pos);
                //definir boton menos
                menos.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if(et_nuevo_surtido.getText().toString().equalsIgnoreCase(""))
                        {
                            mensajes("Ingrese cantidad");
                            et_nuevo_surtido.setText(""+recibida_aux);
                        }
                        else
                        {
                            float nuevo_conteo= Float.parseFloat(et_nuevo_surtido.getText().toString());
                            if(nuevo_conteo<=0)
                            {
                                mensajes("Surtidas es igual a 0");
                            }
                            else
                            {
                                if(recibida_aux<nuevo_conteo)
                                nuevo_conteo=nuevo_conteo-1;
                                //String nuevo_conteo_str=Float.toString(nuevo_conteo);
                                et_nuevo_surtido.setText(nuevo_conteo+"");
                            }
                        }

                    }
                });
                //definir boton mas
                mas.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(et_nuevo_surtido.getText().toString().equalsIgnoreCase(""))
                        {
                            mensajes("Ingrese cantidad");
                            et_nuevo_surtido.setText("0.0");
                        }
                        else
                        {
                            float nuevo_conteo= Float.parseFloat(et_nuevo_surtido.getText().toString());
                            float cant=Recibido_list.get(position).getCantidad();
                            if(nuevo_conteo<cant)
                            {
                                nuevo_conteo=nuevo_conteo+1;
                                et_nuevo_surtido.setText(nuevo_conteo+"");
                            }
                            else
                            {
                                if(nuevo_conteo==cant)
                                {
                                    mensajes("Articulo Surtido Completamente");
                                }
                            }
                        }


                    }
                });
                //edittext


                AlertDialog dialog = new AlertDialog.Builder(FragmentInicial.this.getContext())
                        .setTitle("Editar Surtidas")
                        .setView(v)
                        .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if(et_nuevo_surtido.getText().toString().equalsIgnoreCase(""))
                                {
                                    mensajes("Ingrese cantidad");
                                }
                                else
                                {
                                    Float surtidas_lcl,por_surtir_lcl,cantidad_lcl;
                                    surtidas_lcl=Float.parseFloat(et_nuevo_surtido.getText().toString());
                                    cantidad_lcl=Recibido_list.get(position).getCantidad();
                                    String codigo_lcl=Recibido_list.get(position).getCodigo();
                                    if(surtidas_lcl<0)
                                    {
                                        mensajes("Cantidad Menor que 0 ");
                                    }
                                    else
                                    {
                                        if(surtidas_lcl>cantidad_lcl)
                                        {
                                            mensajes("Cantidad Mayor que "+cantidad_lcl);
                                        }
                                        else
                                        {
                                            Log.i("recibido","|"+recibida_aux+"|"+surtidas_lcl);
                                            if(recibida_aux<=surtidas_lcl)
                                            {
                                                por_surtir_lcl=cantidad_lcl-surtidas_lcl;
                                                actualizarBDpos(surtidas_lcl,por_surtir_lcl,codigo_lcl,pos);
                                                actualizarLista();
                                            }
                                            else
                                            {
                                                mensajes("Cantidad menor a la inicial");
                                            }

                                        }
                                    }


                                }


                            }
                        })
                        .setNegativeButton("Cancelar", null)
                        .create();
                dialog.show();

                return false;
            }
        });


        return v;
    }
    public void procesandoOrden()
    {

        if(consultasBD.getCrear_comren(contexto)==false)
        {
            new crearOrdenJSON().execute();
        }
        else
        {
            if(consultasBD.getComent_completos(contexto)==false)
            {
                new crearComentarioOCJSON().execute();
            }
            else
            {
                if(consultasBD.getMod_back(contexto)==false)
                {
                    new modificarBackorderJson().execute();
                }
                else
                {
                    if(consultasBD.getComent_completos(contexto)==false)
                    {
                        obtenerDatosComdoc();
                    }
                    else
                    {
                        dialogOrdenCompleta();
                        //mensajes("orden creada");
                    }
                }



            }


        }
    }
    public float calcularDescuento()
    {
        float descuentoTotal = 0;
        try{
            Database admin = new Database(getContext(),null,1);
            SQLiteDatabase db = admin.getWritableDatabase();
            Cursor fila = db.rawQuery("SELECT surtido,surtidoaux,costo,descuento1,descuento2,descuento3,descuento4,descuento5,tipocambio FROM articulos where surtidoaux!=surtido",null);
            if(fila.moveToFirst())
            {
                do{
                    float cant=fila.getFloat(0)-fila.getFloat(1);
                    float descuento=fila.getFloat(3);
                    float tipocambio=fila.getFloat(8);
                    float costoTotal=cant*fila.getFloat(2)*tipocambio;
                    if(descuento>0)//descuento1
                    {
                        descuento=descuento/100;
                        descuentoTotal=descuentoTotal+costoTotal*descuento;
                        descuento=fila.getFloat(4);
                        if(descuento>0)//descuento2
                        {
                            descuento=descuento/100;
                            descuentoTotal=descuentoTotal+costoTotal*descuento;
                            descuento=fila.getFloat(5);
                            if(descuento>0)//descuento3
                            {
                                descuento=descuento/100;
                                descuentoTotal=descuentoTotal+costoTotal*descuento;
                                descuento=fila.getFloat(6);
                                if(descuento>0)//descuento4
                                {
                                    descuento=descuento/100;
                                    descuentoTotal=descuentoTotal+costoTotal*descuento;
                                    descuento=fila.getFloat(7);
                                    if(descuento>0)//descuento5
                                    {
                                        descuento=descuento/100;
                                        descuentoTotal=descuentoTotal+costoTotal*descuento;
                                    }

                                }

                            }
                        }


                    }
                    //descuentoTotal=descuentoTotal*tipocambio;
                    Log.i("consulta"," | "+fila.getString(0)+
                            " | "+fila.getString(1)+
                            " | "+fila.getString(2)+
                            " | "+fila.getString(3)+
                            " | "+fila.getString(4)+
                            " | "+fila.getString(5)+
                            " | "+fila.getString(6)+
                            " | "+fila.getString(7)
                    );
                    Log.i("consultaCalculo",
                            " | "+cant+
                            " | "+costoTotal+
                                    " | "+descuentoTotal+
                                    " | "+tipocambio
                    );
                }while (fila.moveToNext());
            }
            else
            {
                descuentoTotal=0;
            }

            db.close();
        }catch (Exception e)
        {
            Log.e("Error:",""+e.getMessage());
            descuentoTotal=0;
        }
        if(descuentoTotal>0)
        {
            String valor=""+descuentoTotal;
            valor=""+formatearDecimales(Double.parseDouble(valor),2);
            descuentoTotal= Float.parseFloat(valor);

        }
        return descuentoTotal;
    }
    public void quitarDatos()
    {
        eliminarTabla("articulos");
        eliminarTabla("documento");
        eliminarTabla("comentarios");
        actualizarLista();
        if(et_folio_factura.getVisibility()==View.GONE)
        {
            et_folio_factura.setVisibility(View.VISIBLE);
            et_codigo.setText("");
            et_codigo.setVisibility(View.GONE);
        }
        tv_folio.setText("----");
        tv_prov.setText("----");
    }
    public void aumentarFolio()
    {
        String folioOC="",serie="";
        String numero="";
        try{
            Database admin = new Database(getContext(),null,1);
            SQLiteDatabase db = admin.getWritableDatabase();
            Cursor fila = db.rawQuery("SELECT serieOC,numeroOC FROM login",null);
            if(fila.moveToFirst())
            {
                do{
                    numero=fila.getString(1);
                    serie=fila.getString(0);


                }while (fila.moveToNext());
            }
            else {
                //Toast.makeText(getContext(), "Sin cambios", Toast.LENGTH_SHORT).show();
            }
            db.close();
        }catch (SQLiteException e)
        {
            mensajes("Error al consultar codigo:"+e.getMessage());
        }
        String cadenas_Aux[] = new String[numero.length()];
        //cadenas_Aux=numero.charAt(0);
        //folioOC=serie+numero;
        int i=numero.length()-1;
        boolean siguiente=true;
        int num;
        while (i>=0)
        {
            //cadenas_Aux[i]=""+numero.charAt(i);
            //num=Integer.parseInt(cadenas_Aux[i]);
            num=Integer.parseInt(""+numero.charAt(i));
            if(siguiente==true)
            {
                if(num==9)
                {
                    siguiente=true;
                    num=0;
                }
                else
                {
                    num=num+1;
                    siguiente=false;
                }

            }
            cadenas_Aux[i]=""+num;

            //Log.i("cadena","probar"+cadenas_Aux[i]+" i:"+i);
            i--;
        }
        int max=cadenas_Aux.length-1;
        int x=0;
        String numero_aux="";
        while (x<=max)
        {
            numero_aux=numero_aux+cadenas_Aux[x];
            x++;
        }
        folioOC=serie+numero_aux;
        Log.i("cadena","probar "+folioOC+" x:"+max);
        folio_OC_gbl=folioOC;
        serie_OC_gbl=serie;
        numero_OC_gbl=numero_aux;
        new consultaFolio().execute(folioOC,almacenSeleccionado());
        //mensajes(""+cadenas_Aux[0]);
    }
    public void obtenerDatosComdoc()
    {
        String folio_previo,almacen,folio_orden,totalreg,totaluds,sumatotal,iva,total,descuento;
        Float totalf;
        folio_previo=tv_folio.getText().toString();
        almacen=tv_almacen.getText().toString();
        folio_orden=getFolioOC();
        totalreg=getTotalreg();
        totaluds=getTotaluds();
        //sumatotal=""+formatearDecimales(Double.parseDouble(getSumatotal()),2);
        sumatotal=""+calculos.getSumatotal(contexto);
        iva=""+calculos.getIva(contexto);
        //totalf=Float.parseFloat(sumatotal)+Float.parseFloat(iva);
        total=""+calculos.getTotal(contexto);
        descuento=""+calculos.calcularDescuento(contexto);
        //total=""+formatearDecimales(Double.parseDouble(total),2);
        //mensajes(folio_previo+"|"+almacen+"|"+folio_orden+"|"+totalreg+"|"+totaluds+"|"+sumatotal);
        Log.i("obtenerdatoscomdocs",folio_previo+"|"+almacen+"|"+folio_orden+"|"+totalreg+"|"+totaluds+"|"+sumatotal+"|"+iva+"|"+total);
        new crearComdoc().execute(folio_previo,almacen,folio_orden,totalreg,totaluds,sumatotal,iva,total,""+descuento);

    }
    public void modificarBackorder()
    {
        String cantidad,articulo;
        try{
            Database admin = new Database(getContext(),null,1);
            SQLiteDatabase db = admin.getWritableDatabase();
            Cursor fila = db.rawQuery("SELECT codigo,surtido,surtidoaux FROM articulos WHERE surtidoaux!=surtido and backorder='false'",null);
            if(fila.moveToFirst())
            {
                do{

                    articulo=fila.getString(0);
                    Float cant=fila.getFloat(1)-fila.getFloat(2);
                    cantidad=""+cant;
                    Log.i("consultacrearComren",cantidad+"|"+articulo+"|");
                    new modificarBack().execute(articulo,cantidad);
                }while (fila.moveToNext());
            }
            db.close();
        }catch (SQLiteException e)
        {
            mensajes("Error al consultar codigo:"+e.getMessage());
        }
    }
    public void crearComren()
    {
        String folio_previo,cantidad,articulo,folio_orden;
        int posicion=0;
        folio_previo=tv_folio.getText().toString();
        folio_orden=getFolioOC();
        try{
            Database admin = new Database(getContext(),null,1);
            SQLiteDatabase db = admin.getWritableDatabase();
            Cursor fila = db.rawQuery("SELECT codigo,surtido,surtidoaux FROM articulos WHERE surtidoaux!=surtido and crear='false' ",null);
            if(fila.moveToFirst())
            {
                do{
                    posicion=posicion+1;
                    articulo=fila.getString(0);
                    Float cant=fila.getFloat(1)-fila.getFloat(2);
                    cantidad=""+cant;
                    Log.i("consultacrearComren",folio_previo+"|"+posicion+"|"+cantidad+"|"+articulo+"|"+folio_orden+"|"+fila.getCount());
                    new crearComren().execute(folio_previo,""+posicion,cantidad,articulo,folio_orden,""+fila.getCount());
                    //new modificarBack().execute(articulo,cantidad);

                }while (fila.moveToNext());
            }
            db.close();
        }catch (SQLiteException e)
        {
            mensajes("Error al consultar codigo:"+e.getMessage());
        }

        //consultasBD.posComtDoc(posicion,folio_previo,contexto);
    }
    public void consultarComentarios(int posicion)
    {

            Log.i("consultacoment","comentarios");
            String folio_orden,coment;
        String resultadoAsynctask;
        boolean validar=false;
            //int posicion=0;
            folio_orden=getFolioOC();
            try{
                Database admin = new Database(getContext(),null,1);
                SQLiteDatabase db = admin.getWritableDatabase();
                Cursor fila = db.rawQuery("SELECT comentario,id FROM comentarios where estatus='A' ",null);
                if(fila.moveToFirst())
                {
                    do{
                        Log.i("consultacoment","con comentarios");
                        posicion=posicion+1;
                        coment=fila.getString(0);
                        String cadena=coment.replace(".","");
                        cadena=cadena.replace("-","");
                        cadena=cadena.replace(" ","");
                        //new insertarComentariosOC().execute(folio_orden,""+posicion,cadena,fila.getString(1),coment);
                        new insertarComentariosOCJson().execute(folio_orden,""+posicion,cadena,fila.getString(1),coment);
                    }while (fila.moveToNext());
                }
                else
                {
                    Log.i("consultacoment","sin comentarios");
                    consultasBD.verificarComentarios(tv_folio.getText().toString(),contexto);
                    //consultasBD.getComent_completos(contexto);
                    actualizarVistaInfo();
                }
                db.close();
            }catch (SQLiteException e)
            {
                mensajes("Error al consultar codigo:"+e.getMessage());
            }
    }
    class modificarBackorderJson extends AsyncTask<String,Integer,String>
    {
        String validar;
        private ProgressDialog progreso;

        @Override
        protected void onPreExecute()
        {
            progreso = new ProgressDialog(getContext());
            progreso.setMessage("Modificando Backorder");
            progreso.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progreso.setCancelable(false);
            progreso.show();
            super.onPreExecute();
        }
        @Override
        protected String doInBackground(String... params)
        {
            Log.i("CREARORDEN","prueba");

            try{
                java.net.URL url = new URL(URL+"modificar_backorder");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setRequestProperty("Accept", "application/json");
                /*
                conn.setConnectTimeout(timeoutConnection);//10 segundos espera
                conn.setReadTimeout(timeoutSocket);

                 */
                conn.setDoOutput(true);
                conn.setDoInput(true);
                conn.connect();
                DataOutputStream os = new DataOutputStream(conn.getOutputStream());
                os.writeBytes(crearJson.crearJsonBackorder(almacenSeleccionado(),contexto).toString());
                os.flush();
                os.close();
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    sb.append(line + "\n");
                }

                int status = conn.getResponseCode();
                Log.i("CREARORDEN","entro"+status);
                if (status <= 500) {
                    validar = "OK";
                    String finalJSON = sb.toString();
                    JSONObject jObject = new JSONObject(finalJSON); //Obtenemos el JSON global
                    Log.i("CREARORDEN",""+jObject.getBoolean("success"));
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
            }catch ( Exception e)
            {
                validar="false";
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
                //mensajes("se creo orden");
                consultasBD.cambiarEstatusBackorder("true",tv_folio.getText().toString(),contexto);
                //trabajando
                obtenerDatosComdoc();
            }
            else
            {
                consultasBD.cambiarEstatusBackorder("true",tv_folio.getText().toString(),contexto);
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

    class modificarPrevioJson extends AsyncTask<String,Integer,String>
    {
        String validar;
        private ProgressDialog progreso;

        @Override
        protected void onPreExecute()
        {
            progreso = new ProgressDialog(getContext());
            progreso.setMessage("Creando Orden");
            progreso.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progreso.setCancelable(false);
            //progreso.show();
            super.onPreExecute();
        }
        @Override
        protected String doInBackground(String... params)
        {
            Log.i("CREARORDEN","prueba");

            try{
                java.net.URL url = new URL(URL+"modificar_previo_json");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setRequestProperty("Accept", "application/json");
                /*
                conn.setConnectTimeout(timeoutConnection);//10 segundos espera
                conn.setReadTimeout(timeoutSocket);

                 */
                conn.setDoOutput(true);
                conn.setDoInput(true);
                conn.connect();
                DataOutputStream os = new DataOutputStream(conn.getOutputStream());
                os.writeBytes(crearJson.crearJsonPrevio(tv_folio.getText().toString(),contexto).toString());
                os.flush();
                os.close();
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    sb.append(line + "\n");
                }

                int status = conn.getResponseCode();
                Log.i("CREARORDEN","entro"+status);
                if (status <= 500) {
                    validar = "OK";
                    String finalJSON = sb.toString();
                    JSONObject jObject = new JSONObject(finalJSON); //Obtenemos el JSON global
                    Log.i("CREARORDEN",""+jObject.getBoolean("success"));
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
            }catch ( Exception e)
            {
                validar="false";
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
            consultasBD.cambiarDocComren("true",tv_folio.getText().toString(),contexto);
            //progreso.dismiss();
            if(surtidoParcial()==true)
            {
                //InsertarMacropro
                new modificarPrevioComdoc().execute(tv_folio.getText().toString().trim(),almacenSeleccionado(),"A");
                //enviarAclaracion("1");

            }
            else
            {
                aclacarion=false;
                new modificarPrevioComdoc().execute(tv_folio.getText().toString().trim(),almacenSeleccionado(),"S");
                //enviarAclaracion("3");
            }
            /*
            if(s.equalsIgnoreCase("OK"))
            {
                //mensajes("se creo orden");
                //consultasBD.cambiarEstatusComentarios("true",tv_folio.getText().toString(),contexto);
                //new modificarBackorderJson().execute();
                //trabajando
            }
            else
            {
                //consultasBD.cambiarEstatusComentarios("false",tv_folio.getText().toString(),contexto);
                if(s.equalsIgnoreCase("false"))
                {
                   // mensajes(mensajeGlobal);
                }
                else
                {
                    //mensajes(mensajeGlobal);
                }
            }

             */
            super.onPostExecute(s);
        }
    }
    class crearComentarioOCJSON extends AsyncTask<String,Integer,String>
    {
        String validar;
        private ProgressDialog progreso;

        @Override
        protected void onPreExecute()
        {
            progreso = new ProgressDialog(getContext());
            progreso.setMessage("Creando Orden");
            progreso.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progreso.setCancelable(false);
            progreso.show();
            super.onPreExecute();
        }
        @Override
        protected String doInBackground(String... params)
        {
            Log.i("CREARORDEN","prueba");

            try{
                java.net.URL url = new URL(URL+"crear_comentarios");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setRequestProperty("Accept", "application/json");
                /*
                conn.setConnectTimeout(timeoutConnection);//10 segundos espera
                conn.setReadTimeout(timeoutSocket);

                 */
                conn.setDoOutput(true);
                conn.setDoInput(true);
                conn.connect();
                DataOutputStream os = new DataOutputStream(conn.getOutputStream());
                os.writeBytes(crearJson.crearJsonComentariosOC(getFolioOC(),contexto).toString());
                os.flush();
                os.close();
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    sb.append(line + "\n");
                }

                int status = conn.getResponseCode();
                Log.i("CREARORDEN","entro"+status);
                if (status <= 500) {
                    validar = "OK";
                    String finalJSON = sb.toString();
                    JSONObject jObject = new JSONObject(finalJSON); //Obtenemos el JSON global
                    Log.i("CREARORDEN",""+jObject.getBoolean("success"));
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
            }catch ( Exception e)
            {
                validar="false";
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
                //mensajes("se creo orden");
                consultasBD.cambiarEstatusComentarios("true",tv_folio.getText().toString(),contexto);
                new modificarBackorderJson().execute();
                //trabajando
            }
            else
            {
                consultasBD.cambiarEstatusComentarios("false",tv_folio.getText().toString(),contexto);
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
    //trabajando
    class crearOrdenJSON extends AsyncTask<String,Integer,String>
    {
        String validar;
        private ProgressDialog progreso;

        @Override
        protected void onPreExecute()
        {
            progreso = new ProgressDialog(getContext());
            progreso.setMessage("Creando Orden");
            progreso.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progreso.setCancelable(false);
            progreso.show();
            super.onPreExecute();
        }
        @Override
        protected String doInBackground(String... params)
        {
            Log.i("CREARORDEN","prueba");

            try{
                java.net.URL url = new URL(URL+"crear_orden");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setRequestProperty("Accept", "application/json");
                /*
                conn.setConnectTimeout(timeoutConnection);//10 segundos espera
                conn.setReadTimeout(timeoutSocket);

                 */
                conn.setDoOutput(true);
                conn.setDoInput(true);
                conn.connect();
                DataOutputStream os = new DataOutputStream(conn.getOutputStream());
                os.writeBytes(crearJson.crearJsonOC(getFolioOC(),contexto).toString());
                os.flush();
                os.close();
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    sb.append(line + "\n");
                }

                int status = conn.getResponseCode();
                Log.i("CREARORDEN","entro"+status);
                if (status <= 500) {
                    validar = "OK";
                    String finalJSON = sb.toString();
                    JSONObject jObject = new JSONObject(finalJSON); //Obtenemos el JSON global
                    Log.i("CREARORDEN",""+jObject.getBoolean("success"));
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
            }catch ( Exception e)
            {
                validar="false";
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
                //mensajes("se creo orden");
                consultasBD.cambiarEstatusComren("true",tv_folio.getText().toString(),contexto);
                new crearComentarioOCJSON().execute();

            }
            else
            {
                consultasBD.cambiarEstatusComren("false",tv_folio.getText().toString(),contexto);
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
    class insertarComentariosOCJson extends AsyncTask<String,Integer,String>
    {
        String validar,folio;
        String comentario;
        String idComentario;
        private ProgressDialog progreso;

        @Override
        protected void onPreExecute()
        {
            progreso = new ProgressDialog(getContext());
            progreso.setMessage("Insertando comentarios");
            progreso.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progreso.setCancelable(false);
            //progreso.show();
            super.onPreExecute();
        }
        @Override
        protected String doInBackground(String... params)
        {
            folio=params[0];
            String posicion=params[1];
            comentario=params[2];
            idComentario=params[3];
            //creando json de comentario
            JSONObject jsonComent = new JSONObject();
            try {
                jsonComent.put("comentario", params[4]);

            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }


            try{
                java.net.URL url = new URL(URL+"insert_comren_comentario/"+folio+"/"+posicion+"/"+comentario);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setRequestProperty("Accept", "application/json");
                /*
                conn.setConnectTimeout(timeoutConnection);//10 segundos espera
                conn.setReadTimeout(timeoutSocket);

                 */
                conn.setDoOutput(true);
                conn.setDoInput(true);
                conn.connect();
                DataOutputStream os = new DataOutputStream(conn.getOutputStream());
                os.writeBytes(jsonComent.toString());
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
            }catch ( Exception e)
            {
                validar="false";
                mensajeGlobal="Error:"+e.getMessage();
            }


            /*

            try {
                HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
                HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
                HttpClient cliente = new DefaultHttpClient(httpParameters);
                HttpGet htpoget = new HttpGet(URL+"insert_comren_coment/"+folio+"/"+posicion+"/"+comentario);
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
                validar="false";
                mensajeGlobal="Error:"+e.getMessage();
            }

            */

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
                //mensajes("se inserto comentario");
                consultasBD.cambiarEstatusComentario("G",idComentario,contexto);
            }
            else
            {
                consultasBD.cambiarEstatusComentario("A",idComentario,contexto);
                if(s.equalsIgnoreCase("false"))
                {
                    //mensajes(mensajeGlobal);
                }
                else
                {
                    //mensajes(mensajeGlobal);
                }
            }
            consultasBD.verificarComentarios(tv_folio.getText().toString(),contexto);
            actualizarVistaInfo();
            super.onPostExecute(s);
        }
    }
    public static Double formatearDecimales(Double numero, Integer numeroDecimales) {
        return Math.round(numero * Math.pow(10, numeroDecimales)) / Math.pow(10, numeroDecimales);
    }
    public void modificarComren()
    {
        try{
            Database admin = new Database(getContext(),null,1);
            SQLiteDatabase db = admin.getWritableDatabase();
            Cursor fila = db.rawQuery("SELECT * FROM articulos WHERE surtidoaux!=surtido and modificar='false'",null);
            if(fila.moveToFirst())
            {
                do{
                    //Log.i("consultamodificarcomren",""+fila.getString(0)+"-"+fila.getString(5));
                    new modificarPrevioComren().execute(tv_folio.getText().toString().trim(),fila.getString(5),fila.getString(0));
                }while (fila.moveToNext());
            }
            else {
                //Toast.makeText(getContext(), "Sin cambios", Toast.LENGTH_SHORT).show();
            }
            db.close();
        }catch (SQLiteException e)
        {
            mensajes("Error al consultar codigo:"+e.getMessage());
        }
    }

    public boolean existenCambios()
    {
        boolean cambios=false;
        try{
            Database admin = new Database(getContext(),null,1);
            SQLiteDatabase db = admin.getWritableDatabase();
            Cursor fila = db.rawQuery("SELECT * FROM articulos WHERE surtidoaux!=surtido",null);
            if(fila.moveToFirst())
            {
                cambios=true;
            }
            else {
                cambios=false;
                //Toast.makeText(getContext(), "Sin cambios", Toast.LENGTH_SHORT).show();
            }
            db.close();
        }catch (SQLiteException e)
        {
            cambios=false;
            mensajes("Error al consultar codigo:"+e.getMessage());
        }

        return cambios;
    }
    public Float surtidaAux(String codigo)
    {
        Float surt= Float.valueOf(0);
        try{
            Database admin = new Database(getContext(),null,1);
            SQLiteDatabase db = admin.getWritableDatabase();
            Cursor fila = db.rawQuery("SELECT surtidoaux FROM articulos WHERE codigo='"+codigo+"' OR codigo2='"+codigo+"'",null);
            if(fila.moveToFirst())
            {
                do{
                    surt=fila.getFloat(0);
                }while (fila.moveToNext());
            }
            db.close();
        }catch (SQLiteException e)
        {
            mensajes("Error al consultar codigo:"+e.getMessage());
        }

        return surt;
    }
    public boolean surtidoParcial()
    {
        boolean surtidoParcial=false;
        int cant=0;
        try{
            Database admin = new Database(getContext(),null,1);
            SQLiteDatabase db = admin.getWritableDatabase();
            Cursor fila = db.rawQuery("SELECT * FROM articulos WHERE por_surtir>0",null);
            if(fila.moveToFirst())
            {
                surtidoParcial=true;
            }
            else {
                surtidoParcial=false;
                //Toast.makeText(getContext(), "Sin cambios", Toast.LENGTH_SHORT).show();
            }
            db.close();
        }catch (SQLiteException e)
        {
            surtidoParcial=false;
            mensajes("Error al consultar codigo:"+e.getMessage());
        }

        return surtidoParcial;
    }
    public void contarSurtido()
    {

        if(consultaCodigo(et_codigo.getText().toString().trim())==true)
        {
            if(por_surtir_gbl<1)
            {
                mensajes("Articulo Surtido Completo");
                et_codigo.setText("");
            }
            else
            {
                if(por_surtir_gbl>0)
                {
                    por_surtir_gbl=por_surtir_gbl-1;
                    surtido_gbl=surtido_gbl+1;
                    //actualizarBD(surtido_gbl,por_surtir_gbl,codigo1_gbl);
                    actualizarBDpos(surtido_gbl,por_surtir_gbl,codigo1_gbl,posicion_gbl);
                    actualizarLista();
                    et_codigo.setText("");
                }

            }

        }
    }
    public void actualizarBD(float surtido, float por_surtir, String codigo)
    {
        try{
            Database admin=new Database(getContext(),null,1);
            SQLiteDatabase db = admin.getWritableDatabase();
            ContentValues r = new ContentValues();
            r.put("surtido",surtido);
            r.put("por_surtir",por_surtir);
            db.update("articulos",r, "codigo='"+ codigo +"'",null);
            db.close();
        }catch (SQLiteException e)
        {
            Toast.makeText(getContext(), "Error:"+e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    public void actualizarBDpos(float surtido, float por_surtir, String codigo,String pos)
    {
        try{
            Database admin=new Database(getContext(),null,1);
            SQLiteDatabase db = admin.getWritableDatabase();
            ContentValues r = new ContentValues();
            r.put("surtido",surtido);
            r.put("por_surtir",por_surtir);
            db.update("articulos",r, "codigo='"+ codigo +"' and posicion='"+pos+"'",null);
            db.close();
        }catch (SQLiteException e)
        {
            Toast.makeText(getContext(), "Error:"+e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    public boolean consultaCodigo(String codigo)
    {

        boolean consulta=false,seguir=true;

        try{
            Database admin = new Database(getContext(),null,1);
            SQLiteDatabase db = admin.getWritableDatabase();
            Cursor fila = db.rawQuery("SELECT * FROM articulos WHERE codigo='"+codigo+"' OR codigo2='"+codigo+"'",null);
            Log.i("buscarCod",""+fila.getCount());
            if(fila.getCount()==0)
            {
                Toast.makeText(getContext(), "codigo no encontrado", Toast.LENGTH_SHORT).show();
            }
            if(fila.getCount()==1)
            {
                if(fila.moveToFirst())
                {
                    do{
                        codigo1_gbl=fila.getString(0);
                        cantidad_gbl=fila.getFloat(4);
                        surtido_gbl=fila.getFloat(5);
                        por_surtir_gbl=fila.getFloat(6);
                        posicion_gbl=fila.getString(3);

                        Log.i("buscarCod",
                                " | "+fila.getString(0)+
                                        " | "+fila.getString(1)+
                                        " | "+fila.getString(2)+
                                        " | "+fila.getString(3)+
                                        " | "+fila.getString(4)+
                                        " | "+fila.getString(5)+
                                        " | "+fila.getString(6)+
                                        " | "+fila.getString(7)
                        );
                    }while (fila.moveToNext());

                    consulta=true;
                }
                else {
                    consulta=false;
                    Toast.makeText(getContext(), "codigo no encontrado", Toast.LENGTH_SHORT).show();
                }
            }
            else
            {
                if(fila.getCount()>1)
                {
                    fila.moveToFirst();
                    int cont=0;
                    while(seguir==true && cont<fila.getCount())
                    {
                        cont=cont+1;
                        if(fila.getFloat(6)>0)
                        {
                            codigo1_gbl=fila.getString(0);
                            cantidad_gbl=fila.getFloat(4);
                            surtido_gbl=fila.getFloat(5);
                            por_surtir_gbl=fila.getFloat(6);
                            posicion_gbl=fila.getString(3);
                            Log.i("buscarCod",
                                    " | "+fila.getString(0)+
                                            " | "+fila.getString(1)+
                                            " | "+fila.getString(2)+
                                            " | "+fila.getString(3)+
                                            " | "+fila.getString(4)+
                                            " | "+fila.getString(5)+
                                            " | "+fila.getString(6)+
                                            " | "+fila.getString(7)
                            );


                            consulta=true;
                            seguir=false;
                        }
                        else {
                            consulta=false;
                            seguir=true;
                            fila.moveToNext();
                            if(cont==fila.getCount())
                            {
                                Toast.makeText(getContext(), "codigos surtidos completamente", Toast.LENGTH_SHORT).show();
                            }

                        }
                    }


                }
            }

            db.close();
        }catch (SQLiteException e)
        {
            consulta=false;
            mensajes("Error al consultar codigo:"+e.getMessage());
        }

        return consulta;

    }
    class cargarFactura extends AsyncTask<String,Integer,String>
    {
        String validar;
        private ProgressDialog progreso;

        @Override
        protected void onPreExecute()
        {
            progreso = new ProgressDialog(getContext());
            progreso.setMessage("Consultando Previo");
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
            String folio=params[0],almacen=params[1];
            try {

                HttpConnectionParams.setConnectionTimeout(httpParameters, 180000);
                HttpConnectionParams.setSoTimeout(httpParameters, 180000);


                HttpClient cliente = new DefaultHttpClient(httpParameters);
                HttpGet htpoget = new HttpGet(URL+"consulta_previo/"+folio+"/"+almacen);
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
                    JSONArray jArray = jObject.getJSONArray("previo");
                    Log.i("cargarcomentario",""+jObject.getBoolean("success_coment"));
                    if(jObject.getBoolean("success_coment")==true)
                    {
                        JSONArray cArray = jObject.getJSONArray("comentarios");
                        for (int i=0; i < cArray.length(); i++) //Miramos en todos los objetos del array de objetos results
                        {
                            try {

                                JSONObject objeto2 = cArray.getJSONObject(i); //Obtenemos cada uno de los objetos del array results
                                String comentario=objeto2.getString("comentario");
                                Log.i("cargarcomentario",""+comentario);
                                try{
                                    //publishProgress(i+1);
                                    Database admin=new Database(getContext(),null,1);
                                    SQLiteDatabase db = admin.getWritableDatabase();
                                    ContentValues r = new ContentValues();
                                    r.put("folio_previo",folio);
                                    r.put("comentario",comentario);
                                    r.put("estatus","A");
                                    r.put("id",i);
                                    db.insert("comentarios",null,r);
                                    db.close();
                                }
                                catch (SQLiteException e)
                                {
                                    mensajeGlobal="error al insertar comentarios en base de datos:"+e.getMessage();
                                    validar="false";
                                }
                            } catch (JSONException e) {
                                mensajeGlobal="error al hacer peticion: "+e.getMessage();
                                validar="false";
                                Log.e("cargarcomentario",e.getMessage());
                            }
                        }
                    }
                    Log.i("comentMacro",""+jObject.getBoolean("success_coment"));
                    progreso.setMax(jArray.length());
                    for (int i=0; i < jArray.length(); i++) //Miramos en todos los objetos del array de objetos results
                    {
                        try {

                            JSONObject objeto = jArray.getJSONObject(i); //Obtenemos cada uno de los objetos del array results
                            String codigo1=objeto.getString("articulo");
                            String codigo2=objeto.getString("codigo2");
                            Float cantidad=Float.parseFloat(objeto.getString("cantidad"));
                            Float surtido=Float.parseFloat(objeto.getString("surtido")) ;
                            String posicion=objeto.getString("posicion");
                            String descripcion=objeto.getString("descripcion");
                            Float porsurtir=cantidad-surtido;
                            Float costo=Float.parseFloat(objeto.getString("costo"));
                            Float iva=Float.parseFloat(objeto.getString("iva"));
                            String descuento1=objeto.getString("descuento1");
                            String descuento2=objeto.getString("descuento2");
                            String descuento3=objeto.getString("descuento3");
                            String descuento4=objeto.getString("descuento4");
                            String descuento5=objeto.getString("descuento5");
                            String tipocambio=objeto.getString("tipocambio");
                            String factor=objeto.getString("factor");
                            String clasificacion=objeto.getString("clasificacion");
                            String proveedor=objeto.getString("proveedor");
                            String imp1=objeto.getString("imp1");
                            String imp2=objeto.getString("imp2");
                            String imp1_tab=objeto.getString("imp1_tab");
                            String imp2_tab=objeto.getString("imp2_tab");
                            String cantbackorder=objeto.getString("cantbackorder");


                            try{
                                publishProgress(i+1);
                                Database admin=new Database(getContext(),null,1);
                                SQLiteDatabase db = admin.getWritableDatabase();
                                ContentValues r = new ContentValues();
                                r.put("codigo",codigo1);
                                r.put("codigo2",codigo2);
                                r.put("descripcion",descripcion);
                                r.put("posicion",posicion);
                                r.put("cantidad",cantidad);
                                r.put("surtido",surtido);
                                r.put("por_surtir",porsurtir);
                                r.put("folio",folio);
                                r.put("costo",costo);
                                r.put("surtidoaux",surtido);
                                r.put("iva",iva);
                                r.put("descuento1",descuento1);
                                r.put("descuento2",descuento2);
                                r.put("descuento3",descuento3);
                                r.put("descuento4",descuento4);
                                r.put("descuento5",descuento5);
                                r.put("tipocambio",tipocambio);
                                r.put("backorder","false");
                                r.put("modificar","false");
                                r.put("crear","false");
                                r.put("factor",factor);
                                r.put("clasificacion",clasificacion);
                                r.put("proveedor",proveedor);
                                r.put("imp1",imp1);
                                r.put("imp2",imp2);
                                r.put("imp1_tab",imp1_tab);
                                r.put("imp2_tab",imp2_tab);
                                r.put("cantbackorder",cantbackorder);

                                db.insert("articulos",null,r);
                                db.close();
                            }
                            catch (SQLiteException e)
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
            if(s.equalsIgnoreCase("OK"))
            {
                //consultatabla();
                mensajes("Se agrego factura con exito");
                actualizarLista();
                tv_folio.setText(et_folio_factura.getText());
                new cargarComplementosWS().execute(et_folio_factura.getText().toString().trim(),almacenSeleccionado());
                et_folio_factura.setText("");
                et_folio_factura.setVisibility(View.GONE);
                et_codigo.setVisibility(View.VISIBLE);
                surtircompleto();
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
    class cargarComplementosWS extends AsyncTask<String,Integer,String>
    {
        String validar;
        private ProgressDialog progreso;

        @Override
        protected void onPreExecute()
        {
            /*
            progreso = new ProgressDialog(getContext());
            progreso.setMessage("Iniciando");
            progreso.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progreso.setCancelable(false);
            progreso.setMax(100);
            progreso.setProgress(0);
            progreso.show();
            */
            btn_guardar.setEnabled(false);
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
                HttpGet htpoget = new HttpGet(URL+"complementos/"+folio+"/"+almacen);
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
                    JSONArray jArray = jObject.getJSONArray("complementos");
                    //progreso.setMax(jArray.length());
                    for (int i=0; i < jArray.length(); i++) //Miramos en todos los objetos del array de objetos results
                    {

                        try {
                            JSONObject objeto = jArray.getJSONObject(i); //Obtenemos cada uno de los objetos del array results
                            String foliows=objeto.getString("folio");
                            String almacenws=objeto.getString("almacen");
                            String fecha=objeto.getString("fecha");
                            String uds_surt=objeto.getString("unidades_a_surtir");
                            String cod_prov=objeto.getString("codigo_prov");
                            String nom_prov=objeto.getString("nom_prov");
                            try{
                                //publishProgress(i+1);
                                Database admin=new Database(getContext(),null,1);
                                SQLiteDatabase db = admin.getWritableDatabase();
                                ContentValues r = new ContentValues();
                                r.put("folio_previo",foliows);
                                r.put("fecha",fecha);
                                r.put("almacen",almacenws);
                                r.put("codigo_prov",cod_prov);
                                r.put("proveedor",nom_prov);
                                r.put("unidades_a_surtir",uds_surt);
                                r.put("mod_comdoc","false");
                                r.put("mod_comren","false");
                                r.put("crear_comdoc","false");
                                r.put("crear_conren","false");
                                r.put("envio","false");
                                r.put("guardar_completo","false");
                                r.put("mod_back","false");
                                r.put("pos_coment",0);
                                r.put("coment_completos","false");
                                db.insert("documento",null,r);
                                db.close();

                            }catch (SQLiteException e)
                            {
                                mensajeGlobal="error al insertar almacenes:"+e.getMessage();
                                validar="false";
                            }
                        } catch (JSONException e) {
                            mensajeGlobal="error al hacer peticion: "+e.getMessage();
                            validar="false";
                            //Log.e("cargausuario",e.getMessage());
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
           //progreso.setProgress(i[0]);
        }
        protected void onPostExecute(String s)
        {
            //progreso.dismiss();
            consultatabla("*","documento");
            if(s.equalsIgnoreCase("OK"))
            {
                btn_guardar.setEnabled(true);
                mensajes("Se agregaron datos del documento");
                tv_prov.setText(consultaDato("proveedor","documento"));
            }
            else
            {
                //new cargarComplementosWS().execute(et_folio_factura.getText().toString().trim(),almacenSeleccionado());
                quitarDatos();
                if(s.equalsIgnoreCase("false"))
                {
                    mensajes("No se cargaron complementos"+mensajeGlobal);
                }
                else
                {
                    mensajes("No se cargaron complementos"+mensajeGlobal);
                }
            }
            super.onPostExecute(s);
        }
    }

    class modificarPrevioComdoc extends AsyncTask<String,Integer,String>
    {
        String validar,folio;

        private ProgressDialog progreso;

        @Override
        protected void onPreExecute()
        {
            progreso = new ProgressDialog(getContext());
            progreso.setMessage("Cargando...");
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
            folio=params[0];
            String almacen=params[1],estatus=params[2];
            try {
                HttpConnectionParams.setConnectionTimeout(httpParameters, 180000);
                HttpConnectionParams.setSoTimeout(httpParameters, 180000);
                HttpClient cliente = new DefaultHttpClient(httpParameters);
                //HttpGet htpoget = new HttpGet(URL+"consulta_previo/"+folio+"/"+almacen);
                HttpPut httpPut=new HttpPut(URL+"modificar_previo_comdoc/"+folio+"/"+almacen+"/"+getUdsSurt()+"/"+estatus);
                org.apache.http.HttpResponse resx = cliente.execute(httpPut);
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
            consultasBD.cambiarDocComdoc("true",tv_folio.getText().toString(),contexto);
            if(surtidoParcial()==true)
            {
                //InsertarMacropro
                aclacarion=true;
                enviarAclaracion("1");

            }
            else
            {
                aclacarion=false;
                enviarAclaracion("3");

            }
            /*
            if(s.equalsIgnoreCase("OK"))
            {
                mensajes(mensajeGlobal);
                Log.i("guardando","modifcarpreviocomdoc con exito");
                consultasBD.cambiarDocComdoc("TRUE",folio,contexto);
                actualizarVistaInfo();

            }
            else
            {
                Log.i("guardando","modifcarpreviocomdoc sin exito");
                if(s.equalsIgnoreCase("false"))
                {
                    mensajes(mensajeGlobal);
                }
                else
                {
                    mensajes(mensajeGlobal);
                }
            }

             */
            super.onPostExecute(s);
        }
    }
    public void actualizarVistaInfo()
    {
        if(consultasBD.getMod_comdoc(contexto)==true)
        {
           // mensajes("se modifico comdoc previo");
            iv_mp1.setImageResource(R.drawable.ic_completado);
        }
        else
        {
            //mensajes("no se modifico comdoc");

            iv_mp1.setImageResource(R.drawable.ic_incompleto);
        }
        if(consultasBD.getCrear_comren(contexto)==true)
        {
            //mensajes("se creo comren");
            iv_co1.setImageResource(R.drawable.ic_completado);
        }
        else
        {
            //mensajes("no se modifico comren previo");
            iv_co1.setImageResource(R.drawable.ic_incompleto);
        }
        if(consultasBD.getMod_back(contexto)==true)
        {
            //mensajes("se modifico backorder ");
            iv_mb.setImageResource(R.drawable.ic_completado);
        }
        else
        {
            //mensajes("no se modifico backorder");
            iv_mb.setImageResource(R.drawable.ic_incompleto);
        }
        if(consultasBD.getMod_comren(contexto)==true)
        {
            //mensajes("se modifico previo ");
            iv_mp2.setImageResource(R.drawable.ic_completado);
        }
        else
        {
            //mensajes("no se modifico previo");
            iv_mp2.setImageResource(R.drawable.ic_incompleto);
        }
        if(consultasBD.getCrear_comdoc(contexto)==true)
        {
            //mensajes("se creo comdoc ");
            iv_co2.setImageResource(R.drawable.ic_completado);
        }
        else
        {
            //mensajes("no se creo comdoc");
            iv_co2.setImageResource(R.drawable.ic_incompleto);
        }
        if(consultasBD.getComent_completos(contexto)==true)
        {
            iv_comen.setImageResource(R.drawable.ic_completado);
        }
        else
        {
            iv_comen.setImageResource(R.drawable.ic_incompleto);
        }

        consultasBD.verificarCompleto(tv_folio.getText().toString(),contexto);
        if(consultasBD.getCompleto(contexto)==true)
        {
            Log.i("actual","completo");
            tv_fol_dialog.setText(""+getFolioOC());
            //task.cancel(true);
        }




    }
    class modificarPrevioComren extends AsyncTask<String,Integer,String>
    {
        String validar,articulo,folio;
        private ProgressDialog progreso;

        @Override
        protected void onPreExecute()
        {
            progreso = new ProgressDialog(getContext());
            progreso.setMessage("Modificando previo");
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
            String cantidad=params[1];
            folio=params[0];
            articulo=params[2];
            try {
                HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
                HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
                HttpClient cliente = new DefaultHttpClient(httpParameters);
                //HttpGet htpoget = new HttpGet(URL+"consulta_previo/"+folio+"/"+almacen);
                HttpPut httpPut=new HttpPut(URL+"modificar_previo_comren/"+folio+"/"+cantidad+"/"+articulo);
                org.apache.http.HttpResponse resx = cliente.execute(httpPut);
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
                consultasBD.modificarArtComren("true",articulo,contexto);
                Log.i("guardando","modificarpreviocomren con exito");
                //consultatabla();
                mensajes(mensajeGlobal);

            }
            else
            {
                consultasBD.modificarArtComren("false",articulo,contexto);
                Log.i("guardando","modificarpreviocomren sin exito");
                if(s.equalsIgnoreCase("false"))
                {
                    mensajes(mensajeGlobal);
                }
                else
                {
                    mensajes(mensajeGlobal);
                }
            }
            consultasBD.verificarModArticulos(folio,contexto);
            actualizarVistaInfo();
            super.onPostExecute(s);
        }
    }
    class crearComdoc extends AsyncTask<String,Integer,String>
    {
        String validar,folio_previo;
        private ProgressDialog progreso;

        @Override
        protected void onPreExecute()
        {
            progreso = new ProgressDialog(getContext());
            progreso.setMessage("Creando orden");
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
            folio_previo=params[0];
            String almacen=params[1],folio_orden=params[2],totalreg=params[3],totaluds=params[4],sumatotal=params[5],iva=params[6],total=params[7],descuento=params[8];
            try {
                HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
                HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
                HttpClient cliente = new DefaultHttpClient(httpParameters);
                //HttpGet htpoget = new HttpGet(URL+"consulta_previo/"+folio+"/"+almacen);
                HttpGet httpGet=new HttpGet(URL+"consultar_datos_comdoc/"+folio_previo+"/"+almacen+"/"+folio_orden+"/"+totalreg+"/"+totaluds+"/"+sumatotal+"/"+iva+"/"+total+"/"+descuento);
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
                    //mensajeGlobal=""+jObject.getString("message");
                    mensajeGlobal="Orden de Compra generada con folio: "+folio_orden;
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
                consultasBD.crearDocComdoc("true",folio_previo,contexto);
                Log.i("guardando","crear comdoc con exito");
                dialogOrdenCompleta();

            }
            else
            {
                consultasBD.crearDocComdoc("false",folio_previo,contexto);
                Log.i("guardando","crearcomdoc sin exito");
                if(s.equalsIgnoreCase("false"))
                {
                    mensajes(mensajeGlobal);
                }
                else
                {
                    mensajes(mensajeGlobal);
                }
            }
            //actualizarVistaInfo();
            super.onPostExecute(s);
        }
    }
    public void dialogOrdenCompleta()
    {
       if(consultasBD.getCrear_comren(contexto)==true && consultasBD.getCrear_comdoc(contexto)==true && consultasBD.getComent_completos(contexto)==true && consultasBD.getMod_back(contexto)==true)
       {
           AlertDialog dialog = new AlertDialog.Builder(FragmentInicial.this.getContext())
                   .setTitle("ORDEN DE COMPRA")
                   .setMessage("Se creo orden de compra con el folio: "+getFolioOC())
                   .setPositiveButton("continuar", new DialogInterface.OnClickListener() {
                       @Override
                       public void onClick(DialogInterface dialog, int which) {

                           if(consultasBD.getMod_comren(contexto)==false)
                           {
                               new modificarPrevioJson().execute();
                           }
                           else
                           {
                               if(consultasBD.getMod_comdoc(contexto)==false)
                               {
                                   //modificarPrevioComdoc
                                   if(surtidoParcial()==true)
                                   {
                                       //InsertarMacropro
                                       aclacarion=true;
                                       new modificarPrevioComdoc().execute(tv_folio.getText().toString().trim(),almacenSeleccionado(),"A");


                                   }
                                   else
                                   {
                                       aclacarion=false;
                                       new modificarPrevioComdoc().execute(tv_folio.getText().toString().trim(),almacenSeleccionado(),"S");

                                   }
                               }
                               else
                               {
                                   if(surtidoParcial()==true)
                                   {
                                       //InsertarMacropro
                                       aclacarion=true;
                                       enviarAclaracion("1");

                                   }
                                   else
                                   {
                                       aclacarion=false;
                                       enviarAclaracion("3");

                                   }
                               }
                           }
                           //trabajando

                       }
                   })

                   .create();
           dialog.show();
       }
       else
       {
           AlertDialog dialog = new AlertDialog.Builder(FragmentInicial.this.getContext())
                   .setTitle("ORDEN DE COMPRA")
                   .setMessage("Re-enviar orden")
                   .setPositiveButton("continuar", new DialogInterface.OnClickListener() {
                       @Override
                       public void onClick(DialogInterface dialog, int which) {
                           procesandoOrden();

                       }
                   })
                   .setNegativeButton("cancelar", new DialogInterface.OnClickListener() {
                       @Override
                       public void onClick(DialogInterface dialogInterface, int i) {

                       }
                   })
                   .create();
           dialog.show();
       }


    }
    class crearComren extends AsyncTask<String,Integer,String>
    {
        String validar,cant_art_info,folio_previo,articulo;
        private ProgressDialog progreso;

        @Override
        protected void onPreExecute()
        {
            progreso = new ProgressDialog(getContext());
            progreso.setMessage("Creando");
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
            folio_previo=params[0];
            articulo=params[3];
            String posicion=params[1],cantidad=params[2],folio_orden=params[4];
            cant_art_info=params[5];
            try {
                HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
                HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
                HttpClient cliente = new DefaultHttpClient(httpParameters);
                //HttpGet htpoget = new HttpGet(URL+"consulta_previo/"+folio+"/"+almacen);
                HttpGet httpGet=new HttpGet(URL+"consultar_datos_comren/"+folio_previo+"/"+posicion+"/"+cantidad+"/"+articulo+"/"+folio_orden);
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
                Log.i("guardando","crearcomren con exito");
                //consultatabla();
                mensajes(mensajeGlobal);
                consultasBD.crearArtComren("true",articulo,contexto);

            }
            else
            {
                consultasBD.crearArtComren("false",articulo,contexto);
                Log.i("guardando","crearcomren sin exito");
                if(s.equalsIgnoreCase("false"))
                {
                    mensajes(mensajeGlobal);
                }
                else
                {
                    mensajes(mensajeGlobal);
                }
            }
            consultasBD.verificarCrearArticulos(folio_previo,contexto);
            actualizarVistaInfo();
            super.onPostExecute(s);
        }
    }
    public void surtircompleto()
    {
        AlertDialog dialog = new AlertDialog.Builder(FragmentInicial.this.getContext())
                .setTitle("¿Desea Surtir previo completo?")
                .setPositiveButton("si", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try{
                            Database admin = new Database(getContext(),null,1);
                            SQLiteDatabase db = admin.getWritableDatabase();
                            Cursor fila = db.rawQuery("SELECT codigo,cantidad FROM articulos WHERE por_surtir>0",null);
                            if(fila.moveToFirst())
                            {
                                do{
                                    String cod=fila.getString(0);
                                    Float cant=fila.getFloat(1);
                                    actualizarBD(cant,0,cod);
                                    Log.i("surtircompleto",cod+"|"+cant);
                                }while (fila.moveToNext());
                            }
                            db.close();
                        }catch (SQLiteException e)
                        {
                            mensajes("Error al consultar codigo:"+e.getMessage());
                        }
                        actualizarLista();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .create();
        dialog.show();
        //aumentarFolio();
    }
    public void enviarAclaracion(final String estatus)
    {
        LayoutInflater inflater = FragmentInicial.this.getLayoutInflater();
        View v = inflater.inflate(R.layout.dialog_aclaracion, null);
        final EditText comentario=v.findViewById(R.id.comentarios);
        AlertDialog dialog = new AlertDialog.Builder(FragmentInicial.this.getContext())
                .setTitle("Registrar Aclaracion")
                .setView(v)
                .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Date d = new Date();
                        CharSequence s = DateFormat.format("yyyy-MM-dd", d.getTime());
                        String folio_previo="",fecha_previo="",folio_oc="",fecha_oc="",id_provedor="",proveedor="",id_almacen="",almacen="",id_empresa="",empresa="",usuario="",comentario_in="";
                        folio_previo=tv_folio.getText().toString();
                        fecha_previo=getFechaPrevio();
                        folio_oc=getFolioOC();
                        fecha_oc=s.toString();
                        id_provedor=getProveedor();
                        proveedor=getNombreProveedor();
                        id_almacen=almacenSeleccionado();
                        almacen=getNombreAlmacen();
                        id_empresa=getIdEmpresa();
                        empresa=getNombreEmpresa();
                        usuario=getUsuario();
                        comentario_in=comentario.getText().toString();
                        Log.i("aclaracion",folio_previo+"|"+
                                        fecha_previo+"|"+
                                        folio_oc+"|"+
                                        fecha_oc+"|"+
                                        id_provedor+"|"+
                                        proveedor+"|"+
                                        id_almacen+"|"+
                                        almacen+"|"+
                                        id_empresa+"|"+
                                        empresa+"|"+
                                        usuario+"|"+
                                        comentario_in
                                );
                        new insertarAclaracion().execute(folio_previo,fecha_previo,folio_oc,fecha_oc,id_provedor,proveedor,id_almacen,almacen,id_empresa,empresa,usuario,comentario_in,estatus);


                        //enviar articulos

                    }
                })
                .create();
        dialog.show();
        //aumentarFolio();
    }
    public Float getUdsSurt()
    {
        Float uds= Float.valueOf(0);
        try{
            Database admin = new Database(getContext(),null,1);
            SQLiteDatabase db = admin.getWritableDatabase();
            Cursor fila = db.rawQuery("SELECT surtido FROM articulos",null);
            if(fila.moveToFirst())
            {
                do{
                    Log.i("consultauds"," | "+fila.getString(0)
                    );
                    uds=uds+fila.getFloat(0);
                }while (fila.moveToNext());
            }

            db.close();
        }catch (Exception e)
        {
            Log.e("Error:",""+e.getMessage());
            mensajes("Error al obtener unidades:"+e.getMessage());
        }

        return uds;
    }
    public String getFechaPrevio()
    {
        String dato="";
        try{
            Database admin = new Database(getContext(),null,1);
            SQLiteDatabase db = admin.getWritableDatabase();
            Cursor fila = db.rawQuery("SELECT fecha FROM documento",null);
            // Cursor fila = db.rawQuery("SELECT * FROM documento",null);
            if(fila.moveToFirst())
            {
                dato=fila.getString(0);
            }
            db.close();
        }catch (SQLiteException e)
        {
            Log.e("Error:",""+e.getMessage());
        }
        return dato;
    }
    public String getProveedor()
    {
        String dato="";
        try{
            Database admin = new Database(getContext(),null,1);
            SQLiteDatabase db = admin.getWritableDatabase();
            Cursor fila = db.rawQuery("SELECT codigo_prov FROM documento",null);
            // Cursor fila = db.rawQuery("SELECT * FROM documento",null);
            if(fila.moveToFirst())
            {
                dato=fila.getString(0);
            }
            db.close();
        }catch (SQLiteException e)
        {
            Log.e("Error:",""+e.getMessage());
        }
        return dato;
    }
    public String getNombreProveedor()
    {
        String dato="";
        try{
            Database admin = new Database(getContext(),null,1);
            SQLiteDatabase db = admin.getWritableDatabase();
            Cursor fila = db.rawQuery("SELECT proveedor FROM documento",null);
            // Cursor fila = db.rawQuery("SELECT * FROM documento",null);
            if(fila.moveToFirst())
            {
                dato=fila.getString(0);
            }
            db.close();
        }catch (SQLiteException e)
        {
            Log.e("Error:",""+e.getMessage());
        }
        return dato;
    }
    public void actualizarLista()
    {
        Recibido_list = new ArrayList<Recibido>();
        Database admin = new Database(getContext(),null,1);
        SQLiteDatabase db = admin.getWritableDatabase();
        Cursor fila = db.rawQuery("SELECT * FROM articulos",null);
        if(fila.moveToFirst())
        {
            do{
                String folio = fila.getString(7);
                folio_gbl=folio;
                String codigo = fila.getString(0);
                String codigo2 = fila.getString(1);
                String descripcion = fila.getString(2);
                Float cantidad = fila.getFloat(4);
                Float surtido = fila.getFloat(5);
                Float por_surtir = fila.getFloat(6);
                String posicion = fila.getString(3);
                Recibido_list.add(new Recibido(folio,codigo,codigo2,descripcion,cantidad,surtido,por_surtir,posicion));
            }while (fila.moveToNext());
        }
        else {
            Toast.makeText(getContext(), "sin datos", Toast.LENGTH_SHORT).show();
        }
        db.close();

        Recibido_adap = new RecibidoAdapter( Recibido_list,getContext());
        lvItems.setAdapter(Recibido_adap);

    }
    public String consultaDato(String select,String from)
    {
        String dato="";
        try{
            Database admin = new Database(getContext(),null,1);
            SQLiteDatabase db = admin.getWritableDatabase();
            Cursor fila = db.rawQuery("SELECT "+select+" FROM "+from+" ",null);
            // Cursor fila = db.rawQuery("SELECT * FROM documento",null);
            if(fila.moveToFirst())
            {
                dato=fila.getString(0);
            }
            db.close();
        }catch (SQLiteException e)
        {
            Log.e("Error:",""+e.getMessage());
        }
        return dato;
    }
    public void consultatabla(String select,String from)
    {
        try{
            Database admin = new Database(getContext(),null,1);
            SQLiteDatabase db = admin.getWritableDatabase();
            Cursor fila = db.rawQuery("SELECT "+select+" FROM "+from+" ",null);
           // Cursor fila = db.rawQuery("SELECT * FROM documento",null);
            if(fila.moveToFirst())
            {
                do{

                    Log.i("consultaArticulos"," | "+fila.getString(0)+
                            " | "+fila.getString(1)+
                            " | "+fila.getString(2)+
                            " | "+fila.getString(3)+
                            " | "+fila.getString(4)+
                            " | "+fila.getString(5)+
                            " | "+fila.getColumnCount()
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
            Database admin=new Database(getContext(),null,1);
            SQLiteDatabase db = admin.getWritableDatabase();
            //db.execSQL("DROP TABLE IF EXISTS "+tabla);
            db.execSQL("DELETE FROM " + tabla);
            db.close();
            mensajes("Se elimino factura");

        }catch (SQLiteException e)
        {
            mensajes("Error al eliminar tabla "+tabla+":"+e.getMessage());
        }

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
    public String getFolioOC()
    {
        String alm="";
        try{
            Database admin = new Database(getContext(),null,1);
            SQLiteDatabase db = admin.getWritableDatabase();
            Cursor fila = db.rawQuery("SELECT folioOC FROM login",null);
            if(fila.moveToFirst())
            {
                do{
                    Log.i("consultafolioOC"," | "+fila.getString(0)
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
    public String getTotalreg()
    {
        int alm=0;
        try{
            Database admin = new Database(getContext(),null,1);
            SQLiteDatabase db = admin.getWritableDatabase();
            Cursor fila = db.rawQuery("SELECT * FROM articulos where surtidoaux!=surtido",null);
            if(fila.moveToFirst())
            {
                do{
                    Log.i("consultaAlmacen"," | "+fila.getString(0)
                    );
                    //alm=fila.getString(0);
                    alm=fila.getCount();
                }while (fila.moveToNext());
            }
            else
            {
                alm=0;
            }
            db.close();
        }catch (Exception e)
        {
            Log.e("Error:",""+e.getMessage());
            alm=0;
            //mensajes("Error al validar login:"+e.getMessage());
        }

        return ""+alm;
    }
    public String getTotaluds()
    {
        Float alm= Float.valueOf(0);
        try{
            Database admin = new Database(getContext(),null,1);
            SQLiteDatabase db = admin.getWritableDatabase();
            Cursor fila = db.rawQuery("SELECT surtido,surtidoaux FROM articulos where surtidoaux!=surtido",null);
            if(fila.moveToFirst())
            {
                do{
                    alm=alm+fila.getFloat(0)-fila.getFloat(1);
                    Log.i("consultaAlmacen"," | "+fila.getString(0)
                    );
                    //alm=fila.getString(0);

                }while (fila.moveToNext());
            }
            else
            {
                alm=Float.valueOf(0);
            }
            db.close();
        }catch (Exception e)
        {
            Log.e("Error:",""+e.getMessage());
            alm=Float.valueOf(0);
            //mensajes("Error al validar login:"+e.getMessage());
        }

        return ""+alm;
    }
    public String getSumatotal()
    {
        Float alm= Float.valueOf(0);
        try{
            Database admin = new Database(getContext(),null,1);
            SQLiteDatabase db = admin.getWritableDatabase();

            Cursor fila = db.rawQuery("SELECT surtido,surtidoaux,costo FROM articulos where surtidoaux!=surtido",null);
                if(fila.moveToFirst())
                {
                    do{
                        alm=alm+(fila.getFloat(0)-fila.getFloat(1))*fila.getFloat(2);
                        Log.i("consultaAlmacen"," | "+fila.getString(0)
                        );
                        //alm=fila.getString(0);

                    }while (fila.moveToNext());
                }
                else
                {
                    alm=Float.valueOf(0);
                }

            db.close();
        }catch (Exception e)
        {
            Log.e("Error:",""+e.getMessage());
            alm=Float.valueOf(0);
            //mensajes("Error al validar login:"+e.getMessage());
        }

        return ""+alm;
    }
    public String getIva()
    {
        Float alm= Float.valueOf(0);
        Float iva,cantidad,ivatotal,total;
        try{
            Database admin = new Database(getContext(),null,1);
            SQLiteDatabase db = admin.getWritableDatabase();

            Cursor fila = db.rawQuery("SELECT surtido,surtidoaux,costo,iva FROM articulos where surtidoaux!=surtido",null);
            if(fila.moveToFirst())
            {
                do{
                    cantidad=fila.getFloat(0)-fila.getFloat(1);
                    total=cantidad*fila.getFloat(2);
                    iva=fila.getFloat(3)/100;
                    ivatotal=total*iva;
                    alm=alm+ivatotal;
                    Log.i("consultaAlmacen"," | "+fila.getString(0)
                    );
                    //alm=fila.getString(0);

                }while (fila.moveToNext());
            }
            else
            {
                alm=Float.valueOf(0);
            }

            db.close();
        }catch (Exception e)
        {
            Log.e("Error:",""+e.getMessage());
            alm=Float.valueOf(0);
            //mensajes("Error al validar login:"+e.getMessage());
        }

        return ""+alm;
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
                mensajes(mensajeGlobal);

            }
            else
            {
                if(s.equalsIgnoreCase("false"))
                {
                    mensajes(mensajeGlobal);
                    mensajes("Modificar folio manual");
                }
                else
                {
                    mensajes(mensajeGlobal);
                }
            }
            super.onPostExecute(s);
        }
    }

    class soloConsultaFolio extends AsyncTask<String,Integer,String>
    {
        String validar;
        private ProgressDialog progreso;

        @Override
        protected void onPreExecute()
        {
            progreso = new ProgressDialog(getContext());
            progreso.setMessage("Consultando folio");
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
                //mensajes(mensajeGlobal);
                //consultasBD.mensajes("prueba",contexto);
                //procesando();
                procesandoOrden();
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
    public void procesando()
    {

        LayoutInflater inflater = FragmentInicial.this.getLayoutInflater();
        View v = inflater.inflate(R.layout.dialog_info, null);
        final AlertDialog dialog;
        dialog = new AlertDialog.Builder(FragmentInicial.this.getContext())
                .setTitle("Registrando")
                .setCancelable(false)
                .setView(v)
                .create();
        tv_fol_dialog=v.findViewById(R.id.tv_fol_dialog);
        tv_fol_dialog.setText("0000");
        //imageview dialog
        iv_mp1=v.findViewById(R.id.iv_mp1);
        iv_mp2=v.findViewById(R.id.iv_mp2);
        iv_mb=v.findViewById(R.id.iv_mb);
        iv_co1=v.findViewById(R.id.iv_co1);
        iv_co2=v.findViewById(R.id.iv_co2);
        iv_comen=v.findViewById(R.id.iv_comen);

        btn_reenviar=v.findViewById(R.id.btn_reenviar);
        btn_continuar=v.findViewById(R.id.btn_continuar);


        btn_reenviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //mensajes("reenviar");
                consultasBD.verificarCompleto(tv_folio.getText().toString(),contexto);
                if(consultasBD.getCompleto(contexto)==false)
                {
                    insertandoMovimiento();
                }
                actualizarVistaInfo();
                vistaBotones();

            }
        });
        btn_continuar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                consultasBD.verificarCompleto(tv_folio.getText().toString(),contexto);
                if(consultasBD.getCompleto(contexto)==true)
                {
                    dialog.dismiss();
                    if(surtidoParcial()==true)
                    {
                        //InsertarMacropro
                        aclacarion=true;
                        enviarAclaracion("1");

                    }
                    else
                    {
                        aclacarion=false;
                        enviarAclaracion("3");

                    }
                }
                else
                {
                    mensajes("No se a guardado completamente");
                }
                actualizarVistaInfo();
                vistaBotones();
            }
        });
        dialog.show();
        insertandoMovimiento();
        Log.i("thread","antes");

    }
    private class myUpdateAsyncTask extends AsyncTask<Intent, Integer, Boolean> {

        private Context mContext;
        private String title;
        private ProgressDialog progreso;

        @Override
        protected void onPreExecute()
        {
            progreso = new ProgressDialog(getContext());
            progreso.setMessage("Espere");
            progreso.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progreso.setCancelable(false);
            progreso.show();
            super.onPreExecute();
        }

        public myUpdateAsyncTask (Context context) {
            this.mContext = context;
        }


        @Override
        protected Boolean doInBackground(Intent... intents) {
            //@Todo Tarea a procesar
            title = "soy un titulo asyncTask";
            try {
                Thread.sleep(21000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            //publishProgress(1); //Para mandar control para actualizar la UI
            return true;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            //@Todo values[0] para obtener el código de control, actualizar la UI
            if (values[0]==1); //textView.setText(title)
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);

            if (result) {
                //@Todo AsyncTask finalizado
                progreso.dismiss();
                consultasBD.verificarCompleto(tv_folio.getText().toString(),contexto);
                vistaBotones();

                if(consultasBD.getCompleto(contexto)==true)
                {
                    tv_fol_dialog.setText(""+getFolioOC());
                }
                Log.i("thread","espero 21 segundos");
            }
        }

    }
    public void vistaBotones()
    {
        if(consultasBD.getCompleto(contexto)==true)
        {
            btn_continuar.setVisibility(View.VISIBLE);
            btn_reenviar.setVisibility(View.GONE);
        }
        else
        {
            btn_reenviar.setVisibility(View.VISIBLE);
            btn_continuar.setVisibility(View.GONE);
        }
    }

    public void insertandoMovimiento()
    {
        if(surtidoParcial()==true)
        {
            //InsertarMacropro
            aclacarion=true;
            if(consultasBD.getMod_comdoc(contexto)==false)
            {
                new modificarPrevioComdoc().execute(tv_folio.getText().toString().trim(),almacenSeleccionado(),"A");
            }
            if(consultasBD.getCrear_comren(contexto)==false)
            {
                crearComren();
            }
            if(consultasBD.getMod_back(contexto)==false)
            {
                modificarBackorder();
            }
            if(consultasBD.getMod_comren(contexto)==false)
            {
                modificarComren();
            }
            if(consultasBD.getCrear_comdoc(contexto)==false)
            {
                obtenerDatosComdoc();
            }
            if(consultasBD.getComent_completos(contexto)==false)
            {
                consultarComentarios(consultasBD.getPosicionComentarios(contexto));
            }
            //enviarAclaracion("1");

        }
        else
        {
            aclacarion=false;
            if(consultasBD.getMod_comdoc(contexto)==false)
            {
                new modificarPrevioComdoc().execute(tv_folio.getText().toString().trim(),almacenSeleccionado(),"S");
            }
            if(consultasBD.getCrear_comren(contexto)==false)
            {
                crearComren();
            }
            if(consultasBD.getMod_back(contexto)==false)
            {
                modificarBackorder();
            }
            if(consultasBD.getMod_comren(contexto)==false)
            {
                modificarComren();
            }
            if(consultasBD.getCrear_comdoc(contexto)==false)
            {
                obtenerDatosComdoc();
            }
            if(consultasBD.getComent_completos(contexto)==false)
            {
                consultarComentarios(consultasBD.getPosicionComentarios(contexto));
            }
            //enviarAclaracion("3");

        }
       //task=(myUpdateAsyncTask) new myUpdateAsyncTask(contexto).execute();
        new myUpdateAsyncTask(contexto).execute();
    }

    class insertarAclaracion extends AsyncTask<String,Integer,String>
    {
        String validar;
        private ProgressDialog progreso;

        @Override
        protected void onPreExecute()
        {
            progreso = new ProgressDialog(getContext());
            progreso.setMessage("Insertando folios");
            progreso.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progreso.setCancelable(false);
            progreso.show();
            super.onPreExecute();
        }
        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        protected String doInBackground(String... params)
        {
            //String folio=params[0],almacen=params[1];
            String folio_previo=params[0],
                    fecha_previo=params[1],
                    folio_oc=params[2],
                    fecha_oc=params[3],
                    id_provedor=params[4],
                    proveedor=params[5],
                    id_almacen=params[6],
                    almacen=params[7],
                    id_empresa=params[8],
                    empresa=params[9],
                    usuario=params[10],
                    comentario_in=params[11],
                    estatus=params[12];
            try {
                JSONObject obj = new JSONObject();
                try {
                    obj.put("folio_previo", folio_previo);
                    obj.put("fecha_previo", fecha_previo);
                    obj.put("folio_oc", folio_oc);
                    obj.put("fecha_oc", fecha_oc);
                    obj.put("id_provedor", id_provedor);
                    obj.put("proveedor", proveedor);
                    obj.put("id_almacen", id_almacen);
                    obj.put("almacen", almacen);
                    obj.put("id_detalles","");
                    obj.put("id_empresa", id_empresa);
                    obj.put("empresa", empresa);
                    obj.put("usuario", usuario);
                    obj.put("comentario_in", comentario_in);
                    obj.put("estatus", estatus);
                }
                catch (JSONException e)
                {
                    Log.e("error",e.getMessage());
                    //mensajes(e.getMessage());
                }
                URL url = new URL(URL+"insert"); //in the real code, there is an ip and a port
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setRequestProperty("Accept","application/json");
                conn.setConnectTimeout(timeoutConnection);
                conn.setReadTimeout(timeoutSocket);
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
                    sb.append(line+"\n");
                }


                //String status=String.valueOf(conn.getResponseCode());
                int status = conn.getResponseCode();
                if(status<400)
                {
                    validar="OK";
                    //mensajeGlobal=conn.getResponseMessage();
                    String finalJSON = sb.toString();
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
                    br.close();

                }
                else
                {
                    validar="false";
                    mensajeGlobal=conn.getResponseMessage();
                }
                Log.i("STATUS", String.valueOf(conn.getResponseCode()));
                Log.i("MSG" , conn.getResponseMessage());
                conn.disconnect();
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
                mensajes(mensajeGlobal);
                new insertarArticulosAclaracion().execute(getIdEmpresa());
                consultasBD.cambiarEnvioAclaracion("true",tv_folio.getText().toString(),contexto);
            }
            else
            {
                if(s.equalsIgnoreCase("false"))
                {
                    mensajes(mensajeGlobal);
                    //mensajes("Modificar folio manual");
                }
                else
                {
                    mensajes(mensajeGlobal);
                }
            }
            super.onPostExecute(s);
        }
    }
    class modificarBack extends AsyncTask<String,Integer,String>
    {
        String validar,back_articulo;
        private ProgressDialog progreso;

        @Override
        protected void onPreExecute()
        {
            progreso = new ProgressDialog(getContext());
            progreso.setMessage("Modificando back");
            progreso.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progreso.setCancelable(false);
            // progreso.setMax(100);
            // progreso.setProgress(0);
            progreso.show();
            super.onPreExecute();
        }
        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        protected String doInBackground(String... params)
        {
            String back_almacen=almacenSeleccionado(),
                    back_cantidad=params[1];
            back_articulo=params[0];

            try {
                JSONObject obj = new JSONObject();
                try {
                    obj.put("almacen", back_almacen);
                    obj.put("articulo", back_articulo);
                    obj.put("cantidad", back_cantidad);

                }
                catch (JSONException e)
                {
                    Log.e("error",e.getMessage());
                    //mensajes(e.getMessage());
                }
                URL url = new URL(URL+"modificarback"); //in the real code, there is an ip and a port
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setRequestProperty("Accept","application/json");
                conn.setConnectTimeout(10000);
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
                    sb.append(line+"\n");
                }


                //String status=String.valueOf(conn.getResponseCode());
                int status = conn.getResponseCode();
                if(status<400)
                {
                    validar="OK";
                    //mensajeGlobal=conn.getResponseMessage();
                    String finalJSON = sb.toString();
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
                    br.close();

                }
                else
                {
                    validar="false";
                    mensajeGlobal=conn.getResponseMessage();
                }
                Log.i("STATUS", String.valueOf(conn.getResponseCode()));
                Log.i("MSG" , conn.getResponseMessage());
                conn.disconnect();
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
                mensajes(mensajeGlobal);
                consultasBD.cambiarArtBack("true",back_articulo,contexto);
            }
            else
            {
                consultasBD.cambiarArtBack("false",back_articulo,contexto);
                if(s.equalsIgnoreCase("false"))
                {
                    mensajes(mensajeGlobal);
                    //mensajes("Modificar folio manual");
                }
                else
                {
                    mensajes(mensajeGlobal);
                }
            }
            consultasBD.verificarBackorderArticulos(tv_folio.getText().toString(),contexto);
            actualizarVistaInfo();
            super.onPostExecute(s);
        }
    }
    class insertarArticulosAclaracion extends AsyncTask<String,Integer,String>
    {
        String validar;
        private ProgressDialog progreso;

        @Override
        protected void onPreExecute()
        {
            progreso = new ProgressDialog(getContext());
            progreso.setMessage("Insertando detalle");
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
            //String folio=params[0],almacen=params[1];
            String id_empresa=params[0],id_articulo="",articulo="",cantidad="",recibido="",folio_oc="";
            folio_oc=getFolioOC();
            JSONArray jsonArray = new JSONArray();


            try{
                Database admin = new Database(getContext(),null,1);
                SQLiteDatabase db = admin.getWritableDatabase();
                Cursor fila = db.rawQuery("SELECT codigo,descripcion,cantidad,surtido FROM articulos WHERE por_surtir>0",null);
                if(fila.moveToFirst())
                {
                    do{
                        //crear json

                        id_articulo=fila.getString(0);
                        articulo=fila.getString(1);
                        cantidad=fila.getString(2);
                        recibido=fila.getString(3);

                        JSONObject articuloDetalle=new JSONObject();
                        try {

                            articuloDetalle.put("id_articulo", id_articulo);
                            articuloDetalle.put("articulo",articulo);
                            articuloDetalle.put("folio_oc", folio_oc);
                            articuloDetalle.put("id_empresa", id_empresa);
                            articuloDetalle.put("cantidad", cantidad);
                            articuloDetalle.put("recibido", recibido);


                        } catch (JSONException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                        jsonArray.put(articuloDetalle);

                        Log.i("consultacrearComren",id_empresa+"|"+id_articulo+"|"+cantidad+"|"+articulo+"|"+recibido);
                    }while (fila.moveToNext());

                        Log.i("jsonprincipal",jsonArray.toString());
                }
                else {
                    //Toast.makeText(getContext(), "Sin cambios", Toast.LENGTH_SHORT).show();
                }
                db.close();
            }catch (SQLiteException e)
            {
                //mensajes("Error al consultar codigo:"+e.getMessage());
                Log.i("aclaracion",e.getMessage());
            }

            try {

                URL url = new URL(URL+"insertDtl"); //in the real code, there is an ip and a port
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setRequestProperty("Accept","application/json");
                conn.setConnectTimeout(10000);
                conn.setDoOutput(true);
                conn.setDoInput(true);
                conn.connect();
                DataOutputStream os = new DataOutputStream(conn.getOutputStream());
                os.writeBytes(jsonArray.toString());
                os.flush();
                os.close();
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    sb.append(line+"\n");
                }
                //String status=String.valueOf(conn.getResponseCode());
                int status = conn.getResponseCode();
                if(status<400)
                {
                    validar="OK";
                    //mensajeGlobal=conn.getResponseMessage();
                    String finalJSON = sb.toString();
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
                    br.close();

                }
                else
                {
                    validar="false";
                }


                Log.i("STATUS", String.valueOf(conn.getResponseCode()));
                Log.i("MSG" , conn.getResponseMessage());
                conn.disconnect();

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
                //new insertarArticulosA().execute(getIdEmpresa());
                mensajes(mensajeGlobal);
                aumentarFolio();
                quitarDatos();

            }
            else
            {
                if(s.equalsIgnoreCase("false"))
                {
                    mensajes(mensajeGlobal);
                    //mensajes("Modificar folio manual");
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
        String folioOC=folio_OC_gbl;
        try{
            Database admin=new Database(getContext(),null,1);
            SQLiteDatabase db = admin.getWritableDatabase();
            ContentValues r = new ContentValues();
            r.put("folioOC",folioOC);
            r.put("serieOC",serie_OC_gbl);
            r.put("numeroOC",numero_OC_gbl);
            db.update("login",r, "usuario='"+ getUsuario() +"'",null);
            db.close();
        }catch (SQLiteException e)
        {
            Toast.makeText(getContext(), "Error:"+e.getMessage(), Toast.LENGTH_SHORT).show();
        }
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
    public String getIdEmpresa()
    {
        String alm="";
        try{
            Database admin = new Database(getContext(),null,1);
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
    public String getNombreEmpresa()
    {
        String alm="";
        try{
            Database admin = new Database(getContext(),null,1);
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
    public String getNombreAlmacen()
    {
        String alm="";
        try{
            Database admin = new Database(getContext(),null,1);
            SQLiteDatabase db = admin.getWritableDatabase();
            Cursor fila = db.rawQuery("SELECT almacen FROM almacenes where cod_alm='"+almacenSeleccionado()+"'",null);
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
