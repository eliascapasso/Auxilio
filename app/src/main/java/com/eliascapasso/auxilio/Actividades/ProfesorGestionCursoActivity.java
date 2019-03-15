package com.eliascapasso.auxilio.Actividades;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.eliascapasso.auxilio.Adaptadores.AdaptadorCursos;
import com.eliascapasso.auxilio.Modelo.Curso;
import com.eliascapasso.auxilio.Modelo.Usuario;
import com.eliascapasso.auxilio.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class ProfesorGestionCursoActivity extends AppCompatActivity {
    public static int CREA = 0;
    public static int EDITA = 1;

    final Calendar myCalendar = Calendar.getInstance();


    private boolean flagCrea;

    private JsonObjectRequest jsonObjectRequest;
    private RequestQueue request;
    private ProgressDialog progressDialog;

    private ArrayList<Curso> listaCursos;

    private EditText edtTitulo;
    private EditText edtDescr;
    private EditText edtCupos;
    private EditText edtCosto;
    private EditText edtFecha;
    private Button btnAceptar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profesor_gestion_curso);

        edtCosto = (EditText)findViewById(R.id.edtCostoCurso);
        edtCupos = (EditText)findViewById(R.id.edtCuposCurso);
        edtDescr = (EditText)findViewById(R.id.edtDescrCurso);
        edtFecha = (EditText)findViewById(R.id.edtFechaCurso);
        edtTitulo = (EditText)findViewById(R.id.edtTituloCurso);
        btnAceptar = (Button)findViewById(R.id.btnAceptarCurso);

        inicializar();

        fechaCurso();

        guardarCurso();
    }

    private void guardarCurso() {
        btnAceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(validarCampos()){
                    //Si se está registrando un nuevo curso
                    if(flagCrea){
                        crearCurso();
                        finish();
                    }
                    //si se está actualizando un curso existente
                    else {
                        actualizarCurso();
                        finish();
                    }
                }
                else{
                    Toast.makeText(ProfesorGestionCursoActivity.this, "Campos inválidos", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void actualizarCurso() {

    }

    private void crearCurso() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Cargando...");
        progressDialog.show();

        //Agrega el curso a la bd
        String ip = getString(R.string.ip);
        String url = "http://"+ ip +"/auxilioBD/wsJSONRegistroCurso.php?";
        url = url.replace(" ", "%20");

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                response -> {
                    progressDialog.hide();
                    if(response.trim().equalsIgnoreCase("registra")){
                        Toast.makeText(ProfesorGestionCursoActivity.this, "Registro exitoso", Toast.LENGTH_SHORT).show();

                        edtTitulo.setText("");
                        edtDescr.setText("");
                        edtFecha.setText("");
                        edtCosto.setText("");
                        edtCupos.setText("");
                    }
                    else{
                        Toast.makeText(ProfesorGestionCursoActivity.this, "No se ha podido registrar el curso", Toast.LENGTH_SHORT).show();
                        Log.i("MENSAJE: ",response.toString() + "\n\n\n\n");
                    }
                },
                error -> {
                    progressDialog.hide();
                    Toast.makeText(ProfesorGestionCursoActivity.this, "No se ha podido conectar al servidor", Toast.LENGTH_SHORT).show();
                }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("titulo", edtTitulo.getText().toString());
                params.put("descripcion", edtDescr.getText().toString());
                params.put("fecha", edtFecha.getText().toString());
                params.put("cupos", edtCupos.getText().toString());
                params.put("costo", edtCosto.getText().toString());

                params.put("calificacion", "0");
                params.put("dni_profesor", "38442199"); //TODO: temporal

                return params;
            }
        };

        request.add(stringRequest);
    }

    private void inicializar() {
        listaCursos = new ArrayList<Curso>();
        request = Volley.newRequestQueue(this);

        Bundle extras = getIntent().getExtras();
        int tipoGestion = extras.getInt("gestion");

        if(tipoGestion == this.CREA){
            flagCrea = true;
        }
        else if (tipoGestion == this.EDITA){
            obtenerCurso();
            flagCrea = false;
        }
        else{
            Toast.makeText(this, "ERROR", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void fechaCurso(){
        DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                actualizarCampo();
            }
        };

        edtFecha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(ProfesorGestionCursoActivity.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
    }

    private void actualizarCampo() {
        String myFormat = "MM/dd/yy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, new Locale("es"));

        edtFecha.setText(sdf.format(myCalendar.getTime()).toString());
    }

    private void obtenerCurso() {
        String ip=getString(R.string.ip);

        String url="http://"+ ip +"/auxilioBD/wsJSONConsultarListaCursos.php";

        jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                //Se conecta exitosamente
                response -> {
                    Curso curso=null;

                    JSONArray json=response.optJSONArray("curso");

                    try {

                        for (int i=0;i<json.length();i++){
                            curso = new Curso();
                            JSONObject jsonObject=null;
                            jsonObject=json.getJSONObject(i);

                            //TODO:Obtener id curso

                            curso.setIdCurso(jsonObject.optInt("id_curso"));
                            curso.setTitulo(jsonObject.optString("titulo"));
                            curso.setDescripcion(jsonObject.optString("descripcion"));
                            curso.setFecha(jsonObject.optString("fecha"));
                            curso.setCosto(jsonObject.optInt("costo"));
                            curso.setCupos(jsonObject.optInt("cupos"));
                            curso.setCalificacion(jsonObject.optDouble("calificacion"));
                            curso.setDni_profesor(jsonObject.optInt("dni_profesor"));

                            listaCursos.add(curso);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                //No se conecta
                error -> {
                    Log.i("ERROR: ", error.toString());
                });
        request.add(jsonObjectRequest);
    }

    private boolean validarCampos(){

        if(edtTitulo.getText().toString().length() == 0 ||
                edtDescr.getText().toString().length() == 0 ||
                edtCupos.getText().toString().length() == 0 ||
                edtFecha.getText().toString().length() == 0 ||
                edtCosto.getText().toString().length() == 0){
            return false;
        }

        return true;
    }
}
