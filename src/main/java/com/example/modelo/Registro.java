package com.example.modelo;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class Registro {
    private String nombre;
    private int jobNo;
    private String tarjeta;
    private LocalDate fecha;
    private LocalTime hora;
    private String inOut;
    private int readId;
    private int eventMainCode;
    private int eventSubCode;
    private String status;
    private int serialNo;

    // Constructor completo
    public Registro(String nombre, int jobNo, String tarjeta, LocalDate fecha, LocalTime hora,
                    String inOut, int readId, int eventMainCode, int eventSubCode, String status, int serialNo) {
        this.nombre = nombre;
        this.jobNo = jobNo;
        this.tarjeta = tarjeta;
        this.fecha = fecha;
        this.hora = hora;
        this.inOut = inOut;
        this.readId = readId;
        this.eventMainCode = eventMainCode;
        this.eventSubCode = eventSubCode;
        this.status = status;
        this.serialNo = serialNo;
    }

    // Getters (necesarios para el Buscador)
    public String getNombre() { return nombre; }
    public int getJobNo() { return jobNo; }
    public String getTarjeta() { return tarjeta; }
    public LocalDate getFecha() { return fecha; }
    public LocalTime getHora() { return hora; }
    public String getInOut() { return inOut; }
    public int getReadId() { return readId; }
    public int getEventMainCode() { return eventMainCode; }
    public int getEventSubCode() { return eventSubCode; }
    public String getStatus() { return status; }
    public int getSerialNo() { return serialNo; }

   private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm:ss");

public Object[] toTableRow() {
    return new Object[]{
        nombre,
        jobNo,
        tarjeta != null ? tarjeta : "N/A",
        fecha != null ? fecha.format(DATE_FORMAT) : "",
        hora != null ? hora.format(TIME_FORMAT) : "",
        inOut,
        readId,
        status
    };
}
}
