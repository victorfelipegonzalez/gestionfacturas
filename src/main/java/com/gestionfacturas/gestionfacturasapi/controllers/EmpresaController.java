package com.gestionfacturas.gestionfacturasapi.controllers;

import com.gestionfacturas.gestionfacturasapi.database.SQLDatabaseManager;
import com.gestionfacturas.gestionfacturasapi.models.EmpresaModel;
import com.gestionfacturas.gestionfacturasapi.models.ResponseModel;
import com.gestionfacturas.gestionfacturasapi.repositories.EmpresaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.sql.*;
import java.util.Optional;

@RestController
@RequestMapping("/api/empresas")
public class EmpresaController {
    @Autowired
    private EmpresaRepository empresaRepository;
    private Connection connection;

    private boolean initDBConnection(){
        try {
            connection = SQLDatabaseManager.connect();
            return true;
        } catch (SQLException e) {
            System.err.println("Error al conectar con la base de datos");
        }
        return false;
    }
    private boolean closeDBConnection(){
        try {
            SQLDatabaseManager.disconnect(connection);
            return true;
        } catch (SQLException e) {
            System.err.println("Error al desconectar la base de datos");
        }
        return false;
    }

    //Método para buscar Empresas por correo
    @GetMapping("/buscarporcorreo/{correo}")
    public ResponseEntity<ResponseModel> getEmpresa(@PathVariable String correo){
        Optional<EmpresaModel> empresaOptional = empresaRepository.findByCorreo(correo);
        var response = new ResponseModel();
        if(empresaOptional.isPresent()) {
            response.setSuccess(0);
            response.setMessage("Empresa encontrada");
            response.setData(empresaOptional.get());
        }else{
            response.setSuccess(1);
            response.setMessage("Empresa no encontrada");
        }
        return ResponseEntity.ok(response);
    }
    // Método para insertar Empresas en la base de datos PostgresSQL
    @Transactional
    @PostMapping("/insertar")
    public ResponseEntity<ResponseModel> insertarEmpresa(@RequestBody EmpresaModel nuevaEmpresa) {
        var response = new ResponseModel();
        if (initDBConnection()){
            String query = "{? = call crear_empresa(?,?,?,?,?,?,?,?,?,?,?)}";
            int resultado;
            try {
                BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
                String pwd = passwordEncoder.encode(nuevaEmpresa.getPwd());
                CallableStatement statement = connection.prepareCall(query);
                statement.registerOutParameter(1, Types.INTEGER);
                statement.setString(2,nuevaEmpresa.getNombre_empresa());
                statement.setString(3,nuevaEmpresa.getNif_empresa());
                statement.setInt(4,nuevaEmpresa.getTelefono_empresa());
                statement.setString(5,nuevaEmpresa.getDireccion_empresa());
                statement.setString(6,nuevaEmpresa.getCiudad_empresa());
                statement.setString(7,nuevaEmpresa.getPais_empresa());
                statement.setInt(8,nuevaEmpresa.getCp_empresa());
                statement.setString(9,nuevaEmpresa.getCorreo());
                statement.setString(10,nuevaEmpresa.getNombreJefe());
                statement.setString(11,pwd);
                statement.setString(12,nuevaEmpresa.getCorreoJefe());
                statement.execute();
                resultado = statement.getInt(1);
            } catch (SQLException e) {
                response.setSuccess(1);
                response.setMessage(e.getMessage());
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
            }finally {
                closeDBConnection();
            }
            if(resultado == 0){
                response.setSuccess(0);
                response.setMessage("Empresa creada con éxito");
            }else if (resultado == 1){
                response.setSuccess(1);
                response.setMessage("NIF ya registrado");
            }else if (resultado == 2){
                response.setSuccess(2);
                response.setMessage("Correo registrado\npor otra empresa");
            } else if (resultado == 3) {
                response.setSuccess(3);
                response.setMessage("Correo registrado\npor otro usuario");
            }
        }
        return ResponseEntity.ok(response);
    }
    @GetMapping("/{id_empresa}")
    public ResponseEntity<ResponseModel> obtenerEmpresa(@PathVariable long id_empresa){
        var response = new ResponseModel();
        var empresa = new EmpresaModel();
        if(initDBConnection()){
            String query = "SELECT * FROM empresa WHERE id_empresa = "+id_empresa;
            try {
                PreparedStatement statement = connection.prepareStatement(query);
                ResultSet resultSet = statement.executeQuery();
                if(resultSet.next()){
                    empresa.setId_empresa(resultSet.getLong("id_empresa"));
                    empresa.setNombre_empresa(resultSet.getString("nombre_empresa"));
                    empresa.setNif_empresa(resultSet.getString("nif_empresa"));
                    empresa.setTelefono_empresa(resultSet.getInt("telefono_empresa"));
                    empresa.setCorreo(resultSet.getString("correo"));
                    empresa.setDireccion_empresa(resultSet.getString("direccion_empresa"));
                    empresa.setCp_empresa(resultSet.getInt("cp_empresa"));
                    empresa.setCiudad_empresa(resultSet.getString("ciudad_empresa"));
                    empresa.setPais_empresa(resultSet.getString("pais_empresa"));
                    response.setSuccess(0);
                    response.setData(empresa);
                }
            } catch (SQLException e) {
                response.setSuccess(1);
                response.setMessage(e.getMessage());
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
            }finally {
                closeDBConnection();
            }
        }
        return ResponseEntity.ok(response);
    }
    @Transactional
    @PutMapping()
    public ResponseEntity<ResponseModel> actualizarEmpresa(@RequestBody EmpresaModel empresaModel){
        var response = new ResponseModel();
        if(initDBConnection()){
            String query = "{call actualizar_empresa(?,?,?,?,?,?,?,?,?)}";
            try {
                CallableStatement statement = connection.prepareCall(query);
                statement.setLong(1,empresaModel.getId_empresa());
                statement.setString(2,empresaModel.getNombre_empresa());
                statement.setString(3,empresaModel.getNif_empresa());
                statement.setInt(4,empresaModel.getTelefono_empresa());
                statement.setString(5,empresaModel.getCorreo());
                statement.setString(6,empresaModel.getDireccion_empresa());
                statement.setInt(7,empresaModel.getCp_empresa());
                statement.setString(8,empresaModel.getCiudad_empresa());
                statement.setString(9,empresaModel.getPais_empresa());
                boolean hasResults = statement.execute();
                if (hasResults) {
                    ResultSet resultSet = statement.getResultSet();
                    EmpresaModel empresa = new EmpresaModel();
                    while (resultSet.next()) {
                        empresa.setId_empresa(resultSet.getLong("id_empresa"));
                        empresa.setNombre_empresa(resultSet.getString("nombre_empresa"));
                        empresa.setNif_empresa(resultSet.getString("nif_empresa"));
                        empresa.setTelefono_empresa(resultSet.getInt("telefono_empresa"));
                        empresa.setCorreo(resultSet.getString("correo"));
                        empresa.setDireccion_empresa(resultSet.getString("direccion_empresa"));
                        empresa.setCp_empresa(resultSet.getInt("cp_empresa"));
                        empresa.setCiudad_empresa(resultSet.getString("ciudad_empresa"));
                        empresa.setPais_empresa(resultSet.getString("pais_empresa"));
                        boolean esActualizado = resultSet.getBoolean("es_actualizado");
                        if (esActualizado) {
                            response.setSuccess(0);
                            response.setData(empresa);
                            response.setMessage("Datos actualizada");
                        } else {
                            response.setSuccess(1);
                            response.setData(empresaModel);
                            response.setMessage("Error de actualización");
                        }
                    }
                }
            } catch (SQLException e) {
                response.setSuccess(2);
                response.setMessage(e.getMessage());
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
            }finally {
                closeDBConnection();
            }
        }
        return ResponseEntity.ok(response);
    }
}
