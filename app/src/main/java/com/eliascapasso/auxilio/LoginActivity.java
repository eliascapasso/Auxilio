package com.eliascapasso.auxilio;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;

public class LoginActivity extends AppCompatActivity {

    private EditText edtEmail;
    private EditText edtPass;
    private Button btnIniciarSesion;
    private Button btnRegistrarse;
    private Switch swRecordarInicio;

    private static String PREFS_KEY = "login_preferences";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        edtEmail = (EditText) findViewById(R.id.edtEmail);
        edtPass = (EditText) findViewById(R.id.edtPass);
        btnIniciarSesion = (Button) findViewById(R.id.btnRegistrar);
        btnRegistrarse = (Button) findViewById(R.id.btnRegistrarse);
        swRecordarInicio = (Switch) findViewById(R.id.swSesionIniciada);

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

                Intent main = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(main);
                finish();
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
}
