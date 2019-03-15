package com.eliascapasso.auxilio.Fragmentos;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.eliascapasso.auxilio.Actividades.LoginActivity;
import com.eliascapasso.auxilio.Adaptadores.AdaptadorCursos;
import com.eliascapasso.auxilio.Enumerados.EstadoMembresia;
import com.eliascapasso.auxilio.Modelo.AlumnoCurso;
import com.eliascapasso.auxilio.Modelo.Curso;
import com.eliascapasso.auxilio.Modelo.Usuario;
import com.eliascapasso.auxilio.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

public class AlumnoListaCursosFragment extends android.support.v4.app.Fragment {
    private static String PREFS_KEY = "login_preferences";

    private ArrayList<Curso> listaCursos;
    private ArrayList<AlumnoCurso> listaAlumnosCursos;
    private ListView lvCursos;
    private ToggleButton tgBtnFiltroCursos;

    ProgressDialog progressDialog;

    RequestQueue request;
    JsonObjectRequest jsonObjectRequest;
    private Usuario usuarioActual;

    public AlumnoListaCursosFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_alumno_lista_cursos, container, false);

        lvCursos = (ListView) view.findViewById(R.id.lstCursosAlumno);
        tgBtnFiltroCursos = (ToggleButton) view.findViewById(R.id.tgBtnFiltroCursos);

        request = Volley.newRequestQueue(getContext());

        inicializarAtributos(view);

        filtroCursos();

        return view;
    }

    private void inicializarAtributos(View v){
        listaCursos = new ArrayList<Curso>();
        listaAlumnosCursos = new ArrayList<AlumnoCurso>();

        obtenerUsuario();
    }

    private void obtenerUsuario() {
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Cargando...");
        progressDialog.show();

        //Obtiene el usuario de la bd con el correo
        String ip = getString(R.string.ip);
        String url = "http://"+ ip +"/auxilioBD/wsJSONConsultarUsuario.php?correo=" +
                obtenerLoginSharedPreferencesString(getContext(),"email");

        jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                //Se conecta exitosamente
                response -> {
                    JSONArray jsonArray = response.optJSONArray("usuario");

                    JSONObject jsonObject = null;
                    try {
                        jsonObject = jsonArray.getJSONObject(0);
                        usuarioActual = new Usuario(jsonObject.optInt("dni"),
                                jsonObject.optString("nombre"),
                                jsonObject.optString("apellido"),
                                jsonObject.optString("nacimiento"),
                                jsonObject.optString("correo"),
                                jsonObject.optString("pass"));
                        usuarioActual.setDato(jsonObject.optString("foto"));

                        switch (jsonObject.optString("estado_membresia_profesor")){
                            case "DESHABILITADA":
                                usuarioActual.setMembresia(EstadoMembresia.DESHABILITADA);
                                break;
                            case "HABILITADA":
                                usuarioActual.setMembresia(EstadoMembresia.HABILITADA);
                                break;
                            case "EN_ESPERA":
                                usuarioActual.setMembresia(EstadoMembresia.EN_ESPERA);
                                break;
                        }

                        obtenerAlumnosCursos();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                //No se conectar
                error -> {
                    progressDialog.hide();
                    Log.i("ERROR: ", error.toString());
                });
        request.add(jsonObjectRequest);
    }

    private void obtenerAlumnosCursos(){
        String ip=getString(R.string.ip);

        String url="http://"+ ip +"/auxilioBD/wsJSONConsultarListaAlumnosCursos.php";

        jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                //Se conecta exitosamente
                response -> {
                    AlumnoCurso alumnoCurso=null;

                    JSONArray json=response.optJSONArray("alumno_curso");

                    try {

                        for (int i=0;i<json.length();i++){
                            alumnoCurso = new AlumnoCurso();
                            JSONObject jsonObject=null;
                            jsonObject=json.getJSONObject(i);

                            alumnoCurso.setId_curso(jsonObject.optInt("id_curso"));
                            alumnoCurso.setDni_alumno(jsonObject.optInt("dni_alumno"));
                            alumnoCurso.setPagado(jsonObject.optBoolean("pagado"));

                            listaAlumnosCursos.add(alumnoCurso);
                        }

                        obtenerCursos();

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(getContext(), "No se ha podido establecer conexión con el servidor" +
                                " "+response, Toast.LENGTH_LONG).show();
                        progressDialog.hide();
                    }
                },
                //No se conecta
                error -> {
                    progressDialog.hide();

                    obtenerCursos();
                    Log.i("ERROR: ", error.toString());
                });
        request.add(jsonObjectRequest);
    }

    private void obtenerCursos() {
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
                        progressDialog.hide();
                        lvCursos.setAdapter(new AdaptadorCursos(getView().getContext(), listaCursos));

                    } catch (JSONException e) {
                        e.printStackTrace();
                        progressDialog.hide();
                    }
                },
                //No se conecta
                error -> {
                    progressDialog.hide();
                    Log.i("ERROR: ", error.toString());
                });
        request.add(jsonObjectRequest);
    }

    private void filtroCursos() {
        tgBtnFiltroCursos.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(listaAlumnosCursos.size() != 0){
                    if(isChecked)
                    {
                        ArrayList<Integer> idCursos = new ArrayList<Integer>();
                        ArrayList<Curso> cursosFiltrados = new ArrayList<Curso>();
                        for(AlumnoCurso ac: listaAlumnosCursos){
                            if(usuarioActual.getDni() == ac.getDni_alumno()){
                                idCursos.add(ac.getId_curso());
                            }
                        }

                        for (Curso c: listaCursos){
                            for(Integer idC: idCursos){
                                if(idC.equals(c.getIdCurso())){
                                    cursosFiltrados.add(c);
                                }
                            }
                        }

                        lvCursos.setAdapter(new AdaptadorCursos(getView().getContext(), cursosFiltrados));
                    }
                    else
                    {
                        lvCursos.setAdapter(new AdaptadorCursos(getView().getContext(), listaCursos));
                    }
                }
            }
        });
    }

    public static String obtenerLoginSharedPreferencesString(Context context, String keyPref) {
        SharedPreferences preferences = context.getSharedPreferences(PREFS_KEY, MODE_PRIVATE);
        return  preferences.getString(keyPref, "");
    }
}
