package com.eliascapasso.auxilio;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class LoginActivity extends AppCompatActivity {

    private EditText edtEmail;
    private EditText edtPass;
    private Button btnIniciarSesion;
    private Button btnRegistrarse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        edtEmail = (EditText) findViewById(R.id.edtEmail);
        edtPass = (EditText) findViewById(R.id.edtPass);
        btnIniciarSesion = (Button) findViewById(R.id.btnRegistrar);
        btnRegistrarse = (Button) findViewById(R.id.btnRegistrarse);

        iniciarSesion();

        registrarse();
    }

    private void iniciarSesion(){
        btnIniciarSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent main = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(main);
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
}
