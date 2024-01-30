package com.gestionfacturas.gestionfacturasapi.repositories;
import com.gestionfacturas.gestionfacturasapi.models.EmpleadoModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.ArrayList;
import java.util.Optional;


public interface EmpleadoRepository extends JpaRepository<EmpleadoModel,Long> {
    Optional<EmpleadoModel>findByCorreo(String correoEmpleado);
    @Query("SELECT e FROM EmpleadoModel e WHERE e.id_empresa = :id_empresa ORDER BY e.nombre_empleado")
    Optional<ArrayList<EmpleadoModel>>findById_empresa(@Param("id_empresa") long id_empresa);
}
