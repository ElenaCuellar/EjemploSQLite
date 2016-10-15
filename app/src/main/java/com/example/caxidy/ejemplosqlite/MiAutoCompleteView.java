/*Personalizacion del View de Autocompletar*/
package com.example.caxidy.ejemplosqlite;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.AutoCompleteTextView;

public class MiAutoCompleteView extends AutoCompleteTextView {
    //Constructores:
    public MiAutoCompleteView(Context context) {
        super(context);
    }

    public MiAutoCompleteView(Context context, AttributeSet atributos) {
        super(context, atributos);
    }

    public MiAutoCompleteView(Context context, AttributeSet atributos, int estilo) {
        super(context,atributos, estilo);
    }

    // Deshabilitar el filtro de AutoCompleteTextView
    @Override
    protected void performFiltering(final CharSequence text, final int keyCode) {
        String filtroTexto = "";
        super.performFiltering(filtroTexto, keyCode);
    }

    // Tras una seleccion, cogemos el nuevo valor y se lo añadimos al texto existente
    @Override
    protected void replaceText(final CharSequence text) {
        super.replaceText(text);
    }
}
