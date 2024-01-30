package com.gestionfacturas.gestionfacturasapi.repositories;

import com.gestionfacturas.gestionfacturasapi.models.FacturaModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FacturaRepository extends JpaRepository<FacturaModel,Long> {

}


