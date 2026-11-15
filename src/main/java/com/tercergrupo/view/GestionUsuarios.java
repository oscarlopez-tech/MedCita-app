package com.tercergrupo.view;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.sql.SQLException;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import com.tercergrupo.controller.UsuariosController;
import com.tercergrupo.model.RolUsuario;
import com.tercergrupo.model.User;
import com.tercergrupo.model.UserSession;

public class GestionUsuarios extends JPanel {
    private static final String TODOS = "Todos los usuarios";
    private static final String SOLICITUDES = "Solicitudes (sin acceso)";

    private final UsuariosController controller = new UsuariosController();
    private final DefaultTableModel modelo = new DefaultTableModel(
            new Object[]{"Nombre", "Correo", "Teléfono", "Rol", "Estado"}, 0) {
        @Override
        public boolean isCellEditable(int fila, int columna) {
            return false;
        }
    };
    private final JTable tabla = new JTable(modelo);
    private final JComboBox<String> filtro = new JComboBox<>(new String[]{TODOS, SOLICITUDES});
    private final JComboBox<RolUsuario> nuevoRol = new JComboBox<>(RolUsuario.values());
    private final JButton asignarRol = EstiloMedCita.boton("Asignar rol", true);
    private final JButton bloquear = EstiloMedCita.boton("Bloquear", false);
    private final JButton desbloquear = EstiloMedCita.boton("Desbloquear", false);
    private final JButton eliminar = EstiloMedCita.boton("Eliminar", false);
    private List<User> usuarios = List.of();

    public GestionUsuarios() {
        setLayout(new BorderLayout(0, 12));
        setBackground(EstiloMedCita.FONDO);
        setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        JPanel superior = new JPanel(new FlowLayout(FlowLayout.LEFT));
        superior.setOpaque(false);
        superior.add(new JLabel("Mostrar:"));
        superior.add(filtro);

        JPanel acciones = new JPanel(new FlowLayout(FlowLayout.LEFT));
        acciones.setOpaque(false);
        acciones.add(nuevoRol);
        acciones.add(asignarRol);
        acciones.add(bloquear);
        acciones.add(desbloquear);
        acciones.add(eliminar);

        add(superior, BorderLayout.NORTH);
        add(new JScrollPane(tabla), BorderLayout.CENTER);
        add(acciones, BorderLayout.SOUTH);

        filtro.addActionListener(event -> cargarUsuarios());
        asignarRol.addActionListener(event -> asignarRolSeleccionado());
        bloquear.addActionListener(event -> cambiarEstadoSeleccionado(false));
        desbloquear.addActionListener(event -> cambiarEstadoSeleccionado(true));
        eliminar.addActionListener(event -> eliminarSeleccionado());

        cargarUsuarios();
    }

    private void cargarUsuarios() {
        try {
            User admin = UserSession.getInstance().getLoggedInUser();
            usuarios = SOLICITUDES.equals(filtro.getSelectedItem())
                    ? controller.listarSolicitudes(admin)
                    : controller.listarUsuarios(admin);
            modelo.setRowCount(0);
            for (User usuario : usuarios) {
                modelo.addRow(new Object[]{
                    usuario.getNombre(),
                    usuario.getCorreoElectronico(),
                    usuario.getTelefono(),
                    usuario.getRol(),
                    usuario.isActivo() ? "Activo" : "Bloqueado"
                });
            }
        } catch (SQLException | RuntimeException error) {
            mostrarError(error);
        }
    }

    private void asignarRolSeleccionado() {
        try {
            User seleccionado = usuarioSeleccionado();
            controller.asignarRol(UserSession.getInstance().getLoggedInUser(),
                    seleccionado.getCorreoElectronico(), (RolUsuario) nuevoRol.getSelectedItem());
            cargarUsuarios();
        } catch (SQLException | RuntimeException error) {
            mostrarError(error);
        }
    }

    private void cambiarEstadoSeleccionado(boolean activo) {
        try {
            User seleccionado = usuarioSeleccionado();
            controller.cambiarEstado(UserSession.getInstance().getLoggedInUser(),
                    seleccionado.getCorreoElectronico(), activo);
            cargarUsuarios();
        } catch (SQLException | RuntimeException error) {
            mostrarError(error);
        }
    }

    private void eliminarSeleccionado() {
        try {
            User seleccionado = usuarioSeleccionado();
            int respuesta = JOptionPane.showConfirmDialog(this,
                    "¿Eliminar a " + seleccionado.getNombre() + "? También se borrarán sus citas.",
                    "MedCita", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (respuesta == JOptionPane.YES_OPTION) {
                controller.eliminarUsuario(UserSession.getInstance().getLoggedInUser(),
                        seleccionado.getCorreoElectronico());
                cargarUsuarios();
            }
        } catch (SQLException | RuntimeException error) {
            mostrarError(error);
        }
    }

    private User usuarioSeleccionado() {
        int fila = tabla.getSelectedRow();
        if (fila < 0) {
            throw new IllegalArgumentException("Selecciona un usuario de la lista.");
        }
        return usuarios.get(fila);
    }

    private void mostrarError(Exception error) {
        JOptionPane.showMessageDialog(this, error.getMessage(), "MedCita", JOptionPane.ERROR_MESSAGE);
    }
}
