package com.gestionfacturas.gestionfacturasapi.repositories;

import com.gestionfacturas.gestionfacturasapi.models.ProductoModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.ArrayList;
import java.util.Optional;

public interface ProductoRepository extends JpaRepository<ProductoModel,Long> {
    @Query("SELECT p FROM ProductoModel p WHERE p.id_empresa = :id_empresa ORDER BY p.descripcion")
    Optional<ArrayList<ProductoModel>> findAllById_empresaOrderByDescripcion(@Param("id_empresa") long id_empresa);
}
