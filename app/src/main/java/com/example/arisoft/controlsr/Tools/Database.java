package com.example.arisoft.controlsr.Tools;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class Database extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "ControlSR";
    public static final int DATABASE_VERSION = 6;
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
            "surtidoaux Float," +//cantidad surtida cuando es parcial
            "iva Float," +
            "modificar text," +//por articulo true/false
            "crear text," +//por articulo true/false
            "descuento1 text," +
            "descuento2 text," +
            "descuento3 text," +
            "descuento4 text," +
            "descuento5 text," +
            "tipocambio text," +
            "backorder text," +//por articulo true/false
            "factor text," +//datos extras
            "clasificacion text," +//datos extras
            "proveedor text," +//datos extras
            "imp1 text," +//datos extras
            "imp2 text," +//datos extras
            "imp1_tab text," +//datos extras
            "imp2_tab text," +//datos extras
            "cantbackorder text)";//cantidad backorder sirve para modificarla restandole la cantidad
    public static final String SQL_DOC="CREATE TABLE "+TABLA_DOC+"(folio_previo text ," +
            "fecha text," +
            "almacen text," +
            "codigo_prov text," +
            "proveedor text," +
            "unidades_a_surtir text," +
            "mod_comdoc text," + //true/false
            "mod_comren text," +//true/false
            "crear_comdoc text," +//true/false
            "crear_conren text," +//true/false
            "envio text," +//true/false
            "guardar_completo text," +//true/false
            "mod_back text," +//true/false
            "pos_coment INTEGER," +
            "coment_completos text)";//true/false
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
        if(oldVersion==1 && newVersion>1)
        {
            db.execSQL("ALTER TABLE comentarios ADD COLUMN estatus TEXT DEFAULT 'A'");
            db.execSQL("ALTER TABLE comentarios ADD COLUMN id INTEGER ");
        }
        if(oldVersion<=2 && newVersion>2)
        {
            db.execSQL("ALTER TABLE articulos ADD COLUMN descuento1 TEXT");
            db.execSQL("ALTER TABLE articulos ADD COLUMN descuento2 TEXT");
            db.execSQL("ALTER TABLE articulos ADD COLUMN descuento3 TEXT");
            db.execSQL("ALTER TABLE articulos ADD COLUMN descuento4 TEXT");
            db.execSQL("ALTER TABLE articulos ADD COLUMN descuento5 TEXT");
            db.execSQL("ALTER TABLE articulos ADD COLUMN tipocambio TEXT");
        }
        if(oldVersion<=3 && newVersion>3)
        {
            db.execSQL("ALTER TABLE articulos ADD COLUMN envio TEXT");
            db.execSQL("ALTER TABLE articulos ADD COLUMN guardar_completo TEXT");
        }
        if(oldVersion<=4 && newVersion>4)
        {
            db.execSQL("ALTER TABLE articulos ADD COLUMN backorder TEXT DEFAULT 'false' ");
            db.execSQL("ALTER TABLE documento ADD COLUMN mod_back TEXT DEFAULT 'false' ");
            db.execSQL("ALTER TABLE documento ADD COLUMN pos_coment INTEGER ");
            db.execSQL("ALTER TABLE documento ADD COLUMN coment_completos text ");

        }
        if(oldVersion<=4 && newVersion>5)
        {
            db.execSQL("ALTER TABLE articulos ADD COLUMN factor TEXT");
            db.execSQL("ALTER TABLE articulos ADD COLUMN clasificacion TEXT");
            db.execSQL("ALTER TABLE articulos ADD COLUMN proveedor TEXT");
            db.execSQL("ALTER TABLE articulos ADD COLUMN imp1 TEXT");
            db.execSQL("ALTER TABLE articulos ADD COLUMN imp2 TEXT");
            db.execSQL("ALTER TABLE articulos ADD COLUMN imp1_tab TEXT");
            db.execSQL("ALTER TABLE articulos ADD COLUMN imp2_tab TEXT");
            db.execSQL("ALTER TABLE articulos ADD COLUMN cantbackorder TEXT");
        }


    }

}
