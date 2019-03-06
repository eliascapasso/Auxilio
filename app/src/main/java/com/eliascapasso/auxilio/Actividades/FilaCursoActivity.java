package com.eliascapasso.auxilio.Actividades;

import android.content.Context;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.eliascapasso.auxilio.Adaptadores.CursoHolder;
import com.eliascapasso.auxilio.R;

public class FilaCursoActivity extends LinearLayout{
    public TextView tvTituloCurso;
    public TextView tvCostoCurso;
    public RatingBar rtgCalificacion;
    private CursoHolder cursoHolder;

    public FilaCursoActivity(Context context){
        super(context);
        inflate(context, R.layout.list_item_cursos, this);
    }

    public void createViews(){
        //Instanciamos los elementos de la vista
        cursoHolder = (CursoHolder) this.getTag();
        tvTituloCurso = cursoHolder.tvTituloCurso;
        tvCostoCurso = cursoHolder.tvCostoCurso;
        rtgCalificacion = cursoHolder.rtgCalificacion;
    }
}
