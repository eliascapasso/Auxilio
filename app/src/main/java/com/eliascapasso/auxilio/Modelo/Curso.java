package com.eliascapasso.auxilio.Modelo;

import java.sql.Time;
import java.util.Date;

public class Curso {
    private static int idCurso = 0;
    private Date fecha;
    private Time hora;
    private Double costo;
    private int cupos;
    private Double calificacion;
    private int idProfesor;

    public Curso(Date fecha, Time hora, Double costo, int cupos, Double calificacion, int idProfesor) {
        idCurso++;
        this.fecha = fecha;
        this.hora = hora;
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

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public Time getHora() {
        return hora;
    }

    public void setHora(Time hora) {
        this.hora = hora;
    }

    public Double getCosto() {
        return costo;
    }

    public void setCosto(Double costo) {
        this.costo = costo;
    }

    public int getCupos() {
        return cupos;
    }

    public void setCupos(int cupos) {
        this.cupos = cupos;
    }

    public Double getCalificacion() {
        return calificacion;
    }

    public void setCalificacion(Double calificacion) {
        this.calificacion = calificacion;
    }

    public int getIdProfesor() {
        return idProfesor;
    }

    public void setIdProfesor(int idProfesor) {
        this.idProfesor = idProfesor;
    }
}
