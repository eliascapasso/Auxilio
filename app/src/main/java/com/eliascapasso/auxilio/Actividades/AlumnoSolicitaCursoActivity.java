package com.eliascapasso.auxilio.Actividades;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.RatingBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.eliascapasso.auxilio.Adaptadores.AdaptadorCursos;
import com.eliascapasso.auxilio.Modelo.Curso;
import com.eliascapasso.auxilio.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class AlumnoSolicitaCursoActivity extends AppCompatActivity {
    private static String PREFS_KEY = "login_preferences";

    private TextView txtTituloCurso;
    private TextView txtCuposCurso;
    private TextView txtNombreProfesor;
    private RatingBar rbCalificacionProfesor;
    private TextView txtDescrCurso;
    private TextView txtFechaCurso;
    private TextView txtCostoCurso;
    private RatingBar rbCalificacionCurso;

    RequestQueue request;
    JsonObjectRequest jsonObjectRequest;

    private Curso curso;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alumno_solicita_curso);

        txtTituloCurso = (TextView) findViewById(R.id.txtTituloCurso);
        txtCuposCurso = (TextView) findViewById(R.id.txtCuposCurso);
        txtNombreProfesor = (TextView) findViewById(R.id.txtNombreProfesor);
        rbCalificacionProfesor = (RatingBar) findViewById(R.id.rtgCalificacionProfesor);
        txtDescrCurso = (TextView) findViewById(R.id.txtDescrCurso);
        txtFechaCurso = (TextView) findViewById(R.id.txtFechaCurso);
        txtCostoCurso = (TextView) findViewById(R.id.txtCostoCurso);
        rbCalificacionCurso = (RatingBar) findViewById(R.id.rtgCalificacionCurso);

        request = Volley.newRequestQueue(this);

        inicializar();
    }

    private void inicializar() {
        Bundle extras = getIntent().getExtras();
        int idCurso = extras.getInt("idCurso");

        obtenerCurso(idCurso);
    }

    private void obtenerCurso(int idCurso) {
        String ip=getString(R.string.ip);

        String url="http://"+ ip +"/auxilioBD/wsJSONConsultarListaCursos.php";

        jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                //Se conecta exitosamente
                response -> {

                    JSONArray json=response.optJSONArray("curso");

                    try {
                        curso = new Curso();

                        for (int i=0;i<json.length();i++){
                            JSONObject jsonObject=null;
                            jsonObject=json.getJSONObject(i);

                            if(jsonObject.optInt("id_curso") == idCurso){
                                curso.setIdCurso(jsonObject.optInt("id_curso"));
                                curso.setTitulo(jsonObject.optString("titulo"));
                                curso.setDescripcion(jsonObject.optString("descripcion"));
                                curso.setFecha(jsonObject.optString("fecha"));
                                curso.setCosto(jsonObject.optInt("costo"));
                                curso.setCupos(jsonObject.optInt("cupos"));
                                curso.setCalificacion(jsonObject.optDouble("calificacion"));
                                curso.setDni_profesor(jsonObject.optInt("dni_profesor"));

                                i = json.length();
                            }
                        }

                        txtTituloCurso.setText(curso.getTitulo());
                        txtDescrCurso.setText(curso.getDescripcion());
                        txtCostoCurso.setText("Bs" + String.valueOf(curso.getCosto()));
                        txtCuposCurso.setText("Cupos: " + String.valueOf(curso.getCupos()));
                        txtFechaCurso.setText(curso.getFecha());
                        //rbCalificacionCurso.setRating(curso.getCalificacion().floatValue());

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

    public static String obtenerLoginSharedPreferencesString(Context context, String keyPref) {
        SharedPreferences preferences = context.getSharedPreferences(PREFS_KEY, MODE_PRIVATE);
        return  preferences.getString(keyPref, "");
    }
}
