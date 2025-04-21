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
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;

/**
 *
 * @author Andreina Dev
 */
public class Clientes1 extends javax.swing.JFrame {

    int columnas;
    DefaultTableModel model = new DefaultTableModel(columnas, 0);

    public Clientes1() {
        initComponents();

        this.setTitle("Clientes del Inventario JF");

        tablaMetodos.setModel(model);

        ImageIcon icono = new ImageIcon(getClass().getResource("/imagenes/Logo Inversiones Figuera JG, C.A. - copia.jpg"));
        setIconImage(icono.getImage());

        // Asignar el modelo a la tabla
        estilizarTabla(tablaMetodos);

        // Añadir columnas al modelo de la tabla
        model.addColumn("ID");
        model.addColumn("Nombre");
        model.addColumn("Cedula");
        model.addColumn("Direccion");
        model.addColumn("Telefono");

    }

   private void guardarTablaComoCSV(DefaultTableModel modelo, String archivoCSV) {
    File archivo = new File(archivoCSV);
    boolean archivoExiste = archivo.exists();
    int ultimoID = 0;
    Set<String> cedulasExistentes = new HashSet<>();
    int registrosGuardados = 0;

    try {
        // Leer cédulas existentes y el último ID del archivo
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
                    } catch (NumberFormatException ignored) {}

                    if (datos.length > 2) {
                        cedulasExistentes.add(datos[2].trim());  // ✅ Cedula en posición 2
                    }
                }
            }
            reader.close();
        }

        // Escribir en modo append
        try (FileWriter writer = new FileWriter(archivoCSV, true)) {
            // Escribir encabezado si el archivo está vacío
            if (!archivoExiste || archivo.length() == 0) {
                writer.write("ID;Nombre;Cedula;Direccion;Telefono\n");
            }

            for (int i = 0; i < modelo.getRowCount(); i++) {
                Object cedulaObj = modelo.getValueAt(i, 2); // columna "Cedula"
                if (cedulaObj != null) {
                    String cedula = cedulaObj.toString().trim();

                    if (!cedulasExistentes.contains(cedula)) {
                        int nuevoID = ++ultimoID;

                        // Asignar el nuevo ID a la columna 0 del modelo
                        modelo.setValueAt(nuevoID, i, 0);

                        StringBuilder fila = new StringBuilder();
                        fila.append(nuevoID).append(";");                               // ID
                        fila.append(modelo.getValueAt(i, 1)).append(";"); // Nombre
                        fila.append(modelo.getValueAt(i, 2)).append(";"); // Cedula
                        fila.append(modelo.getValueAt(i, 3)).append(";"); // Direccion
                        fila.append(modelo.getValueAt(i, 4)).append(";"); // Telefono

                        writer.write(fila.toString() + "\n");

                        cedulasExistentes.add(cedula);  // Registrar la cédula
                        registrosGuardados++;
                    }
                }
            }

            writer.flush();
        }

        // Mostrar mensaje dependiendo del resultado
        if (registrosGuardados > 0) {
            modelo.setRowCount(0);  // ✅ Limpiar solo si se guardó algo
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
        List<String> lineas = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new FileReader(archivo));
        String encabezado = reader.readLine();
        String[] columnas = encabezado.split(";");

        lineas.add(encabezado);

        int indiceId = -1;
        int indiceCedula = -1;

        for (int i = 0; i < columnas.length; i++) {
            if (columnas[i].equalsIgnoreCase("ID")) {
                indiceId = i;
            } else if (columnas[i].equalsIgnoreCase("Cedula")) {
                indiceCedula = i;
            }
        }

        if (indiceId == -1 || indiceCedula == -1) {
            JOptionPane.showMessageDialog(null, "No se encontraron las columnas 'ID' o 'Cedula' en el CSV.");
            reader.close();
            return;
        }

        String idSeleccionado = modelo.getValueAt(filaSeleccionada, 0).toString().trim();
        String cedulaNueva = modelo.getValueAt(filaSeleccionada, 2).toString().trim();

        // Validar si la cédula ya existe en otro registro
        String linea;
        while ((linea = reader.readLine()) != null) {
            String[] datos = linea.split(";");
            if (datos.length > indiceCedula &&
                datos[indiceCedula].trim().equals(cedulaNueva) &&
                !datos[indiceId].trim().equals(idSeleccionado)) {
                JOptionPane.showMessageDialog(null, "La cédula ya existe en otro registro del archivo CSV.");
                reader.close();
                return;
            }
            lineas.add(linea);
        }
        reader.close();

        // Buscar y reemplazar la línea correspondiente
        boolean actualizado = false;
        for (int i = 1; i < lineas.size(); i++) {
            String[] datos = lineas.get(i).split(";");
            if (datos.length > indiceId && datos[indiceId].trim().equals(idSeleccionado)) {
                String nuevaLinea = modelo.getValueAt(filaSeleccionada, 0).toString().trim() + ";" +  // ID
                                    modelo.getValueAt(filaSeleccionada, 1).toString().trim() + ";" +  // Nombre
                                    modelo.getValueAt(filaSeleccionada, 2).toString().trim() + ";" +  // Cedula
                                    modelo.getValueAt(filaSeleccionada, 3).toString().trim() + ";" +  // Direccion
                                    modelo.getValueAt(filaSeleccionada, 4).toString().trim() + ";";   // Telefono

                lineas.set(i, nuevaLinea);
                actualizado = true;
                break;
            }
        }

        if (!actualizado) {
            JOptionPane.showMessageDialog(null, "No se encontró el ID en el archivo CSV.");
            return;
        }

        // Reescribir archivo completo
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
            int indiceCedula = -1;

            // Buscar índice de las columnas "ID" y "Cedula"
            String[] columnas = encabezado.split(";");
            for (int i = 0; i < columnas.length; i++) {
                if (columnas[i].equalsIgnoreCase("ID")) {
                    indiceID = i;
                } else if (columnas[i].equalsIgnoreCase("Cedula")) {
                    indiceCedula = i;
                }
            }

            if (indiceID == -1 || indiceCedula == -1) {
                JOptionPane.showMessageDialog(null, "No se encontraron las columnas 'ID' o 'Cedula' en el archivo.");
                reader.close();
                return;
            }

            // Obtener el ID y la Cédula de la fila seleccionada
            String idSeleccionado = modelo.getValueAt(filaSeleccionada, indiceID).toString().trim();
            String cedulaSeleccionada = modelo.getValueAt(filaSeleccionada, indiceCedula).toString().trim();

            boolean filaEliminada = false;

            // Leer y conservar solo las filas que no coincidan con el ID y Cedula
            while ((linea = reader.readLine()) != null) {
                String[] datos = linea.split(";");
                if (datos.length > indiceID && datos[indiceID].trim().equals(idSeleccionado)
                        && datos.length > indiceCedula && datos[indiceCedula].trim().equals(cedulaSeleccionada)) {
                    filaEliminada = true; // Omitimos esta línea (la eliminamos)
                } else {
                    lineas.add(linea);
                }
            }

            reader.close();

            if (!filaEliminada) {
                JOptionPane.showMessageDialog(null, "No se encontró la fila con el ID y Cédula seleccionados en el archivo.");
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

    private void buscarPorNombre() {
        String archivoCSV = "C:\\SistemaVentasJF\\src\\vistas\\registro_personas.csv";
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
                if (datos.length >= 5) {
                    String nombreCliente = datos[1].trim().toLowerCase(); // Columna "Nombre"

                    if (nombreCliente.contains(nombreBuscado)) {
                        model.addRow(new Object[]{
                            datos[0], // id
                            datos[1], // nombre
                             datos[2], // cedula
                              datos[3], // direccion
                               datos[4], // telefono
                            
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
        if (!archivo.exists()) {
            return 1;
        }

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
                    } catch (NumberFormatException ignored) {
                    }
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

        menuClientes = new javax.swing.JPopupMenu();
        btnAgregarFilas = new javax.swing.JMenuItem();
        btnAgregarClientes = new javax.swing.JMenuItem();
        btnActualizarClientes = new javax.swing.JMenuItem();
        btnEliminarClientes = new javax.swing.JMenuItem();
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
        menuClientes.add(btnAgregarFilas);

        btnAgregarClientes.setText("Agregar Clientes");
        btnAgregarClientes.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAgregarClientesActionPerformed(evt);
            }
        });
        menuClientes.add(btnAgregarClientes);

        btnActualizarClientes.setText("Actualizar Clientes");
        btnActualizarClientes.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnActualizarClientesActionPerformed(evt);
            }
        });
        menuClientes.add(btnActualizarClientes);

        btnEliminarClientes.setText("Eliminar Clientes");
        btnEliminarClientes.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEliminarClientesActionPerformed(evt);
            }
        });
        menuClientes.add(btnEliminarClientes);

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
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
        jLabel10.setText("Clientes");
        jPanel1.add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(190, 50, -1, -1));
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
        tablaMetodos.setComponentPopupMenu(menuClientes);
    }//GEN-LAST:event_tablaMetodosMousePressed

    private void btnAgregarFilasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAgregarFilasActionPerformed

        int nuevoID = obtenerSiguienteID("C:\\SistemaVentasJF\\src\\vistas\\registro_personas.csv");
        model.addRow(new Object[]{nuevoID, "", "", "",""});

    }//GEN-LAST:event_btnAgregarFilasActionPerformed

    private void btnAgregarClientesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAgregarClientesActionPerformed

        guardarTablaComoCSV((DefaultTableModel) tablaMetodos.getModel(), "C:\\SistemaVentasJF\\src\\vistas\\registro_personas.csv");
    }//GEN-LAST:event_btnAgregarClientesActionPerformed

    private void jPanel1MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPanel1MousePressed
        jPanel1.setComponentPopupMenu(menuClientes);

    }//GEN-LAST:event_jPanel1MousePressed

    private void btnBuscarMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnBuscarMouseClicked
        buscarPorNombre();
    }//GEN-LAST:event_btnBuscarMouseClicked

    private void btnActualizarClientesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnActualizarClientesActionPerformed

        actualizarFilaSeleccionada(model, tablaMetodos, "C:\\SistemaVentasJF\\src\\vistas\\registro_personas.csv");


    }//GEN-LAST:event_btnActualizarClientesActionPerformed

    private void btnEliminarClientesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEliminarClientesActionPerformed
        eliminarFilaSeleccionada(model, tablaMetodos, "C:\\SistemaVentasJF\\src\\vistas\\registro_personas.csv");
    }//GEN-LAST:event_btnEliminarClientesActionPerformed

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
            java.util.logging.Logger.getLogger(Clientes1.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Clientes1.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Clientes1.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Clientes1.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Clientes1().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuItem btnActualizarClientes;
    private javax.swing.JMenuItem btnAgregarClientes;
    private javax.swing.JMenuItem btnAgregarFilas;
    private javax.swing.JLabel btnBuscar;
    private javax.swing.JMenuItem btnEliminarClientes;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JPopupMenu menuClientes;
    private javax.swing.JTable tablaMetodos;
    private javax.swing.JTextField txtBuscar;
    // End of variables declaration//GEN-END:variables
}
