package com.tercergrupo.controller;

import com.tercergrupo.model.Cita;
import com.tercergrupo.model.RolUsuario;
import com.tercergrupo.model.User;
import org.junit.Test;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

public class CitasControllerTest {
    private static final String DOCTOR = "doctora@medcita.test";

    @Test
    public void doctorPuedeCrearYVerSoloSusCitas() throws SQLException {
        RepositorioEnMemoria repositorio = new RepositorioEnMemoria();
        CitasController controller = new CitasController(repositorio);
        User doctor = usuario(RolUsuario.DOCTOR, DOCTOR);

        long id = controller.crearCita(doctor, cita(DOCTOR));

        assertEquals(1, id);
        assertEquals(1, controller.listarCitas(doctor).size());
    }

    @Test
    public void facturacionPuedeVerPeroNoCrearCitas() throws SQLException {
        RepositorioEnMemoria repositorio = new RepositorioEnMemoria();
        CitasController controller = new CitasController(repositorio);
        User doctor = usuario(RolUsuario.DOCTOR, DOCTOR);
        controller.crearCita(doctor, cita(DOCTOR));

        assertEquals(1, controller.listarCitas(usuario(RolUsuario.FACTURACION, "cxc@medcita.test")).size());
        assertThrows(SecurityException.class,
                () -> controller.crearCita(usuario(RolUsuario.FACTURACION, "cxc@medcita.test"), cita(DOCTOR)));
    }

    @Test
    public void doctorNoPuedeAgendarParaOtroDoctorNiVerCitasAjenas() throws SQLException {
        CitasController controller = new CitasController(new RepositorioEnMemoria());
        User doctor = usuario(RolUsuario.DOCTOR, DOCTOR);

        assertThrows(SecurityException.class,
                () -> controller.crearCita(doctor, cita("otro@medcita.test")));
    }

    @Test
    public void doctorPuedeModificarSuCitaPeroNoLaDeOtroDoctor() throws SQLException {
        RepositorioEnMemoria repositorio = new RepositorioEnMemoria();
        CitasController controller = new CitasController(repositorio);
        User administrador = usuario(RolUsuario.ADMINISTRADOR, "admin@medcita.test");
        long idAjeno = controller.crearCita(administrador, cita("otro@medcita.test"));
        long idPropio = controller.crearCita(usuario(RolUsuario.DOCTOR, DOCTOR), cita(DOCTOR));
        User doctor = usuario(RolUsuario.DOCTOR, DOCTOR);

        controller.actualizarCita(doctor,
                new Cita(idPropio, "Paciente actualizado", DOCTOR,
                        LocalDate.of(2027, 2, 1), LocalTime.of(10, 0)));
        assertThrows(SecurityException.class,
                () -> controller.actualizarCita(doctor,
                        new Cita(idAjeno, "Paciente actualizado", "otro@medcita.test",
                                LocalDate.of(2027, 2, 1), LocalTime.of(11, 0))));
    }

    @Test
    public void rolesDesconocidosNoObtienenPermisos() {
        assertEquals(RolUsuario.SIN_ACCESO, RolUsuario.desdeNombre("Recepcionista"));
        assertEquals(RolUsuario.FACTURACION, RolUsuario.desdeNombre("Facturación/CxC"));
    }

    @Test
    public void noPermiteDosCitasDelMismoDoctorEnElMismoHorario() throws SQLException {
        CitasController controller = new CitasController(new RepositorioEnMemoria());
        User doctor = usuario(RolUsuario.DOCTOR, DOCTOR);
        controller.crearCita(doctor, cita(DOCTOR));

        assertThrows(IllegalArgumentException.class, () -> controller.crearCita(doctor, cita(DOCTOR)));
    }

    private User usuario(RolUsuario rol, String correo) {
        User usuario = new User();
        usuario.setCorreoElectronico(correo);
        usuario.setRol(rol);
        return usuario;
    }

    private Cita cita(String doctor) {
        return Cita.nueva("Paciente de prueba", doctor, LocalDate.of(2027, 2, 1), LocalTime.of(9, 0));
    }

    private static class RepositorioEnMemoria implements CitaRepository {
        private final List<Cita> citas = new ArrayList<>();

        @Override
        public long crear(Cita cita) {
            Cita guardada = new Cita(citas.size() + 1L, cita.paciente(), cita.doctorCorreo(),
                    cita.fecha(), cita.hora());
            citas.add(guardada);
            return guardada.id();
        }

        @Override
        public void actualizar(Cita cita) {
            citas.removeIf(guardada -> guardada.id() == cita.id());
            citas.add(cita);
        }

        @Override
        public Optional<Cita> buscarPorId(long id) {
            return citas.stream().filter(cita -> cita.id() == id).findFirst();
        }

        @Override
        public List<Cita> listarTodas() {
            return List.copyOf(citas);
        }

        @Override
        public List<Cita> listarPorDoctor(String doctorCorreo) {
            return citas.stream()
                    .filter(cita -> cita.doctorCorreo().equalsIgnoreCase(doctorCorreo))
                    .toList();
        }

        @Override
        public boolean horarioOcupado(String doctorCorreo, Cita cita, long excluirId) {
            return citas.stream().anyMatch(guardada ->
                    guardada.id() != excluirId
                            && guardada.doctorCorreo().equalsIgnoreCase(doctorCorreo)
                            && guardada.fecha().equals(cita.fecha())
                            && guardada.hora().equals(cita.hora()));
        }
    }
}
