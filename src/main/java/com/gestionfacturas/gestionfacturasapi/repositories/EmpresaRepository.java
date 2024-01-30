package com.gestionfacturas.gestionfacturasapi.repositories;
import com.gestionfacturas.gestionfacturasapi.models.EmpresaModel;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.Optional;

public interface EmpresaRepository extends JpaRepository<EmpresaModel,Long> {

    Optional<EmpresaModel>findByCorreo(String correo);

}
