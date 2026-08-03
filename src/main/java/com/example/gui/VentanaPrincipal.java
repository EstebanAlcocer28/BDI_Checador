package com.example.gui;
import java.awt.FlowLayout;
import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Component;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.List;
import javax.swing.table.DefaultTableModel;

import com.example.buscador.Buscador;
import com.example.lector.LectorExcel;
import com.example.modelo.Registro;

public class VentanaPrincipal extends JFrame {
    private List<Registro> todosLosRegistros;
    private JTable tabla;
    private DefaultTableModel modeloTabla;
    private JLabel etiquetaEstado;

    // Componentes del panel de búsqueda (los declaramos como atributos para acceder luego)
    private JTextField campoNombre;
    private JTextField campoJobNo;
    private JTextField campoFechaDesde;
    private JTextField campoFechaHasta;
    private JComboBox<String> comboEstado;
    private JButton btnBuscar;
    private JButton btnCargar;
    private Buscador buscador;

    public VentanaPrincipal() {
        setTitle("Buscador de Asistencia");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 650);
        setLocationRelativeTo(null);  // centrar en pantalla
        setLayout(new BorderLayout(10, 10));

        // -------------------- Barra superior con botón Cargar --------------------
        JPanel panelSuperior = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        btnCargar = new JButton("Cargar Excel...");
        btnCargar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        etiquetaEstado = new JLabel("Ningún archivo cargado");
        etiquetaEstado.setForeground(Color.GRAY);
        panelSuperior.add(btnCargar);
        panelSuperior.add(Box.createHorizontalStrut(20));
        panelSuperior.add(etiquetaEstado);
        add(panelSuperior, BorderLayout.NORTH);

        // -------------------- Panel de búsqueda --------------------
        JPanel panelBusqueda = crearPanelBusqueda();
        add(panelBusqueda, BorderLayout.WEST);   // lo ponemos a la izquierda

        // -------------------- Tabla de resultados --------------------
        modeloTabla = new DefaultTableModel(
            new String[]{"Nombre","JobNo","Tarjeta","Fecha","Hora","IN/OUT","ReadID","Estado"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;  // que no se pueda editar desde la tabla
            }
        };
        tabla = new JTable(modeloTabla);
        tabla.setAutoCreateRowSorter(true);  // permite ordenar al hacer clic en la cabecera
        JScrollPane scrollTabla = new JScrollPane(tabla);
        add(scrollTabla, BorderLayout.CENTER);

        // -------------------- Eventos iniciales --------------------
        btnCargar.addActionListener(this::accionCargar);
        btnBuscar.addActionListener(e -> ejecutarBusqueda());
    }

    private JPanel crearPanelBusqueda() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createTitledBorder("Criterios de búsqueda"));
        panel.setPreferredSize(new Dimension(280, 300));

        // Nombre
        panel.add(new JLabel("Nombre contiene:"));
        campoNombre = new JTextField();
        campoNombre.setMaximumSize(new Dimension(Integer.MAX_VALUE, 25));
        panel.add(campoNombre);
        panel.add(Box.createVerticalStrut(8));

        // Número de empleado
        panel.add(new JLabel("JobNo (exacto):"));
        campoJobNo = new JTextField();
        campoJobNo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 25));
        panel.add(campoJobNo);
        panel.add(Box.createVerticalStrut(8));

        // Fecha desde
        panel.add(new JLabel("Fecha desde (dd/mm/aaaa):"));
        campoFechaDesde = new JTextField();
        campoFechaDesde.setMaximumSize(new Dimension(Integer.MAX_VALUE, 25));
        panel.add(campoFechaDesde);
        panel.add(Box.createVerticalStrut(8));

        // Fecha hasta
        panel.add(new JLabel("Fecha hasta (dd/mm/aaaa):"));
        campoFechaHasta = new JTextField();
        campoFechaHasta.setMaximumSize(new Dimension(Integer.MAX_VALUE, 25));
        panel.add(campoFechaHasta);
        panel.add(Box.createVerticalStrut(8));

        // Estado
        panel.add(new JLabel("Estado:"));
        comboEstado = new JComboBox<>(new String[]{"Todos", "checkIn", "checkOut", "undefined"});
        comboEstado.setMaximumSize(new Dimension(Integer.MAX_VALUE, 25));
        panel.add(comboEstado);
        panel.add(Box.createVerticalStrut(15));

        // Botón Buscar
        btnBuscar = new JButton("Buscar");
        btnBuscar.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(btnBuscar);

        // Separador y botón para limpiar filtros (opcional)
        panel.add(Box.createVerticalStrut(10));
        JButton btnLimpiar = new JButton("Limpiar filtros");
        btnLimpiar.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnLimpiar.addActionListener(e -> limpiarCampos());
        panel.add(btnLimpiar);

        return panel;
    }

    private void accionCargar(ActionEvent e) {
    JFileChooser fileChooser = new JFileChooser();
    fileChooser.setDialogTitle("Seleccionar archivo Excel");
    int resultado = fileChooser.showOpenDialog(this);
    if (resultado == JFileChooser.APPROVE_OPTION) {
        String ruta = fileChooser.getSelectedFile().getAbsolutePath();
        try {
            LectorExcel lector = new LectorExcel();
            todosLosRegistros = lector.leer(ruta);
            actualizarTablaCompleta();
            etiquetaEstado.setText(todosLosRegistros.size() + " registros cargados.");
            todosLosRegistros = lector.leer(ruta);
buscador = new Buscador(todosLosRegistros);
actualizarTablaCompleta();
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, 
                "Error al leer el archivo:\n" + ex.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

}

    private void ejecutarBusqueda() {
    if (todosLosRegistros == null || buscador == null) {
        JOptionPane.showMessageDialog(this, "Primero debe cargar un archivo Excel.");
        return;
    }

    String nombre = campoNombre.getText().trim();
    String jobNo = campoJobNo.getText().trim();
    String fechaDesde = campoFechaDesde.getText().trim();
    String fechaHasta = campoFechaHasta.getText().trim();
    String estado = (String) comboEstado.getSelectedItem();

    List<Registro> resultados = buscador.buscar(nombre, jobNo, fechaDesde, fechaHasta, estado);

    // Mostrar resultados en la tabla
    modeloTabla.setRowCount(0);
    for (Registro r : resultados) {
        modeloTabla.addRow(r.toTableRow());
    }
    etiquetaEstado.setText(resultados.size() + " resultados encontrados.");
}

    private void limpiarCampos() {
        campoNombre.setText("");
        campoJobNo.setText("");
        campoFechaDesde.setText("");
        campoFechaHasta.setText("");
        comboEstado.setSelectedIndex(0);
    }

    // Métodos auxiliares que completaremos después
    private void actualizarTablaCompleta() {
        modeloTabla.setRowCount(0);
        for (Registro r : todosLosRegistros) {
            modeloTabla.addRow(r.toTableRow());
        }
    }
}
