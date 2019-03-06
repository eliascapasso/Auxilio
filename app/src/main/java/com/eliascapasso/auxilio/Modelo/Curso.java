package com.eliascapasso.auxilio.Modelo;

import java.sql.Time;
import java.util.Date;

public class Curso {
    private static int idCurso = 0;
    private String titulo;
    private String fecha;
    private int costo;
    private int cupos;
    private Float calificacion;
    private String duracion;
    private int idProfesor;

    public Curso(String titulo, String fecha, int costo, int cupos, Float calificacion, int idProfesor) {
        idCurso++;
        this.titulo = titulo;
        this.fecha = fecha;
        this.costo = costo;
        this.cupos = cupos;
        this.calificacion = calificacion;
        this.idProfesor = idProfesor;
    }

    public static int getIdCurso() {
        return idCurso;
    }

    public static void setIdCurso(int idCurso) {
        Curso.idCurso = idCurso;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDuracion() {
        return duracion;
    }

    public void setDuracion(String duracion) {
        this.duracion = duracion;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public int getCosto() {
        return costo;
    }

    public void setCosto(int costo) {
        this.costo = costo;
    }

    public int getCupos() {
        return cupos;
    }

    public void setCupos(int cupos) {
        this.cupos = cupos;
    }

    public Float getCalificacion() {
        return calificacion;
    }

    public void setCalificacion(Float calificacion) {
        this.calificacion = calificacion;
    }

    public int getIdProfesor() {
        return idProfesor;
    }

    public void setIdProfesor(int idProfesor) {
        this.idProfesor = idProfesor;
    }
}
