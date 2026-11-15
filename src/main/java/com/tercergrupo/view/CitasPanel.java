package com.tercergrupo.view;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.sql.SQLException;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

import com.tercergrupo.controller.CitasController;
import com.tercergrupo.controller.ControlAcceso;
import com.tercergrupo.model.Cita;
import com.tercergrupo.model.Permiso;
import com.tercergrupo.model.RolUsuario;
import com.tercergrupo.model.UserSession;

public class CitasPanel extends JPanel {
    private final CitasController controller = new CitasController();
    private final DefaultTableModel modelo = new DefaultTableModel(
            new Object[]{"Paciente", "Doctor", "Fecha", "Hora"}, 0) {
        @Override
        public boolean isCellEditable(int fila, int columna) {
            return false;
        }
    };
    private final JTable tabla = new JTable(modelo);
    private final JButton nuevaCita = EstiloMedCita.boton("Nueva cita", true);
    private final JButton editar = EstiloMedCita.boton("Editar", false);
    private final JButton actualizar = EstiloMedCita.boton("Actualizar", false);
    private final JButton volver = EstiloMedCita.boton("Volver al inicio", false);
    private List<Cita> citas = List.of();

    public CitasPanel() {
        setLayout(new BorderLayout(0, 12));
        setBackground(EstiloMedCita.FONDO);
        setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        JPanel acciones = new JPanel(new FlowLayout(FlowLayout.LEFT));
        acciones.setOpaque(false);
        acciones.add(nuevaCita);
        acciones.add(editar);
        acciones.add(actualizar);
        acciones.add(volver);

        add(new JScrollPane(tabla), BorderLayout.CENTER);
        add(acciones, BorderLayout.SOUTH);

        actualizar.addActionListener(event -> cargarCitas());
        nuevaCita.addActionListener(event -> {
            new FormularioCita(SwingUtilities.getWindowAncestor(this),
                    UserSession.getInstance().getLoggedInUser()).setVisible(true);
            cargarCitas();
        });
        editar.addActionListener(event -> editarSeleccionada());
    }

    public void configurarPermisos(RolUsuario rol) {
        nuevaCita.setVisible(ControlAcceso.puede(rol, Permiso.CREAR_CITA));
        editar.setVisible(ControlAcceso.puede(rol, Permiso.MODIFICAR_CITA));
    }

    public void cargarCitas() {
        try {
            citas = controller.listarCitas(UserSession.getInstance().getLoggedInUser());
            modelo.setRowCount(0);
            for (Cita cita : citas) {
                modelo.addRow(new Object[]{
                    cita.paciente(),
                    cita.doctorCorreo(),
                    cita.fecha(),
                    cita.hora()
                });
            }
        } catch (SQLException | RuntimeException error) {
            mostrarError(error);
        }
    }

    private void editarSeleccionada() {
        int fila = tabla.getSelectedRow();
        if (fila < 0) {
            mostrarError(new IllegalArgumentException("Selecciona una cita de la lista."));
            return;
        }
        new FormularioCita(SwingUtilities.getWindowAncestor(this),
                UserSession.getInstance().getLoggedInUser(), citas.get(fila)).setVisible(true);
        cargarCitas();
    }

    public JButton getBotonVolver() {
        return volver;
    }

    private void mostrarError(Exception error) {
        JOptionPane.showMessageDialog(this, error.getMessage(), "MedCita", JOptionPane.ERROR_MESSAGE);
    }
}
