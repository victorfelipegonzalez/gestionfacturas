package com.gestionfacturas.gestionfacturasapi.reports;

import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.util.JRLoader;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class FacturaReport {
    private static final String RUTA = "src/main/java/com/gestionfacturas/gestionfacturasapi/reports/FacturaReport.jasper";

    public static byte[] generarInforme(long id_factura) throws JRException {
        try {
            // Cargar el archivo .jasper
            JasperReport informe = (JasperReport) JRLoader.loadObjectFromFile(RUTA);

            Class.forName("org.postgresql.Driver");
            Connection connection =(Connection) DriverManager.getConnection ("jdbc:postgresql://localhost:5432/Test", "postgres", "admin");

            // Crear un objeto Map para pasar parámetros al informe
            Map<String, Object> parametrosReporte = new HashMap<>();
            parametrosReporte.put("id_factura", id_factura);

            // Llenar el informe con datos y parámetros
            JasperPrint jasperPrint = JasperFillManager.fillReport(informe, parametrosReporte, connection);

            // Convertir el informe a un array de bytes (byte array)
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            JasperExportManager.exportReportToPdfStream(jasperPrint, byteArrayOutputStream);

            // Convertir el informe a un array de bytes (byte array)
            byte[] pdfBytes = JasperExportManager.exportReportToPdf(jasperPrint);

            // Guardar Informe en formato PDF
            String directorio = "src/main/java/com/gestionfacturas/gestionfacturasapi/pdf/factura.pdf";
            FileOutputStream fileOutputStream = new FileOutputStream(directorio);
            fileOutputStream.write(pdfBytes);
            fileOutputStream.close();
            return byteArrayOutputStream.toByteArray();

        } catch (JRException e) {
            e.printStackTrace();
            throw new JRException(e.getMessage());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
    public static byte[] generarFactura(long id_factura) throws JRException {
        try {
            // Cargar el archivo .jasper
            JasperReport informe = (JasperReport) JRLoader.loadObjectFromFile(RUTA);

            Class.forName("org.postgresql.Driver");
            Connection connection =(Connection) DriverManager.getConnection ("jdbc:postgresql://localhost:5432/Test", "postgres", "admin");

            // Crear un objeto Map para pasar parámetros al informe
            Map<String, Object> parametrosReporte = new HashMap<>();
            parametrosReporte.put("id_factura", id_factura);

            // Llenar el informe con datos y parámetros
            JasperPrint jasperPrint = JasperFillManager.fillReport(informe, parametrosReporte, connection);

            // Convertir el informe a un array de bytes (byte array)
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            JasperExportManager.exportReportToPdfStream(jasperPrint, byteArrayOutputStream);

            // Convertir el informe a un array de bytes (byte array)
            byte[] pdfBytes = JasperExportManager.exportReportToPdf(jasperPrint);

            return pdfBytes;

        } catch (JRException e) {
            e.printStackTrace();
            throw new JRException(e.getMessage());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }
}
