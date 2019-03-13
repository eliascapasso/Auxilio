package com.eliascapasso.auxilio.Actividades;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.eliascapasso.auxilio.Enumerados.EstadoMembresia;
import com.eliascapasso.auxilio.Fragmentos.AlumnoListaCursosFragment;
import com.eliascapasso.auxilio.Fragmentos.ProfesorIngresarCodigoFragment;
import com.eliascapasso.auxilio.Fragmentos.ProfesorListaCursosFragment;
import com.eliascapasso.auxilio.Fragmentos.SolicitarCredencialFragment;
import com.eliascapasso.auxilio.Modelo.Usuario;
import com.eliascapasso.auxilio.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {
    private DrawerLayout drawerLayout;
    private NavigationView navView;
    private TabLayout tabLayout;
    private ActionBarDrawerToggle mToggle;

    private static final int INTERVALO = 2000; //2 segundos para salir
    private long tiempoPrimerClick;

    private Usuario usuarioActual;

    private ProgressDialog progressDialog;
    private RequestQueue request;
    private JsonObjectRequest jsonObjectRequest;

    private static String PREFS_KEY = "login_preferences";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        drawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        navView = (NavigationView)findViewById(R.id.navview);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        mToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.abrir, R.string.cerrar);
        drawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        request = Volley.newRequestQueue(this);

        obtenerUsuario();

        setupNavigationDrawerContent(navView);

        setFragment(1);
    }

    private void obtenerUsuario() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Cargando...");
        progressDialog.show();

        //Obtiene el usuario de la bd con el correo
        String ip = "192.168.0.3:8080";
        String url = "http://"+ ip +"/auxilioBD/wsJSONConsultarUsuario.php?correo=" +
                obtenerLoginSharedPreferencesString(MainActivity.this,"email");

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
                    Toast.makeText(MainActivity.this, "No se pudo conectar con el servidor: " + error.toString()  , Toast.LENGTH_SHORT).show();
                    Log.i("ERROR: ", error.toString());
                });
        request.add(jsonObjectRequest);
    }

    private void setupNavigationDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        switch (menuItem.getItemId()) {
                            case R.id.optMiPerfil:
                                Intent reg = new Intent(MainActivity.this, GestionUsuarioActivity.class);
                                reg.putExtra("tipo", GestionUsuarioActivity.ACTUALIZACION);
                                startActivity(reg);
                                return true;
                            case R.id.optAlumno:
                                getSupportFragmentManager().beginTransaction().
                                        remove(getSupportFragmentManager().findFragmentById(R.id.contenido)).commit();
                                menuItem.setChecked(true);
                                setFragment(1);
                                drawerLayout.closeDrawer(GravityCompat.START);
                                return true;
                            case R.id.optProfesor:
                                getSupportFragmentManager().beginTransaction().
                                        remove(getSupportFragmentManager().findFragmentById(R.id.contenido)).commit();
                                menuItem.setChecked(true);
                                setFragment(2);
                                drawerLayout.closeDrawer(GravityCompat.START);
                                return true;
                            case R.id.optCerrarSesion:
                                menuItem.setChecked(true);
                                setFragment(3);
                                drawerLayout.closeDrawer(GravityCompat.START);
                                return true;
                        }
                        return true;
                    }
                });
    }

    public void setFragment(int position) {
        FragmentManager fragmentManager;
        FragmentTransaction fragmentTransaction;
        switch (position) {
            case 1:
                fragmentManager = getSupportFragmentManager();
                fragmentTransaction = fragmentManager.beginTransaction();
                AlumnoListaCursosFragment alumnoListaCursosFragment = new AlumnoListaCursosFragment();
                fragmentTransaction.replace(R.id.contenido, alumnoListaCursosFragment);
                fragmentTransaction.addToBackStack("frag2");
                fragmentTransaction.commit();
                break;
            case 2:
                //El usuario aun no se registró como profesor
                if(usuarioActual.getMembresia().equals(EstadoMembresia.DESHABILITADA)){
                    fragmentManager = getSupportFragmentManager();
                    fragmentTransaction = fragmentManager.beginTransaction();
                    SolicitarCredencialFragment solicitarCredencialFragment = new SolicitarCredencialFragment();
                    fragmentTransaction.replace(R.id.contenido, solicitarCredencialFragment);
                    //fragmentTransaction.addToBackStack("frag3");
                    fragmentTransaction.commit();
                }
                //El usuario se registró como profesor, pero aún no asistió a la reunion
                else if(usuarioActual.getMembresia().equals(EstadoMembresia.EN_ESPERA)){
                    fragmentManager = getSupportFragmentManager();
                    fragmentTransaction = fragmentManager.beginTransaction();
                    ProfesorIngresarCodigoFragment profesorIngresarCodigoFragment = new ProfesorIngresarCodigoFragment();
                    fragmentTransaction.replace(R.id.contenido, profesorIngresarCodigoFragment);
                    //fragmentTransaction.addToBackStack("frag4");
                    fragmentTransaction.commit();
                }
                //Es usuario está registrado y con membresía habilitada
                else if(usuarioActual.getMembresia().equals(EstadoMembresia.HABILITADA)){
                    fragmentManager = getSupportFragmentManager();
                    fragmentTransaction = fragmentManager.beginTransaction();
                    ProfesorListaCursosFragment profesorListaCursosFragment = new ProfesorListaCursosFragment();
                    fragmentTransaction.replace(R.id.contenido, profesorListaCursosFragment);
                    //fragmentTransaction.addToBackStack("frag4");
                    fragmentTransaction.commit();
                }
                break;
            case 3:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Cerrar Sesión");
                builder.setMessage("¿Seguro que desea cerrar sesión?");
                builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Destilda el recordar inicio sesion
                        SharedPreferences sharedPref = getSharedPreferences("login_preferences", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.putBoolean("recordar", false);
                        editor.apply();
                        editor.commit();

                        finishAffinity();
                        Intent loginActivity = new Intent(MainActivity.this, LoginActivity.class);
                        startActivity(loginActivity);
                    }
                });
                builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //No hace nada
                    }
                });
                builder.show();
                break;
        }
    }

    public static String obtenerLoginSharedPreferencesString(Context context, String keyPref) {
        SharedPreferences preferences = context.getSharedPreferences(PREFS_KEY, MODE_PRIVATE);
        return  preferences.getString(keyPref, "");
    }

    @Override
    public void onBackPressed() {
        if (tiempoPrimerClick + INTERVALO > System.currentTimeMillis()){
            finishAffinity();
            return;
        }else {
            Toast.makeText(this, "Vuelve a presionar para salir", Toast.LENGTH_SHORT).show();
        }
        tiempoPrimerClick = System.currentTimeMillis();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.action_bar_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(mToggle.onOptionsItemSelected(item)){
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}