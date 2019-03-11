package com.eliascapasso.auxilio.Modelo;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

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
    private String dato;
    private Bitmap fotoPerfil;
    private String rutaImagen;

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

        this.rutaImagen = "";

        //Profesor
        this.cursosProfesor = new ArrayList<Curso>();
        this.membresia = EstadoMembresia.DESHABILITADA;

        //Alumno
        this.cursosAlumno = new ArrayList<Curso>();
    }

    public Usuario(){

    }

    //Metodos de usuario


    public String getDato() {
        return dato;
    }

    public void setDato(String dato) {
        this.dato = dato;

        try {
            byte[] byteCode= Base64.decode(dato,Base64.DEFAULT);
            //this.imagen= BitmapFactory.decodeByteArray(byteCode,0,byteCode.length);

            int alto=100;//alto en pixeles
            int ancho=150;//ancho en pixeles

            Bitmap foto= BitmapFactory.decodeByteArray(byteCode,0,byteCode.length);
            this.fotoPerfil=Bitmap.createScaledBitmap(foto,alto,ancho,true);


        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public Bitmap getFotoPerfil() {
        return fotoPerfil;
    }

    public void setFotoPerfil(Bitmap fotoPerfil) {
        this.fotoPerfil = fotoPerfil;
    }

    public String getRutaImagen() {
        return rutaImagen;
    }

    public void setRutaImagen(String rutaImagen) {
        this.rutaImagen = rutaImagen;
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

    public void setCursosProfesor(ArrayList<Curso> cursosProfesor) {
        this.cursosProfesor = cursosProfesor;
    }

    public ArrayList<Curso> getCursosProfesor() {
        return cursosProfesor;
    }

    public void addCursoProfesor(Curso curso){
        cursosProfesor.add(curso);
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

    public void setCalificacionAlumno(Double calificacion) {
        this.calificacionAlumno = calificacionAlumno;
    }

    public ArrayList<Curso> getCursosAlumno() {
        return cursosAlumno;
    }

    public void setCursosAlumno(ArrayList<Curso> cursosAlumno) {
        this.cursosAlumno = cursosAlumno;
    }

    public void addCursoAlumno(Curso cursoAlumno) {
        this.cursosAlumno.add(cursoAlumno);
    }
}
