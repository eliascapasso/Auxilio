package com.eliascapasso.auxilio.Modelo;

import java.util.ArrayList;

public class Alumno {
    private static int idAlumno = 0;
    private Double calificacion;
    private ArrayList<Curso> cursos;

    public Alumno(Double calificacion) {
        this.calificacion = calificacion;
        this.cursos = new ArrayList<Curso>();

        idAlumno++;
    }

    public static int getIdAlumno() {
        return idAlumno;
    }

    public static void setIdAlumno(int idAlumno) {
        Alumno.idAlumno = idAlumno;
    }

    public Double getCalificacion() {
        return calificacion;
    }

    public void setCalificacion(Double calificacion) {
        this.calificacion = calificacion;
    }

    public ArrayList<Curso> getCursos() {
        return cursos;
    }

    public void addCurso(Curso curso) {
        this.cursos.add(curso);
    }
}
