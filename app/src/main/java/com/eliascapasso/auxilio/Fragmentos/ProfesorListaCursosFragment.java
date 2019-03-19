package com.eliascapasso.auxilio.Fragmentos;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.eliascapasso.auxilio.Actividades.ProfesorGestionCursoActivity;
import com.eliascapasso.auxilio.Adaptadores.AdaptadorCursos;
import com.eliascapasso.auxilio.Enumerados.EstadoMembresia;
import com.eliascapasso.auxilio.Modelo.Curso;
import com.eliascapasso.auxilio.Modelo.Usuario;
import com.eliascapasso.auxilio.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

public class ProfesorListaCursosFragment extends android.support.v4.app.Fragment {
    private static String PREFS_KEY = "login_preferences";
    private ArrayList<Curso> listaCursos;
    private ListView lvCursos;

    private Usuario usuarioActual;

    private RequestQueue request;
    private JsonObjectRequest jsonObjectRequest;

    public ProfesorListaCursosFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profesor_lista_cursos, container, false);

        lvCursos = (ListView) view.findViewById(R.id.lstCursosProfesor);

        request = Volley.newRequestQueue(getContext());
        listaCursos = new ArrayList<Curso>();

        inicializarAtributos();

        eligeCurso(view);

        return view;
    }

    private void eligeCurso(View v){
        final Intent gestionCursos = new Intent(v.getContext(), ProfesorGestionCursoActivity.class);

        lvCursos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int idCurso = listaCursos.get(position).getIdCurso();
                gestionCursos.putExtra("idCurso", idCurso);
                gestionCursos.putExtra("gestion", ProfesorGestionCursoActivity.EDITA);
                startActivity(gestionCursos);
            }
        });
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

                            if(curso.getDni_profesor() == usuarioActual.getDni()){
                               listaCursos.add(curso);
                            }
                        }
                        lvCursos.setAdapter(new AdaptadorCursos(getView().getContext(), listaCursos));

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

    private void inicializarAtributos() {
        //Obtiene el usuario de la bd con el correo
        String ip = getString(R.string.ip);
        String url = "http://"+ ip +"/auxilioBD/wsJSONConsultarUsuario.php?correo=" + obtenerLoginSharedPreferencesString(getContext(),"email");

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

                        //Obtiene todos los cursos
                        obtenerCursos();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                //No se conectar
                error -> {
                    Log.i("ERROR: ", error.toString());
                });
        request.add(jsonObjectRequest);
    }

    public static String obtenerLoginSharedPreferencesString(Context context, String keyPref) {
        SharedPreferences preferences = context.getSharedPreferences(PREFS_KEY, MODE_PRIVATE);
        return  preferences.getString(keyPref, "");
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.action_bar_main, menu);
        super.onCreateOptionsMenu(menu,inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == R.id.agregarCurso){
            Intent nuevoCurso = new Intent(getContext(), ProfesorGestionCursoActivity.class);
            nuevoCurso.putExtra("gestion", ProfesorGestionCursoActivity.CREA);
            startActivity(nuevoCurso);
        }

        return super.onOptionsItemSelected(item);
    }
}
