package com.gestionfacturas.gestionfacturasapi.repositories;
import com.gestionfacturas.gestionfacturasapi.models.EmpresaModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;


import java.util.Optional;

public interface EmpresaRepository extends JpaRepository<EmpresaModel,Long> {
    Optional<EmpresaModel>findByCorreo(String correo);
    @Query("SELECT e FROM EmpresaModel e WHERE e.id_empresa = :id")
    Optional<EmpresaModel>findById_empresa(long id);

}
