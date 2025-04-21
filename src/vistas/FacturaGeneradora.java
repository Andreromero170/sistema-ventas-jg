/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vistas;



import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import java.awt.Desktop;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.swing.*;
import javax.swing.table.TableModel;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FacturaGeneradora {

    public boolean esImpresoraTermica() {
        PrintService defaultService = PrintServiceLookup.lookupDefaultPrintService();
        if (defaultService != null) {
            String nombre = defaultService.getName().toLowerCase();
            return nombre.contains("termica") || nombre.contains("pos") || nombre.contains("58mm") || nombre.contains("80mm");
        }
        return false;
    }

    
       public void imprimirPDF(String nombreArchivo) {
        try {
            File archivo = new File(nombreArchivo);
            if (archivo.exists()) {
                if (Desktop.isDesktopSupported()) {
                    Desktop.getDesktop().print(archivo);  // Esto usa la impresora predeterminada
                } else {
                    JOptionPane.showMessageDialog(null, "La impresión no está soportada en este sistema.");
                }
            } else {
                JOptionPane.showMessageDialog(null, "El archivo no existe.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error al imprimir: " + e.getMessage());
        }
    }
    
    public void generarFactura(JTable tablaVentas, String nombreCliente, String cedulaCliente, String totalBolivares, String monedaSeleccionada, String rutaImagenLogo) {
    try {
        // Ruta de la carpeta donde se guardará el PDF
        String rutaCarpeta = "C:\\Facturas";
        File carpeta = new File(rutaCarpeta);
        if (!carpeta.exists()) carpeta.mkdirs();

        // Nombre del archivo PDF con fecha
        String fechaArchivo = new SimpleDateFormat("dd-MM-yyyy_HH-mm-ss").format(new Date());
        String nombreArchivo = rutaCarpeta + "\\Factura_" + fechaArchivo + ".pdf";

        // Calcular tamaño de página según cantidad de productos
        TableModel modelo = tablaVentas.getModel();
        int filas = modelo.getRowCount();
        boolean esTermica = esImpresoraTermica();
        Rectangle pageSize = esTermica
                ? new Rectangle(180, 400 + (filas * 15))  // tamaño para térmica 58mm
                : new Rectangle(226, 400 + (filas * 15)); // tamaño estándar

        Document documento = new Document(pageSize);
        PdfWriter.getInstance(documento, new FileOutputStream(nombreArchivo));
        documento.open();

        // Fuentes
        Font titulo = new Font(Font.FontFamily.HELVETICA, esTermica ? 11 : 14, Font.BOLD);
        Font texto = new Font(Font.FontFamily.HELVETICA, esTermica ? 7 : 9);
        Font encabezadoNegrita = new Font(Font.FontFamily.HELVETICA, esTermica ? 7 : 8, Font.BOLD);

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

        Paragraph rif = new Paragraph("RIF: 411652350", texto);
        rif.setAlignment(Element.ALIGN_CENTER);
        documento.add(rif);

        Paragraph direccion = new Paragraph("Dirección: Caracas - La Vega", texto);
        direccion.setAlignment(Element.ALIGN_CENTER);
        documento.add(direccion);

        documento.add(Chunk.NEWLINE);

        // Nombre y fecha
        PdfPTable tablaNombreFecha = new PdfPTable(2);
        tablaNombreFecha.setWidthPercentage(100);
        tablaNombreFecha.setSpacingBefore(5f);
        PdfPCell celdaNombre = new PdfPCell(new Phrase("Nombre: " + nombreCliente, texto));
        PdfPCell celdaFecha = new PdfPCell(new Phrase("Fecha: " + new SimpleDateFormat("dd/MM/yyyy").format(new Date()), texto));
        celdaNombre.setBorder(Rectangle.NO_BORDER);
        celdaFecha.setBorder(Rectangle.NO_BORDER);
        tablaNombreFecha.addCell(celdaNombre);
        tablaNombreFecha.addCell(celdaFecha);

        PdfPCell celdaCedula = new PdfPCell(new Phrase("Cédula: " + cedulaCliente, texto));
        celdaCedula.setColspan(2);
        celdaCedula.setBorder(Rectangle.NO_BORDER);
        tablaNombreFecha.addCell(celdaCedula);

        documento.add(tablaNombreFecha);
        documento.add(Chunk.NEWLINE);

        // Tabla productos
        PdfPTable tablaProductos = new PdfPTable(4);
        tablaProductos.setWidthPercentage(100);
        float[] anchos = {3f, 1.5f, 2f, 2f};
        tablaProductos.setWidths(anchos);

        String[] encabezados = {"PRODUCTO", "CANT", "P.UNIT", "TOTAL"};
        for (String encabezado : encabezados) {
            PdfPCell celda = new PdfPCell(new Phrase(encabezado, encabezadoNegrita));
            celda.setHorizontalAlignment(Element.ALIGN_CENTER);
            celda.setBorder(Rectangle.BOTTOM);
            tablaProductos.addCell(celda);
        }

        // Determinar columna de precio según moneda seleccionada
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

            prod.setBorder(Rectangle.NO_BORDER);
            cant.setBorder(Rectangle.NO_BORDER);
            punit.setBorder(Rectangle.NO_BORDER);
            tot.setBorder(Rectangle.NO_BORDER);

            tablaProductos.addCell(prod);
            tablaProductos.addCell(cant);
            tablaProductos.addCell(punit);
            tablaProductos.addCell(tot);
        }

        documento.add(tablaProductos);
        documento.add(Chunk.NEWLINE);

        // Línea divisoria centrada
        Paragraph lineaDivisoria = new Paragraph("--------------------------------------------------", texto);
        lineaDivisoria.setAlignment(Element.ALIGN_CENTER);
        documento.add(lineaDivisoria);

        // Mostrar total correctamente formateado
        String simbolo = monedaSeleccionada.contains("Bs") ? "Bs. " : "$ ";
        Paragraph totalFinal = new Paragraph("Total: " + totalBolivares, encabezadoNegrita);
        totalFinal.setAlignment(Element.ALIGN_RIGHT);
        documento.add(totalFinal);

        documento.close();

        // Verificar si el archivo fue creado correctamente
        File archivoGenerado = new File(nombreArchivo);
        if (archivoGenerado.exists()) {
            // Abrir automáticamente el PDF generado
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().open(archivoGenerado);
            }

            // Mostrar mensaje de éxito
            JOptionPane.showMessageDialog(null, "✅ Factura generada como: " + nombreArchivo);
        } else {
            JOptionPane.showMessageDialog(null, "❌ Error al generar el archivo PDF.");
        }


    } catch (Exception e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(null, "❌ Error al generar la factura: " + e.getMessage());
    }
}

 
}
