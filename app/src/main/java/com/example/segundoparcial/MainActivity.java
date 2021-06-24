package com.example.segundoparcial;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;

public class MainActivity extends AppCompatActivity implements Handler.Callback {

    private Handler handler;
    SharedPreferences prefs;
    SharedPreferences.Editor editor;
    JSONArray usuariosArray;
    TextView tvUsuarios;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvUsuarios = this.findViewById(R.id.tvUsuarios);

        this.prefs = getSharedPreferences("miConfig", Context.MODE_PRIVATE);
        this.editor = prefs.edit();
        String strJson = prefs.getString("usuarios", "0");

        if (strJson == null || "0".equalsIgnoreCase(strJson)) {
            Log.d("Prefs not exist", strJson);
            handler = new Handler(this);
            Worker worker = new Worker(handler, "http://10.0.2.2:3001/usuarios");
            worker.start();
        } else {
            Log.d("Prefs exist", strJson);
            this.showUsuarios(strJson);
        }


    }

    private void showUsuarios(String strJson){
        tvUsuarios.setText(strJson);
    }

    @Override
    public boolean handleMessage(@NonNull Message msg) {
        String strJson = (String) msg.obj;
        Log.d("main", strJson);
        this.editor.putString("usuarios", strJson);
        this.editor.commit();
        this.showUsuarios(strJson);
        return true;
    }
}