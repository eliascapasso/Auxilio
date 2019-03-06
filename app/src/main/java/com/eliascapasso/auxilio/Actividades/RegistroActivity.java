package com.eliascapasso.auxilio.Actividades;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.eliascapasso.auxilio.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;

public class RegistroActivity extends AppCompatActivity{
    final Calendar myCalendar = Calendar.getInstance();

    private EditText edtNacimiento;
    private EditText edtDni;
    private EditText edtNombre;
    private EditText edtApellido;
    private EditText edtCorreo;
    private EditText edtPass;
    private EditText edtConfPass;
    private Button btnRegistrar;

    private ProgressDialog progressDialog;
    private RequestQueue request;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        edtNacimiento= (EditText) findViewById(R.id.edtNacimiento);
        edtDni= (EditText) findViewById(R.id.edtDni);
        edtNombre= (EditText) findViewById(R.id.edtNombre);
        edtApellido= (EditText) findViewById(R.id.edtApellido);
        edtCorreo= (EditText) findViewById(R.id.edtEmail);
        edtPass= (EditText) findViewById(R.id.edtPassReg);
        edtConfPass= (EditText) findViewById(R.id.edtConfPass);
        btnRegistrar = (Button) findViewById(R.id.btnRegistrar);

        request = Volley.newRequestQueue(this);

        fechaNacieminto();

        registrarUsuario();
    }

    private void registrarUsuario(){
        btnRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(validarCampos()){
                    cargarUsuario();
                }
                else{
                    Toast.makeText(RegistroActivity.this, "Campos inválidos", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void cargarUsuario(){
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Cargando...");
        progressDialog.show();

        //Agrega el usuario a la bd
        String ip = "192.168.0.38:8080";
        String url = "http://"+ ip +"/auxilioBD/wsJSONRegistro.php?" +
                "dni="+edtDni.getText().toString()+
                "&nombre="+edtNombre.getText().toString()+
                "&apellido="+edtApellido.getText().toString()+
                "&nacimiento="+edtNacimiento.getText().toString()+
                "&correo="+edtCorreo.getText().toString()+
                "&pass="+edtPass.getText().toString();
        url = url.replace(" ", "%20");

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                response -> {
                    progressDialog.hide();
                    Toast.makeText(RegistroActivity.this, "Registro exitoso", Toast.LENGTH_SHORT).show();

                    try {
                        //converting response to json object
                        JSONObject obj = new JSONObject(response);

                        //if no error in response
                        if (!obj.getBoolean("error")) {
                            Log.i("MENSAJE:", obj.getString("message"));
                        } else {
                            Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    edtDni.setText("");
                    edtNombre.setText("");
                    edtApellido.setText("");
                    edtCorreo.setText("");
                    edtNacimiento.setText("");
                    edtPass.setText("");
                    edtConfPass.setText("");
                },
                error -> {
                    progressDialog.hide();
                    Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("nombre", edtNombre.getText().toString());
                params.put("apellido", edtApellido.getText().toString());
                params.put("correo", edtCorreo.getText().toString());
                params.put("nacimiento", edtNacimiento.getText().toString());
                params.put("pass", edtNacimiento.getText().toString());
                return params;
            }
        };

        request.add(stringRequest);
    }

    private boolean validarCampos(){
        if(edtCorreo.getText().toString().length() == 0 ||
                edtPass.getText().toString().length() == 0 ||
                edtApellido.getText().toString().length() == 0 ||
                edtNombre.getText().toString().length() == 0 ||
                edtDni.getText().toString().length() == 0 ||
                edtNacimiento.getText().toString().length() == 0 ||
                edtConfPass.getText().toString().length() == 0){
            return false;
        }
        if(!edtConfPass.getText().toString().equals(edtPass.getText().toString())){
            return false;
        }

        Pattern pattern = Patterns.EMAIL_ADDRESS;
        return pattern.matcher(edtCorreo.getText().toString()).matches();
    }

    private void fechaNacieminto(){
        DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                actualizarLabel();
            }
        };

        edtNacimiento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO Auto-generated method stub
                new DatePickerDialog(RegistroActivity.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
    }

    private void actualizarLabel() {
        String myFormat = "MM/dd/yy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, new Locale("es"));

        edtNacimiento.setText(sdf.format(myCalendar.getTime()).toString());
    }
}
