package com.example.arisoft.controlsr.Modelo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;
import android.widget.Toast;

import com.example.arisoft.controlsr.Tools.Database;

public class ConsultasBD {

    //documento
    public void cambiarDocComdoc(String estatus,String folio, Context contexto)
    {
        try{
            Database admin=new Database(contexto,null,1);
            SQLiteDatabase db = admin.getWritableDatabase();
            ContentValues r = new ContentValues();
            r.put("mod_comdoc",estatus);
            db.update("documento",r, "folio_previo='"+ folio +"' ",null);
            db.close();
        }catch (SQLiteException e)
        {
            Toast.makeText(contexto, "Error:"+e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    public void crearDocComdoc(String estatus,String folio, Context contexto)
    {
        try{
            Database admin=new Database(contexto,null,1);
            SQLiteDatabase db = admin.getWritableDatabase();
            ContentValues r = new ContentValues();
            r.put("crear_comdoc",estatus);
            db.update("documento",r, "folio_previo='"+ folio +"' ",null);
            db.close();
        }catch (SQLiteException e)
        {
            Toast.makeText(contexto, "Error:"+e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    public void posComtDoc(int posicion,String folio, Context contexto)
    {
        try{
            Database admin=new Database(contexto,null,1);
            SQLiteDatabase db = admin.getWritableDatabase();
            ContentValues r = new ContentValues();
            r.put("pos_coment",posicion);
            db.update("documento",r, "folio_previo='"+ folio +"' ",null);
            db.close();
        }catch (SQLiteException e)
        {
            Toast.makeText(contexto, "Error:"+e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    public void cambiarEnvioAclaracion(String estatus,String folio, Context contexto)
    {
        try{
            Database admin=new Database(contexto,null,1);
            SQLiteDatabase db = admin.getWritableDatabase();
            ContentValues r = new ContentValues();
            r.put("envio",estatus);
            db.update("documento",r, "folio_previo='"+ folio +"' ",null);
            db.close();
        }catch (SQLiteException e)
        {
            Toast.makeText(contexto, "Error:"+e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    //articulo
    public void crearArtComren(String estatus,String codigo, Context contexto)
    {
        try{
            Database admin=new Database(contexto,null,1);
            SQLiteDatabase db = admin.getWritableDatabase();
            ContentValues r = new ContentValues();
            r.put("crear",estatus);
            db.update("articulos",r, "codigo='"+ codigo +"' ",null);
            db.close();
        }catch (SQLiteException e)
        {
            Toast.makeText(contexto, "Error:"+e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    public void cambiarArtBack(String estatus,String codigo, Context contexto)
    {
        try{
            Database admin=new Database(contexto,null,1);
            SQLiteDatabase db = admin.getWritableDatabase();
            ContentValues r = new ContentValues();
            r.put("backorder",estatus);
            db.update("articulos",r, "codigo='"+ codigo +"' ",null);
            db.close();
        }catch (SQLiteException e)
        {
            Toast.makeText(contexto, "Error:"+e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    public void modificarArtComren(String estatus,String codigo, Context contexto)
    {
        try{
            Database admin=new Database(contexto,null,1);
            SQLiteDatabase db = admin.getWritableDatabase();
            ContentValues r = new ContentValues();
            r.put("modificar",estatus);
            db.update("articulos",r, "codigo='"+ codigo +"' ",null);
            db.close();
        }catch (SQLiteException e)
        {
            Toast.makeText(contexto, "Error:"+e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }


    //verificaciones
    public void verificarCrearArticulos(String folio, Context contexto)
    {
        boolean resultado=false;
        Cursor fila;
        Database admin = new Database(contexto,null,1);
        SQLiteDatabase db = admin.getWritableDatabase();
        //se encargara de verificar que todos los articulos esten con estatus verdadero en su campo "modificar"
        //y si todos estan como verdadero modificara el documento en su campo "mod_comren"
        try{

             fila = db.rawQuery("SELECT crear FROM articulos where crear='false' ",null);
            if(fila.moveToFirst())
            {
                //no se modifico comren completo
                Log.i("verificarPrevio","resultado:"+fila.getString(0));
                resultado=false;
            }
            else
            {
                resultado=true;
            }

        }catch (SQLiteException e)
        {
            mensajes("Error al consultar codigo:"+e.getMessage(),contexto);
        }
        if (resultado==true)
        {
            //actializar documento
            try{
                ContentValues r = new ContentValues();
                r.put("crear_conren","true");
                db.update("documento",r, "folio_previo='"+ folio +"' ",null);

            }catch (SQLiteException e)
            {
                Toast.makeText(contexto, "Error:"+e.getMessage(), Toast.LENGTH_SHORT).show();
            }

        }
        db.close();
    }
    public void verificarBackorderArticulos(String folio, Context contexto)
    {
        boolean resultado=false;
        Cursor fila;
        Database admin = new Database(contexto,null,1);
        SQLiteDatabase db = admin.getWritableDatabase();
        //se encargara de verificar que todos los articulos esten con estatus verdadero en su campo "modificar"
        //y si todos estan como verdadero modificara el documento en su campo "mod_comren"
        try{

            fila = db.rawQuery("SELECT backorder FROM articulos where backorder='false' ",null);
            if(fila.moveToFirst())
            {
                //no se modifico comren completo
                Log.i("verificarPrevio","resultado:"+fila.getString(0));
                resultado=false;
            }
            else
            {
                resultado=true;
            }

        }catch (SQLiteException e)
        {
            mensajes("Error al consultar codigo:"+e.getMessage(),contexto);
        }
        if (resultado==true)
        {
            //actializar documento
            try{
                ContentValues r = new ContentValues();
                r.put("mod_back","true");
                db.update("documento",r, "folio_previo='"+ folio +"' ",null);

            }catch (SQLiteException e)
            {
                Toast.makeText(contexto, "Error:"+e.getMessage(), Toast.LENGTH_SHORT).show();
            }

        }
        db.close();
    }
    public void verificarModArticulos(String folio, Context contexto)
    {
        boolean resultado=false;
        Cursor fila;
        Database admin = new Database(contexto,null,1);
        SQLiteDatabase db = admin.getWritableDatabase();
        //se encargara de verificar que todos los articulos esten con estatus verdadero en su campo "modificar"
        //y si todos estan como verdadero modificara el documento en su campo "mod_comren"
        try{

            fila = db.rawQuery("SELECT modificar FROM articulos where modificar='false' ",null);
            if(fila.moveToFirst())
            {
                //no se modifico comren completo
                Log.i("verificarPrevio","resultado:"+fila.getString(0));
                resultado=false;
            }
            else
            {
                resultado=true;
            }

        }catch (SQLiteException e)
        {
            mensajes("Error al consultar codigo:"+e.getMessage(),contexto);
        }
        if (resultado==true)
        {
            //actializar documento
            try{
                ContentValues r = new ContentValues();
                r.put("mod_comren","true");
                db.update("documento",r, "folio_previo='"+ folio +"' ",null);

            }catch (SQLiteException e)
            {
                Toast.makeText(contexto, "Error:"+e.getMessage(), Toast.LENGTH_SHORT).show();
            }

        }
        db.close();
    }
    public void verificarComentarios(String folio, Context contexto)
    {
        boolean resultado=false;
        Cursor fila;
        Database admin = new Database(contexto,null,1);
        SQLiteDatabase db = admin.getWritableDatabase();
        //verifica comentarios
        try{

            fila = db.rawQuery("SELECT comentario FROM comentarios where estatus='A' ",null);
            if(fila.moveToFirst())
            {
                //no se modifico comren completo
                Log.i("verificar","resultado:"+fila.getString(0));
                resultado=false;
            }
            else
            {
                resultado=true;
            }

        }catch (SQLiteException e)
        {
            mensajes("Error al consultar codigo:"+e.getMessage(),contexto);
        }
        if (resultado==true)
        {
            //actializar documento
            try{
                ContentValues r = new ContentValues();
                r.put("coment_completos","true");
                db.update("documento",r, "folio_previo='"+ folio +"' ",null);

            }catch (SQLiteException e)
            {
                Toast.makeText(contexto, "Error:"+e.getMessage(), Toast.LENGTH_SHORT).show();
            }

        }
        db.close();
    }
    public void verificarCompleto(String folio, Context contexto)
    {
        String estatus="false";
        if(getMod_comdoc(contexto)==true && getCrear_comren(contexto)==true && getMod_back(contexto)==true
        && getMod_comren(contexto)==true && getCrear_comdoc(contexto)==true && getComent_completos(contexto))
        {
            estatus="true";
        }
        else
        {
            estatus="false";
        }
        try{
            Database admin=new Database(contexto,null,1);
            SQLiteDatabase db = admin.getWritableDatabase();
            ContentValues r = new ContentValues();
            r.put("guardar_completo",estatus);
            db.update("documento",r, "folio_previo='"+ folio +"' ",null);
            db.close();
        }catch (SQLiteException e)
        {
            Toast.makeText(contexto, "Error:"+e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    public void mensajes(String mensaje,Context contexto) {
        Toast.makeText(contexto,mensaje,Toast.LENGTH_SHORT).show();
    }
    //consulta documento
    public boolean getMod_comdoc(Context contexto)
    {
        boolean resultado=false;
        try{
            Database admin = new Database(contexto,null,1);
            SQLiteDatabase db = admin.getWritableDatabase();
            Cursor fila = db.rawQuery("SELECT mod_comdoc FROM documento ",null);
            if(fila.moveToFirst())
            {
                if(fila.getString(0).equalsIgnoreCase("true"))
                {
                    resultado=true;
                }
                else
                {
                    resultado=false;
                }
                Log.i("getMod_comdoc","resultado:"+fila.getString(0));

            }
            else
            {
                resultado=false;
            }
            db.close();
        }catch (SQLiteException e)
        {
            mensajes("Error al consultar codigo:"+e.getMessage(),contexto);
        }
        return resultado;
    }
    public boolean getCrear_comren(Context contexto)
    {
        boolean resultado=false;
        try{
            Database admin = new Database(contexto,null,1);
            SQLiteDatabase db = admin.getWritableDatabase();
            Cursor fila = db.rawQuery("SELECT crear_conren FROM documento ",null);
            if(fila.moveToFirst())
            {
                if(fila.getString(0).equalsIgnoreCase("true"))
                {
                    resultado=true;
                }
                else
                {
                    resultado=false;
                }
                Log.i("getMod_comdoc","resultado:"+fila.getString(0));

            }
            else
            {
                resultado=false;
            }
            db.close();
        }catch (SQLiteException e)
        {
            mensajes("Error al consultar codigo:"+e.getMessage(),contexto);
        }
        return resultado;
    }
    public boolean getMod_back(Context contexto)
    {
        boolean resultado=false;
        try{
            Database admin = new Database(contexto,null,1);
            SQLiteDatabase db = admin.getWritableDatabase();
            Cursor fila = db.rawQuery("SELECT mod_back FROM documento ",null);
            if(fila.moveToFirst())
            {
                if(fila.getString(0).equalsIgnoreCase("true"))
                {
                    resultado=true;
                }
                else
                {
                    resultado=false;
                }
                Log.i("getMod_comdoc","resultado:"+fila.getString(0));

            }
            else
            {
                resultado=false;
            }
            db.close();
        }catch (SQLiteException e)
        {
            mensajes("Error al consultar codigo:"+e.getMessage(),contexto);
        }
        return resultado;
    }
    public boolean getMod_comren(Context contexto)
    {
        boolean resultado=false;
        try{
            Database admin = new Database(contexto,null,1);
            SQLiteDatabase db = admin.getWritableDatabase();
            Cursor fila = db.rawQuery("SELECT mod_comren FROM documento ",null);
            if(fila.moveToFirst())
            {
                if(fila.getString(0).equalsIgnoreCase("true"))
                {
                    resultado=true;
                }
                else
                {
                    resultado=false;
                }
                Log.i("getMod_comdoc","resultado:"+fila.getString(0));

            }
            else
            {
                resultado=false;
            }
            db.close();
        }catch (SQLiteException e)
        {
            mensajes("Error al consultar codigo:"+e.getMessage(),contexto);
        }
        return resultado;
    }
    public boolean getCrear_comdoc(Context contexto)
    {
        boolean resultado=false;
        try{
            Database admin = new Database(contexto,null,1);
            SQLiteDatabase db = admin.getWritableDatabase();
            Cursor fila = db.rawQuery("SELECT crear_comdoc FROM documento ",null);
            if(fila.moveToFirst())
            {
                if(fila.getString(0).equalsIgnoreCase("true"))
                {
                    resultado=true;
                }
                else
                {
                    resultado=false;
                }
                Log.i("getMod_comdoc","resultado:"+fila.getString(0));

            }
            else
            {
                resultado=false;
            }
            db.close();
        }catch (SQLiteException e)
        {
            mensajes("Error al consultar codigo:"+e.getMessage(),contexto);
        }
        return resultado;
    }
    public boolean getComent_completos(Context contexto)
    {
        boolean resultado=false;
        try{
            Database admin = new Database(contexto,null,1);
            SQLiteDatabase db = admin.getWritableDatabase();
            Cursor fila = db.rawQuery("SELECT coment_completos FROM documento ",null);
            if(fila.moveToFirst())
            {
                if(fila.getString(0).equalsIgnoreCase("true"))
                {
                    resultado=true;
                }
                else
                {
                    resultado=false;
                }
                Log.i("getComent_completos","resultado:"+fila.getString(0));

            }
            else
            {
                resultado=false;
            }
            db.close();
        }catch (SQLiteException e)
        {
            mensajes("Error al consultar codigo:"+e.getMessage(),contexto);
        }
        return resultado;
    }


    public int getPosicionComentarios(Context contexto)
    {
        int res=0;
        try{
            Database admin = new Database(contexto,null,1);
            SQLiteDatabase db = admin.getWritableDatabase();
            Cursor fila = db.rawQuery("SELECT pos_coment FROM documento",null);
            if(fila.moveToFirst())
            {
                    Log.i("consulta"," | "+fila.getString(0));
                    res=fila.getInt(0);
            }
            db.close();
        }catch (Exception e)
        {
            Log.e("Error:",""+e.getMessage());
            res=0;
            //mensajes("Error al validar login:"+e.getMessage());
        }

        return res;
    }

    public void cambiarEstatusComentario(String estatus,String id,Context contexto)
    {
        try{
            Database admin=new Database(contexto,null,1);
            SQLiteDatabase db = admin.getWritableDatabase();
            ContentValues r = new ContentValues();
            r.put("estatus",estatus);
            db.update("comentarios",r, "id='"+ id +"' ",null);
            db.close();
        }catch (SQLiteException e)
        {
            Toast.makeText(contexto, "Error:"+e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    public boolean getCompleto(Context contexto)
    {
        boolean resultado=false;
        try{
            Database admin = new Database(contexto,null,1);
            SQLiteDatabase db = admin.getWritableDatabase();
            Cursor fila = db.rawQuery("SELECT guardar_completo FROM documento ",null);
            if(fila.moveToFirst())
            {
                if(fila.getString(0).equalsIgnoreCase("true"))
                {
                    resultado=true;
                }
                else
                {
                    resultado=false;
                }
                Log.i("getMod_comdoc","resultado:"+fila.getString(0));

            }
            else
            {
                resultado=false;
            }
            db.close();
        }catch (SQLiteException e)
        {
            mensajes("Error al consultar codigo:"+e.getMessage(),contexto);
        }
        return resultado;
    }

    public boolean getEnvioAclaracion(Context contexto)
    {
        boolean resultado=false;
        try{
            Database admin = new Database(contexto,null,1);
            SQLiteDatabase db = admin.getWritableDatabase();
            Cursor fila = db.rawQuery("SELECT envio FROM documento ",null);
            if(fila.moveToFirst())
            {
                if(fila.getString(0).equalsIgnoreCase("true"))
                {
                    resultado=true;
                }
                else
                {
                    resultado=false;
                }
                Log.i("getMod_comdoc","resultado:"+fila.getString(0));

            }
            else
            {
                resultado=false;
            }
            db.close();
        }catch (SQLiteException e)
        {
            mensajes("Error al consultar codigo:"+e.getMessage(),contexto);
        }
        return resultado;
    }


}
