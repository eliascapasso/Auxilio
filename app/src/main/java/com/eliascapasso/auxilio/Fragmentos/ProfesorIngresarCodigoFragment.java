package com.eliascapasso.auxilio.Fragmentos;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.eliascapasso.auxilio.Actividades.MainActivity;
import com.eliascapasso.auxilio.Enumerados.EstadoMembresia;
import com.eliascapasso.auxilio.Modelo.Usuario;
import com.eliascapasso.auxilio.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

public class ProfesorIngresarCodigoFragment extends android.support.v4.app.Fragment {
    private EditText edtCodigo;
    private Button btnAceptarCodigo;

    private static String PREFS_KEY = "login_preferences";
    private Usuario usuarioActual;

    private ProgressDialog progressDialog;
    private RequestQueue request;
    private JsonObjectRequest jsonObjectRequest;

    public ProfesorIngresarCodigoFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profesor_ingresar_codigo, container, false);

        edtCodigo = (EditText) view.findViewById(R.id.edtCodigo);
        btnAceptarCodigo = (Button) view.findViewById(R.id.btnAceptarCodigo);

        request = Volley.newRequestQueue(getContext());

        obtenerUsuario();

        //TODO:Temporal
        edtCodigo.setText("aandht26ns7ab39GbdLaM123ba");

        aceptarCodigo();

        return view;
    }

    private void aceptarCodigo() {
        btnAceptarCodigo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(edtCodigo.getText().toString().equals(getString(R.string.codigo_membresia))){
                    actualizarUsuario(usuarioActual);

                    FragmentManager fragmentManager;
                    FragmentTransaction fragmentTransaction;
                    fragmentManager = getFragmentManager();
                    fragmentTransaction = fragmentManager.beginTransaction();
                    ProfesorListaCursosFragment profesorListaCursosFragment = new ProfesorListaCursosFragment();
                    fragmentTransaction.replace(R.id.contenido, profesorListaCursosFragment);
                    //fragmentTransaction.addToBackStack("frag4");
                    fragmentTransaction.commit();
                }
                else{
                    Toast.makeText(getContext(), "El código ingresado es incorrecto", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void actualizarUsuario(Usuario usuarioActual) {
        progressDialog =new ProgressDialog(getContext());
        progressDialog.setMessage("Cargando...");
        progressDialog.show();

        String ip=getString(R.string.ip);

        String url="http://"+ ip +"/auxilioBD/wsJSONActualizarProfesor.php?";

        StringRequest stringRequest =new StringRequest(Request.Method.POST, url,
                response -> {
                    progressDialog.hide();

                    if (response.trim().equalsIgnoreCase("actualiza")){
                        Log.i("RESPUESTA: ",""+response);

                    }else{
                        Log.i("RESPUESTA: ",""+response);
                    }
                },
                error -> {
                    Log.i("ERROR: ", error.toString());
                    progressDialog.hide();
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("dni", String.valueOf(usuarioActual.getDni()));

                //Actualiza el estado de la membresia
                params.put("estado_membresia_profesor", String.valueOf(EstadoMembresia.HABILITADA));

                return params;
            }
        };
        request.add(stringRequest);
        //VolleySingleton.getIntanciaVolley(GestionUsuarioActivity.this).addToRequestQueue(stringRequest);
    }

    private void obtenerUsuario() {
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Cargando...");
        progressDialog.show();

        //Obtiene el usuario de la bd con el correo
        String ip = getString(R.string.ip);
        String url = "http://"+ ip +"/auxilioBD/wsJSONConsultarUsuario.php?correo=" + obtenerLoginSharedPreferencesString(getContext(),"email");

        jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                //Se conecta exitosamente
                response -> {
                    progressDialog.hide();
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

    public static String obtenerLoginSharedPreferencesString(Context context, String keyPref) {
        SharedPreferences preferences = context.getSharedPreferences(PREFS_KEY, MODE_PRIVATE);
        return  preferences.getString(keyPref, "");
    }
}
