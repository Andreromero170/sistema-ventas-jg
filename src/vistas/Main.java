package vistas;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import org.json.JSONArray;
import org.json.JSONObject;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import javax.swing.DefaultCellEditor;
import javax.swing.ImageIcon;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Andreina Dev
 */
public class Main extends javax.swing.JFrame {

    public static DefaultTableModel model2 = new DefaultTableModel() {
        @Override
        public boolean isCellEditable(int row, int column) {
            return column == 4; // Solo la columna "Cantidad"
        }
    };

    public Main() {
        initComponents();
        obtenerFecha();
        obtenerTasaDolar();
        cargarMetodosPago();
        generarCodigoVenta();

        ImageIcon icono = new ImageIcon(getClass().getResource("/imagenes/Logo Inversiones Figuera JG, C.A. - copia.jpg"));
        setIconImage(icono.getImage());

        model2.addColumn("Producto");
        model2.addColumn("Precio en $");
        model2.addColumn("Precio en Bs");
        model2.addColumn("Precio $ BCV");
        model2.addColumn("Cantidad");
        model2.addColumn("Total");

        tablaVentas.setModel(model2);

        tablaVentas.setRowHeight(35);

        // Crear un JTextField con listener para Enter
        javax.swing.JTextField textField = new javax.swing.JTextField();

        textField.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {

                int fila = tablaVentas.getSelectedRow();
                if (fila != -1) {
                    String nombreProducto = tablaVentas.getValueAt(fila, 0).toString(); // Columna 0
                    String cantidadTexto = textField.getText();

                    try {
                        // Parsear como entero directamente
                        int cantidadIngresada = Integer.parseInt(cantidadTexto.replace(",", "."));
                        int disponible = obtenerInventarioDesdeCSV(nombreProducto); // Inventario desde CSV

                        if (disponible == -1) {
                            JOptionPane.showMessageDialog(null,
                                    "❌ No se encontró el producto en el archivo: " + nombreProducto);
                        } else if (cantidadIngresada > disponible) {
                            JOptionPane.showMessageDialog(null,
                                    "❌ Solo hay " + disponible + " unidades disponibles de: " + nombreProducto,
                                    "Inventario insuficiente", JOptionPane.ERROR_MESSAGE);

                            // Reemplazar la cantidad con el valor correcto del inventario
                            tablaVentas.setValueAt(disponible, fila, 4); // Columna 4 es "Cantidad"
                        } else {
                            // Cantidad válida, la colocamos como número entero
                            tablaVentas.setValueAt(cantidadIngresada, fila, 4);
                            JOptionPane.showMessageDialog(null, "✅ Cantidad aceptada para: " + nombreProducto);
                        }

                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(null, "⚠️ Ingresa un número entero válido.");
                    }
                }
            }
        });

// Asignar editor a la columna "Cantidad"
        tablaVentas.getColumnModel().getColumn(4).setCellEditor(new DefaultCellEditor(textField));

