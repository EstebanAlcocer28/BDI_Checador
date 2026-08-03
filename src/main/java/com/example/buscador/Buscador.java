package com.example.buscador;
import com.example.modelo.Registro;
import java.time.LocalDate;
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
     */
    public List<Registro> buscar(String nombreParcial, String jobNoStr,
                                 String fechaDesdeStr, String fechaHastaStr,
                                 String estado) {
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        // Parsear jobNo
        Integer jobNo = null;
        if (jobNoStr != null && !jobNoStr.trim().isEmpty()) {
            try {
                jobNo = Integer.parseInt(jobNoStr.trim());
            } catch (NumberFormatException e) {
                // Si no es un número válido, ignoramos este filtro
            }
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

        // Normalizar estado (si es "Todos" lo tratamos como null para ignorar)
        String estadoFiltro = (estado == null || estado.equals("Todos")) ? null : estado.trim();

        // Stream de filtros encadenados
        final Integer finalJobNo = jobNo;
        final LocalDate finalFechaDesde = fechaDesde;
        final LocalDate finalFechaHasta = fechaHasta;
        final String finalEstadoFiltro = estadoFiltro;

        return registros.stream()
            .filter(r -> {
                // Filtro por nombre (contiene, ignorando mayúsculas/minúsculas)
                if (nombreParcial != null && !nombreParcial.trim().isEmpty()) {
                    if (r.getNombre() == null || !r.getNombre().toLowerCase()
                            .contains(nombreParcial.trim().toLowerCase())) {
                        return false;
                    }
                }
                return true;
            })
            .filter(r -> {
                // Filtro por jobNo (exacto)
                if (finalJobNo != null) {
                    if (r.getJobNo() != finalJobNo) {
                        return false;
                    }
                }
                return true;
            })
            .filter(r -> {
                // Filtro por rango de fecha
                if (finalFechaDesde != null || finalFechaHasta != null) {
                    LocalDate fechaReg = r.getFecha();
                    if (fechaReg == null) return false;  // si no tiene fecha, no pasa
                    if (finalFechaDesde != null && fechaReg.isBefore(finalFechaDesde)) {
                        return false;
                    }
                    if (finalFechaHasta != null && fechaReg.isAfter(finalFechaHasta)) {
                        return false;
                    }
                }
                return true;
            })
            .filter(r -> {
                // Filtro por estado (exacto)
                if (finalEstadoFiltro != null) {
                    if (r.getStatus() == null || !r.getStatus().equals(finalEstadoFiltro)) {
                        return false;
                    }
                }
                return true;
            })
            .collect(Collectors.toList());
    }
}