package com.gestionfacturas.gestionfacturasapi.models;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "empleado")
public class EmpleadoModel {
    public static final String ROL_ADMINISTRADOR = "JEFE";
    public static final String ROL_EMPLEADO= "EMPLEADO";
    @Id
    private long id_empleado;
    private String nombre_empleado;
    @Column(unique = true)
    private String  correo;
    private String tipo_empleado;
    public long id_empresa;
    private String password;
}
