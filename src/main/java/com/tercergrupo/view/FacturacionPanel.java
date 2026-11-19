package com.tercergrupo.view;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

import com.tercergrupo.controller.FacturacionController;
import com.tercergrupo.model.Cita;
import com.tercergrupo.model.EstadoCuenta;
import com.tercergrupo.model.Factura;
import com.tercergrupo.model.User;
import com.tercergrupo.model.UserSession;

public class FacturacionPanel extends JPanel {
    private final FacturacionController controller = new FacturacionController();
    private final DefaultTableModel modeloCitas = modelo("Cita", "Paciente", "Atención", "Fecha", "Hora", "Doctor");
    private final DefaultTableModel modeloFacturas = modelo("Factura", "Paciente", "Saldo CRC", "Estado",
            "Total CRC", "Pagado CRC", "Cita", "Doctor", "Fecha cita", "Emisión", "Concepto");
    private final JTable tablaCitas = new JTable(modeloCitas);
    private final JTable tablaFacturas = new JTable(modeloFacturas);
    private final JComboBox<String> filtro = new JComboBox<>(new String[]{"Todas", "Pendientes", "Pagadas"});
    private final JLabel resumen = new JLabel("Importes en colones (CRC).");
    private final JButton volver = EstiloMedCita.boton("Volver al inicio", false);
    private List<Cita> citas = List.of();
    private List<Factura> facturas = List.of();

    public FacturacionPanel() {
        setLayout(new BorderLayout(0, 12));
        setBackground(EstiloMedCita.FONDO);
        setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        JLabel titulo = new JLabel("Facturación y Cuentas por cobrar");
        titulo.setFont(titulo.getFont().deriveFont(java.awt.Font.BOLD, 18));
        add(titulo, BorderLayout.NORTH);

        JButton atender = EstiloMedCita.boton("Confirmar atención", false);
        JButton generar = EstiloMedCita.boton("Generar factura", true);
        JButton pagar = EstiloMedCita.boton("Registrar pago", true);
        JButton actualizar = EstiloMedCita.boton("Actualizar", false);

        JPanel citasPanel = new JPanel(new BorderLayout(0, 10));
        citasPanel.setOpaque(false);
        citasPanel.add(new JLabel("<html>Confirma la atención y luego genera la factura<br>de la cita seleccionada.</html>"), BorderLayout.NORTH);
        citasPanel.add(tablaConScroll(tablaCitas), BorderLayout.CENTER);
        JPanel accionesCitas = new JPanel(new GridLayout(0, 2, 8, 8));
        accionesCitas.setOpaque(false);
        accionesCitas.add(atender);
        accionesCitas.add(generar);
        citasPanel.add(accionesCitas, BorderLayout.SOUTH);

        JPanel cuentasPanel = new JPanel(new BorderLayout(0, 10));
        cuentasPanel.setOpaque(false);
        JPanel superior = new JPanel(new GridLayout(0, 1, 0, 8));
        superior.setOpaque(false);
        superior.add(filtro);
        superior.add(resumen);
        cuentasPanel.add(superior, BorderLayout.NORTH);
        cuentasPanel.add(tablaConScroll(tablaFacturas), BorderLayout.CENTER);
        cuentasPanel.add(pagar, BorderLayout.SOUTH);

        JTabbedPane pestanas = new JTabbedPane();
        pestanas.addTab("Citas sin facturar", citasPanel);
        pestanas.addTab("Cuentas por cobrar", cuentasPanel);
        add(pestanas, BorderLayout.CENTER);
        JPanel acciones = new JPanel(new GridLayout(0, 2, 8, 8));
        acciones.setOpaque(false);
        acciones.add(actualizar);
        acciones.add(volver);
        add(acciones, BorderLayout.SOUTH);

        atender.addActionListener(event -> confirmarAtencion());
        generar.addActionListener(event -> generarFactura());
        pagar.addActionListener(event -> registrarPago());
        actualizar.addActionListener(event -> cargarDatos());
        filtro.addActionListener(event -> cargarDatos());
    }

