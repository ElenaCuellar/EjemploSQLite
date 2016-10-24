/*Navegador web que almacena en una BD las webs a las que vamos accediendo y luego al empezar
*a escribir la direccion, salen en el historial automaticamente.*/

package com.example.caxidy.ejemplosqlite;

import android.content.Context;
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
import java.util.List;

public class MainActivity extends AppCompatActivity {
    WebView w;
    ArrayAdapter<String> adp;
    AdminSQL admin;
    MiAutoCompleteView autocomp;
    String[] item;

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
            autocomp = (MiAutoCompleteView) findViewById(R.id.autocompletar);
            //Añadir listener para mostrar sugerencias de webs mientras se escribe
            autocomp.addTextChangedListener(new MiAutoCompleteTextChangedListener(this));
            //Configurar adaptador
            adp = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, item);
            autocomp.setAdapter(adp);

            w = (WebView) findViewById(R.id.web);
            w.setWebViewClient(new WebViewClient()); //para que las URL se abran en nuestro navegador

            //Pagina de inicio
            autocomp.setText(getString(R.string.urlDefault));
            w.loadUrl(getString(R.string.urlDefault));
            //Soporte para Javascript
            w.getSettings().setJavaScriptEnabled(true);

            /*Evento al pulsar intro en la barra de direcciones*/
            autocomp.setOnKeyListener(new View.OnKeyListener() {
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                        //Acceder a la web mediante INTRO
                        w.loadUrl(autocomp.getText().toString());
                        alta();
                        esconderTeclado();
                        return true;
                    }
                    return false;
                }
            });
        }catch (Exception e){
            Toast.makeText(this,getString(R.string.error)+ e.getMessage(),Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }
    //Metodos del menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==R.id.itembaja)
            baja();
        return super.onOptionsItemSelected(item);
    }

    @Override
    //Para guardar la informacion necesaria que se pierde al girar la pantalla
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putString("url", autocomp.getText().toString());
        savedInstanceState.putString("urlweb", w.getUrl());
        w.saveState(savedInstanceState);
    }
    @Override
    //Para recuperar la informacion guardada al girar la pantalla
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        autocomp.setText(savedInstanceState.getString("url"));
        w.loadUrl(savedInstanceState.getString("urlweb"));
        w.restoreState(savedInstanceState);
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
