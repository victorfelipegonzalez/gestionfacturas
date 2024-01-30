package com.gestionfacturas.gestionfacturasapi.models;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "empleadoclientefactura")
public class EmpleadoClienteFacturaModel {
    @Id
    private Long id_empleado;
    @Id
    private Long id_cliente;
    @Id
    private Long id_factura;
}
