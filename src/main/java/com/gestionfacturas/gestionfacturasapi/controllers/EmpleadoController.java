package com.gestionfacturas.gestionfacturasapi.controllers;

import com.gestionfacturas.gestionfacturasapi.database.SQLDatabaseManager;
import com.gestionfacturas.gestionfacturasapi.models.EmpleadoModel;
import com.gestionfacturas.gestionfacturasapi.models.ResponseModel;
import com.gestionfacturas.gestionfacturasapi.repositories.EmpleadoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Optional;

@RestController
@RequestMapping("/api/empleados")
public class EmpleadoController {
    @Autowired
    private EmpleadoRepository empleadoRepository;
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

    @Transactional
    // Método para insertar Emppleados en la base de datos PostgreSQL
    @PostMapping("/insertar")
    public ResponseEntity<ResponseModel> insertarEmpleado(@RequestBody EmpleadoModel nuevoEmpleado) {
        var response = new ResponseModel();
        if(initDBConnection()){
            String query="{? = call crear_empleado_inicial(?,?,?)}";
            int resultado;
            try {
                CallableStatement statement = connection.prepareCall(query);
                statement.registerOutParameter(1, Types.INTEGER);
                statement.setString(2,nuevoEmpleado.getNombre_empleado());
                statement.setString(3,nuevoEmpleado.getCorreo());
                statement.setString(4,nuevoEmpleado.getTipo_empleado());
                statement.execute();
                resultado = statement.getInt(1);
            } catch (SQLException e) {
                response.setSuccess(1);
                response.setMessage(e.getMessage());
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
            } catch (Exception e){
                response.setSuccess(1);
                response.setMessage(e.getMessage());
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
            } finally {
                closeDBConnection();
            }
            if(resultado == 0){
                response.setSuccess(0);
                response.setMessage("Empleado registrado con éxito");
            }else if (resultado == 1){
                response.setSuccess(1);
                response.setMessage("Correo ya registrado");
            }
        }else{
            response.setSuccess(2);
            response.setMessage("Error de conexión con la base de datos");
        }
        return ResponseEntity.ok(response);
    }

    // Método para obtener todos los empleados
    @GetMapping("/all/{id_empresa}")
    public ResponseEntity<ResponseModel> obtenerTodosLosEmpleados(@PathVariable long id_empresa) {
        Optional<ArrayList<EmpleadoModel>> empleadoOptional = empleadoRepository.findById_empresa(id_empresa);
        var response = new ResponseModel();

        if (empleadoOptional.isPresent()) {
            response.setSuccess(0);
            response.setMessage("Empleado encontrado");
            response.setData(empleadoOptional.get());
            return ResponseEntity.ok(response);
        } else {
            response.setSuccess(1);
            response.setMessage("Empleado no encontrado");
            return  ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }
    // Método para obetener un empleado por su correo electronico
    @GetMapping("/buscarCorreo/{correoEmpleado}")
    public ResponseEntity<ResponseModel> obtenerEmpleadoPorCorreo(@PathVariable String correoEmpleado) {
        var response = new ResponseModel();
        try{
            Optional<EmpleadoModel> empleadoOptional = empleadoRepository.findByCorreo(correoEmpleado);
            if (empleadoOptional.isPresent()) {
                response.setSuccess(0);
                response.setMessage("Empleado encontrado");
                response.setData(empleadoOptional.get());
            } else {
                response.setSuccess(1);
                response.setMessage("Empleado no encontrado");
            }
        }catch (Exception e){
            response.setSuccess(1);
            response.setMessage("Empleado no asociado \na ninguna empresa");
        }


        return ResponseEntity.ok(response);
    }
    // Método para asociar un empleado a una empresa
    @Transactional
    @PostMapping("/actualizarEmpleado")
    public ResponseEntity<ResponseModel> actualizarEmpleado(@RequestBody EmpleadoModel empleadoActualizado){
        var response = new ResponseModel();
        if(initDBConnection()){
            String query="{? = call crear_empleado_empresa(?,?,?)}";
            int resultado;
            try {
                CallableStatement statement = connection.prepareCall(query);
                statement.registerOutParameter(1, Types.INTEGER);
                statement.setString(2,empleadoActualizado.getCorreo());
                statement.setString(3,empleadoActualizado.getTipo_empleado());
                statement.setLong(4,empleadoActualizado.getId_empresa());
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
                response.setMessage("Empleado añadido con éxito");
            }else if (resultado == 1){
                response.setSuccess(1);
                response.setMessage("Empleado no registrado");
            }else if (resultado == 2){
                response.setSuccess(2);
                response.setMessage("Empleado registrado en otra empresa");
            }else if(resultado == 3){
                response.setSuccess(3);
                response.setMessage("Empleado ya registrado");
            }

        }else{
            response.setSuccess(4);
            response.setMessage("Error de conexión con la base de datos");
        }
        return ResponseEntity.ok(response);
    }
}
