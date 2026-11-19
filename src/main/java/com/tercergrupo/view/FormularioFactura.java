package com.tercergrupo.view;

import java.awt.Dialog.ModalityType;
import java.awt.Window;
import java.math.BigDecimal;
import java.sql.SQLException;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.tercergrupo.controller.FacturacionController;
import com.tercergrupo.model.Cita;
import com.tercergrupo.model.User;

public class FormularioFactura extends JDialog {
    private final JTextField concepto = new JTextField("Consulta médica");
    private final JTextField total = new JTextField();
    private final FacturacionController controller = new FacturacionController();

    public FormularioFactura(Window propietario, User usuario, Cita cita) {
        super(propietario, "Generar factura", ModalityType.APPLICATION_MODAL);
        JPanel panel = new JPanel();
        JPanel tarjeta = EstiloMedCita.formulario(panel, "Generar factura", "La factura se crea con saldo pendiente.");
        EstiloMedCita.aviso(tarjeta, new JLabel("Cita #" + cita.id() + " - " + cita.fecha() + " " + cita.hora()));
        JTextField paciente = new JTextField(cita.paciente());
        paciente.setEditable(false);
        EstiloMedCita.campo(tarjeta, "Paciente", paciente);
        EstiloMedCita.campo(tarjeta, "Concepto", concepto);
        EstiloMedCita.campo(tarjeta, "Total en colones (ejemplo: 25000.00)", total);
        JButton guardar = EstiloMedCita.boton("Generar factura", true);
        JButton cancelar = EstiloMedCita.boton("Cancelar", false);
        EstiloMedCita.acciones(tarjeta, guardar, cancelar);
        guardar.addActionListener(event -> {
            try {
                BigDecimal monto = new BigDecimal(total.getText().trim().replace(',', '.'));
                controller.generarFactura(usuario, cita.id(), concepto.getText(), monto);
                JOptionPane.showMessageDialog(this, "Factura creada. Puedes registrar el pago en Cuentas por cobrar.");
                dispose();
            } catch (NumberFormatException error) {
                mostrarError("Ingresa un monto válido sin separadores de miles.");
            } catch (SQLException | RuntimeException error) {
                mostrarError(error.getMessage());
            }
        });
        cancelar.addActionListener(event -> dispose());
        add(panel);
        pack();
        setLocationRelativeTo(propietario);
    }

    private void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "MedCita", JOptionPane.ERROR_MESSAGE);
    }
}
