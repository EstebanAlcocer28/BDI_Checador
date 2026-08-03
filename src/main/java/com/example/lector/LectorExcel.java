package com.example.lector;

import com.example.modelo.Registro;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

public class LectorExcel {

    public List<Registro> leer(String rutaArchivo) throws IOException {
        List<Registro> registros = new ArrayList<>();

        try (FileInputStream fis = new FileInputStream(rutaArchivo);
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheetAt(0);

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                String nombre          = getStringValue(row, 0);
                int jobNo              = getIntValue(row, 1);
                String tarjeta         = getStringValue(row, 2);
                LocalDate fecha        = getDateValue(row, 3);
                LocalTime hora         = getTimeValue(row, 4);
                String inOut           = getStringValue(row, 5);
                int readId             = getIntValue(row, 6);
                int eventMainCode      = getIntValue(row, 7);
                int eventSubCode       = getIntValue(row, 8);
                String status          = getStringValue(row, 9);
                int serialNo           = getIntValue(row, 10);

                if ("NULL".equalsIgnoreCase(tarjeta)) {
                    tarjeta = null;
                }

                Registro registro = new Registro(
                    nombre, jobNo, tarjeta, fecha, hora,
                    inOut, readId, eventMainCode, eventSubCode,
                    status, serialNo
                );
                registros.add(registro);
            }
        }
        return registros;
    }

    // --- Métodos auxiliares ---

    private String getStringValue(Row row, int index) {
        Cell cell = row.getCell(index);
        if (cell == null) return null;
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue().trim();
            case NUMERIC:
                // Si es numérico y no es fecha, devolver el número como texto sin decimales
                if (DateUtil.isCellDateFormatted(cell)) {
                    // Es fecha, pero aquí lo tratamos en getDateValue/ getTimeValue
                    return cell.getLocalDateTimeCellValue().toString(); // nunca se llama para fecha/hora
                }
                return String.valueOf((int) cell.getNumericCellValue());
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                return cell.getCellFormula();
            default:
                return null;
        }
    }

    private int getIntValue(Row row, int index) {
        Cell cell = row.getCell(index);
        if (cell == null) return 0;
        if (cell.getCellType() == CellType.NUMERIC) {
            return (int) cell.getNumericCellValue();
        } else if (cell.getCellType() == CellType.STRING) {
            try {
                return Integer.parseInt(cell.getStringCellValue().trim());
            } catch (NumberFormatException e) {
                return 0;
            }
        }
        return 0;
    }

    private LocalDate getDateValue(Row row, int index) {
        Cell cell = row.getCell(index);
        if (cell == null) return null;
        try {
            if (cell.getCellType() == CellType.NUMERIC && DateUtil.isCellDateFormatted(cell)) {
                return cell.getLocalDateTimeCellValue().toLocalDate();
            } else if (cell.getCellType() == CellType.STRING) {
                String str = cell.getStringCellValue().trim();
                if (str.isEmpty()) return null;
                // Asumimos formato dd/MM/yyyy
                String[] partes = str.split("/");
                if (partes.length == 3) {
                    int dia = Integer.parseInt(partes[0]);
                    int mes = Integer.parseInt(partes[1]);
                    int anio = Integer.parseInt(partes[2]);
                    return LocalDate.of(anio, mes, dia);
                }
            }
        } catch (Exception e) { /* retorna null */ }
        return null;
    }

    private LocalTime getTimeValue(Row row, int index) {
        Cell cell = row.getCell(index);
        if (cell == null) return null;
        try {
            if (cell.getCellType() == CellType.NUMERIC && DateUtil.isCellDateFormatted(cell)) {
                return cell.getLocalDateTimeCellValue().toLocalTime();
            } else if (cell.getCellType() == CellType.STRING) {
                String str = cell.getStringCellValue().trim();
                if (str.isEmpty()) return null;
                // Formato HH:mm:ss
                String[] partes = str.split(":");
                if (partes.length == 3) {
                    int hora = Integer.parseInt(partes[0]);
                    int min = Integer.parseInt(partes[1]);
                    int seg = Integer.parseInt(partes[2]);
                    return LocalTime.of(hora, min, seg);
                }
            }
        } catch (Exception e) { }
        return null;
    }
}