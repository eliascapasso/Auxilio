package com.eliascapasso.auxilio.Adaptadores;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RatingBar;
import android.widget.TextView;

import com.eliascapasso.auxilio.Actividades.FilaCursoActivity;
import com.eliascapasso.auxilio.Modelo.Curso;
import com.eliascapasso.auxilio.R;

import java.util.ArrayList;

public class AdaptadorCursos extends BaseAdapter {
    private Context contexto;
    private ArrayList<Curso> listaCursos;

    public AdaptadorCursos(Context contexto, ArrayList<Curso> listaCursos) {
        this.contexto = contexto;
        this.listaCursos = listaCursos;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup parent) {
        CursoHolder cursoHolder;
        //Convertimos la vista por defecto en el tipo de nuestra vista personalizada
        FilaCursoActivity view = (FilaCursoActivity) convertView;
        if(view == null){
            //Instanciamos la vista y el PedidoHolder
            cursoHolder = new CursoHolder();
            view = new FilaCursoActivity(contexto);
            //Instanciamos los recursos
            cursoHolder.tvTituloCurso = (TextView) view.findViewById(R.id.tvTituloCurso);
            cursoHolder.tvCostoCurso = (TextView) view.findViewById(R.id.tvCostoCurso);
            cursoHolder.rtgCalificacion = (RatingBar) view.findViewById(R.id.rtgCalificacion);
            //asignamos el viewHolder a la vista
            view.setTag(cursoHolder);
            //Al cambiar el codigo, debemos llamar nosotros al metodo createViews() de la vista
            view.createViews();
        }else{
            //Si la vista ya existe, recuperamos el viewHolder asociado
            cursoHolder = (CursoHolder) view.getTag();
        }

        cursoHolder.tvTituloCurso.setText(listaCursos.get(i).getTitulo());
        cursoHolder.tvCostoCurso.setText("Bs" + listaCursos.get(i).getCosto());
        cursoHolder.rtgCalificacion.setRating(listaCursos.get(i).getCalificacion());
        cursoHolder.rtgCalificacion.setIsIndicator(true);

        return view;
    }

    @Override
    public int getCount() {
        return listaCursos.size();
    }

    @Override
    public Curso getItem(int i) {
        return listaCursos.get(i);
    }

    @Override
    public long getItemId(int i) {
        return getItem(i).getIdCurso();
    }
}
