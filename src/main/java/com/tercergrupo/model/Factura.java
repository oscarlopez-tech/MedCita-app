package com.tercergrupo.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public record Factura(long id, long citaId, String paciente, String doctorCorreo,
        LocalDate fechaCita, LocalDate fechaEmision, String concepto,
        long totalCentavos, long pagadoCentavos) {

    public BigDecimal total() {
        return BigDecimal.valueOf(totalCentavos, 2);
    }

    public BigDecimal pagado() {
        return BigDecimal.valueOf(pagadoCentavos, 2);
    }

    public BigDecimal saldo() {
        return BigDecimal.valueOf(totalCentavos - pagadoCentavos, 2);
    }

    // Se calcula a partir del saldo para que el estado y los montos siempre coincidan.
    public EstadoCuenta estado() {
        return totalCentavos == pagadoCentavos ? EstadoCuenta.PAGADO : EstadoCuenta.PENDIENTE;
    }
}
