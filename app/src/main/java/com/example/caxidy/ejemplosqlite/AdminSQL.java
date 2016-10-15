package com.example.caxidy.ejemplosqlite;

import java.util.ArrayList;
import java.util.List;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.ContentValues;
import android.database.Cursor;
import android.widget.Toast;

public class AdminSQL extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 4;
    protected static final String DATABASE_NAME = "DireccionesWeb";
    public String nombreTabla = "direcciones";
    public String campoId = "id";
    public String campoDir = "direccion";
    Context contexto;

    public AdminSQL(Context context){
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db){
        String sql = "CREATE TABLE " + nombreTabla +" ( "+campoId + " INTEGER PRIMARY KEY AUTOINCREMENT, "+campoDir + " TEXT "+" ) ";
        db.execSQL(sql);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        db.execSQL("Drop table if exists "+nombreTabla);
        onCreate(db);
    }

    /*Crear nuevos registros:
    *@param miObj añade filas*/
    public boolean crear(MiObjeto miObj) {

        boolean creacionSatisfactoria = false;
        /*Creamos un substring con el comienzo de la url a comprobar para que, si no comienza por "http"
        no guarde la direccion en la BD, puesto que sera basura*/
        String comienzoUrl="";
        if(miObj.nombreObj.length()>4)
            comienzoUrl= miObj.nombreObj.substring(0,4);
        //Si la url no existe en la BD y ademas comienza por "http"...
        if(!comprobarRegistro(miObj.nombreObj) && comienzoUrl.equals("http")){
            SQLiteDatabase db = this.getWritableDatabase();
            //...creamos un nuevo registro
            ContentValues values = new ContentValues();
            values.put(campoDir, miObj.nombreObj); //se pone la url en el campo de direccion (la id se crea automaticamente)
            //Devuelve true si se insertan mas de 0 registros
            creacionSatisfactoria = db.insert(nombreTabla, null, values) > 0;
            db.close();
        }
        return creacionSatisfactoria;
    }
    //Comprueba si un registro existe para no volver a insertarlo
    public boolean comprobarRegistro(String nombreObjeto){

        boolean registroExistente = false;

        SQLiteDatabase db = this.getWritableDatabase();
        //Recorremos la BD...
        //Selecciona las ids de la tabla cuyo campo direccion coincida con la url introducida
        Cursor cursor = db.rawQuery(
                "SELECT " + campoId + " FROM " + nombreTabla + " WHERE " + campoDir + " = '" + nombreObjeto + "'", null);
        //Si el cursor no es nulo y ademas tiene mas de 0 registros...quiere decir que la url ya existe en la BD
        if(cursor!=null) {
            if(cursor.getCount()>0) {
                registroExistente = true;
            }
        }
        cursor.close();
        db.close();
        return registroExistente;
    }
    // Lee registros que coincidan con lo escrito en la barra de url
    public List<MiObjeto> lee(String termino) {

        List<MiObjeto> listaReg = new ArrayList<MiObjeto>();
        //Consulta = selecciona urls que contienen el termino, ordenadas por id
        String sql = "";
        sql += "SELECT * FROM " + nombreTabla;
        sql += " WHERE " + campoDir + " LIKE '%" + termino + "%'";
        sql += " ORDER BY " + campoId + " DESC";
        sql += " LIMIT 0,5";
        SQLiteDatabase db = this.getWritableDatabase();
        //Ejecutar consulta
        Cursor cursor = db.rawQuery(sql, null);
        //Vamos pasando por cada fila con el cursor y añadiendo a la lista las urls
        if (cursor.moveToFirst()) { //El cursor se mueve al principio, que devuelve true si hay registros
            do {
                String nombreObj = cursor.getString(cursor.getColumnIndex(campoDir));
                MiObjeto miObj = new MiObjeto(nombreObj);
                listaReg.add(miObj);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return listaReg;
    }
}
