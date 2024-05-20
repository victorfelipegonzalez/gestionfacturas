package com.gestionfacturas.gestionfacturasapi.repositories;

import com.gestionfacturas.gestionfacturasapi.models.ClienteModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.ArrayList;
import java.util.Optional;

public interface ClienteRepository extends JpaRepository<ClienteModel,Long> {
    @Query("SELECT c FROM ClienteModel c WHERE c.id_empresa = :id_empresa ORDER BY c.nombre_cliente")
    Optional<ArrayList<ClienteModel>> findAllById_empresaOrderByNombre_cliente(@Param("id_empresa") long id_empresa);
    @Query("SELECT c FROM ClienteModel c WHERE c.id_cliente = :id_cliente")
    ClienteModel findById_cliente(@Param("id_cliente")long id_cliente);
    @Modifying
    @Query("UPDATE ClienteModel c SET c.nombre_cliente = :nombre, c.nif_cliente = :nif, c.telefono_cliente = :telefono, c.direccion_cliente = :direccion, c.ciudad_cliente = :ciudad, c.cp_cliente = :cp, c.pais_cliente = :pais, c.correo_cliente = :correo WHERE c.id_cliente = :id")
    void actualizarCliente(@Param("id") Long id,
                           @Param("nombre") String nombre,
                           @Param("nif") String nif,
                           @Param("telefono") int telefono,
                           @Param("direccion") String direccion,
                           @Param("ciudad") String ciudad,
                           @Param("cp") int cp,
                           @Param("pais") String pais,
                           @Param("correo") String correo);

    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END FROM ClienteModel c WHERE c.correo_cliente = :correo AND c.id_cliente <> :id")
    boolean existsByCorreoClienteAndIdClienteNot(@Param("correo") String correo, @Param("id") Long id);

    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END FROM ClienteModel c WHERE c.nif_cliente = :nif AND c.id_cliente <> :id")
    boolean existsByNifClienteAndIdClienteNot(@Param("nif") String nif, @Param("id") Long id);
}
