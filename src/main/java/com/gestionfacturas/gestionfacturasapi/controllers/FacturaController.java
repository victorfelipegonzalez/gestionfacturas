package com.gestionfacturas.gestionfacturasapi.controllers;

import com.gestionfacturas.gestionfacturasapi.database.SQLDatabaseManager;
import com.gestionfacturas.gestionfacturasapi.models.ClienteModel;
import com.gestionfacturas.gestionfacturasapi.models.FacturaModel;
import com.gestionfacturas.gestionfacturasapi.models.ResponseModel;
import com.gestionfacturas.gestionfacturasapi.reports.EstadisticaAnualReport;
import com.gestionfacturas.gestionfacturasapi.reports.EstadisticasAnualClientesReport;
import com.gestionfacturas.gestionfacturasapi.reports.EstadisticasFacturaReport;
import com.gestionfacturas.gestionfacturasapi.reports.FacturaReport;
import com.gestionfacturas.gestionfacturasapi.repositories.ClienteRepository;
import com.gestionfacturas.gestionfacturasapi.repositories.FacturaRepository;
import com.gestionfacturas.gestionfacturasapi.services.EmailService;
import net.sf.jasperreports.engine.JRException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Base64;

@RestController
@RequestMapping("/api/facturas")
public class FacturaController {
    @Autowired
    FacturaRepository facturaRepository;
    @Autowired
    ClienteRepository clienteRepository;
    @Autowired
    EmailService emailService;
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
    @PostMapping("/insertar")
    public ResponseEntity<ResponseModel> crearFactura(@RequestBody FacturaModel nuevaFactura) throws SQLException {
        var response = new ResponseModel();
        Date fecha = Date.valueOf(LocalDate.now());
        nuevaFactura.setFecha(fecha);
        if(initDBConnection()){
            try{
                String query="INSERT INTO factura (fecha) VALUES (?)";
                PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
                statement.setDate(1,nuevaFactura.getFecha());
                statement.executeUpdate();
                ResultSet resultSet = statement.getGeneratedKeys();
                if(resultSet.next()){
                    long numFactura = resultSet.getLong(1);
                    String queryLineaFactura = "INSERT INTO lineafactura (id_factura,id_producto,cantidad,precio_lineafactura) VALUES (?,?,?,?)";
                    PreparedStatement statementLinea = connection.prepareStatement(queryLineaFactura);
                    for(var linea : nuevaFactura.getLineasFactura()){
                        statementLinea.setLong(1,numFactura);
                        statementLinea.setLong(2,linea.getId_producto());
                        statementLinea.setInt(3,linea.getCantidad());
                        statementLinea.setBigDecimal(4, BigDecimal.valueOf(linea.getPrecio()));
                        statementLinea.executeUpdate();
                    }
                    String queryFacturaCliente ="INSERT INTO empleadoclientefactura (id_empleado, id_cliente, id_factura) VALUES (?,?,?)";
                    PreparedStatement statementFacturaCliente = connection.prepareStatement(queryFacturaCliente);
                    statementFacturaCliente.setLong(1,nuevaFactura.getId_empleado());
                    statementFacturaCliente.setLong(2,nuevaFactura.getId_cliente());
                    statementFacturaCliente.setLong(3,numFactura);
                    statementFacturaCliente.executeUpdate();
                    response.setSuccess(0);
                    response.setMessage("Factura creada con éxito");
                    ClienteModel cliente = clienteRepository.findById_cliente(nuevaFactura.getId_cliente());
                    byte[] pdfBytes = FacturaReport.generarFactura(numFactura);
                    emailService.sendBill(cliente,pdfBytes);
                }else{
                    response.setSuccess(1);
                    response.setMessage("Error al crear Factura");
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
                }
            }catch (SQLException e){
                response.setSuccess(1);
                response.setMessage(e.getMessage());
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
            } catch (JRException e) {

            } finally {
                closeDBConnection();
            }

        }
        return ResponseEntity.ok(response);
    }
    @GetMapping("/clientes/{id_empresa}/{id_cliente}")
    public ResponseEntity<ResponseModel> obtenerListaFacturaClienteB(@PathVariable Long id_empresa,@PathVariable Long id_cliente){
        var response = new ResponseModel();
        var listaFacturas = new ArrayList<FacturaModel>();
        if (initDBConnection()){
            try{
                String query ="{call obtener_facturas_clientes(?,?)}";
                CallableStatement statement = connection.prepareCall(query);
                statement.setLong(1,id_cliente);
                statement.setLong(2,id_empresa);
                statement.execute();
                ResultSet resultSet = statement.getResultSet();
                while (resultSet.next()){
                    var factura = new FacturaModel();
                    factura.setId_factura(resultSet.getLong(1));
                    factura.setFecha(resultSet.getDate(2));
                    listaFacturas.add(factura);
                }
            }catch (SQLException e){
                response.setSuccess(1);
                response.setMessage("Error en la base de datos");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
            }finally {
                closeDBConnection();
            }
            if (!listaFacturas.isEmpty()){
                response.setSuccess(0);
                response.setMessage("Lista de facturas encontrada");
                response.setData(listaFacturas);
            }else {
                response.setSuccess(1);
                response.setMessage("No se encontraron facturas");
                response.setData(listaFacturas);
            }
        }
        return ResponseEntity.ok(response);
    }
    @GetMapping("/empleados/{id_empresa}/{id_empleado}")
    public ResponseEntity<ResponseModel> obtenerListaFacturaEmpleados(@PathVariable Long id_empresa,@PathVariable Long id_empleado){
        var response = new ResponseModel();
        var listaFacturas = new ArrayList<FacturaModel>();
        if (initDBConnection()){
            try{
                String query ="{call obtener_facturas_empleados(?,?)}";
                CallableStatement statement = connection.prepareCall(query);
                statement.setLong(1,id_empleado);
                statement.setLong(2,id_empresa);
                statement.execute();
                ResultSet resultSet = statement.getResultSet();
                while (resultSet.next()){
                    var factura = new FacturaModel();
                    factura.setId_factura(resultSet.getLong(1));
                    factura.setFecha(resultSet.getDate(2));
                    listaFacturas.add(factura);
                }
            }catch (SQLException e){
                response.setSuccess(1);
                response.setMessage("Error en la base de datos");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
            }finally {
                closeDBConnection();
            }
            if (!listaFacturas.isEmpty()){
                response.setSuccess(0);
                response.setMessage("Lista de facturas encontrada");
                response.setData(listaFacturas);
            }else {
                response.setSuccess(1);
                response.setMessage("No se encontraron facturas");
                response.setData(listaFacturas);
            }
        }
        return ResponseEntity.ok(response);
    }
    // Método para generar una FacturaPDF
    @GetMapping("/informes/{id_factura}")
    public ResponseEntity<ResponseModel> generarInformeFactura(@PathVariable long id_factura){
            var response = new ResponseModel();
        try{
            byte[] factura = FacturaReport.generarInforme(id_factura);
            String facturaBase64 = Base64.getEncoder().encodeToString(factura);
            if(factura != null){
                response.setSuccess(0);
                response.setMessage("Factura generada con éxito");
                response.setData(facturaBase64);
            }else{
                response.setSuccess(1);
                response.setMessage("Error al generar la factura");
            }
            return ResponseEntity.ok(response);
        }catch (JRException e){
            e.printStackTrace();
            response.setSuccess(1);
            response.setMessage("Error al procesar la solicitud");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }catch (Exception e){
            e.printStackTrace();
            response.setSuccess(1);
            response.setMessage("Error al procesar la solicitud");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    // Método para generar un informe de facturas en PDF
    @GetMapping("/estadisticasfacturas/{id_empresa}")
    public ResponseEntity<ResponseModel> generarInformeEstadisticas(@PathVariable long id_empresa){
        var response = new ResponseModel();
        try{
            byte[] factura = EstadisticasFacturaReport.generarInformeEstadisticas(id_empresa);
            String facturaBase64 = Base64.getEncoder().encodeToString(factura);
            if(factura != null){
                response.setSuccess(0);
                response.setMessage("Informe generado con éxito");
                response.setData(facturaBase64);
            }else{
                response.setSuccess(1);
                response.setMessage("Error al generar informe");
            }
            return ResponseEntity.ok(response);
        }catch (JRException e){
            e.printStackTrace();
            response.setSuccess(1);
            response.setMessage("Error al procesar la solicitud");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }catch (Exception e){
            e.printStackTrace();
            response.setSuccess(1);
            response.setMessage("Error al procesar la solicitud");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    // Método para generar un informe de facturas por años en PDF
    @GetMapping("/aniosfacturas/{id_empresa}")
    public ResponseEntity<ResponseModel> listaAniosFacturas(@PathVariable long id_empresa){
        var response = new ResponseModel();
        var listaAniosFacturas = new ArrayList<Integer>();
        if (initDBConnection()){
            try{
                String query ="{call lista_anios_facturas(?)}";
                CallableStatement statement = connection.prepareCall(query);
                statement.setLong(1,id_empresa);
                statement.execute();
                ResultSet resultSet = statement.getResultSet();
                while (resultSet.next()){
                    int anio = resultSet.getInt(1);
                    listaAniosFacturas.add(anio);
                }
            }catch (SQLException e){
                System.out.println(e);
                response.setSuccess(1);
                response.setMessage("Error en la base de datos");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
            }finally {
                closeDBConnection();
            }
            if (!listaAniosFacturas.isEmpty()){
                response.setSuccess(0);
                response.setMessage("Lista de años de facturas encontrada");
                response.setData(listaAniosFacturas);
            }else {
                response.setSuccess(1);
                response.setMessage("No se encontraron facturas");
                response.setData(listaAniosFacturas);
            }
        }
        return ResponseEntity.ok(response);
    }
    // Método para generar un informe de facturas en un año en PDF
    @GetMapping("/estadisticasanuales/{id_empresa}/{anio}")
    public ResponseEntity<ResponseModel> generarInformeEstadisticasAnuales(@PathVariable long id_empresa,@PathVariable int anio){
        var response = new ResponseModel();
        try{
            byte[] informe = EstadisticaAnualReport.generarInformeEstadisticas(id_empresa,anio);
            String facturaBase64 = Base64.getEncoder().encodeToString(informe);
            if(informe != null){
                response.setSuccess(0);
                response.setMessage("Informe generado con éxito");
                response.setData(facturaBase64);
            }else{
                response.setSuccess(1);
                response.setMessage("Error al generar informe");
            }
            return ResponseEntity.ok(response);
        }catch (JRException e){
            e.printStackTrace();
            response.setSuccess(1);
            response.setMessage("Error al procesar la solicitud");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }catch (Exception e){
            e.printStackTrace();
            response.setSuccess(1);
            response.setMessage("Error al procesar la solicitud");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    // Método para generar un informe de facturas de clientes en PDF
    @GetMapping("/estadisticasanualesclientes/{id_empresa}/{anio}")
    public ResponseEntity<ResponseModel> generarInformeEstadisticasAnualesClientes(@PathVariable long id_empresa,@PathVariable int anio){
        var response = new ResponseModel();
        try{
            byte[] informe = EstadisticasAnualClientesReport.generarInformeEstadisticas(id_empresa,anio);
            String facturaBase64 = Base64.getEncoder().encodeToString(informe);
            if(informe != null){
                response.setSuccess(0);
                response.setMessage("Informe generado con éxito");
                response.setData(facturaBase64);
            }else{
                response.setSuccess(1);
                response.setMessage("Error al generar informe");
            }
            return ResponseEntity.ok(response);
        }catch (JRException e){
            e.printStackTrace();
            response.setSuccess(1);
            response.setMessage("Error al procesar la solicitud");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }catch (Exception e){
            e.printStackTrace();
            response.setSuccess(1);
            response.setMessage("Error al procesar la solicitud");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
