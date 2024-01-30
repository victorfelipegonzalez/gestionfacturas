package com.gestionfacturas.gestionfacturasapi.models;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "lineafactura")
public class LineaFacturaModel {
    @Id
    private long id_lineafactura;
    private long id_factura;
    private long id_producto;
    private int cantidad;
    private double precio;
    @ManyToOne
    @JoinColumn(name = "id_factura", insertable = false, updatable = false)
    private FacturaModel factura;
}
