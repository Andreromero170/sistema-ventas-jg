/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vistas;

/**
 *
 * @author Andreina Dev
 */

import java.io.*;
import java.util.*;

public class GenerarIDCSV {

    public static void main(String[] args) {
        String archivoEntrada = "C:\\Users\\Andreina Dev\\DocumentEs\\NetBeansProjects\\SistemaVentasJF\\src\\vistas\\productos.csv";
        String archivoSalida = "C:\\Users\\Andreina Dev\\DocumentEs\\NetBeansProjects\\SistemaVentasJF\\src\\vistas\\productos1.csv";

        try {
            BufferedReader reader = new BufferedReader(new FileReader(archivoEntrada));
            BufferedWriter writer = new BufferedWriter(new FileWriter(archivoSalida));

            String encabezadoOriginal = reader.readLine();
            if (encabezadoOriginal == null) {
                System.out.println("El archivo está vacío.");
                reader.close();
                writer.close();
                return;
            }

            // Agregar la columna ID al encabezado
            writer.write("ID;" + encabezadoOriginal);
            writer.newLine();

            String linea;
            int contadorId = 1;

            while ((linea = reader.readLine()) != null) {
                writer.write(contadorId + ";" + linea);
                writer.newLine();
                contadorId++;
            }

            reader.close();
            writer.close();

            System.out.println("Archivo generado correctamente con ID en: " + archivoSalida);

        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Hubo un error al procesar el archivo.");
        }
    }
}
