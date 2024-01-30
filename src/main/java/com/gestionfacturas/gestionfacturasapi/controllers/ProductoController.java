package com.gestionfacturas.gestionfacturasapi.controllers;

import com.gestionfacturas.gestionfacturasapi.database.SQLDatabaseManager;
import com.gestionfacturas.gestionfacturasapi.models.ProductoModel;
import com.gestionfacturas.gestionfacturasapi.models.ResponseModel;
import com.gestionfacturas.gestionfacturasapi.repositories.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Optional;

@RestController
@RequestMapping("/api/productos")
public class ProductoController {
    @Autowired
    ProductoRepository productoRepository;
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
    public ResponseEntity<ResponseModel> obtenerProductos(@PathVariable long id_empresa){
        var response = new ResponseModel();
        Optional<ArrayList<ProductoModel>> listaProductos = productoRepository.findAllById_empresaOrderByDescripcion(id_empresa);
        if(listaProductos.isPresent()){
           response.setSuccess(0);
           response.setMessage("Lista de productos encontrada");
           response.setData(listaProductos.get());
           return ResponseEntity.ok(response);
        }else{
            response.setSuccess(1);
            response.setMessage("No se encontro nada");
            return  ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }
    @Transactional
    @PostMapping("/insertar")
    public ResponseEntity<ResponseModel> insertarProducto(@RequestBody ProductoModel nuevoProducto){
        var response = new ResponseModel();
        int resultado = 1;
        if (initDBConnection()){
            String query = "{? = call crear_producto(?,?,?)}";
            try {
                CallableStatement statement = connection.prepareCall(query);
                statement.registerOutParameter(1, Types.INTEGER);
                statement.setString(2,nuevoProducto.getDescripcion());
                statement.setBigDecimal(3, BigDecimal.valueOf(nuevoProducto.getPrecio()));
                statement.setLong(4,nuevoProducto.getId_empresa());
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
                response.setMessage("Producto añadido con éxito");
            }else {
                response.setSuccess(1);
                response.setMessage("Error al añadir Producto");
            }
        }
        return ResponseEntity.ok(response);
    }
}
