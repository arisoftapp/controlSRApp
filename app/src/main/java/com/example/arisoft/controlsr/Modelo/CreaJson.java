package com.example.arisoft.controlsr.Modelo;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;
import android.widget.Toast;

import com.example.arisoft.controlsr.Tools.Database;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

public class CreaJson {
    ConsultasBD consultasBD=new ConsultasBD();
    public JSONObject crearJsonOC(String folioOC,Context contexto)
    {
        //boolean resultado=false;
        JSONArray jsonArrayArt = new JSONArray();
        JSONObject jsonCrearOrden= new JSONObject();
        JSONObject jsonArt;
        String articulo,cantidad,folio_orden=folioOC,factor,clasificacion,proveedor,costo,tipocambio,imp1,imp2,imp1_tab,imp2_tab,descuento1,descuento2,descuento3,descuento4,descuento5,folio="";
        int posicion=0;
        try{
            Database admin = new Database(contexto,null,1);
            SQLiteDatabase db = admin.getWritableDatabase();
            Cursor fila = db.rawQuery("SELECT codigo,surtido,surtidoaux,factor,clasificacion,proveedor," +
                    "costo,tipocambio,imp1,imp2,imp1_tab,imp2_tab," +
                    "descuento1,descuento2,descuento3,descuento4,descuento5,folio " +
                    "FROM articulos WHERE surtidoaux!=surtido ",null);
            if(fila.moveToFirst())
            {
                do{
                    posicion=posicion+1;
                    articulo=fila.getString(0);
                    Float cant=fila.getFloat(1)-fila.getFloat(2);
                    cantidad=""+cant;
                    factor=fila.getString(3);
                    clasificacion=fila.getString(4);
                    proveedor=fila.getString(5);
                    costo=fila.getString(6);
                    tipocambio=fila.getString(7);
                    imp1=fila.getString(8);
                    imp2=fila.getString(9);
                    imp1_tab=fila.getString(10);
                    imp2_tab=fila.getString(11);
                    descuento1=fila.getString(12);
                    descuento2=fila.getString(13);
                    descuento3=fila.getString(14);
                    descuento4=fila.getString(15);
                    descuento5=fila.getString(16);
                    folio=fila.getString(17);
                    //creando json articulos
                    jsonArt=new JSONObject();
                    if(articulo.contains("¥"))
                    {
                        //articulo=articulo.replace("¥","Ñ");
                        Log.i("replace","si entro "+articulo);
                    }
                    jsonArt.put("articulo",articulo);
                    jsonArt.put("posicion",posicion);
                    jsonArt.put("cantidad",cantidad);
                    jsonArt.put("factor",factor);
                    jsonArt.put("clasificacion",clasificacion);
                    jsonArt.put("proveedor",proveedor);
                    jsonArt.put("costo",costo);
                    jsonArt.put("tipocambio",tipocambio);
                    jsonArt.put("imp1",imp1);
                    jsonArt.put("imp2",imp2);
                    jsonArt.put("imp1_tab",imp1_tab);
                    jsonArt.put("imp2_tab",imp2_tab);
                    jsonArt.put("descuento1",descuento1);
                    jsonArt.put("descuento2",descuento2);
                    jsonArt.put("descuento3",descuento3);
                    jsonArt.put("descuento4",descuento4);
                    jsonArt.put("descuento5",descuento5);
                    jsonArrayArt.put(jsonArt);

                    Log.i("creajsonoc","-------------------------------------------------------------------------");
                    Log.i("creajsonoc",folio_orden+"|"+posicion+"|"+cantidad+"|"+articulo+"|"+factor+"|"+fila.getCount());
                    Log.i("creajsonoc",clasificacion+"|"+proveedor+"|"+costo+"|"+tipocambio+"|"+imp1+"|"+imp2);
                    Log.i("creajsonoc",imp1_tab+"|"+imp2_tab+"|"+descuento1+"|"+descuento2+"|"+descuento3+"|"+descuento4);
                    Log.i("creajsonoc",descuento5+"|"+folio);
                    Log.i("creajsonoc","-------------------------------------------------------------------------");
                    Log.i("creajsonoc",jsonArt+"|");
                }while (fila.moveToNext());
            }
            db.close();
            jsonCrearOrden.put("folio_orden",folio_orden);
            jsonCrearOrden.put("articulos",jsonArrayArt);

//            Log.i("creajsonoc",jsonCrearOrden.getString("folio_orden"));
            Log.i("creajsonoc",jsonCrearOrden.getString("articulos"));

        }catch (SQLiteException e)
        {
            mensajes("Error al consultar codigo:"+e.getMessage(),contexto);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        consultasBD.setPosicionComentarios(posicion,folio,contexto);
        return jsonCrearOrden;
    }

    public JSONObject crearJsonComentariosOC(String folioOC,Context contexto)
    {

        JSONArray jsonArrayArt = new JSONArray();
        JSONObject jsonCrearOrden= new JSONObject();
        JSONObject jsonArt;
        String folio_orden=folioOC,comentario;
        int posicion=consultasBD.getPosicionComentarios(contexto);
        try{
            Database admin = new Database(contexto,null,1);
            SQLiteDatabase db = admin.getWritableDatabase();
            Cursor fila = db.rawQuery("SELECT comentario FROM comentarios ",null);
            if(fila.moveToFirst())
            {
                do{

                    comentario=fila.getString(0);


                    //creando json articulos
                    posicion++;
                    jsonArt=new JSONObject();
                    jsonArt.put("comentario",comentario);
                    jsonArt.put("posicion",posicion);
                    jsonArrayArt.put(jsonArt);

                    Log.i("creajsonoc","-------------------------------------------------------------------------");
                    Log.i("creajsonoc",folio_orden+"|"+posicion+"|"+comentario+"|"+fila.getCount());
                    Log.i("creajsonoc","-------------------------------------------------------------------------");
                }while (fila.moveToNext());
            }
            db.close();
            jsonCrearOrden.put("folio_orden",folio_orden);
            jsonCrearOrden.put("comentarios",jsonArrayArt);

//            Log.i("creajsonoc",jsonCrearOrden.getString("folio_orden"));
            //Log.i("creajsonoc",jsonCrearOrden.getString("articulos"));

        }catch (SQLiteException e)
        {
            mensajes("Error al consultar codigo:"+e.getMessage(),contexto);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonCrearOrden;
    }
    public JSONObject crearJsonBackorder(String almacen,Context contexto)
    {

        JSONArray jsonArrayArt = new JSONArray();
        JSONObject jsonCrearOrden= new JSONObject();
        JSONObject jsonArt;
        String articulo,cantidad;
        try{
            Database admin = new Database(contexto,null,1);
            SQLiteDatabase db = admin.getWritableDatabase();
            Cursor fila = db.rawQuery("SELECT codigo,cantbackorder,surtido,surtidoaux FROM articulos WHERE surtidoaux!=surtido",null);
            if(fila.moveToFirst())
            {
                do{
                    articulo=fila.getString(0);
                    Float cant=fila.getFloat(2)-fila.getFloat(3);
                    Float cant2=fila.getFloat(1)-cant;
                    cantidad=""+cant2;

                    //creando json articulos

                    jsonArt=new JSONObject();
                    jsonArt.put("articulo",articulo);
                    jsonArt.put("cantidad",cantidad);
                    jsonArrayArt.put(jsonArt);

                    Log.i("creajsonoc","-------------------------------------------------------------------------");
                    Log.i("creajsonoc",articulo+"|"+cantidad+"|"+almacen+"|"+fila.getCount());
                    Log.i("creajsonoc","-------------------------------------------------------------------------");
                }while (fila.moveToNext());
            }
            db.close();
            jsonCrearOrden.put("almacen",almacen);
            jsonCrearOrden.put("articulos",jsonArrayArt);

//            Log.i("creajsonoc",jsonCrearOrden.getString("folio_orden"));
            //Log.i("creajsonoc",jsonCrearOrden.getString("articulos"));

        }catch (SQLiteException e)
        {
            mensajes("Error al consultar codigo:"+e.getMessage(),contexto);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonCrearOrden;
    }
    public JSONObject crearJsonPrevio(String folio_previo,Context contexto)
    {

        JSONArray jsonArrayArt = new JSONArray();
        JSONObject jsonCrearOrden= new JSONObject();
        JSONObject jsonArt;
        String articulo,cantidad;
        try{
            Database admin = new Database(contexto,null,1);
            SQLiteDatabase db = admin.getWritableDatabase();
            Cursor fila = db.rawQuery("SELECT codigo,surtido FROM articulos WHERE surtidoaux!=surtido",null);
            if(fila.moveToFirst())
            {
                do{
                    articulo=fila.getString(0);
                    cantidad=fila.getString(1);

                    //creando json articulos

                    jsonArt=new JSONObject();
                    jsonArt.put("articulo",articulo);
                    jsonArt.put("cantidad",cantidad);
                    jsonArrayArt.put(jsonArt);

                    Log.i("creajsonoc","-------------------------------------------------------------------------");
                    Log.i("creajsonoc",articulo+"|"+cantidad+"|"+folio_previo+"|"+fila.getCount());
                    Log.i("creajsonoc","-------------------------------------------------------------------------");
                }while (fila.moveToNext());
            }
            db.close();
            jsonCrearOrden.put("folio_previo",folio_previo);
            jsonCrearOrden.put("articulos",jsonArrayArt);

//            Log.i("creajsonoc",jsonCrearOrden.getString("folio_orden"));
            //Log.i("creajsonoc",jsonCrearOrden.getString("articulos"));

        }catch (SQLiteException e)
        {
            mensajes("Error al consultar codigo:"+e.getMessage(),contexto);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonCrearOrden;
    }
    public void mensajes(String mensaje,Context contexto) {
        Toast.makeText(contexto,mensaje,Toast.LENGTH_SHORT).show();
    }
}
