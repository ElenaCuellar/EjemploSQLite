/*Navegador web que almacena en una BD las webs a las que vamos accediendo y luego al empezar
*a escribir la direccion, salen en el historial automaticamente.*/

package com.example.caxidy.ejemplosqlite;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import android.app.AlertDialog;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    WebView w;
    ArrayAdapter<String> adp;
    AdminSQL admin;
    MiAutoCompleteView autocomp;
    String[] item;
    ArrayList<String> historial;
    private static final int SUBACTIVIDAD1 = 1;
    private static final int RES_LIMPIAR = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        try {
            admin = new AdminSQL(MainActivity.this);
            //Poner datos por defecto en la BD
            insertarAlgunaUrl();

            item = new String[]{getString(R.string.introduzca)};
            historial = new ArrayList<>();
            historial.add("Seleccione web..."); //espacio en blanco de la posicion 0

            autocomp = (MiAutoCompleteView) findViewById(R.id.autocompletar);
            //Añadir listener para mostrar sugerencias de webs mientras se escribe
            autocomp.addTextChangedListener(new MiAutoCompleteTextChangedListener(this));
            //Configurar adaptador
            adp = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, item);
            autocomp.setAdapter(adp);

            w = (WebView) findViewById(R.id.web);
            w.setWebViewClient(new WebViewClient()); //para que las URL se abran en nuestro navegador

            //Soporte para Javascript
            w.getSettings().setJavaScriptEnabled(true);

            /*Evento al pulsar intro en la barra de direcciones*/
            autocomp.setOnKeyListener(new View.OnKeyListener() {
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                        //Acceder a la web mediante INTRO
                        w.loadUrl(autocomp.getText().toString());
                        alta();
                        historial.add(autocomp.getText().toString());
                        esconderTeclado();
                        return true;
                    }
                    return false;
                }
            });

            //Recuperar la informacion con SharedPreferences:
            SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
            autocomp.setText(sharedPref.getString("url",getString(R.string.urlDefault)));
            w.loadUrl(sharedPref.getString("urlweb",getString(R.string.urlDefault)));
        }catch (Exception e){
            Toast.makeText(this,getString(R.string.error)+ e.getMessage(),Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }
    @Override
    protected void onPause(){ //Guardar la información con SharedPreferences:
        super.onPause();
        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("url", autocomp.getText().toString());
        editor.putString("urlweb", w.getUrl());
        editor.commit();
    }
    @Override
    public void onSaveInstanceState(Bundle savedBundle){
        savedBundle.putStringArrayList("arraylisthist",historial);
        w.saveState(savedBundle);
    }
    @Override
    public void onRestoreInstanceState(Bundle restoredBundle){
        historial.clear(); //para quitar el registro por defecto "Seleccione web..."
        historial.addAll(restoredBundle.getStringArrayList("arraylisthist"));
        w.restoreState(restoredBundle);
    }
    //Metodos del menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==R.id.itembaja) {
            //Mostrar un dialog de confirmar baja
            AlertDialog.Builder alertbu = new AlertDialog.Builder(this);
            alertbu.setTitle("Confirme Eliminar registro");
            alertbu.setMessage("Pulse OK si desea borrar la web de la base de datos");
            alertbu.setIcon(R.mipmap.ic_launcher);

            alertbu.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    baja();
                }
            });
            alertbu.setNeutralButton("Cancelar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {/*No se hace nada*/}
            });
            AlertDialog dialog = alertbu.create();
            dialog.show();
        }
        else if(item.getItemId()==R.id.itemhistorial){
            //Abrir el activity Historial, pasando la información del ArrayList historial
            Intent i = new Intent(this,Historial.class);
            i.putExtra("mihistorial",historial);
            startActivityForResult(i,SUBACTIVIDAD1);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int codigoActividad, int codigoResultado, Intent datos){
        if(codigoActividad==SUBACTIVIDAD1) {
            if (codigoResultado == RESULT_OK) {
                //se pone la direccion del historial seleccionada en la barra de direcciones y se accede
                autocomp.setText(datos.getStringExtra("webseleccionada"));
                acceder(autocomp);
            }
            else if (codigoResultado == RES_LIMPIAR){
                //se vacia el historial
                historial.clear();
                historial.add("Seleccione web..."); //posicion 0
                Snackbar.make(autocomp,"Historial borrado",Snackbar.LENGTH_LONG).show();
            }
        }
    }

    /*Funcionamiento del navegador*/

    // Para obtener las url coincidentes. Se usa en la clase MiAutoCompleteTextChangedListener.java
    public String[] obtenerItemsBD(String termino){
        //Añade items de forma dinamica
        List<MiObjeto> urls = admin.lee(termino); //Filtra el conjunto de urls coincidentes como una lista de MiObjeto
        int contFilas = urls.size();

        String[] items = new String[contFilas];
        int pos = 0;
        //Guardamos todos los nombres de los objetos
        for (MiObjeto registro : urls) {
            items[pos] = registro.nombreObj;
            pos++;
        }
        return items;
    }

    public void acceder(View v){ //Asociado al boton "Ir"
        w.loadUrl(autocomp.getText().toString()); //cargar la web
        alta(); //Guardar la direccion como un nuevo registro de la bd
        historial.add(autocomp.getText().toString());
        esconderTeclado();
    }

    public void irAtras(View v){
        if(w.canGoBack()) {
            w.goBack();
            autocomp.setText(w.getOriginalUrl());
        }
    }

    //Ocultar el teclado virtual
    public void esconderTeclado() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(autocomp.getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
    }

    /*Funciones de la BD*/

    public void insertarAlgunaUrl(){
        admin.crear( new MiObjeto(getString(R.string.urlDefault)));
        admin.crear( new MiObjeto(getString(R.string.urlGit)));
    }

    public void alta(){
        boolean insertado;
        try{
            String direc=autocomp.getText().toString(); //Guardamos la direccion web
            insertado=admin.crear(new MiObjeto(direc));
            if(insertado)
                Toast.makeText(getApplicationContext(), getString(R.string.insertado), Toast.LENGTH_LONG).show();
        }catch(Exception e){
            Toast.makeText(this,getString(R.string.error)+ e.getMessage(),Toast.LENGTH_LONG).show();
        }
    }
    public void baja(){
        String dirBorrar = autocomp.getText().toString();
        long nreg_afectados = admin.borrarRegistro(dirBorrar);
        if (nreg_afectados <= 0) {
            Toast.makeText(this,"No se ha borrado ningun registro.",Toast.LENGTH_LONG).show();
        }
        else {
            Toast.makeText(this,"Registro borrado.",Toast.LENGTH_LONG).show();
            autocomp.setText(getString(R.string.urlDefault));
        }
    }
}