    private DefaultTableModel modelo(String... columnas) {
        return new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int fila, int columna) {
                return false;
            }
        };
    }

    private JScrollPane tablaConScroll(JTable tabla) {
        tabla.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tabla.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        for (int i = 0; i < tabla.getColumnCount(); i++) {
            tabla.getColumnModel().getColumn(i).setPreferredWidth(i == 0 ? 60 : i == 1 ? 140 : 110);
        }
        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setColumnHeaderView(tabla.getTableHeader());
        return scroll;
    }

    public void cargarDatos() {
        // Limpiar primero evita mostrar información de una sesión anterior si falla la consulta.
        modeloCitas.setRowCount(0);
        modeloFacturas.setRowCount(0);
        citas = List.of();
        facturas = new ArrayList<>();
        resumen.setText("Importes en colones (CRC).");
        try {
            User usuario = usuarioActual();
            citas = controller.listarCitasSinFactura(usuario);
            for (Cita cita : citas) {
                modeloCitas.addRow(new Object[]{cita.id(), cita.paciente(),
                    controller.estaAtendida(usuario, cita.id()) ? "Atendida" : "Por confirmar",
                    cita.fecha(), cita.hora(), cita.doctorCorreo()});
            }
            BigDecimal saldoTotal = BigDecimal.ZERO;
            for (Factura factura : controller.listarFacturas(usuario)) {
                saldoTotal = saldoTotal.add(factura.saldo());
                if ("Pendientes".equals(filtro.getSelectedItem()) && factura.estado() != EstadoCuenta.PENDIENTE
                        || "Pagadas".equals(filtro.getSelectedItem()) && factura.estado() != EstadoCuenta.PAGADO) {
                    continue;
                }
                facturas.add(factura);
                modeloFacturas.addRow(new Object[]{factura.id(), factura.paciente(), factura.saldo(), factura.estado(),
                    factura.total(), factura.pagado(), factura.citaId(), factura.doctorCorreo(),
                    factura.fechaCita(), factura.fechaEmision(), factura.concepto()});
            }
            resumen.setText("Saldo pendiente de todas las cuentas: CRC " + saldoTotal.setScale(2));
        } catch (SQLException | RuntimeException error) {
            mostrarError(error);
        }
    }

    private void confirmarAtencion() {
        try {
            Cita cita = citaSeleccionada();
            int respuesta = JOptionPane.showConfirmDialog(this,
                    "¿Confirmas que el paciente " + cita.paciente() + " fue atendido? La cita quedará sin edición.",
                    "MedCita", JOptionPane.YES_NO_OPTION);
            if (respuesta == JOptionPane.YES_OPTION) {
                controller.marcarAtendida(usuarioActual(), cita.id());
                cargarDatos();
            }
        } catch (SQLException | RuntimeException error) {
            mostrarError(error);
        }
    }

    private void generarFactura() {
        try {
            Cita cita = citaSeleccionada();
            if (!controller.estaAtendida(usuarioActual(), cita.id())) {
                throw new IllegalArgumentException("Primero confirma que la cita fue atendida.");
            }
            new FormularioFactura(SwingUtilities.getWindowAncestor(this), usuarioActual(), cita).setVisible(true);
            cargarDatos();
        } catch (SQLException | RuntimeException error) {
            mostrarError(error);
        }
    }

    private void registrarPago() {
        try {
            int fila = tablaFacturas.getSelectedRow();
            if (fila < 0) {
                throw new IllegalArgumentException("Selecciona una factura de la lista.");
            }
            Factura factura = facturas.get(fila);
            if (factura.estado() == EstadoCuenta.PAGADO) {
                throw new IllegalArgumentException("La factura ya está pagada.");
            }
            String entrada = JOptionPane.showInputDialog(this,
                    "Factura #" + factura.id() + " - " + factura.paciente()
                    + "\nSaldo: CRC " + factura.saldo() + "\nMonto recibido (sin separadores de miles):",
                    factura.saldo().toPlainString());
            if (entrada == null) {
                return;
            }
            BigDecimal monto = new BigDecimal(entrada.trim().replace(',', '.'));
            int respuesta = JOptionPane.showConfirmDialog(this,
                    "¿Registrar un pago de CRC " + monto.toPlainString() + " en la factura #" + factura.id() + "?",
                    "MedCita", JOptionPane.YES_NO_OPTION);
            if (respuesta == JOptionPane.YES_OPTION) {
                controller.registrarPago(usuarioActual(), factura.id(), monto);
                cargarDatos();
            }
        } catch (NumberFormatException error) {
            mostrarError(new IllegalArgumentException("Ingresa un monto válido sin separadores de miles."));
        } catch (SQLException | RuntimeException error) {
            mostrarError(error);
        }
    }

    private Cita citaSeleccionada() {
        int fila = tablaCitas.getSelectedRow();
        if (fila < 0) {
            throw new IllegalArgumentException("Selecciona una cita de la lista.");
        }
        return citas.get(fila);
    }

    private User usuarioActual() {
        return UserSession.getInstance().getLoggedInUser();
    }

    public JButton getBotonVolver() {
        return volver;
    }

    private void mostrarError(Exception error) {
        JOptionPane.showMessageDialog(this, error.getMessage(), "MedCita", JOptionPane.ERROR_MESSAGE);
    }
}
