package com.gestionfacturas.gestionfacturasapi.controllers;

import com.gestionfacturas.gestionfacturasapi.database.SQLDatabaseManager;
import com.gestionfacturas.gestionfacturasapi.models.ClienteModel;
import com.gestionfacturas.gestionfacturasapi.models.ResponseModel;
import com.gestionfacturas.gestionfacturasapi.repositories.ClienteRepository;
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
@RequestMapping("/api/clientes")
public class ClienteController {
    @Autowired
    private ClienteRepository clienteRepository;
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
    @GetMapping("/all/{id_empresa}")
    public ResponseEntity<ResponseModel> obtenerTodosLosClientes(@PathVariable long id_empresa){
        var response = new ResponseModel();
        Optional<ArrayList<ClienteModel>> listaClientes = clienteRepository.findAllById_empresaOrderByNombre_cliente(id_empresa);
        if (listaClientes.isPresent()){
            response.setSuccess(0);
            response.setMessage("Lista de clientes encontrada");
            response.setData(listaClientes.get());
            return ResponseEntity.ok(response);
        }else{
            response.setSuccess(1);
            response.setMessage("No se encontro nada");
            return  ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }
    @Transactional
    @PostMapping("/insertar")
    public ResponseEntity<ResponseModel> insertarCliente(@RequestBody ClienteModel nuevoCliente) {
        var response = new ResponseModel();
            if (initDBConnection()){
                String query = "{? = call crear_cliente(?,?,?,?,?,?,?,?,?)}";
                int resultado;
                try {
                    CallableStatement statement = connection.prepareCall(query);
                    statement.registerOutParameter(1, Types.INTEGER);
                    statement.setString(2,nuevoCliente.getNombre_cliente());
                    statement.setString(3,nuevoCliente.getNif_cliente());
                    statement.setInt(4,nuevoCliente.getTelefono_cliente());
                    statement.setString(5,nuevoCliente.getDireccion_cliente());
                    statement.setString(6,nuevoCliente.getCiudad_cliente());
                    statement.setString(7,nuevoCliente.getPais_cliente());
                    statement.setInt(8,nuevoCliente.getCp_cliente());
                    statement.setString(9,nuevoCliente.getCorreo_cliente());
                    statement.setLong(10,nuevoCliente.getId_empresa());
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
                    response.setMessage("Cliente añadido con éxito");
                }else if (resultado == 1){
                    response.setSuccess(1);
                    response.setMessage("NIF ya registrado");
                }else if (resultado == 2){
                    response.setSuccess(2);
                    response.setMessage("Correo registrado\npor otro cliente");
                }
            }
            return ResponseEntity.ok(response);
    }
}
