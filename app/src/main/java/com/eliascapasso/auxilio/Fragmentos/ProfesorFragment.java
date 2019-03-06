package com.eliascapasso.auxilio.Fragmentos;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.eliascapasso.auxilio.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class ProfesorFragment extends android.support.v4.app.Fragment{

    final Calendar myCalendar = Calendar.getInstance();

    private EditText edtFechaReunion;
    private EditText edtMateria;
    private EditText edtRazonPostulacion;
    private Button btnSolicitarCredencial;

    public ProfesorFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_profesor_solicitud_credencial, container, false);

        edtFechaReunion = (EditText) v.findViewById(R.id.edtFechaReunion);
        edtMateria = (EditText) v.findViewById(R.id.edtMateria);
        edtRazonPostulacion = (EditText) v.findViewById(R.id.edtRazonPostulacion);
        btnSolicitarCredencial = (Button) v.findViewById(R.id.btnSolicitarCredencial);

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
                            //Hacer cosas aqui al hacer clic en el boton de aceptar
                        }
                    });
                    builder.show();
                }
            }
        });

    }
}
