package com.eliascapasso.auxilio.Modelo;

public class Curso {
    private  int idCurso;
    private String titulo;
    private String descripcion;
    private String fecha;
    private int costo;
    private int cupos;
    private Double calificacion;
    private String duracion;
    private int dni_profesor;

    public Curso(String titulo, String fecha, int costo, int cupos, Double calificacion, int dni_profesor) {
        this.titulo = titulo;
        this.fecha = fecha;
        this.costo = costo;
        this.cupos = cupos;
        this.calificacion = calificacion;
        this.dni_profesor = dni_profesor;
    }

    public Curso(){

    }

    public int getIdCurso() {
        return idCurso;
    }

    public void setIdCurso(int idCurso) {
        this.idCurso = idCurso;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
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

    public Double getCalificacion() {
        return calificacion;
    }

    public void setCalificacion(Double calificacion) {
        this.calificacion = calificacion;
    }

    public int getDni_profesor() {
        return dni_profesor;
    }

    public void setDni_profesor(int dni_profesor) {
        this.dni_profesor = dni_profesor;
    }
}
