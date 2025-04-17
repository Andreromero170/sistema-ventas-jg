/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vistas;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import com.itextpdf.text.Image;  // Aquí está la clase Image de iText
import java.io.FileOutputStream;
import javax.swing.table.TableModel;

public class FacturaGeneradora {
    
      public void generarFactura(JTable tablaVentas, String nombreCliente, String cedulaCliente, String totalBolivares, String monedaSeleccionada, String rutaImagenLogo) {
        try {
            // Ruta de la carpeta donde se guardará el PDF
            String rutaCarpeta = "C:\\Facturas";
            File carpeta = new File(rutaCarpeta);

            // Verifica si la carpeta existe, si no, la crea
            if (!carpeta.exists()) {
                carpeta.mkdirs();  // Crea la carpeta si no existe
            }

            // Nombre del archivo PDF con la fecha actual
            String fechaArchivo = new SimpleDateFormat("dd-MM-yyyy_HH-mm-ss").format(new Date());
            String nombreArchivo = rutaCarpeta + "\\Factura_" + fechaArchivo + ".pdf";  // Guardar en la carpeta específica

            // Crea el documento PDF
            com.itextpdf.text.Rectangle pageSize = new com.itextpdf.text.Rectangle(226, 700);
            Document documento = new Document(pageSize);
            PdfWriter.getInstance(documento, new FileOutputStream(nombreArchivo));
            documento.open();

            // Fuentes
            com.itextpdf.text.Font titulo = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 14, com.itextpdf.text.Font.BOLD);
            com.itextpdf.text.Font texto = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 9);
            com.itextpdf.text.Font encabezadoNegrita = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 8, com.itextpdf.text.Font.BOLD);

            // Logo
            if (rutaImagenLogo != null && !rutaImagenLogo.isEmpty()) {
                Image logo = Image.getInstance(rutaImagenLogo);
                logo.scaleToFit(40, 40);
                logo.setAlignment(Element.ALIGN_CENTER);
                documento.add(logo);
            }

            // Encabezado
            Paragraph nombreEmpresa = new Paragraph("Inversiones Figuera JG, C.A", titulo);
            nombreEmpresa.setAlignment(Element.ALIGN_CENTER);
            documento.add(nombreEmpresa);

            Paragraph direccionParrafo = new Paragraph("RIF: 411652350", texto);
            direccionParrafo.setAlignment(Element.ALIGN_CENTER);
            documento.add(direccionParrafo);

            Paragraph telefonoParrafo = new Paragraph("Dirección: Caracas - La Vega", texto);
            telefonoParrafo.setAlignment(Element.ALIGN_CENTER);
            documento.add(telefonoParrafo);

            documento.add(Chunk.NEWLINE);

            // Nombre y fecha en una misma línea
            PdfPTable tablaNombreFecha = new PdfPTable(2);
            tablaNombreFecha.setWidthPercentage(100);
            tablaNombreFecha.setSpacingBefore(5f);
            PdfPCell celdaNombre = new PdfPCell(new Phrase("Nombre: " + nombreCliente, texto));
            PdfPCell celdaFecha = new PdfPCell(new Phrase("Fecha: "
                    + new SimpleDateFormat("dd/MM/yyyy").format(new Date()), texto));
            celdaNombre.setBorder(com.itextpdf.text.Rectangle.NO_BORDER);
            celdaFecha.setBorder(com.itextpdf.text.Rectangle.NO_BORDER);
            tablaNombreFecha.addCell(celdaNombre);
            tablaNombreFecha.addCell(celdaFecha);

            // Fila 2: Cédula (ocupa ambas columnas)
            PdfPCell celdaCedula = new PdfPCell(new Phrase("Cédula: " + cedulaCliente, texto));
            celdaCedula.setColspan(2);
            celdaCedula.setBorder(com.itextpdf.text.Rectangle.NO_BORDER);
            tablaNombreFecha.addCell(celdaCedula);

            // Añadir tabla al documento
            documento.add(tablaNombreFecha);
            documento.add(Chunk.NEWLINE);


    // Tabla de productos con encabezados
    PdfPTable tablaProductos = new PdfPTable(4);
    tablaProductos.setWidthPercentage(100);
    float[] anchos = {3f, 1.5f, 2f, 2f};
    tablaProductos.setWidths(anchos);

    // Encabezados
    String[] encabezados = {"PRODUCTO", "CANT", "P.UNIT", "TOTAL"};
    for (String encabezado : encabezados) {
        PdfPCell celda = new PdfPCell(new Phrase(encabezado, encabezadoNegrita));
        celda.setHorizontalAlignment(Element.ALIGN_CENTER);
        celda.setBorder(com.itextpdf.text.Rectangle.BOTTOM);
        tablaProductos.addCell(celda);
    }

    // Determinar columna del precio según la moneda seleccionada
    TableModel modelo = tablaVentas.getModel();
    int colPrecio;
    switch (monedaSeleccionada) {
        case "Precio en $":
            colPrecio = 1;
            break;
        case "Precio en Bs":
            colPrecio = 2;
            break;
        case "Precio $ BCV":
            colPrecio = 3;
            break;
        default:
            colPrecio = 1;
            break;
    }

    String simbolo = monedaSeleccionada.contains("$") ? "$" : "Bs.";

    // Agregar filas de productos
    for (int i = 0; i < modelo.getRowCount(); i++) {
        String producto = modelo.getValueAt(i, 0).toString();
        String cantidad = modelo.getValueAt(i, 4).toString();
        String precio = modelo.getValueAt(i, colPrecio).toString();
        String total = modelo.getValueAt(i, 5).toString();

        PdfPCell prod = new PdfPCell(new Phrase(producto, texto));
        PdfPCell cant = new PdfPCell(new Phrase(cantidad, texto));
        PdfPCell punit = new PdfPCell(new Phrase(precio, texto));
        PdfPCell tot = new PdfPCell(new Phrase(total, texto));

        prod.setHorizontalAlignment(Element.ALIGN_CENTER);
        cant.setHorizontalAlignment(Element.ALIGN_CENTER);
        punit.setHorizontalAlignment(Element.ALIGN_CENTER);
        tot.setHorizontalAlignment(Element.ALIGN_CENTER);

        prod.setBorder(com.itextpdf.text.Rectangle.NO_BORDER);
        cant.setBorder(com.itextpdf.text.Rectangle.NO_BORDER);
        punit.setBorder(com.itextpdf.text.Rectangle.NO_BORDER);
        tot.setBorder(com.itextpdf.text.Rectangle.NO_BORDER);

        tablaProductos.addCell(prod);
        tablaProductos.addCell(cant);
        tablaProductos.addCell(punit);
        tablaProductos.addCell(tot);
    }

    documento.add(tablaProductos);
    documento.add(new Paragraph("--------------------------------------------------", texto));
    documento.add(new Paragraph("Total en Bolívares: " + totalBolivares, texto));

    documento.add(Chunk.NEWLINE);

    documento.close();
    JOptionPane.showMessageDialog(null, "✅ Factura generada como: " + rutaCarpeta);

} catch (Exception e) {
    e.printStackTrace();
    JOptionPane.showMessageDialog(null, "❌ Error al generar la factura: " + e.getMessage());
}

    }

    // Método para mostrar el preview en un JTextArea
    public void mostrarPreviewFactura(JTable tablaVentas, JTextArea txtPreview, String direccion, String telefono,
            String nombreCliente,
            String cedulaCliente, String totalDolares, String totalBolivares,
            String monedaSeleccionada, String imagenRuta) {

        StringBuilder sb = new StringBuilder();
        sb.append("SISTEMA FIGUEIRA\n");
        sb.append("Dirección: ").append(direccion);
        sb.append("Teléfono: ").append(telefono);

        sb.append("Nombre: ").append(nombreCliente).append("\n");
        sb.append("Fecha: ").append(new SimpleDateFormat("dd/MM/yyyy").format(new Date())).append("\n");
        sb.append("Cédula: ").append(cedulaCliente).append("\n\n");

        sb.append(String.format("%-12s%5s%7s%8s\n", "Producto", "Cant", "P.U.", "Total"));
        sb.append("-------------------------------------------------\n");

        int colPrecio;
        switch (monedaSeleccionada) {
            case "Precio en $":
                colPrecio = 1;
                break;
            case "Precio en Bs":
                colPrecio = 2;
                break;
            case "Precio $ BCV":
                colPrecio = 3;
                break;
            default:
                colPrecio = 1;
                break;
        }

        String simbolo = monedaSeleccionada.contains("$") ? "$" : "Bs.";

        // Recorrer filas de la tabla
        for (int i = 0; i < tablaVentas.getRowCount(); i++) {
            String producto = tablaVentas.getValueAt(i, 0).toString();
            String cantidad = tablaVentas.getValueAt(i, 4).toString();
            String precio = tablaVentas.getValueAt(i, colPrecio).toString();
            String total = tablaVentas.getValueAt(i, 5).toString();

            sb.append(String.format("%-12s%5s%7s%8s\n", acortar(producto, 12), cantidad, simbolo + precio, simbolo + total));
        }

        sb.append("Total $: ").append(totalDolares).append("\n");
        sb.append("Total Bs: ").append(totalBolivares).append("\n");
        sb.append("Moneda: ").append(monedaSeleccionada).append("\n\n");

        // Actualizar el JTextArea con el texto del preview
        txtPreview.setText(sb.toString());
    }

    // Método para acortar el nombre de los productos a 12 caracteres (si es necesario)
    private String acortar(String texto, int longitud) {
        return texto.length() > longitud ? texto.substring(0, longitud - 1) + "." : texto;
    }
}
