package com.eliascapasso.auxilio.Fragmentos;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.eliascapasso.auxilio.R;

public class ProfesorIngresarCodigoFragment extends android.support.v4.app.Fragment {
    public ProfesorIngresarCodigoFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profesor_ingresar_codigo, container, false);

        return view;
    }
}