// Formatear los totales
        totalDolares.setText("$0,00");
        totalBolivares.setText("Bs. 0,00");

    }

    public int obtenerInventarioDesdeCSV(String nombreProductoBuscado) {
        String archivoCSV = "C:\\SistemaVentasJF\\src\\vistas\\productos.csv";

        try (BufferedReader br = new BufferedReader(new FileReader(archivoCSV))) {
            String linea;
            br.readLine(); // Saltar la cabecera

            while ((linea = br.readLine()) != null) {
                String[] datos = linea.split(";");
                if (datos.length >= 7) {
                    String nombre = datos[1].trim().toLowerCase();

                    if (nombre.equals(nombreProductoBuscado.trim().toLowerCase())) {
                        // Verificar si el valor de inventario está vacío o nulo
                        String inventarioTexto = datos[5].trim().replace(",", ".");
                        if (inventarioTexto.isEmpty()) {
                            // Si está vacío, asignamos un valor predeterminado, por ejemplo 0
                            return 0;
                        }

                        try {
                            // Convertir a número (Double)
                            double inventarioDecimal = Double.parseDouble(inventarioTexto);

                            // Convertir a int para que no tenga decimales
                            int inventarioEntero = (int) inventarioDecimal;

                            System.out.println(inventarioEntero);
                            return inventarioEntero; // Devolvemos el valor como entero

                        } catch (NumberFormatException e) {
                            // Si no se puede convertir, mostramos un mensaje de error
                            JOptionPane.showMessageDialog(null, "Error al leer inventario de: " + nombreProductoBuscado);
                            return -1;
                        }
                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error al leer inventario del archivo CSV.");
        }

        return -1; // Producto no encontrado
    }

    public void generarCodigoVenta() {
        String letras = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        Random random = new Random();
        StringBuilder codigo = new StringBuilder();

        // Agregar 2 letras aleatorias
        for (int i = 0; i < 2; i++) {
            int index = random.nextInt(letras.length());
            codigo.append(letras.charAt(index));
        }

        // Agregar 4 números aleatorios
        for (int i = 0; i < 4; i++) {
            int numero = random.nextInt(10); // Del 0 al 9
            codigo.append(numero);
        }

        // Asignar el resultado al campo de texto
        txtNumeroVenta.setText(codigo.toString());
    }

// Método en la segunda ventana para cargar los datos en la tabla
    public void cargarDatosEnTabla(String producto, String precioUSD, String precioBs, String precioBCV) {
        // Obtener el modelo de la tabla en la segunda ventana
        DefaultTableModel model = (DefaultTableModel) tablaVentas.getModel();

        // Agregar una nueva fila con los datos recibidos
        model.addRow(new Object[]{
            producto, // Producto
            precioUSD, // Precio en $
            precioBs, // Precio en Bs
            precioBCV, // Precio $ BCV
            0, // Cantidad (valor predeterminado, puedes modificarlo)
            0 // Total (valor predeterminado, puedes modificarlo)
        });
    }

    private void limpiarFormulario() {
        txtCedula1.setText("");
        txtNombre.setText("");
        txtDireccion.setText("");
        txtTelefono.setText("");
        txtNumeroVenta.setText("");
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        menuVenta = new javax.swing.JPopupMenu();
        btnRemoverProducto = new javax.swing.JMenuItem();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tablaVentas = new javax.swing.JTable();
        jPanel3 = new javax.swing.JPanel();
        jLabel10 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        txtNombre = new javax.swing.JLabel();
        txtDireccion = new javax.swing.JLabel();
        txtTelefono = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        totalBolivares = new javax.swing.JTextField();
        totalDolares = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        txtNumeroVenta = new javax.swing.JTextField();
        txtCedula1 = new javax.swing.JTextField();
        txtFecha1 = new javax.swing.JTextField();
        btnReset = new javax.swing.JButton();
        cbxMonedas = new javax.swing.JComboBox<>();
        btnRegistrar = new javax.swing.JButton();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        cbxTasaDolar = new javax.swing.JComboBox<>();
        btnBuscarProducto = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        btnAjusteInventario = new javax.swing.JButton();
        btnAgregarClientes = new javax.swing.JButton();
        btnAgregarMetodosPago = new javax.swing.JButton();
        btnAgregarInventario = new javax.swing.JButton();
        cbxMetodosPago1 = new javax.swing.JComboBox<>();
        jLabel14 = new javax.swing.JLabel();

        menuVenta.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                menuVentaMouseClicked(evt);
            }
        });

        btnRemoverProducto.setText("Remover Producto");
        btnRemoverProducto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRemoverProductoActionPerformed(evt);
            }
        });
        menuVenta.add(btnRemoverProducto);

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setBackground(new java.awt.Color(0, 0, 0));
        setResizable(false);

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        tablaVentas.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        tablaVentas.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "PRODUCTO", "PRECIO $", "PRECIO $", "PRECIO $ BCV", "CANTIDAD", "TOTAL"
            }
        ));
        tablaVentas.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tablaVentasMouseClicked(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                tablaVentasMousePressed(evt);
            }
        });
        jScrollPane1.setViewportView(tablaVentas);

        jPanel1.add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 80, 740, 410));

        jPanel3.setBackground(new java.awt.Color(0, 0, 204));

        jLabel10.setFont(new java.awt.Font("Tahoma", 1, 36)); // NOI18N
        jLabel10.setForeground(new java.awt.Color(255, 255, 255));
        jLabel10.setText("FACTURACION");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(237, 237, 237)
                .addComponent(jLabel10)
                .addContainerGap(236, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel10)
                .addContainerGap())
        );

        jPanel1.add(jPanel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 740, 60));

        jPanel2.setBackground(new java.awt.Color(0, 0, 204));
        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel1.setFont(new java.awt.Font("Times New Roman", 1, 15)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setText("Fecha:");
        jPanel2.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(270, 130, -1, 30));

        jLabel2.setFont(new java.awt.Font("Times New Roman", 1, 15)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setText("PRECIO DOLAR");
        jPanel2.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 20, -1, -1));

        txtNombre.setFont(new java.awt.Font("Times New Roman", 1, 15)); // NOI18N
        txtNombre.setForeground(new java.awt.Color(255, 255, 255));
        jPanel2.add(txtNombre, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 158, 140, 20));

        txtDireccion.setFont(new java.awt.Font("Times New Roman", 1, 15)); // NOI18N
        txtDireccion.setForeground(new java.awt.Color(255, 255, 255));
        jPanel2.add(txtDireccion, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 188, 140, 20));

        txtTelefono.setFont(new java.awt.Font("Times New Roman", 1, 15)); // NOI18N
        txtTelefono.setForeground(new java.awt.Color(255, 255, 255));
        jPanel2.add(txtTelefono, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 220, 140, 20));

        jLabel6.setFont(new java.awt.Font("Times New Roman", 1, 15)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(255, 255, 255));
        jLabel6.setText("Monedas:");
        jPanel2.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(300, 260, -1, -1));

        jLabel7.setFont(new java.awt.Font("Times New Roman", 1, 15)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(255, 255, 255));
        jLabel7.setText("Venta Nº:");
        jPanel2.add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(250, 170, -1, 40));

        jLabel8.setFont(new java.awt.Font("Times New Roman", 1, 25)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(255, 255, 255));
        jLabel8.setText("TOTAL");
        jPanel2.add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 60, 90, 30));

        totalBolivares.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jPanel2.add(totalBolivares, new org.netbeans.lib.awtextra.AbsoluteConstraints(320, 80, 140, 40));

        totalDolares.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jPanel2.add(totalDolares, new org.netbeans.lib.awtextra.AbsoluteConstraints(320, 30, 140, 40));

        jLabel9.setFont(new java.awt.Font("Times New Roman", 1, 15)); // NOI18N
        jLabel9.setForeground(new java.awt.Color(255, 255, 255));
        jLabel9.setText("Cedula:");
        jPanel2.add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 110, -1, 30));

        txtNumeroVenta.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jPanel2.add(txtNumeroVenta, new org.netbeans.lib.awtextra.AbsoluteConstraints(320, 170, 87, 40));

        txtCedula1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtCedula1KeyPressed(evt);
            }
        });
        jPanel2.add(txtCedula1, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 110, 130, 30));
        jPanel2.add(txtFecha1, new org.netbeans.lib.awtextra.AbsoluteConstraints(320, 130, 140, 30));

        btnReset.setBackground(new java.awt.Color(255, 255, 255));
        btnReset.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/actualizar.png"))); // NOI18N
        btnReset.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnResetActionPerformed(evt);
            }
        });
        jPanel2.add(btnReset, new org.netbeans.lib.awtextra.AbsoluteConstraints(410, 170, 50, 40));

        cbxMonedas.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        cbxMonedas.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Precio en $", "Precio en Bs", "Precio $ BCV" }));
        cbxMonedas.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbxMonedasActionPerformed(evt);
            }
        });
        jPanel2.add(cbxMonedas, new org.netbeans.lib.awtextra.AbsoluteConstraints(250, 290, 180, 40));

        btnRegistrar.setBackground(new java.awt.Color(255, 255, 255));
        btnRegistrar.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        btnRegistrar.setText("REGISTRAR");
        btnRegistrar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRegistrarActionPerformed(evt);
            }
        });
        jPanel2.add(btnRegistrar, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 340, 140, 40));

        jLabel11.setFont(new java.awt.Font("Times New Roman", 1, 15)); // NOI18N
        jLabel11.setForeground(new java.awt.Color(255, 255, 255));
        jLabel11.setText("Nombre:");
        jPanel2.add(jLabel11, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 160, -1, -1));

        jLabel12.setFont(new java.awt.Font("Times New Roman", 1, 15)); // NOI18N
        jLabel12.setForeground(new java.awt.Color(255, 255, 255));
        jLabel12.setText("Direccion:");
        jPanel2.add(jLabel12, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 190, -1, -1));

        jLabel13.setFont(new java.awt.Font("Times New Roman", 1, 15)); // NOI18N
        jLabel13.setForeground(new java.awt.Color(255, 255, 255));
        jLabel13.setText("Telefono:");
        jPanel2.add(jLabel13, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 220, -1, -1));

        cbxTasaDolar.setBackground(new java.awt.Color(153, 255, 153));
        cbxTasaDolar.setEditable(true);
        cbxTasaDolar.setFont(new java.awt.Font("Tahoma", 0, 13)); // NOI18N
        cbxTasaDolar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbxTasaDolarActionPerformed(evt);
            }
        });
        jPanel2.add(cbxTasaDolar, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 40, 160, 40));

        btnBuscarProducto.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/buscar (1).png"))); // NOI18N
        btnBuscarProducto.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnBuscarProductoMouseClicked(evt);
            }
        });
        jPanel2.add(btnBuscarProducto, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 330, 40, 50));

        jPanel4.setBackground(new java.awt.Color(255, 255, 255));

        btnAjusteInventario.setBackground(new java.awt.Color(153, 0, 153));
        btnAjusteInventario.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        btnAjusteInventario.setForeground(new java.awt.Color(255, 255, 255));
        btnAjusteInventario.setText("Ajuste de Inventario");
        btnAjusteInventario.setBorder(javax.swing.BorderFactory.createEtchedBorder(java.awt.Color.white, java.awt.Color.white));
        btnAjusteInventario.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAjusteInventarioActionPerformed(evt);
            }
        });

        btnAgregarClientes.setBackground(new java.awt.Color(255, 102, 0));
        btnAgregarClientes.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        btnAgregarClientes.setForeground(new java.awt.Color(255, 255, 255));
        btnAgregarClientes.setText("Agregar Clientes");
        btnAgregarClientes.setBorder(javax.swing.BorderFactory.createEtchedBorder(java.awt.Color.white, java.awt.Color.white));
        btnAgregarClientes.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAgregarClientesActionPerformed(evt);
            }
        });

        btnAgregarMetodosPago.setBackground(new java.awt.Color(51, 255, 51));
        btnAgregarMetodosPago.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        btnAgregarMetodosPago.setForeground(new java.awt.Color(255, 255, 255));
        btnAgregarMetodosPago.setText("Agregar Metodos de Pago");
        btnAgregarMetodosPago.setBorder(javax.swing.BorderFactory.createEtchedBorder(java.awt.Color.white, java.awt.Color.white));
        btnAgregarMetodosPago.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAgregarMetodosPagoActionPerformed(evt);
            }
        });

        btnAgregarInventario.setBackground(new java.awt.Color(204, 0, 0));
        btnAgregarInventario.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        btnAgregarInventario.setForeground(new java.awt.Color(255, 255, 255));
        btnAgregarInventario.setText("Agregar al Inventario");
        btnAgregarInventario.setBorder(javax.swing.BorderFactory.createEtchedBorder(java.awt.Color.white, java.awt.Color.white));
        btnAgregarInventario.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAgregarInventarioActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnAgregarClientes, javax.swing.GroupLayout.PREFERRED_SIZE, 183, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGap(52, 52, 52)
                        .addComponent(btnAgregarMetodosPago, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(btnAjusteInventario, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnAgregarInventario, javax.swing.GroupLayout.DEFAULT_SIZE, 182, Short.MAX_VALUE))
                .addGap(67, 67, 67))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnAjusteInventario, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnAgregarClientes, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(btnAgregarMetodosPago, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnAgregarInventario, javax.swing.GroupLayout.DEFAULT_SIZE, 40, Short.MAX_VALUE))
                .addContainerGap(27, Short.MAX_VALUE))
        );

        jPanel2.add(jPanel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 410, 490, -1));

        cbxMetodosPago1.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        cbxMetodosPago1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbxMetodosPago1ActionPerformed(evt);
            }
        });
        jPanel2.add(cbxMetodosPago1, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 290, 180, 40));

        jLabel14.setFont(new java.awt.Font("Times New Roman", 1, 15)); // NOI18N
        jLabel14.setForeground(new java.awt.Color(255, 255, 255));
        jLabel14.setText("Metodos de Pago:");
        jPanel2.add(jLabel14, new org.netbeans.lib.awtextra.AbsoluteConstraints(80, 260, -1, -1));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 754, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 484, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 517, Short.MAX_VALUE)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, 517, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnRegistrarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRegistrarActionPerformed
        // Obtener los datos del cliente desde los campos de texto
        String nombreCliente = txtNombre.getText();
        String cedulaCliente = txtCedula1.getText();
        String direccion = txtDireccion.getText();
        String telefono = txtTelefono.getText();
        String numeroVenta = txtNumeroVenta.getText();
        // Obtener los totales (por ejemplo, ya calculados en algún lugar de tu código)
        String totalDolares = this.totalDolares.getText();  // Asegúrate de tener estos valores calculados previamente
        String totalBolivares = this.totalBolivares.getText();
        String monedaSeleccionada = (String) cbxMonedas.getSelectedItem();  // Obtenemos la moneda seleccionada

        // Llamar al método para generar la factura
        FacturaGeneradora facturaGeneradora = new FacturaGeneradora();
        // Crear una instancia de la clase RegistroVenta
        RegistroVenta registro = new RegistroVenta();

        // Llamar al método registrarVenta con los parámetros obtenidos
        registro.registrarVenta(tablaVentas, nombreCliente, cedulaCliente, totalDolares, totalBolivares, numeroVenta);
        // Crear una instancia de ActualizarInventario
        ActualizarInventario actualizador = new ActualizarInventario();

        // Llamar al método actualizarInventario para actualizar el archivo del inventario
        String archivoInventario = "C:\\SistemaVentasJF\\src\\vistas\\productos.csv";  // Ruta del archivo de inventario
        actualizador.actualizarInventario(archivoInventario, tablaVentas);
