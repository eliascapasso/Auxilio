package com.eliascapasso.auxilio.Fragmentos;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.eliascapasso.auxilio.Enumerados.EstadoMembresia;
import com.eliascapasso.auxilio.Modelo.MailJob;
import com.eliascapasso.auxilio.Modelo.Usuario;
import com.eliascapasso.auxilio.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import static android.content.Context.MODE_PRIVATE;

public class SolicitarCredencialFragment extends android.support.v4.app.Fragment{

    final Calendar myCalendar = Calendar.getInstance();

    private EditText edtFechaReunion;
    private EditText edtMateria;
    private EditText edtRazonPostulacion;
    private Button btnSolicitarCredencial;

    private static String PREFS_KEY = "login_preferences";

    private Usuario usuarioActual;

    private ProgressDialog progressDialog;
    private RequestQueue request;
    private JsonObjectRequest jsonObjectRequest;

    public SolicitarCredencialFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_profesor_solicitud_credencial, container, false);

        edtFechaReunion = (EditText) v.findViewById(R.id.edtFechaReunion);
        edtMateria = (EditText) v.findViewById(R.id.edtMateria);
        edtRazonPostulacion = (EditText) v.findViewById(R.id.edtRazonPostulacion);
        btnSolicitarCredencial = (Button) v.findViewById(R.id.btnSolicitarCredencial);

        request = Volley.newRequestQueue(getContext());

        obtenerUsuario();

        fechaReunion();

        solicitarCredencial();
        return v;
    }

    private void fechaReunion(){
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

        edtFechaReunion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO Auto-generated method stub
                new DatePickerDialog(getActivity(), date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
    }

    private void actualizarLabel() {
        String myFormat = "MM/dd/yy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, new Locale("es"));

        edtFechaReunion.setText(sdf.format(myCalendar.getTime()).toString());
    }

    private void solicitarCredencial(){
        btnSolicitarCredencial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String materia = edtMateria.getText().toString();
                String razonPostulacion = edtRazonPostulacion.getText().toString();
                String fechaReunion = edtFechaReunion.getText().toString();

                if(materia.length() == 0){
                    Toast.makeText(getContext(), "Debe ingresar la materia que desea postular", Toast.LENGTH_SHORT).show();
                }
                else if(razonPostulacion.length() == 0){
                    Toast.makeText(getContext(), "Debe ingresar la razón de su postulación", Toast.LENGTH_SHORT).show();
                }
                else if(fechaReunion.length() == 0){
                    Toast.makeText(getContext(), "Debe ingresar una fecha para coordinar una reunión con nosotros", Toast.LENGTH_LONG).show();
                }
                else{
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setTitle("Solicitud Credencial");
                    builder.setMessage("La solicitud ha sido enviada con éxito. Pronto nos comunicaremos con usted por correo.");
                    builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //Envia un correo al administrador
                            new MailJob("capassoelias@gmail.com", "39033653").execute(
                                    new MailJob.Mail("capassoelias@gmail.com",
                                                    "elias_capasso@live.com",
                                                    "Solicitud de credencial",
                                                    "Datos del solicitante:\n"+
                                                            "Apellido: " + usuarioActual.getApellido()+"\n"+
                                                            "Nombre: " + usuarioActual.getNombre()+"\n"+
                                                            "Nacimiento: " + usuarioActual.getNacimiento()+"\n"+
                                                            "Correo: " + usuarioActual.getCorreo()+"\n"+
                                                            "Materia a postular: " + edtMateria.getText().toString()+"\n"+
                                                            "Razones de postulación: " + edtRazonPostulacion.getText().toString()+"\n"+
                                                            "Fecha disponible para la reunión: " + edtFechaReunion.getText().toString()+"\n")
                            );

                            //Envia un correo al usuario
                            new MailJob("capassoelias@gmail.com", "39033653").execute(
                                    new MailJob.Mail("capassoelias@gmail.com",
                                                    usuarioActual.getCorreo(),
                                                    "Solicitud de credencial",
                                                    "Hemos recibido con éxito su petición para adquirir una credencial para dar cursos en nuestro portal.\n"+
                                                            "Pronto nos estaremos comunicando con usted.\n\n"+
                                                            "Datos del solicitante:\n"+
                                                            "Apellido: " + usuarioActual.getApellido()+"\n"+
                                                            "Nombre: " + usuarioActual.getNombre()+"\n"+
                                                            "Nacimiento: " + usuarioActual.getNacimiento()+"\n"+
                                                            "Correo: " + usuarioActual.getCorreo()+"\n"+
                                                            "Materia a postular: " + edtMateria.getText().toString()+"\n"+
                                                            "Razones de postulación: " + edtRazonPostulacion.getText().toString()+"\n"+
                                                            "Fecha disponible para la reunión: " + edtFechaReunion.getText().toString()+"\n")
                            );

                            //Cambia el estado de la credencial del profesor
                            usuarioActual.setMembresia(EstadoMembresia.EN_ESPERA);
                            actualizarUsuario(usuarioActual);

                            FragmentManager fragmentManager;
                            FragmentTransaction fragmentTransaction;
                            fragmentManager = getFragmentManager();
                            fragmentTransaction = fragmentManager.beginTransaction();
                            ProfesorIngresarCodigoFragment profesorIngresarCodigoFragment = new ProfesorIngresarCodigoFragment();
                            fragmentTransaction.replace(R.id.contenido, profesorIngresarCodigoFragment);
                            fragmentTransaction.addToBackStack(null);
                            fragmentTransaction.commit();
                        }
                    });
                    builder.show();
                }
            }
        });

    }

    private void actualizarUsuario(Usuario usuarioActual) {
        //Implementar
    }

    private void obtenerUsuario() {
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Cargando...");
        progressDialog.show();

        //Obtiene el usuario de la bd con el correo
        String ip = "192.168.0.18:8080";
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
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                //No se conectar
                error -> {
                    progressDialog.hide();
                    Toast.makeText(getContext(), "No se pudo conectar con el servidor: " + error.toString()  , Toast.LENGTH_SHORT).show();
                    Log.i("ERROR: ", error.toString());
                });
        request.add(jsonObjectRequest);
    }

    public static String obtenerLoginSharedPreferencesString(Context context, String keyPref) {
        SharedPreferences preferences = context.getSharedPreferences(PREFS_KEY, MODE_PRIVATE);
        return  preferences.getString(keyPref, "");
    }
}
