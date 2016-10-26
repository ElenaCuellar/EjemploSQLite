package com.example.caxidy.ejemplosqlite;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;

public class Historial extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    ArrayList<String> listaHistorial;
    ArrayAdapter<String> adapt;
    Spinner sp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.historial);

        sp = (Spinner) findViewById(R.id.sphistorial);
        sp.setOnItemSelectedListener(this);
        //se recibe el arraylist con las direcciones que tendra el historial y se pone en el spinner
        listaHistorial = (ArrayList<String>) getIntent().getSerializableExtra("mihistorial");
        adapt = new ArrayAdapter<>(this,R.layout.support_simple_spinner_dropdown_item,listaHistorial);
        sp.setAdapter(adapt);
    }

    //Metodos de los botones
    public void limpiarHistorial(View v){
        //Borrar contenido del spinner y pasar el parametro RESULT_CANCELED al activity principal
        listaHistorial.clear();
        adapt.notifyDataSetChanged();
        sp.setAdapter(adapt);
        Intent i = new Intent();
        setResult(RESULT_CANCELED,i);
        finish();
    }

    public void volver(View v){

        finish();
    }
    //Metodos sobreescritos
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        //cogemos la direccion seleccionada y la añadimos al intent que se pasara a la actividad principal
        if(position>0) {
            String seleccion = parent.getSelectedItem().toString();
            Intent i = new Intent();
            i.putExtra("webseleccionada", seleccion);
            setResult(RESULT_OK, i);
            finish();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {}
    @Override
    public void onBackPressed(){
        finish();
    }
}
