package com.example.segundoparcial;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class MiDialogo extends DialogFragment {
    DialogInterface.OnClickListener listener;
    View dialogoView;

    public MiDialogo(DialogInterface.OnClickListener lister, View dialogoView) {
        this.listener = lister;
        this.dialogoView = dialogoView;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        Spinner spinner = (Spinner) this.dialogoView.findViewById(R.id.sRol);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(), R.array.roles,android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        builder.setTitle("Nuevo Contacto");
        builder.setView(this.dialogoView);
        builder.setPositiveButton("GUARDAR", this.listener);
        builder.setNegativeButton("CERRAR", this.listener);
        AlertDialog ad = builder.create();
        return ad;
    }
}
