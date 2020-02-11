package com.example.arisoft.controlsr.Modelo;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.arisoft.controlsr.Tools.Database;

public class Calculos {
    public String getIva(Context contexto)
    {
        Float res= Float.valueOf(0);
        Float iva,cantidad,ivatotal,total,descuento1,descuento2,descuento3,descuento4,descuento5,tipocambio;
        try{
            Database admin = new Database(contexto,null,1);
            SQLiteDatabase db = admin.getWritableDatabase();

            Cursor fila = db.rawQuery("SELECT surtido,surtidoaux,costo,iva,descuento1,descuento2,descuento3,descuento4,descuento5,tipocambio FROM articulos where surtidoaux!=surtido",null);
            if(fila.moveToFirst())
            {
                do{
                    descuento1=fila.getFloat(4);
                    descuento2=fila.getFloat(5);
                    descuento3=fila.getFloat(6);
                    descuento4=fila.getFloat(7);
                    descuento5=fila.getFloat(8);
                    tipocambio=fila.getFloat(9);
                    //modificar calculos
                    cantidad=fila.getFloat(0)-fila.getFloat(1);
                    total=cantidad*fila.getFloat(2);
                    if(descuento1>0)
                    {
                        total=calculodescuentoiva(descuento1,total);
                    }
                    if(descuento2>0)
                    {
                        total=calculodescuentoiva(descuento2,total);
                    }
                    if(descuento3>0)
                    {
                        total=calculodescuentoiva(descuento3,total);
                    }
                    if(descuento4>0)
                    {
                        total=calculodescuentoiva(descuento4,total);
                    }
                    if(descuento5>0)
                    {
                        total=calculodescuentoiva(descuento5,total);
                    }


                    iva=fila.getFloat(3)/100;
                    ivatotal=total*iva;
                    ivatotal=ivatotal*tipocambio;
                    res=res+ivatotal;
                    Log.i("calculosiva"," iva | "+iva);
                    Log.i("calculosiva"," cantidad | "+cantidad);
                    Log.i("calculosiva"," total | "+total);
                    Log.i("calculosiva"," ivatotal | "+ivatotal);
                    Log.i("calculosiva"," des1 | "+descuento1);
                    Log.i("calculosiva"," | "+descuento2);
                    Log.i("calculosiva"," | "+descuento3);
                    Log.i("calculosiva"," | "+descuento4);
                    Log.i("calculosiva"," | "+descuento5);
                    Log.i("calculosiva","tipocambio "+tipocambio);
                    Log.i("calculosiva","-----------------------------------------------------------------");


                    //alm=fila.getString(0);

                }while (fila.moveToNext());
            }
            else
            {
                res=Float.valueOf(0);
            }

            db.close();
        }catch (Exception e)
        {
            Log.e("Error:",""+e.getMessage());
            res=Float.valueOf(0);
            //mensajes("Error al validar login:"+e.getMessage());
        }
        String valor=""+res;

        valor=""+formatearDecimales(Double.parseDouble(valor),2);
        Log.i("calculosiva","valor "+valor);
        return ""+valor;
    }
    public Float calculodescuentoiva(Float descuento,Float total)
    {
        descuento=descuento/100;
        descuento=total*descuento;
        total=total-descuento;
        return total;
    }
    public String getSumatotal(Context contexto)
    {
        Float res= Float.valueOf(0);
        float iva=Float.valueOf(0);
        float tipocambio = Float.valueOf(0);
        try{
            Database admin = new Database(contexto,null,1);
            SQLiteDatabase db = admin.getWritableDatabase();

            Cursor fila = db.rawQuery("SELECT surtido,surtidoaux,costo,tipocambio,iva FROM articulos where surtidoaux!=surtido",null);
            if(fila.moveToFirst())
            {
                do{
                    tipocambio=fila.getFloat(3);
                    iva=fila.getFloat(4);
                    res=res+(fila.getFloat(0)-fila.getFloat(1))*fila.getFloat(2);
                    Log.i("calcularsumatotal"," costo "+res);
                    Log.i("calcularsumatotal"," iva "+iva);
                    Log.i("calcularsumatotal"," tipocambio "+tipocambio);
                    //alm=fila.getString(0);

                }while (fila.moveToNext());
            }
            else
            {
                res=Float.valueOf(0);
            }

            db.close();

            if(tipocambio>0)
            {
                res=res*tipocambio;
            }
            res=res-calcularDescuento(contexto);
            Log.i("calcularsumatotal"," subtotal "+res);

        }catch (Exception e)
        {
            Log.e("Error:",""+e.getMessage());
            res=Float.valueOf(0);
            //mensajes("Error al validar login:"+e.getMessage());
        }
        String valor=""+res;
        valor=""+formatearDecimales(Double.parseDouble(valor),2);
        return ""+valor;
    }
    public String getTotal(Context contexto)
    {
        Float res= Float.valueOf(0);
        float iva=Float.valueOf(0);
        float tipocambio = Float.valueOf(0);
        try{
            Database admin = new Database(contexto,null,1);
            SQLiteDatabase db = admin.getWritableDatabase();

            Cursor fila = db.rawQuery("SELECT surtido,surtidoaux,costo,tipocambio,iva FROM articulos where surtidoaux!=surtido",null);
            if(fila.moveToFirst())
            {
                do{
                    tipocambio=fila.getFloat(3);
                    iva=fila.getFloat(4);
                    res=res+(fila.getFloat(0)-fila.getFloat(1))*fila.getFloat(2);
                    Log.i("calcularsumatotal"," | "+res);
                    Log.i("calcularsumatotal"," iva "+iva);
                    Log.i("calcularsumatotal"," tipocambio "+tipocambio);
                    //alm=fila.getString(0);

                }while (fila.moveToNext());
            }
            else
            {
                res=Float.valueOf(0);
            }

            db.close();

            if(tipocambio>0)
            {
                res=res*tipocambio;
            }
            res=res-calcularDescuento(contexto);
            if(iva>0)
            {
                iva=iva/100;
                iva=iva+1;
                res=res*iva;
            }
            Log.i("calcularsumatotal"," total "+res);

        }catch (Exception e)
        {
            Log.e("Error:",""+e.getMessage());
            res=Float.valueOf(0);
            //mensajes("Error al validar login:"+e.getMessage());
        }
        String valor=""+res;
        valor=""+formatearDecimales(Double.parseDouble(valor),2);
        return ""+valor;
    }
    //el calculo de descuento es despues de multiplicarlo por tipo de cambio
    public float calcularDescuento(Context contexto)
    {
        float descuentoTotal = 0;
        float descuentoGlobal=0;
        try{
            Database admin = new Database(contexto,null,1);
            SQLiteDatabase db = admin.getWritableDatabase();
            Cursor fila = db.rawQuery("SELECT surtido,surtidoaux,costo,descuento1,descuento2,descuento3,descuento4,descuento5,tipocambio FROM articulos where surtidoaux!=surtido",null);
            if(fila.moveToFirst())
            {
                do{
                    float cant=fila.getFloat(0)-fila.getFloat(1);
                    float descuento1=fila.getFloat(3);
                    float descuento2=fila.getFloat(4);
                    float descuento3=fila.getFloat(5);
                    float descuento4=fila.getFloat(6);
                    float descuento5=fila.getFloat(7);

                    float tipocambio=fila.getFloat(8);
                    float costoTotal=cant*fila.getFloat(2);
                    if(descuento1>0)
                    {
                        descuento1=descuento1/100;
                        descuentoTotal=descuentoTotal+(costoTotal*descuento1);
                        costoTotal=costoTotal-(costoTotal*descuento1);

                    }

                    if(descuento2>0)
                    {
                        descuento2=descuento2/100;
                        descuentoTotal=descuentoTotal+(costoTotal*descuento2);
                        costoTotal=costoTotal-(costoTotal*descuento2);

                    }

                    if(descuento3>0)
                    {
                        descuento3=descuento3/100;
                        descuentoTotal=descuentoTotal+(costoTotal*descuento3);
                        costoTotal=costoTotal-(costoTotal*descuento3);
                    }

                    if(descuento4>0)
                    {
                        descuento4=descuento4/100;
                        descuentoTotal=descuentoTotal+(costoTotal*descuento4);
                        costoTotal=costoTotal-(costoTotal*descuento4);
                    }
                    if(descuento5>0)
                    {
                        descuento5=descuento5/100;
                        descuentoTotal=descuentoTotal+(costoTotal*descuento5);
                        costoTotal=costoTotal-(costoTotal*descuento5);
                    }





                    //descuentoTotal=descuentoTotal*tipocambio;
                    /*
                    Log.i("calculardescuento"," | "+fila.getString(0)+
                            " | "+fila.getString(1)+
                            " | "+fila.getString(2)+
                            " | "+fila.getString(3)+
                            " | "+fila.getString(4)+
                            " | "+fila.getString(5)+
                            " | "+fila.getString(6)+
                            " | "+fila.getString(7)
                    );
                    Log.i("calculardescuento",
                            " | "+cant+
                                    " | "+costoTotal+
                                    " | "+descuentoTotal+
                                    " | "+tipocambio

                    );

                     */
                    descuentoGlobal=descuentoGlobal+(descuentoTotal*tipocambio);
                    Log.i("calculardescuento","costototal "+costoTotal);
                    Log.i("calculardescuento","descuentototal "+descuentoTotal);
                    Log.i("calculardescuento","------------------------");
                }while (fila.moveToNext());
            }
            else
            {
                descuentoGlobal=0;
            }

            db.close();
        }catch (Exception e)
        {
            Log.e("Error:",""+e.getMessage());
            descuentoGlobal=0;
        }
        if(descuentoTotal>0)
        {
            String valor=""+descuentoGlobal;
            valor=""+formatearDecimales(Double.parseDouble(valor),2);
            descuentoGlobal= Float.parseFloat(valor);

        }
        return descuentoGlobal;
    }

    public static Double formatearDecimales(Double numero, Integer numeroDecimales) {
        return Math.round(numero * Math.pow(10, numeroDecimales)) / Math.pow(10, numeroDecimales);
    }
}
