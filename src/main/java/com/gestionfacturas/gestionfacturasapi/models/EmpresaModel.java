package com.gestionfacturas.gestionfacturasapi.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "empresa")
public class EmpresaModel {
    @Id
    private long id_empresa;
    private String nombre_empresa;
    @Column(unique = true)
    private String nif_empresa;
    private int telefono_empresa;
    private String direccion_empresa;
    private String ciudad_empresa;
    private int cp_empresa;
    private String pais_empresa;
    @Column(unique = true)
    private String correo;
    private String nombreJefe;


}
