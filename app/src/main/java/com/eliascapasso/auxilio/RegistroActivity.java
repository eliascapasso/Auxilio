package com.eliascapasso.auxilio;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.net.sip.SipSession;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class RegistroActivity extends AppCompatActivity implements Response.Listener<JSONObject>, Response.ErrorListener{
    final Calendar myCalendar = Calendar.getInstance();

    private EditText edtNacimiento;
    private EditText edtDni;
    private EditText edtNombre;
    private EditText edtApellido;
    private EditText edtCorreo;
    private EditText edtPass;
    private EditText edtConfPass;
    private Button btnRegistrar;

    private RequestQueue request;
    private JsonObjectRequest jsonObjectRequest;
    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        edtNacimiento= (EditText) findViewById(R.id.edtNacimiento);
        edtDni= (EditText) findViewById(R.id.edtDni);
        edtNombre= (EditText) findViewById(R.id.edtNombre);
        edtApellido= (EditText) findViewById(R.id.edtApellido);
        edtCorreo= (EditText) findViewById(R.id.edtEmail);
        edtPass= (EditText) findViewById(R.id.edtPass);
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
                cargarWebService();
            }
        });
    }

    private void cargarWebService(){
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Cargando...");
        progressDialog.show();

        //Agrega el usuario a la bd
        String ip = "192.168.0.43:8080";
        String url = "http://"+ ip +"/auxilioBD/wsJSONRegistro.php?" +
                "dni="+edtDni.getText().toString()+
                "&nombre="+edtNombre.getText().toString()+
                "&apellido="+edtApellido.getText().toString()+
                "&nacimiento="+edtNacimiento.getText().toString()+
                "&correo="+edtCorreo.getText().toString()+
                "&pass="+edtPass.getText().toString();
        url = url.replace(" ", "%20");

        jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url,null, this, this);
        request.add(jsonObjectRequest);
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

    @Override
    public void onResponse(JSONObject response) {
        Toast.makeText(this, "Se ha registrado el usuario exitosamente", Toast.LENGTH_SHORT).show();
        progressDialog.hide();
        edtNacimiento.setText("");
        edtNombre.setText("");
        edtApellido.setText("");
        edtCorreo.setText("");
        edtPass.setText("");
        edtConfPass.setText("");
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        progressDialog.hide();
        Toast.makeText(this, "No se pudo registrar el usuario: " + error.toString()  , Toast.LENGTH_SHORT).show();
        Log.i("ERROR: ", error.toString());
    }
}
