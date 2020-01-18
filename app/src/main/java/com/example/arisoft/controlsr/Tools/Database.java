package com.example.arisoft.controlsr.Tools;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class Database extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "ControlSR";
    public static final int DATABASE_VERSION = 2;
    public Database(Context context, SQLiteDatabase.CursorFactory factory,int version) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
    }

    public static final String TABLA_LOGIN = "login";
    public static final String TABLA_ALM = "almacenes";
    public static final String TABLA_ART = "articulos";
    public static final String TABLA_DOC = "documento";
    public static final String TABLA_COM = "comentarios";

    public static final String SQL_LOGIN="CREATE TABLE "+TABLA_LOGIN+"(success text ," +
            "nomEmpresa text," +
            "id_empresa text,"+
            "dominio text," +
            "usuario text," +
            "almacen text," +
            "folioOC text," +
            "serieOC text," +
            "numeroOC text," +
            "almacenaux text)";
    public static final String SQL_ALM="CREATE TABLE "+TABLA_ALM+"(cod_alm text ," +
            "almacen text)";
    public static final String SQL_ART="CREATE TABLE "+TABLA_ART+"(codigo text ," +
            "codigo2 text," +
            "descripcion text," +
            "posicion text," +
            "cantidad Float,"+
            "surtido Float," +
            "por_surtir Float," +
            "folio text," +
            "costo Float," +
            "surtidoaux Float," +
            "iva Float," +
            "modificar text," +
            "crear text)";
    public static final String SQL_DOC="CREATE TABLE "+TABLA_DOC+"(folio_previo text ," +
            "fecha text," +
            "almacen text," +
            "codigo_prov text," +
            "proveedor text," +
            "unidades_a_surtir text," +
            "mod_comdoc text," +
            "mod_comren text," +
            "crear_comdoc text," +
            "crear_conren text)";
    public static final String SQL_COM="CREATE TABLE "+TABLA_COM+"( folio_previo text ," +
            "comentario text," +
            "estatus text," + //A=activo//G=guardado
            "id INTEGER PRIMARY KEY AUTOINCREMENT)";

    private static final String SQL_INICIOLOGIN = "DROP TABLE IF EXISTS "+TABLA_LOGIN;
    private static final String SQL_INICIOALM = "DROP TABLE IF EXISTS "+TABLA_ALM;
    private static final String SQL_INICIOART = "DROP TABLE IF EXISTS "+TABLA_ART;
    private static final String SQL_INICIODOC = "DROP TABLE IF EXISTS "+TABLA_DOC;
    private static final String SQL_INICIOCOM = "DROP TABLE IF EXISTS "+TABLA_COM;


    public void onCreate(SQLiteDatabase db) {
        //eliminar si existe
        db.execSQL(SQL_INICIOLOGIN);
        db.execSQL(SQL_INICIOALM);
        db.execSQL(SQL_INICIOART);
        db.execSQL(SQL_INICIODOC);
        db.execSQL(SQL_INICIOCOM);


        //crear
        db.execSQL(SQL_LOGIN);
        db.execSQL(SQL_ALM);
        db.execSQL(SQL_ART);
        db.execSQL(SQL_DOC);
        db.execSQL(SQL_COM);

    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if(oldVersion==1 && newVersion==2)
        {
            db.execSQL("ALTER TABLE comentarios ADD COLUMN estatus TEXT DEFAULT 'A'");
            db.execSQL("ALTER TABLE comentarios ADD COLUMN id INTEGER ");
        }
    }

}
