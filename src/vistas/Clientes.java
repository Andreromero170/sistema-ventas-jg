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
import java.util.HashSet;
import java.util.List;
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
public class Clientes extends javax.swing.JFrame {

    int columnas;
    DefaultTableModel model = new DefaultTableModel(columnas, 0);

    public Clientes() {
        initComponents();

        this.setTitle("Clientes del Inventario JF");

        tablaClientes.setModel(model);

         ImageIcon icono = new ImageIcon(getClass().getResource("/imagenes/Logo Inversiones Figuera JG, C.A. - copia.jpg"));
        setIconImage(icono.getImage());
        
        
        // Asignar el modelo a la tabla
        estilizarTabla(tablaClientes);

        // Añadir columnas al modelo de la tabla
        model.addColumn("Nombre");
        model.addColumn("Cédula");
        model.addColumn("Dirección");
        model.addColumn("Teléfono");

        model.addRow(new Object[]{"", "", "", ""});

    }
private void guardarTablaComoCSV(DefaultTableModel modelo, String archivoCSV) {
    File archivo = new File(archivoCSV);
    boolean archivoExiste = archivo.exists();
    Set<String> cedulasExistentes = new HashSet<>();

    try {
        // Leer cédulas existentes para evitar duplicados
        if (archivoExiste) {
            BufferedReader reader = new BufferedReader(new FileReader(archivoCSV));
            String linea;
            String[] encabezados = reader.readLine().split(";");
            int indiceCedula = -1;

            // Buscar índice de la columna "Cédula"
            for (int i = 0; i < encabezados.length; i++) {
                if (encabezados[i].equalsIgnoreCase("Cédula")) {
                    indiceCedula = i;
                    break;
                }
            }

            if (indiceCedula == -1) {
                JOptionPane.showMessageDialog(null, "No se encontró la columna 'Cédula' en el CSV.");
                reader.close();
                return;
            }

            while ((linea = reader.readLine()) != null) {
                String[] datos = linea.split(";");
                if (datos.length > indiceCedula) {
                    cedulasExistentes.add(datos[indiceCedula].trim().toLowerCase());
                }
            }

            reader.close();
        }

        // Escribir en modo append
        try (FileWriter writer = new FileWriter(archivoCSV, true)) {
            // Escribir encabezados si el archivo no existe o está vacío
            if (!archivoExiste || archivo.length() == 0) {
                for (int i = 0; i < modelo.getColumnCount(); i++) {
                    writer.write(modelo.getColumnName(i));
                    if (i < modelo.getColumnCount() - 1) {
                        writer.write(";");
                    }
                }
                writer.write("\n");
            }

            int filasAñadidas = 0;
            List<Integer> filasParaEliminar = new ArrayList<>();

            // Buscar índice de la columna "Cédula" en el modelo
            int indiceCedulaModelo = -1;
            for (int i = 0; i < modelo.getColumnCount(); i++) {
                if (modelo.getColumnName(i).equalsIgnoreCase("Cédula")) {
                    indiceCedulaModelo = i;
                    break;
                }
            }

            if (indiceCedulaModelo == -1) {
                JOptionPane.showMessageDialog(null, "No se encontró la columna 'Cédula' en la tabla.");
                return;
            }

            for (int i = 0; i < modelo.getRowCount(); i++) {
                Object cedula = modelo.getValueAt(i, indiceCedulaModelo);
                if (cedula == null) continue;

                String cedulaStr = cedula.toString().trim().toLowerCase();

                if (!cedulasExistentes.contains(cedulaStr)) {
                    StringBuilder fila = new StringBuilder();
                    for (int j = 0; j < modelo.getColumnCount(); j++) {
                        Object valor = modelo.getValueAt(i, j);
                        fila.append(valor != null ? valor.toString() : "");
                        if (j < modelo.getColumnCount() - 1) {
                            fila.append(";");
                        }
                    }

                    writer.write(fila.toString().trim() + "\n");
                    cedulasExistentes.add(cedulaStr);
                    filasAñadidas++;
                    filasParaEliminar.add(i);
                }
            }

            writer.flush();

            // Eliminar las filas que se añadieron exitosamente
            for (int i = filasParaEliminar.size() - 1; i >= 0; i--) {
                modelo.removeRow(filasParaEliminar.get(i));
            }

            if (filasAñadidas > 0) {
                JOptionPane.showMessageDialog(null, "Se añadieron " + filasAñadidas + " fila(s) nuevas y se limpiaron de la tabla.");
            } else {
                JOptionPane.showMessageDialog(null, "No se añadieron filas nuevas (cédulas duplicadas).");
            }

        }
    } catch (IOException e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(null, "Error al guardar el archivo CSV.", "Error", JOptionPane.ERROR_MESSAGE);
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
        // Leer todas las líneas del CSV
        List<String> lineas = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new FileReader(archivo));
        String encabezado = reader.readLine();
        lineas.add(encabezado);

        String linea;
        int indiceCedula = -1;

        // Determinar la posición de la columna "Cédula"
        String[] columnas = encabezado.split(";");
        for (int i = 0; i < columnas.length; i++) {
            if (columnas[i].equalsIgnoreCase("Cédula")) {
                indiceCedula = i;
                break;
            }
        }

        if (indiceCedula == -1) {
            JOptionPane.showMessageDialog(null, "No se encontró la columna 'Cédula' en el CSV.");
            reader.close();
            return;
        }

        // Obtener la cédula de la fila seleccionada en la tabla
        String cedulaSeleccionada = modelo.getValueAt(filaSeleccionada, indiceCedula).toString().trim().toLowerCase();

        boolean filaActualizada = false;

        // Leer el resto de líneas y reemplazar la que coincide
        while ((linea = reader.readLine()) != null) {
            String[] datos = linea.split(";");
            if (datos.length > indiceCedula && datos[indiceCedula].trim().equalsIgnoreCase(cedulaSeleccionada)) {
                // Construir la nueva línea desde el modelo
                StringBuilder nuevaLinea = new StringBuilder();
                for (int j = 0; j < modelo.getColumnCount(); j++) {
                    Object valor = modelo.getValueAt(filaSeleccionada, j);
                    nuevaLinea.append(valor != null ? valor.toString() : "");
                    if (j < modelo.getColumnCount() - 1) {
                        nuevaLinea.append(";");
                    }
                }
                lineas.add(nuevaLinea.toString());
                filaActualizada = true;
            } else {
                lineas.add(linea);
            }
        }

        reader.close();

        if (!filaActualizada) {
            JOptionPane.showMessageDialog(null, "No se encontró la cédula en el archivo CSV.");
            return;
        }

        // Reescribir el archivo completo con la línea actualizada
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
        int indiceCedula = -1;

        // Buscar índice de la columna "Cédula"
        String[] columnas = encabezado.split(";");
        for (int i = 0; i < columnas.length; i++) {
            if (columnas[i].equalsIgnoreCase("Cédula")) {
                indiceCedula = i;
                break;
            }
        }

        if (indiceCedula == -1) {
            JOptionPane.showMessageDialog(null, "No se encontró la columna 'Cédula' en el archivo.");
            reader.close();
            return;
        }

        // Obtener la cédula de la fila seleccionada
        String cedulaSeleccionada = modelo.getValueAt(filaSeleccionada, indiceCedula).toString().trim().toLowerCase();

        boolean filaEliminada = false;

        // Leer y conservar solo las filas que no coincidan con la cédula
        while ((linea = reader.readLine()) != null) {
            String[] datos = linea.split(";");
            if (datos.length > indiceCedula && datos[indiceCedula].trim().equalsIgnoreCase(cedulaSeleccionada)) {
                filaEliminada = true; // Omitimos esta línea (la eliminamos)
            } else {
                lineas.add(linea);
            }
        }

        reader.close();

        if (!filaEliminada) {
            JOptionPane.showMessageDialog(null, "No se encontró la fila a eliminar en el archivo.");
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


    private void buscarPorNombreProducto() {
        String archivoCSV = "C:\\SistemaVentasJF\\src\\vistas\\registro_personas.csv";
        String nombreBuscado = txtBuscar.getText().trim().toLowerCase();

        DefaultTableModel model = (DefaultTableModel) tablaClientes.getModel();
        model.setRowCount(0); // Limpiar tabla antes de buscar

        boolean encontrado = false;

        try (BufferedReader br = new BufferedReader(new FileReader(archivoCSV))) {
            String linea;

            // Ignorar la primera línea (cabecera)
            br.readLine();

            while ((linea = br.readLine()) != null) {
                String[] datos = linea.split(";");

                // Asegurarse de que hay al menos 4 columnas válidas
                if (datos.length >= 4) {
                    String nombreCliente = datos[0].trim().toLowerCase(); // Columna "Nombre"

                    if (nombreCliente.contains(nombreBuscado)) {
                        model.addRow(new Object[]{
                            datos[0], // Nombre
                            datos[1], // Cedula
                            datos[2], // Direccion
                            datos[3], // Telefono
                           
                        });
                        encontrado = true;
                    }
                }
            }

            if (!encontrado) {
                JOptionPane.showMessageDialog(null, "No se encontró ningún cliente con el nombre: " + txtBuscar.getText());
            }

        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error al leer el archivo CSV.");
        }
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
        tablaClientes = new javax.swing.JTable();
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

        tablaClientes.setModel(new javax.swing.table.DefaultTableModel(
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
        tablaClientes.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                tablaClientesMousePressed(evt);
            }
        });
        jScrollPane1.setViewportView(tablaClientes);

        jPanel1.add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 150, 520, 290));

