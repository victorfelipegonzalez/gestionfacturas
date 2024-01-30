package com.gestionfacturas.gestionfacturasapi.models;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table (name="producto")
public class ProductoModel {
    @Id
    private long id_producto;
    private String descripcion;
    private double precio;
    private long id_empresa;
}
