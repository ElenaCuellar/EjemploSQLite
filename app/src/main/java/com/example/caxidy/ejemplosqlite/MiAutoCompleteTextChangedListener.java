/*Listener que se llama cada vez que se presiona una tecla y hay que buscar en la BD*/
package com.example.caxidy.ejemplosqlite;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.ArrayAdapter;

public class MiAutoCompleteTextChangedListener implements TextWatcher {
    Context context;

    public MiAutoCompleteTextChangedListener(Context context){
        this.context = context;
    }

    @Override
    public void afterTextChanged(Editable s) {
        //No hacer nada
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        //No hacer nada
    }

    @Override
    public void onTextChanged(CharSequence userInput, int start, int before, int count) {
        MainActivity mainActivity = ((MainActivity) context);
        //Llamada a la BD dependiendo de lo que se pulse, que obtiene las urls coincidentes
        mainActivity.item = mainActivity.obtenerItemsBD(userInput.toString());
        //Actualizar el adaptador
        mainActivity.adp.notifyDataSetChanged();
        mainActivity.adp = new ArrayAdapter<>(mainActivity, android.R.layout.simple_dropdown_item_1line, mainActivity.item);
        mainActivity.autocomp.setAdapter(mainActivity.adp);
    }
}
