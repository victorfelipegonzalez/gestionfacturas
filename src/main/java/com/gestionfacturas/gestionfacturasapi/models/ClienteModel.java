package com.gestionfacturas.gestionfacturasapi.models;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "cliente")
public class ClienteModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id_cliente;
    private String nombre_cliente;
    @Column(unique = true)
    private String nif_cliente;
    private int telefono_cliente;
    private String direccion_cliente;
    private String ciudad_cliente;
    private int cp_cliente;
    private String pais_cliente;
    @Column(unique = true)
    private String correo_cliente;
    private Long id_empresa;
}