facturaGeneradora.generarFactura(tablaVentas, nombreCliente, cedulaCliente, totalBolivares, monedaSeleccionada, "C:\\SistemaVentasJF\\src\\imagenes\\logo.jpg");
    }//GEN-LAST:event_btnRegistrarActionPerformed

    private void btnResetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnResetActionPerformed
        limpiarFormulario();
        generarCodigoVenta();
        cargarMetodosPago();
    }//GEN-LAST:event_btnResetActionPerformed
    public void buscarPorCedula() {
        String archivo = "C:\\SistemaVentasJF\\src\\vistas\\registro_personas.csv";
        String cedulaBuscada = txtCedula1.getText().trim(); // O directamente "34567890" para probar
        boolean encontrado = false;

        try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
            String linea;

            while ((linea = br.readLine()) != null) {
                String[] datos = linea.split(";");

                if (datos.length >= 4) {
                    String cedulaArchivo = datos[1].trim();
                    if (cedulaArchivo.equals(cedulaBuscada)) {
                        txtCedula1.setText(datos[1].trim());
                        txtNombre.setText(datos[0].trim());
                        txtDireccion.setText(datos[2].trim());
                        txtTelefono.setText(datos[3].trim());

                        JOptionPane.showMessageDialog(null, "Registro encontrado.");
                        encontrado = true;
                        break;
                    }
                }
            }

            if (!encontrado) {
                JOptionPane.showMessageDialog(null, "No se encontró la cédula: " + cedulaBuscada);
            }

        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error al leer el archivo.");
        }
    }

    public void obtenerFecha() {

// Obtener la fecha actual
        SimpleDateFormat formatoFecha = new SimpleDateFormat("dd/MM/yyyy");
        String fechaActual = formatoFecha.format(new Date());

// Establecer la fecha actual en el componente txtFecha1
        txtFecha1.setText(fechaActual);

    }

    public void cargarMetodosPago() {
        String archivo = "C:\\SistemaVentasJF\\src\\vistas\\metodos_pagos.csv";

        cbxMetodosPago1.removeAllItems(); // Limpiar el ComboBox

        try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
            String linea;

            // Leer y descartar el encabezado
            br.readLine();

            // Leer línea por línea desde la primera fila de datos
            while ((linea = br.readLine()) != null) {
                String[] columnas = linea.split(";");
                if (columnas.length > 1) { // Asegurarse de que tenga al menos columna de nombre
                    cbxMetodosPago1.addItem(columnas[1].trim()); // Columna "Nombre"
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void obtenerTasaDolar() {
        try {
            // URL de la API de DolarAPI
            URL url = new URL("https://ve.dolarapi.com/v1/dolares");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            // Agrega un User-Agent en la cabecera para evitar el error 403
            conn.setRequestProperty("User-Agent", "Mozilla/5.0");

            // Lee la respuesta de la API
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
            String inputLine;
            StringBuilder respuesta = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                respuesta.append(inputLine);
            }
            in.close();

            // Muestra la respuesta cruda para verificar qué se recibe
            System.out.println("Respuesta JSON cruda:");
            System.out.println(respuesta.toString());

            // Procesa la respuesta JSON (arreglo)
            JSONArray jsonArray = new JSONArray(respuesta.toString());

            // Itera sobre el arreglo y extrae las tasas
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject json = jsonArray.getJSONObject(i);

                String fuente = json.getString("fuente");
                double promedio = json.getDouble("promedio");
                //System.out.println("| Tasa Promedio: " + promedio);

                // Añadir la fuente y la tasa promedio al ComboBox
                cbxTasaDolar.addItem(fuente + " - " + promedio);

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void actualizarTotales() {
        double tasa = 1.0;
        try {
            String tasaTexto = cbxTasaDolar.getSelectedItem().toString().trim();

            if (tasaTexto.contains("-")) {
                tasaTexto = tasaTexto.split("-")[1].trim().replace(",", ".");
                tasa = Double.parseDouble(tasaTexto);
            } else {
                JOptionPane.showMessageDialog(null, "⚠️ Tasa del dólar inválida.");
                return;
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "⚠️ Tasa del dólar inválida.");
            return;
        }

        String monedaSeleccionada = cbxMonedas.getSelectedItem().toString().trim();
        double totalEnDolares = 0.0;
        double totalEnBs = 0.0;

        // Buscar la columna correspondiente al precio según moneda
        int columnaPrecio = -1;
        for (int col = 0; col < tablaVentas.getColumnCount(); col++) {
            String nombreColumna = tablaVentas.getColumnName(col).trim();
            if (nombreColumna.equalsIgnoreCase(monedaSeleccionada)) {
                columnaPrecio = col;
                break;
            }
        }

        if (columnaPrecio == -1) {
            JOptionPane.showMessageDialog(null, "⚠️ No se encontró la columna para: " + monedaSeleccionada);
            return;
        }

        for (int i = 0; i < tablaVentas.getRowCount(); i++) {
            Object cantidadObj = tablaVentas.getValueAt(i, 4); // Cantidad
            Object precioObj = tablaVentas.getValueAt(i, columnaPrecio); // Precio según moneda

            if (cantidadObj != null && precioObj != null
                    && !cantidadObj.toString().isEmpty() && !precioObj.toString().isEmpty()) {

                double cantidad = Double.parseDouble(cantidadObj.toString().replace(",", "."));
                double precio = Double.parseDouble(precioObj.toString().replace(",", "."));
                double subtotalDolares = 0;
                double subtotalBs = 0;
                String totalFormateado = "";

                switch (monedaSeleccionada) {
                    case "Precio en $":
                        subtotalDolares = cantidad * precio;
                        subtotalBs = subtotalDolares * tasa;
                        totalFormateado = "$ " + String.format("%.2f", subtotalDolares);
                        break;
                    case "Precio en Bs":
                        subtotalBs = cantidad * precio;
                        subtotalDolares = subtotalBs / tasa;
                        totalFormateado = "Bs. " + String.format("%.2f", subtotalBs);
                        break;
                    case "Precio $ BCV":
                        subtotalDolares = cantidad * precio;
                        subtotalBs = subtotalDolares * tasa;
                        totalFormateado = "$ " + String.format("%.2f", subtotalDolares);
                        break;
                }

                totalEnDolares += subtotalDolares;
                totalEnBs += subtotalBs;

                // Mostrar total en la moneda seleccionada en columna "Total" (columna 5)
                tablaVentas.setValueAt(totalFormateado, i, 5);
            }
        }

        totalDolares.setText("$ " + String.format("%.2f", totalEnDolares));
        totalBolivares.setText("Bs. " + String.format("%.2f", totalEnBs));
    }

    private void removerFila() {
        int filaSeleccionada = tablaVentas.getSelectedRow();

        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(null, "⚠️ Selecciona una fila para eliminar.");
            return;
        }

        // Eliminar la fila seleccionada
        DefaultTableModel modelo = (DefaultTableModel) tablaVentas.getModel();
        modelo.removeRow(filaSeleccionada);

        // Actualizar los totales después de eliminar la fila
        actualizarTotales();
    }


    private void txtCedula1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtCedula1KeyPressed

        if (evt.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) {
            buscarPorCedula();
        }


    }//GEN-LAST:event_txtCedula1KeyPressed

    private void btnBuscarProductoMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnBuscarProductoMouseClicked
        tabla_productos ventanaProductos = new tabla_productos();
        ventanaProductos.setVisible(true);
    }//GEN-LAST:event_btnBuscarProductoMouseClicked

    private void tablaVentasMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tablaVentasMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_tablaVentasMouseClicked

    private void tablaVentasMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tablaVentasMousePressed
        tablaVentas.setComponentPopupMenu(menuVenta);
    }//GEN-LAST:event_tablaVentasMousePressed

    private void menuVentaMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_menuVentaMouseClicked
        int FilaSeleccionada = tablaVentas.getSelectedRow();

        if (FilaSeleccionada >= 0) {
            model2.removeRow(FilaSeleccionada);
        }
    }//GEN-LAST:event_menuVentaMouseClicked

    private void btnAgregarClientesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAgregarClientesActionPerformed

        Clientes c = new Clientes();
        c.setVisible(true);


    }//GEN-LAST:event_btnAgregarClientesActionPerformed

    private void cbxMonedasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbxMonedasActionPerformed
        actualizarTotales();
    }//GEN-LAST:event_cbxMonedasActionPerformed

    private void cbxMetodosPago1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbxMetodosPago1ActionPerformed

    }//GEN-LAST:event_cbxMetodosPago1ActionPerformed

    private void cbxTasaDolarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbxTasaDolarActionPerformed
        actualizarTotales();
    }//GEN-LAST:event_cbxTasaDolarActionPerformed

    private void btnRemoverProductoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRemoverProductoActionPerformed
        removerFila();
    }//GEN-LAST:event_btnRemoverProductoActionPerformed

    private void btnAjusteInventarioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAjusteInventarioActionPerformed
        tabla_productos c = new tabla_productos();
        c.setVisible(true);
    }//GEN-LAST:event_btnAjusteInventarioActionPerformed

    private void btnAgregarMetodosPagoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAgregarMetodosPagoActionPerformed
        MetodosPago c = new MetodosPago();
        c.setVisible(true);
    }//GEN-LAST:event_btnAgregarMetodosPagoActionPerformed

    private void btnAgregarInventarioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAgregarInventarioActionPerformed
        tabla_productos c = new tabla_productos();
        c.setVisible(true);
    }//GEN-LAST:event_btnAgregarInventarioActionPerformed

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
            java.util.logging.Logger.getLogger(Main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Main().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAgregarClientes;
    private javax.swing.JButton btnAgregarInventario;
    private javax.swing.JButton btnAgregarMetodosPago;
    private javax.swing.JButton btnAjusteInventario;
    private javax.swing.JLabel btnBuscarProducto;
    private javax.swing.JButton btnRegistrar;
    private javax.swing.JMenuItem btnRemoverProducto;
    private javax.swing.JButton btnReset;
    private javax.swing.JComboBox<String> cbxMetodosPago1;
    private javax.swing.JComboBox<String> cbxMonedas;
    private javax.swing.JComboBox<String> cbxTasaDolar;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JPopupMenu menuVenta;
    private javax.swing.JTable tablaVentas;
    private javax.swing.JTextField totalBolivares;
    private javax.swing.JTextField totalDolares;
    private javax.swing.JTextField txtCedula1;
    private javax.swing.JLabel txtDireccion;
    private javax.swing.JTextField txtFecha1;
    private javax.swing.JLabel txtNombre;
    private javax.swing.JTextField txtNumeroVenta;
    private javax.swing.JLabel txtTelefono;
    // End of variables declaration//GEN-END:variables
}
