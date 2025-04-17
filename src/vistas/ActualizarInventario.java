/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vistas;

import java.io.*;
import java.util.*;
import javax.swing.JTable;

public class ActualizarInventario {

    // Método para actualizar el inventario
    public void actualizarInventario(String archivoInventario, JTable tablaVentas) {
        // Leer todo el contenido del archivo CSV
        List<String[]> inventario = new ArrayList<>();
        String linea;
        try (BufferedReader reader = new BufferedReader(new FileReader(archivoInventario))) {
            // Leer encabezado (se guarda para escribirlo nuevamente)
            String encabezado = reader.readLine();
            
            // Leer el contenido del inventario
            while ((linea = reader.readLine()) != null) {
                String[] datos = linea.split(";");
                inventario.add(datos);
            }

            // Recorrer la tabla de ventas para obtener los productos vendidos
            for (int i = 0; i < tablaVentas.getRowCount(); i++) {
                String productoNombre = tablaVentas.getValueAt(i, 0).toString();  // Producto (columna 0)
                String cantidadVendida = tablaVentas.getValueAt(i, 4).toString();  // Cantidad (columna 4)
                
                // Buscar el producto en el inventario y actualizar su cantidad
                for (String[] producto : inventario) {
                    if (producto[1].equals(productoNombre)) {  // Si el nombre del producto coincide
                        int cantidadInventario = Integer.parseInt(producto[5]);
                        int cantidadVendidaInt = Integer.parseInt(cantidadVendida);

                        // Actualizar el inventario restando la cantidad vendida
                        int nuevaCantidad = cantidadInventario - cantidadVendidaInt;
                        producto[5] = String.valueOf(nuevaCantidad);  // Actualizar el inventario
                        break;
                    }
                }
            }

            // Escribir el archivo CSV actualizado
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(archivoInventario))) {
                writer.write(encabezado);  // Escribir encabezado
                writer.newLine();

                // Escribir las filas del inventario actualizado
                for (String[] producto : inventario) {
                    writer.write(String.join(";", producto));
                    writer.newLine();
                }

                System.out.println("Inventario actualizado correctamente.");

            } catch (IOException e) {
                e.printStackTrace();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
