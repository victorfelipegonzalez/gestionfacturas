package com.gestionfacturas.gestionfacturasapi.repositories;

import com.gestionfacturas.gestionfacturasapi.models.FacturaModel;
import org.springframework.data.jpa.repository.JpaRepository;


public interface FacturaRepository extends JpaRepository<FacturaModel,Long> {

}