        jLabel10.setFont(new java.awt.Font("Tahoma", 1, 36)); // NOI18N
        jLabel10.setForeground(new java.awt.Color(255, 255, 255));
        jLabel10.setText("CLIENTES");
        jPanel1.add(jLabel10, new org.netbeans.lib.awtextra.AbsoluteConstraints(190, 20, -1, -1));
        jPanel1.add(txtBuscar, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 100, 390, 40));

        btnBuscar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/buscar (1).png"))); // NOI18N
        btnBuscar.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnBuscarMouseClicked(evt);
            }
        });
        jPanel1.add(btnBuscar, new org.netbeans.lib.awtextra.AbsoluteConstraints(460, 100, 40, 40));

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

    private void tablaClientesMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tablaClientesMousePressed
        tablaClientes.setComponentPopupMenu(menuClientes);
    }//GEN-LAST:event_tablaClientesMousePressed

    private void btnAgregarFilasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAgregarFilasActionPerformed
        model.addRow(new Object[]{"", "", "", ""});
    }//GEN-LAST:event_btnAgregarFilasActionPerformed

    private void btnAgregarClientesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAgregarClientesActionPerformed

        guardarTablaComoCSV((DefaultTableModel) tablaClientes.getModel(), "C:\\SistemaVentasJF\\src\\vistas\\registro_personas.csv");

    }//GEN-LAST:event_btnAgregarClientesActionPerformed

    private void jPanel1MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPanel1MousePressed
        jPanel1.setComponentPopupMenu(menuClientes);

    }//GEN-LAST:event_jPanel1MousePressed

    private void btnBuscarMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnBuscarMouseClicked
        buscarPorNombreProducto();
    }//GEN-LAST:event_btnBuscarMouseClicked

    private void btnActualizarClientesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnActualizarClientesActionPerformed

actualizarFilaSeleccionada(model, tablaClientes, "C:\\SistemaVentasJF\\src\\vistas\\registro_personas.csv");

    }//GEN-LAST:event_btnActualizarClientesActionPerformed

    private void btnEliminarClientesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEliminarClientesActionPerformed
eliminarFilaSeleccionada(model, tablaClientes, "C:\\SistemaVentasJF\\src\\vistas\\registro_personas.csv");

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
            java.util.logging.Logger.getLogger(Clientes.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Clientes.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Clientes.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Clientes.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Clientes().setVisible(true);
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
    private javax.swing.JTable tablaClientes;
    private javax.swing.JTextField txtBuscar;
    // End of variables declaration//GEN-END:variables
}
