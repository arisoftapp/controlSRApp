package com.example.arisoft.controlsr.Tools;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class Database extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "ControlSR";
    public static final int DATABASE_VERSION = 1;
    public Database(Context context, SQLiteDatabase.CursorFactory factory,int version) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
    }

    public static final String TABLA_LOGIN = "login";
    public static final String TABLA_ALM = "almacenes";
    public static final String TABLA_ART = "articulos";

    public static final String SQL_LOGIN="CREATE TABLE "+TABLA_LOGIN+"(success boolean ," +
            "nomEmpresa text," +
            "id_empresa text,"+
            "dominio text," +
            "usuario text)";
    public static final String SQL_ALM="CREATE TABLE "+TABLA_ALM+"(cod_alm text ," +
            "almacen text)";
    public static final String SQL_ART="CREATE TABLE "+TABLA_ART+"(codigo text ," +
            "descripcion text," +
            "cantidad Float,"+
            "surtido Float," +
            "por_surtir Float)";

    private static final String SQL_INICIOLOGIN = "DROP TABLE IF EXISTS "+TABLA_LOGIN;
    private static final String SQL_INICIOALM = "DROP TABLE IF EXISTS "+TABLA_ALM;
    private static final String SQL_INICIOART = "DROP TABLE IF EXISTS "+TABLA_ART;

    public void onCreate(SQLiteDatabase db) {
        //eliminar si existe
        db.execSQL(SQL_INICIOLOGIN);
        db.execSQL(SQL_INICIOALM);
        db.execSQL(SQL_INICIOART);

        //crear
        db.execSQL(SQL_LOGIN);
        db.execSQL(SQL_ALM);
        db.execSQL(SQL_ART);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

}
