package com.eliascapasso.auxilio.Modelo;

public class AlumnoCurso {
    private int dni_alumno;
    private int id_curso;
    private boolean pagado;

    public AlumnoCurso(int id_curso, int dni_alumno){
        this.id_curso = id_curso;
        this.dni_alumno = dni_alumno;

        this.pagado = false;
    }

    public AlumnoCurso(){}

    public int getDni_alumno() {
        return dni_alumno;
    }

    public void setDni_alumno(int dni_alumno) {
        this.dni_alumno = dni_alumno;
    }

    public int getId_curso() {
        return id_curso;
    }

    public void setId_curso(int id_curso) {
        this.id_curso = id_curso;
    }

    public boolean isPagado() {
        return pagado;
    }

    public void setPagado(boolean pagado) {
        this.pagado = pagado;
    }
}
