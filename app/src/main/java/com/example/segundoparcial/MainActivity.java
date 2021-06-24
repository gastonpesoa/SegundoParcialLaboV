package com.example.segundoparcial;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity implements Handler.Callback, SearchView.OnQueryTextListener, DialogInterface.OnClickListener {

    private Handler handler;
    SharedPreferences prefs;
    SharedPreferences.Editor editor;
    JSONArray usuariosArray;
    TextView tvUsuarios;
    View dialogView;
    EditText etNombre;
    ToggleButton tbAdmin;
    Spinner sRol;

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
            this.setUsuariosArray(strJson);
            this.showUsuarios(strJson);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_principal, menu);
        MenuItem searchItem = menu.findItem(R.id.buscar);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setOnQueryTextListener(this);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.buscar) {
            return true;
        } else if (id == R.id.agregar) {
            Log.d("main", "abrir dialog agregar");
            LayoutInflater li = LayoutInflater.from(this);
            this.dialogView = li.inflate(R.layout.layout_dialogo, null);
            MiDialogo dialogo = new MiDialogo(this, this.dialogView);
            dialogo.show(getSupportFragmentManager(), "dialogo");
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        if(query != null && !query.isEmpty()){
            Log.d("activity", "Hago una busqueda con:" + query);
            String titulo = "Usuario no encontrado";
            String searchResult = "El usuario " + query + " no esta dentro de la lista";
            for (int i = 0; i < this.usuariosArray.length(); i++) {
                try {
                    JSONObject jsonObject = this.usuariosArray.getJSONObject(i);
                    String nombre = jsonObject.get("username").toString();
                    String rol = jsonObject.get("rol").toString();
                    if (nombre.equalsIgnoreCase(query)) {
                        titulo = "Usuario encontrado";
                        searchResult = "El rol del usuario es " + rol;
                        break;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            ResultadoBusquedaDialogo dialogo = new ResultadoBusquedaDialogo(titulo, searchResult);
            dialogo.show(getSupportFragmentManager(), "searchresultdialog");
        }
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        Log.d("activity", "cambio texto:" + newText);
        return false;
    }

    private void showUsuarios(String strJson){
        tvUsuarios.setText(strJson);
    }

    private void setUsuariosArray(String strJson){
        try {
            this.usuariosArray = new JSONArray(strJson);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean handleMessage(@NonNull Message msg) {
        String strJson = (String) msg.obj;
        Log.d("main", strJson);
        this.editor.putString("usuarios", strJson);
        this.editor.commit();
        this.setUsuariosArray(strJson);
        this.showUsuarios(strJson);
        return true;
    }

    public Boolean validarDatos(String nombre, String rol, Boolean esAdmin){
        return nombre != null && !nombre.isEmpty() && rol != null && !rol.isEmpty() && esAdmin != null;
    }

    public String getId(){
        Integer maxId = Integer.MIN_VALUE;
        for (int i = 0; i < this.usuariosArray.length(); i++) {
            try {
                JSONObject jsonObject = this.usuariosArray.getJSONObject(i);
                String strId = jsonObject.get("id").toString();
                Integer userId = Integer.parseInt(strId);
                if (userId > maxId){
                    maxId = userId;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        maxId++;
        return maxId.toString();
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        if (which == AlertDialog.BUTTON_POSITIVE) {
            this.etNombre = (EditText) this.dialogView.findViewById(R.id.eTNombre);
            String strNombre = this.etNombre.getText().toString();
            this.tbAdmin = (ToggleButton) this.dialogView.findViewById(R.id.tbAdmin);
            Boolean esAdmin = this.tbAdmin.isChecked();
            this.sRol = (Spinner) this.dialogView.findViewById(R.id.sRol);
            String strRol = sRol.getSelectedItem().toString();
            Log.d("Nombre",strNombre);
            Log.d("Es admin", esAdmin.toString());
            Log.d("Rol",strRol);

            if(validarDatos(strNombre, strRol, esAdmin)){
                String nuevoUsuarioId = this.getId();
                Log.d("nuevoUsuarioId",nuevoUsuarioId);
                JSONObject nuevoUsuario = new JSONObject();
                try {
                    nuevoUsuario.put("id", nuevoUsuarioId);
                    nuevoUsuario.put("username", strNombre);
                    nuevoUsuario.put("rol", strRol);
                    nuevoUsuario.put("admin", esAdmin);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                this.usuariosArray.put(nuevoUsuario);
                String jsonStr = this.usuariosArray.toString();
                this.editor.putString("usuarios", jsonStr);
                this.editor.commit();
                this.showUsuarios(jsonStr);
            } else {
                Toast.makeText(this.getApplicationContext(),"Los valores no pueden ser vacios", Toast.LENGTH_LONG).show();
            }

        } else if (which == AlertDialog.BUTTON_NEGATIVE){
            Log.d("dialog", "Cancelar!");
        }
    }
}