package com.eliascapasso.auxilio.Modelo;

import java.util.ArrayList;
import java.util.Date;

public class Profesor {
    private static int idProfesor = 0;
    private ArrayList<Curso> cursos;
    private boolean membresiaHabilitada;
    private Date fechaMembresia;
    private Double calificacion;

    public Profesor(boolean membresiaHabilitada, Date fechaMembresia, Double calificacion) {
        this.cursos = new ArrayList<Curso>();
        this.membresiaHabilitada = membresiaHabilitada;
        this.fechaMembresia = fechaMembresia;
        this.calificacion = calificacion;

        idProfesor++;
    }

    public static int getIdProfesor() {
        return idProfesor;
    }

    public static void setIdProfesor(int idProfesor) {
        Profesor.idProfesor = idProfesor;
    }

    public ArrayList<Curso> getCursos() {
        return cursos;
    }

    public void setCursos(ArrayList<Curso> cursos) {
        this.cursos = cursos;
    }

    public boolean isMembresiaHabilitada() {
        return membresiaHabilitada;
    }

    public void setMembresiaHabilitada(boolean membresiaHabilitada) {
        this.membresiaHabilitada = membresiaHabilitada;
    }

    public Date getFechaMembresia() {
        return fechaMembresia;
    }

    public void setFechaMembresia(Date fechaMembresia) {
        this.fechaMembresia = fechaMembresia;
    }

    public Double getCalificacion() {
        return calificacion;
    }

    public void setCalificacion(Double calificacion) {
        this.calificacion = calificacion;
    }
}
