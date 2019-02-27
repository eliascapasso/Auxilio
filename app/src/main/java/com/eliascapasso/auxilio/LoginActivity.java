package com.eliascapasso.auxilio;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.eliascapasso.auxilio.Modelo.Usuario;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity implements Response.Listener<JSONObject>, Response.ErrorListener {

    private EditText edtEmail;
    private EditText edtPass;
    private Button btnIniciarSesion;
    private Button btnRegistrarse;
    private Switch swRecordarInicio;

    private ProgressDialog progressDialog;
    private RequestQueue request;
    private JsonObjectRequest jsonObjectRequest;

    private static String PREFS_KEY = "login_preferences";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        edtEmail = (EditText) findViewById(R.id.edtEmail);
        edtPass = (EditText) findViewById(R.id.edtPass);
        btnIniciarSesion = (Button) findViewById(R.id.btnIniciarSesion);
        btnRegistrarse = (Button) findViewById(R.id.btnRegistrarse);
        swRecordarInicio = (Switch) findViewById(R.id.swSesionIniciada);

        request = Volley.newRequestQueue(this);

        inicializar();

        iniciarSesion();

        registrarse();
    }

    private void inicializar(){
        edtEmail.setText(obtenerLoginSharedPreferencesString(getApplicationContext(),"email"));
        edtPass.setText(obtenerLoginSharedPreferencesString(getApplicationContext(),"pass"));
        swRecordarInicio.setChecked(obtenerLoginSharedPreferencesCheckBoxRecordar(getApplicationContext(), "recordar"));

        if(swRecordarInicio.isChecked()){
            Intent main = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(main);
            finish();
        }
    }

    private void iniciarSesion(){
        btnIniciarSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = edtEmail.getText().toString();
                String pass = edtPass.getText().toString();

                //Preferencias compartidas
                if(swRecordarInicio.isChecked()){
                    guardarLoginSharedPreferences(email, pass);
                }
                else{
                    guardarLoginSharedPreferences("", "");
                }

                cargarWebService();

                /*Intent main = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(main);
                finish();*/
            }
        });
    }

    private void registrarse(){
        btnRegistrarse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent reg = new Intent(LoginActivity.this, RegistroActivity.class);
                startActivity(reg);
            }
        });
    }

    private void cargarWebService(){
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Cargando...");
        progressDialog.show();

        //Obtiene el usuario de la bd con el correo
        String ip = "192.168.0.43:8080";
        String url = "http://"+ ip +"/auxilioBD/wsJSONConsultarUsuario.php?correo=" + edtEmail.getText().toString();

        jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url,null, this, this);
        request.add(jsonObjectRequest);
    }

    private void guardarLoginSharedPreferences(String email, String pass) {
        SharedPreferences sharedPref = getSharedPreferences("login_preferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("email", email);
        editor.putString("pass", pass);

        if(email.equals("") && pass.equals("")){
            editor.putBoolean("recordar", false);
        }
        else{
            editor.putBoolean("recordar", true);
        }

        editor.apply();
        editor.commit();
    }

    public static String obtenerLoginSharedPreferencesString(Context context, String keyPref) {
        SharedPreferences preferences = context.getSharedPreferences(PREFS_KEY, MODE_PRIVATE);
        return  preferences.getString(keyPref, "");
    }

    public static boolean obtenerLoginSharedPreferencesCheckBoxRecordar(Context context, String keyPref) {
        SharedPreferences preferences = context.getSharedPreferences(PREFS_KEY, MODE_PRIVATE);
        return  preferences.getBoolean(keyPref, false);
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        progressDialog.hide();
        Toast.makeText(this, "No se pudo obtener el usuario: " + error.toString()  , Toast.LENGTH_SHORT).show();
        Log.i("ERROR: ", error.toString());
    }

    @Override
    public void onResponse(JSONObject response) {
        progressDialog.hide();

        Usuario miUsuario = new Usuario();
        JSONArray jsonArray = response.optJSONArray("usuario");

        JSONObject jsonObject = null;
        try {
            jsonObject = jsonArray.getJSONObject(0);
            miUsuario.setDni(jsonObject.optInt("dni"));
            miUsuario.setNombre(jsonObject.optString("nombre"));
            miUsuario.setApellido(jsonObject.optString("apellido"));
            miUsuario.setNacimiento(jsonObject.optString("nacimiento"));
            miUsuario.setCorreo(jsonObject.optString("correo"));
            miUsuario.setPass(jsonObject.optString("pass"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Toast.makeText(this, "DNI: " + Integer.toString(miUsuario.getDni())  , Toast.LENGTH_SHORT).show();
    }
}
