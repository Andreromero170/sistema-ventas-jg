package vistas;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;
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

/**
 *
 * @author Andreina Dev
 */
public class tabla_productos extends javax.swing.JFrame {

    // Crear un modelo para la tabla
    DefaultTableModel model = new DefaultTableModel();

    /**
     * Creates new form tabla_productos
     */
    public tabla_productos() {
        initComponents();

        this.setTitle("Productos del Inventario JF");

        tablaProductos.setModel(model);

        // Asignar el modelo a la tabla
        estilizarTabla(tablaProductos);
        
         ImageIcon icono = new ImageIcon(getClass().getResource("/imagenes/Logo Inversiones Figuera JG, C.A. - copia.jpg"));
        setIconImage(icono.getImage());
        

        // Añadir columnas al modelo de la tabla
        model.addColumn("ID");
        model.addColumn("Nombre");
        model.addColumn("Precio en $");
        model.addColumn("Precio en Bs");
        model.addColumn("Precio $ BCV");
        model.addColumn("Inventario(Stock)");
        model.addColumn("Costos");
        

        String archivo = "C:\\SistemaVentasJF\\src\\vistas\\productos.csv";
        cargarDatosDesdeCSV(model, archivo); // Cambia la ruta del archivo según sea necesario 
    }

// Método que obtiene la fila seleccionada de la primera ventana
// Método en la primera ventana para obtener la fila seleccionada
    private void obtenerFilaSeleccionada() {
        int selectedRow = tablaProductos.getSelectedRow(); // Obtén la fila seleccionada

        if (selectedRow != -1) {
            // Obtén los datos de la fila seleccionada
            String producto = (String) tablaProductos.getValueAt(selectedRow, 1); // Producto (columna 0)
            String precioUSD = (String) tablaProductos.getValueAt(selectedRow, 2); // Precio en $ (columna 2)
            String precioBs = (String) tablaProductos.getValueAt(selectedRow, 3); // Precio en Bs (columna 3)
            String precioBCV = (String) tablaProductos.getValueAt(selectedRow, 4); // Precio $ BCV (columna 4)

            Main ventanaVentas = new Main();
            // Aquí llamamos al método de la segunda ventana para cargar los datos
            if (ventanaVentas != null) {
                ventanaVentas.cargarDatosEnTabla(producto, precioUSD, precioBs, precioBCV);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Por favor, selecciona una fila.");
        }
    }

    private void cargarDatosDesdeCSV(DefaultTableModel model, String archivoCSV) {
        try (BufferedReader br = new BufferedReader(new FileReader(archivoCSV))) {
            String linea;

            // Ignorar la primera línea (cabecera)
            br.readLine();

            while ((linea = br.readLine()) != null) {
                // Dividir la línea por punto y coma ";"
                String[] datos = linea.split(";");

                // Verificar si hay al menos 6 columnas
                if (datos.length >= 7) {
                    // Agregar solo las primeras 6 columnas (índice 0 al 5)
                    model.addRow(new Object[]{
                        datos[0], // Código o nombre del producto
                        datos[1], // Lista de precios
                        datos[2], // Precio en $
                        datos[3], // Precio en BS
                        datos[4], // Precio $ BCV
                        datos[5], // Inventario
                        datos[6] // Costes
                       
                    });
                } else {
                    //System.out.println("Línea ignorada, formato incorrecto: " + linea);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error al cargar el archivo CSV", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void buscarPorNombreProducto() {
        String archivoCSV = "C:\\SistemaVentasJF\\src\\vistas\\productos.csv";
        String nombreBuscado = txtBuscar.getText().trim().toLowerCase();

        DefaultTableModel model = (DefaultTableModel) tablaProductos.getModel();
        model.setRowCount(0); // Limpiar tabla antes de buscar

        boolean encontrado = false;

        try (BufferedReader br = new BufferedReader(new FileReader(archivoCSV))) {
            String linea;

            // Ignorar la primera línea (cabecera)
            br.readLine();

            while ((linea = br.readLine()) != null) {
                String[] datos = linea.split(";");

                // Asegurarse de que hay al menos 6 columnas válidas
                if (datos.length >= 7) {
                    String nombreProducto = datos[1].trim().toLowerCase(); // Columna "Nombre"

                    if (nombreProducto.contains(nombreBuscado)) {
                        model.addRow(new Object[]{
                            datos[0], // Nombre
                            datos[1], // Lista de precios
                            datos[2], // Precio $
                            datos[3], // Precio BS
                            datos[4], // Precio $ BCV
                            datos[5], // Inventario
                            datos[6] // Inventario
                        });
                        encontrado = true;
                    }
                }
            }

            if (!encontrado) {
                JOptionPane.showMessageDialog(null, "No se encontró ningún producto con el nombre: " + txtBuscar.getText());
            }

        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error al leer el archivo CSV.");
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
        if (columnModel.getColumnCount() >= 6) {
            columnModel.getColumn(0).setPreferredWidth(200); // Producto
            columnModel.getColumn(1).setPreferredWidth(80);  // Lista precios
            columnModel.getColumn(2).setPreferredWidth(80);  // Precio $
            columnModel.getColumn(3).setPreferredWidth(80);  // Precio BS
            columnModel.getColumn(4).setPreferredWidth(100); // Precio BCV
            columnModel.getColumn(5).setPreferredWidth(80);  // Inventario
        }
    }

    
private void guardarProductosComoCSV(DefaultTableModel modelo, String archivoCSV) {
    File archivo = new File(archivoCSV);
    boolean archivoExiste = archivo.exists();
    Set<String> nombresExistentes = new HashSet<>();

    try {
        // Leer nombres existentes para evitar duplicados
        if (archivoExiste) {
            BufferedReader reader = new BufferedReader(new FileReader(archivoCSV));
            String linea;
            String[] encabezados = reader.readLine().split(";");
            int indiceNombre = -1;

            // Buscar índice de la columna "Nombre"
            for (int i = 0; i < encabezados.length; i++) {
                if (encabezados[i].equalsIgnoreCase("Nombre")) {
                    indiceNombre = i;
                    break;
                }
            }

            if (indiceNombre == -1) {
                JOptionPane.showMessageDialog(null, "No se encontró la columna 'Nombre' en el CSV.");
                reader.close();
                return;
            }

            while ((linea = reader.readLine()) != null) {
                String[] datos = linea.split(";");
                if (datos.length > indiceNombre) {
                    nombresExistentes.add(datos[indiceNombre].trim().toLowerCase());
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

            // Buscar índice de la columna "Nombre" en el modelo
            int indiceNombreModelo = -1;
            for (int i = 0; i < modelo.getColumnCount(); i++) {
                if (modelo.getColumnName(i).equalsIgnoreCase("Nombre")) {
                    indiceNombreModelo = i;
                    break;
                }
            }

            if (indiceNombreModelo == -1) {
                JOptionPane.showMessageDialog(null, "No se encontró la columna 'Nombre' en la tabla.");
                return;
            }

            for (int i = 0; i < modelo.getRowCount(); i++) {
                Object nombre = modelo.getValueAt(i, indiceNombreModelo);
                if (nombre == null) continue;

                String nombreStr = nombre.toString().trim().toLowerCase();

                if (!nombresExistentes.contains(nombreStr)) {
                    StringBuilder fila = new StringBuilder();
                    for (int j = 0; j < modelo.getColumnCount(); j++) {
                        Object valor = modelo.getValueAt(i, j);
                        fila.append(valor != null ? valor.toString() : "");
                        if (j < modelo.getColumnCount() - 1) {
                            fila.append(";");
                        }
                    }

                    writer.write(fila.toString().trim() + "\n");
                    nombresExistentes.add(nombreStr);
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
                JOptionPane.showMessageDialog(null, "Se añadieron " + filasAñadidas + " producto(s) nuevo(s) y se limpiaron de la tabla.");
            } else {
                JOptionPane.showMessageDialog(null, "No se añadieron productos nuevos (nombres duplicados).");
            }

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
        for (int i = 0; i < columnas.length; i++) {
            if (columnas[i].equalsIgnoreCase("ID")) {
                indiceId = i;
                break;
            }
        }

        if (indiceId == -1) {
            JOptionPane.showMessageDialog(null, "No se encontró la columna 'ID' en el CSV.");
            reader.close();
            return;
        }

        // Obtener el ID de la fila seleccionada
        String idSeleccionado = modelo.getValueAt(filaSeleccionada, indiceId).toString().trim();

        boolean filaActualizada = false;
        String linea;
        while ((linea = reader.readLine()) != null) {
            String[] datos = linea.split(";");
            StringBuilder nuevaLinea = new StringBuilder();

            if (datos.length > indiceId && datos[indiceId].trim().equals(idSeleccionado)) {
                // Validar si el nuevo ID ya existe en otra fila con diferente contenido
                for (String l : lineas) {
                    if (l.equals(encabezado)) continue;
                    String[] d = l.split(";");
                    if (d.length > indiceId &&
                        !l.equals(linea) &&
                        d[indiceId].trim().equals(idSeleccionado)) {
                        JOptionPane.showMessageDialog(null, "El ID ingresado ya existe en otra fila del CSV.");
                        reader.close();
                        return;
                    }
                }

                // Actualizar todos los valores de la fila
                for (int j = 0; j < columnas.length; j++) {
                    nuevaLinea.append(modelo.getValueAt(filaSeleccionada, j).toString().trim());
                    if (j < columnas.length - 1) nuevaLinea.append(";");
                }

                filaActualizada = true;
            } else {
                // Mantener la línea original
                for (int j = 0; j < columnas.length; j++) {
                    nuevaLinea.append(j < datos.length ? datos[j] : "");
                    if (j < columnas.length - 1) nuevaLinea.append(";");
                }
            }

            lineas.add(nuevaLinea.toString());
        }

        reader.close();

        if (!filaActualizada) {
            JOptionPane.showMessageDialog(null, "No se encontró el ID en el archivo CSV.");
            return;
        }

        // Reescribir el archivo con las modificaciones
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

private void actualizarInventario(JTable tabla, String archivoCSV) {
    int filaSeleccionada = tabla.getSelectedRow();

    if (filaSeleccionada == -1) {
        JOptionPane.showMessageDialog(null, "Por favor, selecciona una fila de la tabla.");
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
        String encabezado = reader.readLine();  // Leer encabezado
        String[] columnas = encabezado.split(";");
        lineas.add(encabezado);  // Agregar el encabezado al nuevo archivo

        int indiceId = -1;
        int indiceInventario = -1;

        // Encontrar las posiciones de las columnas ID e Inventario
        for (int i = 0; i < columnas.length; i++) {
            if (columnas[i].equalsIgnoreCase("ID")) {
                indiceId = i;
            }
            if (columnas[i].equalsIgnoreCase("Inventario")) {
                indiceInventario = i;
            }
        }

        if (indiceId == -1 || indiceInventario == -1) {
            JOptionPane.showMessageDialog(null, "No se encontraron las columnas 'ID' o 'Inventario' en el archivo CSV.");
            reader.close();
            return;
        }

        // Obtener el ID del producto de la fila seleccionada
        String idProducto = tabla.getValueAt(filaSeleccionada, 0).toString().trim();  // Suponiendo que el ID está en la primera columna

        String linea;
        boolean productoEncontrado = false;

        // Leer todas las líneas del archivo CSV
        while ((linea = reader.readLine()) != null) {
            String[] datos = linea.split(";");

            if (datos.length > indiceId && datos[indiceId].trim().equals(idProducto)) {
                // Producto encontrado, actualizar la cantidad en Inventario
                String nuevaCantidadStr = tabla.getValueAt(filaSeleccionada, 5).toString().trim();  // La nueva cantidad está en la columna 5 de la tabla
                nuevaCantidadStr = nuevaCantidadStr.replace(",", ".");  // Reemplazar coma por punto si es necesario
                int nuevaCantidad = Integer.parseInt(nuevaCantidadStr);  // Convertir la cantidad a entero

                // Actualizar la cantidad en el inventario
                datos[indiceInventario] = String.valueOf(nuevaCantidad);  // Solo actualizamos la cantidad como entero

                productoEncontrado = true;
            }

            // Volver a escribir la línea (modificada o no)
            lineas.add(String.join(";", datos));
        }

        reader.close();

        if (!productoEncontrado) {
            JOptionPane.showMessageDialog(null, "Producto no encontrado en el archivo CSV.");
            return;
        }

        // Reescribir el archivo CSV con las líneas actualizadas
        BufferedWriter writer = new BufferedWriter(new FileWriter(archivo));
        for (String l : lineas) {
            writer.write(l);
            writer.newLine();
        }
        writer.close();

        JOptionPane.showMessageDialog(null, "Inventario actualizado correctamente.");

    } catch (IOException | NumberFormatException e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(null, "Error al actualizar el inventario.", "Error", JOptionPane.ERROR_MESSAGE);
    }
}


private void eliminarFilaSeleccionada(DefaultTableModel modelo, JTable tabla, String archivoCSV) {
    int filaSeleccionada = tabla.getSelectedRow();

    if (filaSeleccionada == -1) {
        JOptionPane.showMessageDialog(null, "Por favor, selecciona una fila de la tabla para eliminar.");
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
        for (int i = 0; i < columnas.length; i++) {
            if (columnas[i].equalsIgnoreCase("ID")) {
                indiceId = i;
                break;
            }
        }

        if (indiceId == -1) {
            JOptionPane.showMessageDialog(null, "No se encontró la columna 'ID' en el CSV.");
            reader.close();
            return;
        }

        // Obtener el ID de la fila seleccionada
        String idSeleccionado = modelo.getValueAt(filaSeleccionada, indiceId).toString().trim();

        boolean filaEliminada = false;
        String linea;
        while ((linea = reader.readLine()) != null) {
            String[] datos = linea.split(";");
            if (datos.length > indiceId && datos[indiceId].trim().equals(idSeleccionado)) {
                filaEliminada = true; // La fila que se quiere eliminar
                continue; // No añadir esta línea al archivo de salida
            }
            lineas.add(linea); // Mantener la línea no eliminada
        }

        reader.close();

        if (!filaEliminada) {
            JOptionPane.showMessageDialog(null, "No se encontró el ID en el archivo CSV.");
            return;
        }

        // Reescribir el archivo sin la fila eliminada
        BufferedWriter writer = new BufferedWriter(new FileWriter(archivo));
        for (String l : lineas) {
            writer.write(l);
            writer.newLine();
        }
        writer.close();

        JOptionPane.showMessageDialog(null, "La fila fue eliminada exitosamente del archivo CSV.");

    } catch (IOException e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(null, "Error al eliminar la fila del archivo CSV.", "Error", JOptionPane.ERROR_MESSAGE);
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



    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        menuOpciones = new javax.swing.JPopupMenu();
        btnAgregarProductos = new javax.swing.JMenuItem();
        btnAñadirFilas = new javax.swing.JMenuItem();
        btnGuardarProductos = new javax.swing.JMenuItem();
        btnActualizarProductos = new javax.swing.JMenuItem();
        btnAjusteInventario = new javax.swing.JMenuItem();
        btnEliminarProductos = new javax.swing.JMenuItem();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tablaProductos = new javax.swing.JTable();
        btnBuscar = new javax.swing.JLabel();
        txtBuscar = new javax.swing.JTextField();

        btnAgregarProductos.setText("Agregar Productos");
        btnAgregarProductos.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnAgregarProductosMouseClicked(evt);
            }
        });
        btnAgregarProductos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAgregarProductosActionPerformed(evt);
            }
        });
        menuOpciones.add(btnAgregarProductos);

        btnAñadirFilas.setText("Agregar Filas");
        btnAñadirFilas.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAñadirFilasActionPerformed(evt);
            }
        });
        menuOpciones.add(btnAñadirFilas);

        btnGuardarProductos.setText("Guardar Productos");
        btnGuardarProductos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGuardarProductosActionPerformed(evt);
            }
        });
        menuOpciones.add(btnGuardarProductos);

        btnActualizarProductos.setText("Actualizar Productos");
        btnActualizarProductos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnActualizarProductosActionPerformed(evt);
            }
        });
        menuOpciones.add(btnActualizarProductos);

        btnAjusteInventario.setText("Ajuste de Inventario");
        btnAjusteInventario.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAjusteInventarioActionPerformed(evt);
            }
        });
        menuOpciones.add(btnAjusteInventario);

        btnEliminarProductos.setText("Eliminar Producto");
        btnEliminarProductos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEliminarProductosActionPerformed(evt);
            }
        });
        menuOpciones.add(btnEliminarProductos);

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setResizable(false);

        jPanel1.setBackground(new java.awt.Color(0, 0, 153));
        jPanel1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                jPanel1MousePressed(evt);
            }
        });
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        tablaProductos.setModel(new javax.swing.table.DefaultTableModel(
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
        tablaProductos.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tablaProductosMouseClicked(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                tablaProductosMousePressed(evt);
            }
        });
        jScrollPane1.setViewportView(tablaProductos);

        jPanel1.add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 110, 850, 290));

        btnBuscar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/buscar (1).png"))); // NOI18N
        btnBuscar.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnBuscarMouseClicked(evt);
            }
        });
        jPanel1.add(btnBuscar, new org.netbeans.lib.awtextra.AbsoluteConstraints(410, 60, 40, 40));
        jPanel1.add(txtBuscar, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 60, 390, 40));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 870, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 428, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnBuscarMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnBuscarMouseClicked
        buscarPorNombreProducto();
    }//GEN-LAST:event_btnBuscarMouseClicked

    private void btnAgregarProductosMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnAgregarProductosMouseClicked

    }//GEN-LAST:event_btnAgregarProductosMouseClicked

    private void tablaProductosMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tablaProductosMouseClicked

    }//GEN-LAST:event_tablaProductosMouseClicked

    private void tablaProductosMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tablaProductosMousePressed
        tablaProductos.setComponentPopupMenu(menuOpciones);
    }//GEN-LAST:event_tablaProductosMousePressed

    private void btnAgregarProductosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAgregarProductosActionPerformed
               int FilaSeleccionada = tablaProductos.getSelectedRow();

        if (FilaSeleccionada >= 0) {
            String Datos[] = new String[5];
            Datos[0] = tablaProductos.getValueAt(FilaSeleccionada, 1).toString();
            Datos[1] = tablaProductos.getValueAt(FilaSeleccionada, 2).toString();
            Datos[2] = tablaProductos.getValueAt(FilaSeleccionada, 3).toString();
            Datos[3] = tablaProductos.getValueAt(FilaSeleccionada, 4).toString();
            Datos[4] = tablaProductos.getValueAt(FilaSeleccionada, 5).toString();
          Main.model2.addRow(Datos);
            model.removeRow(FilaSeleccionada);
        }
    }//GEN-LAST:event_btnAgregarProductosActionPerformed

    private void btnAñadirFilasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAñadirFilasActionPerformed
        
    int nuevoID = obtenerSiguienteID("C:\\SistemaVentasJF\\src\\vistas\\productos.csv");
    model.addRow(new Object[]{nuevoID, "", "", "", "", ""});
       
    }//GEN-LAST:event_btnAñadirFilasActionPerformed

    private void jPanel1MousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPanel1MousePressed
   jPanel1.setComponentPopupMenu(menuOpciones);

    }//GEN-LAST:event_jPanel1MousePressed

    private void btnGuardarProductosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGuardarProductosActionPerformed

        
        guardarProductosComoCSV(model,"C:\\SistemaVentasJF\\src\\vistas\\productos.csv");

    }//GEN-LAST:event_btnGuardarProductosActionPerformed

    private void btnActualizarProductosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnActualizarProductosActionPerformed
        actualizarFilaSeleccionada(model, tablaProductos, "C:\\SistemaVentasJF\\src\\vistas\\productos.csv");
    }//GEN-LAST:event_btnActualizarProductosActionPerformed

    private void btnEliminarProductosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEliminarProductosActionPerformed
        eliminarFilaSeleccionada(model, tablaProductos, "C:\\SistemaVentasJF\\src\\vistas\\productos.csv");


    }//GEN-LAST:event_btnEliminarProductosActionPerformed

    private void btnAjusteInventarioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAjusteInventarioActionPerformed

        actualizarInventario(tablaProductos, "C:\\SistemaVentasJF\\src\\vistas\\productos.csv");
    }//GEN-LAST:event_btnAjusteInventarioActionPerformed

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
            java.util.logging.Logger.getLogger(tabla_productos.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(tabla_productos.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(tabla_productos.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(tabla_productos.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new tabla_productos().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuItem btnActualizarProductos;
    private javax.swing.JMenuItem btnAgregarProductos;
    private javax.swing.JMenuItem btnAjusteInventario;
    private javax.swing.JMenuItem btnAñadirFilas;
    private javax.swing.JLabel btnBuscar;
    private javax.swing.JMenuItem btnEliminarProductos;
    private javax.swing.JMenuItem btnGuardarProductos;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JPopupMenu menuOpciones;
    private javax.swing.JTable tablaProductos;
    private javax.swing.JTextField txtBuscar;
    // End of variables declaration//GEN-END:variables
}
