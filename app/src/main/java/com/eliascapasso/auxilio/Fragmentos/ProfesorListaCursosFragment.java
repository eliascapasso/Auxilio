package com.eliascapasso.auxilio.Fragmentos;

import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.eliascapasso.auxilio.Adaptadores.AdaptadorCursos;
import com.eliascapasso.auxilio.Modelo.Curso;
import com.eliascapasso.auxilio.R;

import java.util.ArrayList;

public class ProfesorListaCursosFragment extends Fragment {
    private ArrayList<Curso> listaCursos;
    private ListView lvCursos;

    public ProfesorListaCursosFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profesor_lista_cursos, container, false);

        lvCursos = (ListView) view.findViewById(R.id.lstCursosProfesor);

        inicializarAtributos(view);

        return view;
    }

    private void inicializarAtributos(View v){
        listaCursos = new ArrayList<Curso>();
        obtenerCursos();
        lvCursos.setAdapter(new AdaptadorCursos(v.getContext(), listaCursos));
    }

    private void obtenerCursos() {
        //Implementar

        Curso curso = new Curso("Algebra Lineal","06/03/2019", 1050, 20, (float) 3.5, 1);

        listaCursos.add(curso);
    }
}
