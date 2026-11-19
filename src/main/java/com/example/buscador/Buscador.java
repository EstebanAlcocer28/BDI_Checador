package com.example.buscador;

import com.example.modelo.Registro;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class Buscador {
    private List<Registro> registros;

    public Buscador(List<Registro> registros) {
        this.registros = registros;
    }

    /**
     * Filtra los registros según los criterios especificados.
     * Cualquier parámetro vacío o nulo se ignora.
     *
     * Fechas: formato dd/MM/yyyy.
     * Horas:  formato H:mm o H:mm:ss (los segundos son opcionales).
     */
    public List<Registro> buscar(String nombreParcial, String jobNoStr,
                                 String fechaDesdeStr, String fechaHastaStr,
                                 String horaDesdeStr, String horaHastaStr,
                                 String estado) {

        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("H:mm[:ss]");

        // Parsear jobNo
        Integer jobNo = null;
        if (jobNoStr != null && !jobNoStr.trim().isEmpty()) {
            try {
                jobNo = Integer.parseInt(jobNoStr.trim());
            } catch (NumberFormatException e) { }
        }

        // Parsear fechas
        LocalDate fechaDesde = null;
        if (fechaDesdeStr != null && !fechaDesdeStr.trim().isEmpty()) {
            try {
                fechaDesde = LocalDate.parse(fechaDesdeStr.trim(), dateFormat);
            } catch (Exception e) { }
        }
        LocalDate fechaHasta = null;
        if (fechaHastaStr != null && !fechaHastaStr.trim().isEmpty()) {
            try {
                fechaHasta = LocalDate.parse(fechaHastaStr.trim(), dateFormat);
            } catch (Exception e) { }
        }

        // Parsear horas  (NUEVO)
        LocalTime horaDesde = null;
        if (horaDesdeStr != null && !horaDesdeStr.trim().isEmpty()) {
            try {
                horaDesde = LocalTime.parse(horaDesdeStr.trim(), timeFormat);
            } catch (Exception e) { }
        }
        LocalTime horaHasta = null;
        if (horaHastaStr != null && !horaHastaStr.trim().isEmpty()) {
            try {
                horaHasta = LocalTime.parse(horaHastaStr.trim(), timeFormat);
            } catch (Exception e) { }
        }

        // Normalizar estado ("Todos" ⇒ sin filtro)
        String estadoFiltro = (estado == null || estado.equals("Todos")) ? null : estado.trim();

        final Integer    finalJobNo       = jobNo;
        final LocalDate  finalFechaDesde  = fechaDesde;
        final LocalDate  finalFechaHasta  = fechaHasta;
        final LocalTime  finalHoraDesde   = horaDesde;
        final LocalTime  finalHoraHasta   = horaHasta;
        final String     finalEstado      = estadoFiltro;

        return registros.stream()
            .filter(r -> {
                // Nombre contiene (case-insensitive)
                if (nombreParcial != null && !nombreParcial.trim().isEmpty()) {
                    if (r.getNombre() == null || !r.getNombre().toLowerCase()
                            .contains(nombreParcial.trim().toLowerCase())) {
                        return false;
                    }
                }
                return true;
            })
            .filter(r -> {
                // JobNo exacto
                if (finalJobNo != null && r.getJobNo() != finalJobNo) {
                    return false;
                }
                return true;
            })
            .filter(r -> {
                // Rango de fechas
                if (finalFechaDesde != null || finalFechaHasta != null) {
                    LocalDate fechaReg = r.getFecha();
                    if (fechaReg == null) return false;
                    if (finalFechaDesde != null && fechaReg.isBefore(finalFechaDesde)) return false;
                    if (finalFechaHasta != null && fechaReg.isAfter(finalFechaHasta))  return false;
                }
                return true;
            })
            .filter(r -> {
                // Rango de horas  (NUEVO)
                if (finalHoraDesde != null || finalHoraHasta != null) {
                    LocalTime horaReg = r.getHora();
                    if (horaReg == null) return false;
                    if (finalHoraDesde != null && horaReg.isBefore(finalHoraDesde)) return false;
                    if (finalHoraHasta != null && horaReg.isAfter(finalHoraHasta))  return false;
                }
                return true;
            })
            .filter(r -> {
                // Estado exacto
                if (finalEstado != null) {
                    if (r.getStatus() == null || !r.getStatus().equals(finalEstado)) {
                        return false;
                    }
                }
                return true;
            })
            .collect(Collectors.toList());
    }
}