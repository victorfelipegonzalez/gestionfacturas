package com.gestionfacturas.gestionfacturasapi.models;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "factura")
public class FacturaModel {
    @Id
    private long id_factura;
    private Date fecha;
    @OneToMany(mappedBy = "factura")
    private List<LineaFacturaModel> lineasFactura = new ArrayList<>();
    private long id_empleado;
    private long id_cliente;
}
