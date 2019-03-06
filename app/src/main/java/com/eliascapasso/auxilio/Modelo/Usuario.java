package com.eliascapasso.auxilio.Modelo;

import com.eliascapasso.auxilio.Enumerados.EstadoMembresia;

import java.util.ArrayList;
import java.util.Date;

public class Usuario {
    //Atributos de usuario
    private int dni;
    private String nombre;
    private String apellido;
    private String nacimiento;
    private String correo;
    private String pass;
    private String fotoPerfil;

    //Atributos de profesor
    private ArrayList<Curso> cursosProfesor;
    private EstadoMembresia membresia;
    private Date fechaMembresia;
    private Double calificacionProfesor;

    //Atributos de alumno
    private Double calificacionAlumno;
    private ArrayList<Curso> cursosAlumno;

    public Usuario(int dni, String nombre, String apellido, String nacimiento, String correo, String pass) {
        //Usuario
        this.dni = dni;
        this.nombre = nombre;
        this.apellido = apellido;
        this.nacimiento = nacimiento;
        this.correo = correo;
        this.pass = pass;
        this.fotoPerfil="";

        //Profesor
        this.cursosProfesor = new ArrayList<Curso>();
        this.membresia = EstadoMembresia.DESHABILITADA;

        //Alumno
        this.cursosAlumno = new ArrayList<Curso>();
    }

    public Usuario(){

    }

    //Metodos de usuario

    public String getFotoPerfil() {
        return fotoPerfil;
    }

    public void setFotoPerfil(String fotoPerfil) {
        this.fotoPerfil = fotoPerfil;
    }

    public int getDni() {
        return dni;
    }

    public void setDni(int dni) {
        this.dni = dni;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getNacimiento() {
        return nacimiento;
    }

    public void setNacimiento(String nacimiento) {
        this.nacimiento = nacimiento;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    //Metodos de profesor

    public ArrayList<Curso> getCursosProfesor() {
        return cursosProfesor;
    }

    public void setCursos(ArrayList<Curso> cursosProfesor) {
        this.cursosProfesor = cursosProfesor;
    }

    public EstadoMembresia getMembresia() {
        return membresia;
    }

    public void setMembresia(EstadoMembresia membresia) {
        this.membresia = membresia;
    }

    public Date getFechaMembresia() {
        return fechaMembresia;
    }

    public void setFechaMembresia(Date fechaMembresia) {
        this.fechaMembresia = fechaMembresia;
    }

    public Double getCalificacionProfesor() {
        return calificacionProfesor;
    }

    public void setCalificacionProfesor(Double calificacionProfesor) {
        this.calificacionProfesor = calificacionProfesor;
    }

    //Atributos de alumno

    public Double getCalificacionAlumno() {
        return calificacionAlumno;
    }

    public void setCalificacion(Double calificacion) {
        this.calificacionAlumno = calificacionAlumno;
    }

    public ArrayList<Curso> getCursosAlumno() {
        return cursosAlumno;
    }

    public void addCursoAlumno(Curso cursoAlumno) {
        this.cursosAlumno.add(cursoAlumno);
    }
}
