package com.gestionfacturas.gestionfacturasapi.repositories;

import com.gestionfacturas.gestionfacturasapi.models.ClienteModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.ArrayList;
import java.util.Optional;

public interface ClienteRepository extends JpaRepository<ClienteModel,Long> {
    @Query("SELECT c FROM ClienteModel c WHERE c.id_empresa = :id_empresa ORDER BY c.nombre_cliente")
    Optional<ArrayList<ClienteModel>> findAllById_empresaOrderByNombre_cliente(@Param("id_empresa") long id_empresa);
}
