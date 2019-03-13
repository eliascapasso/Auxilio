package com.eliascapasso.auxilio.Fragmentos;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

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

import java.util.ArrayList;

public class AlumnoListaCursosFragment extends android.support.v4.app.Fragment {
    private ArrayList<Curso> listaCursos;
    private ListView lvCursos;

    ProgressDialog progressDialog;

    RequestQueue request;
    JsonObjectRequest jsonObjectRequest;

    public AlumnoListaCursosFragment() {

    }

    public static AlumnoListaCursosFragment newInstance(String param1, String param2) {

        return null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_alumno_lista_cursos, container, false);

        lvCursos = (ListView) view.findViewById(R.id.lstCursosAlumno);

        request = Volley.newRequestQueue(getContext());

        inicializarAtributos(view);

        return view;
    }

    private void inicializarAtributos(View v){
        listaCursos = new ArrayList<Curso>();
        obtenerCursos();
    }

    private void obtenerCursos() {
        progressDialog=new ProgressDialog(getContext());
        progressDialog.setMessage("Consultando...");
        progressDialog.show();

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

                            curso.setTitulo(jsonObject.optString("titulo"));
                            curso.setDescripcion(jsonObject.optString("descripcion"));
                            curso.setFecha(jsonObject.optString("fecha"));
                            curso.setCosto(jsonObject.optInt("costo"));
                            curso.setCupos(jsonObject.optInt("cupos"));
                            curso.setCalificacion(jsonObject.optDouble("calificacion"));
                            curso.setDuracion(jsonObject.optString("duracion"));
                            curso.setDni_profesor(jsonObject.optInt("dni_profesor"));
                            listaCursos.add(curso);
                        }
                        progressDialog.hide();
                        lvCursos.setAdapter(new AdaptadorCursos(getView().getContext(), listaCursos));

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
                    Toast.makeText(getContext(), "No se pudo conectar con el servidor: " + error.toString()  , Toast.LENGTH_SHORT).show();
                    Log.i("ERROR: ", error.toString());
                });
        request.add(jsonObjectRequest);
    }
}
