package com.eliascapasso.auxilio.Actividades;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.eliascapasso.auxilio.Enumerados.EstadoMembresia;
import com.eliascapasso.auxilio.Modelo.Usuario;
import com.eliascapasso.auxilio.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class GestionUsuarioActivity extends AppCompatActivity{
    //Constantes
    private static final String CARPETA_PRINCIPAL = "misImagenesApp/";//directorio principal
    private static final String CARPETA_IMAGEN = "imagenes";//carpeta donde se guardan las fotos
    private static final String DIRECTORIO_IMAGEN = CARPETA_PRINCIPAL + CARPETA_IMAGEN;//ruta carpeta de directorios
    private final int MIS_PERMISOS = 100;
    private static final int COD_SELECCIONA = 10;
    private static final int COD_FOTO = 20;
    public static int REGISTRO = 1;
    public static int ACTUALIZACION = 2;
    private static String PREFS_KEY = "login_preferences";

    private String path;//almacena la ruta de la imagen
    File fileImagen;
    Bitmap bitmap;

    final Calendar myCalendar = Calendar.getInstance();

    //Widgets
    private EditText edtNacimiento;
    private EditText edtDni;
    private EditText edtNombre;
    private EditText edtApellido;
    private EditText edtCorreo;
    private EditText edtPass;
    private EditText edtConfPass;
    private Button btnAceptar, btnFotoPerfil;
    private ImageView iwFotoPerfil;

    private ProgressDialog progressDialog;
    private RequestQueue request;

    private Boolean flagActualizacion;

    private JsonObjectRequest jsonObjectRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gestion_usuario);

        edtNacimiento= (EditText) findViewById(R.id.edtNacimiento);
        edtDni= (EditText) findViewById(R.id.edtDni);
        edtNombre= (EditText) findViewById(R.id.edtNombre);
        edtApellido= (EditText) findViewById(R.id.edtApellido);
        edtCorreo= (EditText) findViewById(R.id.edtEmail);
        edtPass= (EditText) findViewById(R.id.edtPassReg);
        edtConfPass= (EditText) findViewById(R.id.edtConfPass);
        btnAceptar = (Button) findViewById(R.id.btnAceptar);
        btnFotoPerfil = (Button) findViewById(R.id.btnFotoPerfil);
        iwFotoPerfil = (ImageView) findViewById(R.id.iwFotoPerfil);

        inicializar();

        fotoPerfil();

        fechaNacieminto();

        gestionarUsuario();
    }

    private void inicializar() {
        request = Volley.newRequestQueue(this);

        Bundle extras = getIntent().getExtras();
        int esRegistro = extras.getInt("tipo");
        if(esRegistro == this.REGISTRO){
            flagActualizacion = false;
        }
        else if (esRegistro == this.ACTUALIZACION){
            inicializarCampos();
            flagActualizacion = true;
        }
        else{
            Toast.makeText(this, "ERROR", Toast.LENGTH_SHORT).show();
            finish();
        }

        //Permisos
        if(solicitaPermisosVersionesSuperiores()){
            btnFotoPerfil.setEnabled(true);
        }else{
            btnFotoPerfil.setEnabled(false);
        }
    }

    private void inicializarCampos() {
        //Obtiene el usuario de la bd con el correo
        String ip = getString(R.string.ip);
        String url = "http://"+ ip +"/auxilioBD/wsJSONConsultarUsuario.php?correo=" +
                obtenerLoginSharedPreferencesString(GestionUsuarioActivity.this,"email");

        jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                //Se conecta exitosamente
                response -> {
                    JSONArray jsonArray = response.optJSONArray("usuario");

                    JSONObject jsonObject = null;
                    try {
                        jsonObject = jsonArray.getJSONObject(0);
                        Usuario usuarioActual = new Usuario(jsonObject.optInt("dni"),
                                jsonObject.optString("nombre"),
                                jsonObject.optString("apellido"),
                                jsonObject.optString("nacimiento"),
                                jsonObject.optString("correo"),
                                jsonObject.optString("pass"));
                        usuarioActual.setDato(jsonObject.optString("foto"));

                        //Setea campos
                        edtDni.setText(String.valueOf(usuarioActual.getDni()));
                        edtNombre.setText(usuarioActual.getNombre());
                        edtApellido.setText(usuarioActual.getApellido());
                        edtNacimiento.setText(usuarioActual.getNacimiento());
                        edtCorreo.setText(usuarioActual.getCorreo());
                        edtPass.setText(usuarioActual.getPass());

                        bitmap = redondearFoto(usuarioActual.getFotoPerfil());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                //No se conectar
                error -> {
                    Toast.makeText(GestionUsuarioActivity.this, "Hubo problemas para conectarse con el servidor: " + error.toString()  , Toast.LENGTH_SHORT).show();
                    Log.i("ERROR: ", error.toString());
                });
        request.add(jsonObjectRequest);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode){
            case COD_SELECCIONA:
                if(data != null){
                    Uri miPath=data.getData();
                    path = miPath.getPath();
                    iwFotoPerfil.setImageURI(miPath);

                    try {
                        bitmap=MediaStore.Images.Media.getBitmap(getContentResolver(),miPath);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case COD_FOTO:
                MediaScannerConnection.scanFile(GestionUsuarioActivity.this, new String[]{path}, null,
                        new MediaScannerConnection.OnScanCompletedListener() {
                            @Override
                            public void onScanCompleted(String path, Uri uri) {
                                Log.i("Path",""+path);
                            }
                        });

                bitmap= BitmapFactory.decodeFile(path);
                break;
        }

        if(bitmap != null){
            bitmap = redondearFoto(bitmap);
            bitmap=redimensionarImagen(bitmap,600,800);
        }
    }

    private Bitmap redondearFoto(Bitmap bmap){
        //Hace cuadrada la foto
        bmap = recortarBitmap(bmap);

        //creamos el drawable redondeado
        RoundedBitmapDrawable roundedDrawable =
                RoundedBitmapDrawableFactory.create(getResources(), bmap);

        //asignamos el CornerRadius
        roundedDrawable.setCornerRadius(bmap.getHeight());

        iwFotoPerfil.setImageDrawable(roundedDrawable);

        return bmap;
    }

    private void gestionarUsuario(){
        btnAceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(validarCampos()){
                    //Si no sube ninguna foto, se setea una imagen por defecto
                    if(bitmap == null){
                        bitmap = BitmapFactory.decodeResource(GestionUsuarioActivity.this.getResources(),
                                R.drawable.perfil);
                    }

                    //Si se está registrando un nuevo usuario
                    if(flagActualizacion){
                        actualizarUsuario();
                        finish();
                    }
                    //si se está actualizando un usuario existente
                    else {
                        cargarUsuario();
                    }
                }
                else{
                    Toast.makeText(GestionUsuarioActivity.this, "Campos inválidos", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void actualizarUsuario() {
        progressDialog =new ProgressDialog(this);
        progressDialog.setMessage("Cargando...");
        progressDialog.show();

        String ip=getString(R.string.ip);

        String url="http://"+ ip +"/auxilioBD/wsJSONActualizarUsuario.php?";

        StringRequest stringRequest =new StringRequest(Request.Method.POST, url,
                response -> {
                    progressDialog.hide();

                    if (response.trim().equalsIgnoreCase("actualiza")){
                        Toast.makeText(GestionUsuarioActivity.this,"Se ha Actualizado con exito",Toast.LENGTH_SHORT).show();
                        Log.i("RESPUESTA: ",""+response);

                        guardarLoginSharedPreferences(edtCorreo.getText().toString(), edtPass.getText().toString());
                    }else{
                        Toast.makeText(GestionUsuarioActivity.this,"No se ha Actualizado ",Toast.LENGTH_SHORT).show();
                        Log.i("RESPUESTA: ",""+response);
                    }

                },
                error -> {
                    Toast.makeText(GestionUsuarioActivity.this,"No se ha podido conectar",Toast.LENGTH_SHORT).show();
                    Log.i("ERROR: ", error.toString());
                    progressDialog.hide();
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("dni", edtDni.getText().toString());
                params.put("nombre", edtNombre.getText().toString());
                params.put("apellido", edtApellido.getText().toString());
                params.put("nacimiento", edtNacimiento.getText().toString());
                params.put("correo", edtCorreo.getText().toString());
                params.put("pass", edtPass.getText().toString());

                String fotoPerfil = convertirImgString(bitmap);
                params.put("foto", fotoPerfil);
                return params;
            }
        };
        request.add(stringRequest);
        //VolleySingleton.getIntanciaVolley(GestionUsuarioActivity.this).addToRequestQueue(stringRequest);
    }

    private void cargarUsuario(){
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Cargando...");
        progressDialog.show();

        //Agrega el usuario a la bd
        String ip = getString(R.string.ip);
        String url = "http://"+ ip +"/auxilioBD/wsJSONRegistroUsuario.php?";
        url = url.replace(" ", "%20");

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                response -> {
                    progressDialog.hide();
                    if(response.trim().equalsIgnoreCase("registra")){
                        Toast.makeText(GestionUsuarioActivity.this, "Registro exitoso", Toast.LENGTH_SHORT).show();

                        edtDni.setText("");
                        edtNombre.setText("");
                        edtApellido.setText("");
                        edtCorreo.setText("");
                        edtNacimiento.setText("");
                        edtPass.setText("");
                        edtConfPass.setText("");
                        iwFotoPerfil.setImageResource(R.drawable.perfil);
                    }
                    else{
                        Toast.makeText(GestionUsuarioActivity.this, "No se ha podido registrar el usuario", Toast.LENGTH_SHORT).show();
                        Log.i("MENSAJE: ",response.toString() + "\n\n\n\n");
                    }
                },
                error -> {
                    progressDialog.hide();
                    Toast.makeText(GestionUsuarioActivity.this, "No se ha podido conectar al servidor", Toast.LENGTH_SHORT).show();
                }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("dni", edtDni.getText().toString());
                params.put("nombre", edtNombre.getText().toString());
                params.put("apellido", edtApellido.getText().toString());
                params.put("nacimiento", edtNacimiento.getText().toString());
                params.put("correo", edtCorreo.getText().toString());
                params.put("pass", edtPass.getText().toString());

                String fotoPerfil = convertirImgString(bitmap);
                params.put("foto", fotoPerfil);
                return params;
            }
        };

        request.add(stringRequest);
    }

    private String convertirImgString(Bitmap bitmap) {

        ByteArrayOutputStream array=new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,array);
        byte[] imagenByte=array.toByteArray();
        String imagenString= Base64.encodeToString(imagenByte,Base64.DEFAULT);

        return imagenString;
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

        if(edtDni.getText().toString().length() != 8){
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
                new DatePickerDialog(GestionUsuarioActivity.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
    }

    private void fotoPerfil() {
        btnFotoPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final CharSequence[] opciones={"Tomar Foto","Elegir de Galeria","Cancelar"};
                final AlertDialog.Builder builder=new AlertDialog.Builder(GestionUsuarioActivity.this);
                builder.setTitle("Elige una Opción");
                builder.setItems(opciones, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (opciones[i].equals("Tomar Foto")){
                            abriCamara();
                        }else{
                            if (opciones[i].equals("Elegir de Galeria")){
                                Intent intent=new Intent(Intent.ACTION_PICK,
                                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                intent.setType("image/");
                                startActivityForResult(intent.createChooser(intent,"Seleccione"),COD_SELECCIONA);
                            }else{
                                dialogInterface.dismiss();
                            }
                        }
                    }
                });
                builder.show();
            }
        });
    }

    private void abriCamara() {
        File miFile=new File(Environment.getExternalStorageDirectory(),DIRECTORIO_IMAGEN);
        boolean isCreada=miFile.exists();

        if(isCreada==false){
            isCreada=miFile.mkdirs();
        }

        if(isCreada==true){
            Long consecutivo= System.currentTimeMillis()/1000;
            String nombre=consecutivo.toString()+".jpg";

            path=Environment.getExternalStorageDirectory()+File.separator+DIRECTORIO_IMAGEN
                    +File.separator+nombre;//indicamos la ruta de almacenamiento

            fileImagen=new File(path);

            Intent intent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT,Uri.fromFile(fileImagen));

            ////
            if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.N)
            {
                String authorities=getPackageName()+".provider";
                Uri imageUri= FileProvider.getUriForFile(this,authorities,fileImagen);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            }else
            {
                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(fileImagen));
            }

            startActivityForResult(intent,COD_FOTO);
        }
    }

    private void actualizarLabel() {
        String myFormat = "MM/dd/yy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, new Locale("es"));

        edtNacimiento.setText(sdf.format(myCalendar.getTime()).toString());
    }

    private Bitmap recortarBitmap(Bitmap original) {
        int width, height;

        if(original.getWidth() < original.getHeight()){
            width = original.getWidth();
            height = original.getWidth();
        }
        else{
            width = original.getHeight();
            height = original.getHeight();
        }


        Bitmap croppedImage = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(croppedImage);

        Rect srcRect = new Rect(0, 0, original.getWidth(), original.getHeight());
        Rect dstRect = new Rect(0, 0, width, height);

        int dx = (srcRect.width() - dstRect.width()) / 2;
        int dy = (srcRect.height() - dstRect.height()) / 2;

        // If the srcRect is too big, use the center part of it.
        srcRect.inset(Math.max(0, dx), Math.max(0, dy));

        // If the dstRect is too big, use the center part of it.
        dstRect.inset(Math.max(0, -dx), Math.max(0, -dy));

        // Draw the cropped bitmap in the center
        canvas.drawBitmap(original, srcRect, dstRect, null);

        original.recycle();

        return croppedImage;
    }

    private Bitmap redimensionarImagen(Bitmap bitmap, float anchoNuevo, float altoNuevo) {

        int ancho=bitmap.getWidth();
        int alto=bitmap.getHeight();

        if(ancho>anchoNuevo || alto>altoNuevo){
            float escalaAncho=anchoNuevo/ancho;
            float escalaAlto= altoNuevo/alto;

            Matrix matrix=new Matrix();
            matrix.postScale(escalaAncho,escalaAlto);

            return Bitmap.createBitmap(bitmap,0,0,ancho,alto,matrix,false);

        }else{
            return bitmap;
        }
    }

    private void cargarDialogoRecomendacion() {
        AlertDialog.Builder dialogo=new AlertDialog.Builder(this);
        dialogo.setTitle("Permisos Desactivados");
        dialogo.setMessage("Debe aceptar los permisos para el correcto funcionamiento de la App");

        dialogo.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPermissions(new String[]{WRITE_EXTERNAL_STORAGE,CAMERA},100);
                }
            }
        });
        dialogo.show();
    }

    private void solicitarPermisosManual() {
        final CharSequence[] opciones={"si","no"};
        final AlertDialog.Builder alertOpciones=new AlertDialog.Builder(this);//estamos en fragment
        alertOpciones.setTitle("¿Desea configurar los permisos de forma manual?");
        alertOpciones.setItems(opciones, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (opciones[i].equals("si")){
                    Intent intent=new Intent();
                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri=Uri.fromParts("package",getPackageName(),null);
                    intent.setData(uri);
                    startActivity(intent);
                }else{
                    Toast.makeText(GestionUsuarioActivity.this,"Los permisos no fueron aceptados",Toast.LENGTH_SHORT).show();
                    dialogInterface.dismiss();
                }
            }
        });
        alertOpciones.show();
    }

    private boolean solicitaPermisosVersionesSuperiores() {
        if (Build.VERSION.SDK_INT<Build.VERSION_CODES.M){//validamos si estamos en android menor a 6 para no buscar los permisos
            return true;
        }

        //validamos si los permisos ya fueron aceptados
        if((checkSelfPermission(WRITE_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED)&&checkSelfPermission(CAMERA)==PackageManager.PERMISSION_GRANTED){
            return true;
        }


        if ((shouldShowRequestPermissionRationale(WRITE_EXTERNAL_STORAGE)||(shouldShowRequestPermissionRationale(CAMERA)))){
            cargarDialogoRecomendacion();
        }else{
            requestPermissions(new String[]{WRITE_EXTERNAL_STORAGE, CAMERA}, MIS_PERMISOS);
        }

        return false;//implementamos el que procesa el evento dependiendo de lo que se defina aqui
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode==MIS_PERMISOS){
            if(grantResults.length==2 && grantResults[0]==PackageManager.PERMISSION_GRANTED && grantResults[1]==PackageManager.PERMISSION_GRANTED){//el dos representa los 2 permisos
                Toast.makeText(this,"Permisos aceptados",Toast.LENGTH_SHORT);
                btnFotoPerfil.setEnabled(true);
            }
        }else{
            solicitarPermisosManual();
        }
    }

    public static String obtenerLoginSharedPreferencesString(Context context, String keyPref) {
        SharedPreferences preferences = context.getSharedPreferences(PREFS_KEY, MODE_PRIVATE);
        return  preferences.getString(keyPref, "");
    }

    private void guardarLoginSharedPreferences(String email, String pass) {
        SharedPreferences sharedPref = getSharedPreferences("login_preferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("email", email);
        editor.putString("pass", pass);

        editor.apply();
        editor.commit();
    }
}
