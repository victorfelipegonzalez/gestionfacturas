package com.gestionfacturas.gestionfacturasapi.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SQLDatabaseManager {
    // Variables de configuración para la conexión a la base de datos PostgreSQL
    private static final String JDBC_URL = "jdbc:postgresql://localhost:5432/Testgestionfacturas";
    private static final String USER = "postgres";
    private static final String PASSWORD = "admin";

    /**
     * Establece una conexión a la base de datos PostgreSQL.
     *
     * @return Una referencia a la conexión a la base de datos.
     * @throws SQLException Si ocurre un error durante la conexión.
     */
    public static Connection connect() throws SQLException {
        try {
            // Cargar el controlador JDBC de PostgreSQL
            Class.forName("org.postgresql.Driver");

            // Establecer la conexión
            Connection connection = DriverManager.getConnection(JDBC_URL, USER, PASSWORD);

            if (connection == null) {
                // Si la conexión no se pudo establecer
                System.out.println("No se pudo conectar a la base de datos PostgreSQL. Asegúrate de que la URL y las credenciales sean correctas.");
            }
            return connection; // Devuelve la conexión (o null si no se pudo conectar)
        } catch (ClassNotFoundException e) {
            // Manejar la excepción de clase no encontrada (por ejemplo, imprimir el error)
            e.printStackTrace();
            throw new SQLException("Error al conectar con la base de datos PostgreSQL: Controlador no encontrado.");
        }
    }

    /**
     * Cierra la conexión a la base de datos PostgreSQL.
     *
     * @param connection La conexión que se debe cerrar.
     * @throws SQLException Si ocurre un error durante la desconexión.
     */
    public static void disconnect(Connection connection) throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }
}
