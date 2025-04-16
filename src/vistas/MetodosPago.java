/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vistas;

import java.awt.Color;
import java.awt.Font;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;

/**
 *
 * @author Andreina Dev
 */
public class MetodosPago extends javax.swing.JFrame {

    int columnas;
    DefaultTableModel model = new DefaultTableModel(columnas, 0);

    public MetodosPago() {
        initComponents();

        this.setTitle("Métodos de Pago del Inventario JF");

        tablaMetodos.setModel(model);

        // Asignar el modelo a la tabla
        estilizarTabla(tablaMetodos);

        // Añadir columnas al modelo de la tabla
        model.addColumn("ID");
        model.addColumn("Nombre");


    }

    private void guardarTablaComoCSV(DefaultTableModel modelo, String archivoCSV) {
        File archivo = new File(archivoCSV);
        boolean archivoExiste = archivo.exists();
        int ultimoID = 0;
        Set<String> nombresExistentes = new HashSet<>();
        int registrosGuardados = 0;

        try {
            // Leer nombres existentes y el último ID del archivo
            if (archivoExiste) {
                BufferedReader reader = new BufferedReader(new FileReader(archivoCSV));
                String linea;
                reader.readLine(); // Saltar encabezado

                while ((linea = reader.readLine()) != null) {
                    String[] datos = linea.split(";");
                    if (datos.length > 0) {
                        try {
                            int id = Integer.parseInt(datos[0].trim());
                            if (id > ultimoID) {
                                ultimoID = id;
                            }
                        } catch (NumberFormatException ignored) {
                        }
                        if (datos.length > 1) {
                            nombresExistentes.add(datos[1].trim().toLowerCase());
                        }
                    }
                }
                reader.close();
            }

            // Escribir en modo append
            try (FileWriter writer = new FileWriter(archivoCSV, true)) {
                // Escribir encabezado si el archivo está vacío
                if (!archivoExiste || archivo.length() == 0) {
                    writer.write("ID;Nombre;\n");
                }

                for (int i = 0; i < modelo.getRowCount(); i++) {
                    Object nombreMetodo = modelo.getValueAt(i, 1); // columna "Nombre"
                    if (nombreMetodo != null) {
                        String nombre = nombreMetodo.toString().trim().toLowerCase();

                        if (!nombresExistentes.contains(nombre)) {
                            int nuevoID = ++ultimoID;

                            // Asignar el nuevo ID a la columna 0 del modelo
                            modelo.setValueAt(nuevoID, i, 0);

                            StringBuilder fila = new StringBuilder();
                            fila.append(nuevoID).append(";");
                            fila.append(nombreMetodo.toString()).append(";");

                            writer.write(fila.toString() + "\n");

                            nombresExistentes.add(nombre);
                            registrosGuardados++;
                        }
                    }
                }

                writer.flush();
            }

            // ✅ Limpiar la tabla después de guardar
            modelo.setRowCount(0);

            // Mostrar mensaje dependiendo del resultado
            if (registrosGuardados > 0) {
                JOptionPane.showMessageDialog(null, "Se guardaron " + registrosGuardados + " nuevos registros correctamente.");
            } else {
                JOptionPane.showMessageDialog(null, "No se guardó nada. Todos los datos ya existen en el archivo CSV.", "Duplicados detectados", JOptionPane.WARNING_MESSAGE);
            }

        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error al guardar el archivo CSV.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void actualizarFilaSeleccionada(DefaultTableModel modelo, JTable tabla, String archivoCSV) {
        int filaSeleccionada = tabla.getSelectedRow();

        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(null, "Por favor, selecciona una fila de la tabla para actualizar.");
            return;
        }

        File archivo = new File(archivoCSV);
        if (!archivo.exists()) {
            JOptionPane.showMessageDialog(null, "El archivo CSV no existe.");
            return;
        }

        try {
            // Leer el archivo completo
            List<String> lineas = new ArrayList<>();
            BufferedReader reader = new BufferedReader(new FileReader(archivo));
            String encabezado = reader.readLine();
            String[] columnas = encabezado.split(";");

            lineas.add(encabezado);

            int indiceId = -1;
            int indiceNombre = -1;

            for (int i = 0; i < columnas.length; i++) {
                if (columnas[i].equalsIgnoreCase("ID")) {
                    indiceId = i;
                } else if (columnas[i].equalsIgnoreCase("Nombre")) {
                    indiceNombre = i;
                }
            }

            if (indiceId == -1 || indiceNombre == -1) {
                JOptionPane.showMessageDialog(null, "No se encontraron las columnas 'ID' o 'Nombre' en el CSV.");
                reader.close();
                return;
            }

            String idSeleccionado = modelo.getValueAt(filaSeleccionada, indiceId).toString().trim();
            String nuevoNombre = modelo.getValueAt(filaSeleccionada, indiceNombre).toString().trim();

            boolean filaActualizada = false;

            String linea;
            while ((linea = reader.readLine()) != null) {
                String[] datos = linea.split(";");
                StringBuilder nuevaLinea = new StringBuilder();

                if (datos.length > indiceId && datos[indiceId].trim().equals(idSeleccionado)) {
                    // Validar si el nuevo nombre ya existe en otra fila (diferente ID)
                    for (String l : lineas) {
                        if (l.equals(encabezado)) {
                            continue; // saltar encabezado
                        }
                        String[] d = l.split(";");
                        if (d.length > indiceNombre && d.length > indiceId
                                && !d[indiceId].trim().equals(idSeleccionado)
                                && d[indiceNombre].trim().equalsIgnoreCase(nuevoNombre)) {
                            JOptionPane.showMessageDialog(null, "El nombre ingresado ya existe en otra fila del CSV.");
                            reader.close();
                            return;
                        }
                    }

                    // Actualizar la línea
                    for (int j = 0; j < columnas.length; j++) {
                        if (j == indiceNombre) {
                            nuevaLinea.append(nuevoNombre);
                        } else {
                            nuevaLinea.append(j < datos.length ? datos[j] : "");
                        }
                        if (j < columnas.length - 1) {
                            nuevaLinea.append(";");
                        }
                    }
                    filaActualizada = true;
                } else {
                    // Mantener la línea original
                    for (int j = 0; j < columnas.length; j++) {
                        nuevaLinea.append(j < datos.length ? datos[j] : "");
                        if (j < columnas.length - 1) {
                            nuevaLinea.append(";");
                        }
                    }
                }

                lineas.add(nuevaLinea.toString());
            }

            reader.close();

            if (!filaActualizada) {
                JOptionPane.showMessageDialog(null, "No se encontró el ID en el archivo CSV.");
                return;
            }

            // Reescribir el archivo
            BufferedWriter writer = new BufferedWriter(new FileWriter(archivo));
            for (String l : lineas) {
                writer.write(l);
                writer.newLine();
            }
            writer.close();

            JOptionPane.showMessageDialog(null, "La fila fue actualizada exitosamente en el archivo CSV.");

        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error al actualizar el archivo CSV.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void eliminarFilaSeleccionada(DefaultTableModel modelo, JTable tabla, String archivoCSV) {
        int filaSeleccionada = tabla.getSelectedRow();

        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(null, "Por favor, selecciona una fila para eliminar.");
            return;
        }

        File archivo = new File(archivoCSV);
        if (!archivo.exists()) {
            JOptionPane.showMessageDialog(null, "El archivo CSV no existe.");
            return;
        }

        try {
            // Leer todas las líneas del archivo CSV
            List<String> lineas = new ArrayList<>();
            BufferedReader reader = new BufferedReader(new FileReader(archivo));
            String encabezado = reader.readLine();
            lineas.add(encabezado); // Siempre se mantiene el encabezado

            String linea;
            int indiceID = -1;
            int indiceNombre = -1;

            // Buscar índice de las columnas "ID" y "Nombre"
            String[] columnas = encabezado.split(";");
            for (int i = 0; i < columnas.length; i++) {
                if (columnas[i].equalsIgnoreCase("ID")) {
                    indiceID = i;
                } else if (columnas[i].equalsIgnoreCase("Nombre")) {
                    indiceNombre = i;
                }
            }

            if (indiceID == -1 || indiceNombre == -1) {
                JOptionPane.showMessageDialog(null, "No se encontraron las columnas 'ID' o 'Nombre' en el archivo.");
                reader.close();
                return;
            }

            // Obtener el ID y el Nombre de la fila seleccionada
            String idSeleccionado = modelo.getValueAt(filaSeleccionada, indiceID).toString().trim();
            String nombreSeleccionado = modelo.getValueAt(filaSeleccionada, indiceNombre).toString().trim().toLowerCase();

            boolean filaEliminada = false;

            // Leer y conservar solo las filas que no coincidan con el ID y Nombre
            while ((linea = reader.readLine()) != null) {
                String[] datos = linea.split(";");
                if (datos.length > indiceID && datos[indiceID].trim().equals(idSeleccionado)
                        && datos.length > indiceNombre && datos[indiceNombre].trim().toLowerCase().equals(nombreSeleccionado)) {
                    filaEliminada = true; // Omitimos esta línea (la eliminamos)
                } else {
                    lineas.add(linea);
                }
            }

            reader.close();

            if (!filaEliminada) {
                JOptionPane.showMessageDialog(null, "No se encontró la fila con el ID y Nombre seleccionados en el archivo.");
                return;
            }

            // Reescribir el archivo sin la fila eliminada
            BufferedWriter writer = new BufferedWriter(new FileWriter(archivo));
            for (String l : lineas) {
                writer.write(l);
                writer.newLine();
            }
            writer.close();

            // Eliminar también de la tabla visual
            modelo.removeRow(filaSeleccionada);

            JOptionPane.showMessageDialog(null, "Fila eliminada correctamente del archivo y de la tabla.");

        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error al eliminar la fila del archivo CSV.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void estilizarTabla(JTable tabla) {
        // Cambiar color de fondo y texto
        tabla.setBackground(new Color(255, 255, 255)); // fondo gris claro
        tabla.setForeground(Color.BLACK);              // texto negro

        // Cambiar fuente
        tabla.setFont(new Font("Arial", Font.PLAIN, 15));
        tabla.setRowHeight(30); // altura de las filas

        // Estilo del encabezado
        JTableHeader header = tabla.getTableHeader();
        header.setBackground(new Color(0, 120, 215));  // azul
        header.setForeground(Color.BLACK);             // texto blanco
        header.setFont(new Font("Arial", Font.BOLD, 14));

        // Ancho de columnas personalizado (opcional)
        TableColumnModel columnModel = tabla.getColumnModel();
        if (columnModel.getColumnCount() >= 4) {
            columnModel.getColumn(0).setPreferredWidth(200); // Producto
            columnModel.getColumn(1).setPreferredWidth(80);  // Lista precios
            columnModel.getColumn(2).setPreferredWidth(80);  // Precio $
            columnModel.getColumn(3).setPreferredWidth(80);  // Precio BS

        }
    }

    private void buscarPorNombreMetodos() {
        String archivoCSV = "C:\\SistemaVentasJF\\src\\vistas\\metodos_pagos.csv";
        String nombreBuscado = txtBuscar.getText().trim().toLowerCase();

        DefaultTableModel model = (DefaultTableModel) tablaMetodos.getModel();
        model.setRowCount(0); // Limpiar tabla antes de buscar

        boolean encontrado = false;

        try (BufferedReader br = new BufferedReader(new FileReader(archivoCSV))) {
            String linea;

            // Ignorar la primera línea (cabecera)
            br.readLine();

            while ((linea = br.readLine()) != null) {
                String[] datos = linea.split(";");

                // Asegurarse de que hay al menos 4 columnas válidas
                if (datos.length >= 2) {
                    String nombreCliente = datos[1].trim().toLowerCase(); // Columna "Nombre"

                    if (nombreCliente.contains(nombreBuscado)) {
                        model.addRow(new Object[]{
                            datos[0], // id
                            datos[1], // nombre
                        });
                        encontrado = true;
                    }
                }
            }

            if (!encontrado) {
                JOptionPane.showMessageDialog(null, "No se encontró ningún método con el nombre: " + txtBuscar.getText());
            }

        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error al leer el archivo CSV.");
        }
    }
    
    
    
private int obtenerSiguienteID(String archivoCSV) {
    int ultimoID = 0;

    File archivo = new File(archivoCSV);
    if (!archivo.exists()) return 1;

    try (BufferedReader reader = new BufferedReader(new FileReader(archivo))) {
        String linea;
        reader.readLine(); // Saltar encabezado

        while ((linea = reader.readLine()) != null) {
            String[] datos = linea.split(";");
            if (datos.length > 0) {
                try {
                    int id = Integer.parseInt(datos[0].trim());
                    if (id > ultimoID) {
                        ultimoID = id;
                    }
                } catch (NumberFormatException ignored) {}
            }
        }
    } catch (IOException e) {
        e.printStackTrace();
    }

    return ultimoID + 1;
}


    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        menuMetodos = new javax.swing.JPopupMenu();
        btnAgregarFilas = new javax.swing.JMenuItem();
        btnAgregarMetodos = new javax.swing.JMenuItem();
        btnActualizarMetodos = new javax.swing.JMenuItem();
        btnEliminarMetodos = new javax.swing.JMenuItem();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tablaMetodos = new javax.swing.JTable();
        jLabel10 = new javax.swing.JLabel();
        txtBuscar = new javax.swing.JTextField();
        btnBuscar = new javax.swing.JLabel();

        btnAgregarFilas.setText("Agregar Filas");
        btnAgregarFilas.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAgregarFilasActionPerformed(evt);
            }
        });
        menuMetodos.add(btnAgregarFilas);

        btnAgregarMetodos.setText("Agregar Métodos de Pago");
        btnAgregarMetodos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAgregarMetodosActionPerformed(evt);
            }
        });
        menuMetodos.add(btnAgregarMetodos);

        btnActualizarMetodos.setText("Actualizar Métodos de Pago");
        btnActualizarMetodos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnActualizarMetodosActionPerformed(evt);
            }
        });
        menuMetodos.add(btnActualizarMetodos);

        btnEliminarMetodos.setText("Eliminar Métodos de Pago");
        btnEliminarMetodos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEliminarMetodosActionPerformed(evt);
            }
        });
        menuMetodos.add(btnEliminarMetodos);

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);

        jPanel1.setBackground(new java.awt.Color(0, 0, 204));
        jPanel1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jPanel1MousePressed(evt);
            }
        });
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        tablaMetodos.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        tablaMetodos.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                tablaMetodosMousePressed(evt);
            }
        });
        jScrollPane1.setViewportView(tablaMetodos);

        jPanel1.add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 150, 520, 290));

        jLabel10.setFont(new java.awt.Font("Tahoma", 1, 36)); // NOI18N
        jLabel10.setForeground(new java.awt.Color(255, 255, 255));
        jLabel10.setText("Métodos de Pago");
        jPanel1.add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(120, 40, -1, -1));
        jPanel1.add(txtBuscar, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 100, 250, 40));

        btnBuscar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/buscar (1).png"))); // NOI18N
        btnBuscar.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnBuscarMouseClicked(evt);
            }
        });
        jPanel1.add(btnBuscar, new org.netbeans.lib.awtextra.AbsoluteConstraints(390, 100, 40, 40));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 588, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 491, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void tablaMetodosMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tablaMetodosMousePressed
        tablaMetodos.setComponentPopupMenu(menuMetodos);
    }//GEN-LAST:event_tablaMetodosMousePressed

    private void btnAgregarFilasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAgregarFilasActionPerformed

        int nuevoID = obtenerSiguienteID("C:\\SistemaVentasJF\\src\\vistas\\metodos_pagos.csv");
        model.addRow(new Object[]{nuevoID, "", "", ""});

    }//GEN-LAST:event_btnAgregarFilasActionPerformed

    private void btnAgregarMetodosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAgregarMetodosActionPerformed

        guardarTablaComoCSV((DefaultTableModel) tablaMetodos.getModel(),"C:\\SistemaVentasJF\\src\\vistas\\metodos_pagos.csv");
    }//GEN-LAST:event_btnAgregarMetodosActionPerformed

    private void jPanel1MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPanel1MousePressed
        jPanel1.setComponentPopupMenu(menuMetodos);

    }//GEN-LAST:event_jPanel1MousePressed

    private void btnBuscarMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnBuscarMouseClicked
        buscarPorNombreMetodos();
    }//GEN-LAST:event_btnBuscarMouseClicked

    private void btnActualizarMetodosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnActualizarMetodosActionPerformed

        actualizarFilaSeleccionada(model, tablaMetodos, "C:\\SistemaVentasJF\\src\\vistas\\metodos_pagos.csv");


    }//GEN-LAST:event_btnActualizarMetodosActionPerformed

    private void btnEliminarMetodosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEliminarMetodosActionPerformed
        eliminarFilaSeleccionada(model, tablaMetodos, "C:\\SistemaVentasJF\\src\\vistas\\metodos_pagos.csv");
    }//GEN-LAST:event_btnEliminarMetodosActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(MetodosPago.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(MetodosPago.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(MetodosPago.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MetodosPago.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new MetodosPago().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuItem btnActualizarMetodos;
    private javax.swing.JMenuItem btnAgregarFilas;
    private javax.swing.JMenuItem btnAgregarMetodos;
    private javax.swing.JLabel btnBuscar;
    private javax.swing.JMenuItem btnEliminarMetodos;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JPopupMenu menuMetodos;
    private javax.swing.JTable tablaMetodos;
    private javax.swing.JTextField txtBuscar;
    // End of variables declaration//GEN-END:variables
}
