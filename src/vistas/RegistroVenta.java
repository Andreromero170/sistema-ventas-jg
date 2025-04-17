package vistas;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Andreina Dev
 */

import javax.swing.*;
import javax.swing.table.TableModel;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.OutputStreamWriter;


public class RegistroVenta {


public void registrarVenta(JTable tablaVentas, String nombreCliente, String cedulaCliente,
                           String totalDolares, String totalBolivares, String numeroVenta) {

    String rutaArchivoPrincipal = "C:\\SistemaVentasJF\\src\\vistas\\ventas.csv";
    String rutaCopia = "C:\\Facturas\\ventas.csv";

    try {
        // Crear la carpeta si no existe
        new File("C:\\Facturas").mkdirs();

        // Escribir los datos en ambas rutas
        escribirCSVConUTF8YBOM(rutaArchivoPrincipal, tablaVentas, nombreCliente, cedulaCliente,
                                totalDolares, totalBolivares, numeroVenta);

        escribirCSVConUTF8YBOM(rutaCopia, tablaVentas, nombreCliente, cedulaCliente,
                               totalDolares, totalBolivares, numeroVenta);

        JOptionPane.showMessageDialog(null, "✅ Venta registrada correctamente en ambas rutas.");
    } catch (IOException e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(null, "❌ Error al registrar la venta: " + e.getMessage());
    }
}

// MÉTODO AUXILIAR para escribir el CSV en UTF-8 con BOM
public void escribirCSVConUTF8YBOM(String ruta, JTable tablaVentas, String nombreCliente,
                                   String cedulaCliente, String totalDolares, String totalBolivares,
                                   String numeroVenta) throws IOException {

    File archivo = new File(ruta);
    boolean archivoExiste = archivo.exists();
    boolean archivoVacio = !archivoExiste || archivo.length() == 0;

    // Abrir OutputStream y OutputStreamWriter
    FileOutputStream fos = new FileOutputStream(archivo, true); // true = append
    OutputStreamWriter writer = new OutputStreamWriter(fos, "UTF-8");

    // Agregar BOM solo si el archivo no existe o está vacío
    if (archivoVacio) {
        writer.write('\uFEFF'); // BOM
        writer.write("NúmeroVenta,NombreCliente,Cédula,Fecha,Productos,TotalDólares,TotalBolívares\n");
    }

    // Obtener la fecha actual
    String fechaVenta = new SimpleDateFormat("dd-MM-yyyy").format(new Date());

    // Obtener los productos y cantidades
    StringBuilder productos = new StringBuilder();
    for (int i = 0; i < tablaVentas.getRowCount(); i++) {
        String producto = tablaVentas.getValueAt(i, 0).toString();
        String cantidad = tablaVentas.getValueAt(i, 4).toString();
        productos.append(producto).append(": ").append(cantidad).append(", ");
    }
    if (productos.length() > 0) {
        productos.setLength(productos.length() - 2); // Eliminar última coma
    }

    // Línea de datos
    String linea = numeroVenta + "," + nombreCliente + "," + cedulaCliente + "," +
                   fechaVenta + "," + productos + "," + totalDolares + "," + totalBolivares + "\n";

    // Escribir la línea
    writer.write(linea);
    writer.close();
}


}
