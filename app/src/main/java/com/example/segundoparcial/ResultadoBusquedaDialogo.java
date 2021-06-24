package com.example.segundoparcial;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class ResultadoBusquedaDialogo extends DialogFragment {

    String titulo;
    String resultado;

    public ResultadoBusquedaDialogo(String titulo, String resultado) {
        this.titulo = titulo;
        this.resultado = resultado;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(this.titulo);
        builder.setMessage(this.resultado);
        builder.setPositiveButton("OK", null);
        AlertDialog ad = builder.create();
        return ad;
    }
}
