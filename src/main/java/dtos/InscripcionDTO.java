package dtos;

import java.time.LocalDate;

public class InscripcionDTO {
    private int antiguedad;
    private LocalDate anioInscripcion;
    private LocalDate anioEgreso;
    private boolean graduado;
    private String nombreCarrera;
    private Long luEstudiante;
    private EstudianteDTO estudiante;

    public InscripcionDTO() {}

    public InscripcionDTO(int antiguedad, LocalDate anioInscripcion, LocalDate anioEgreso,
                          boolean graduado, String nombreCarrera, Long luEstudiante) {
        this.antiguedad = antiguedad;
        this.anioInscripcion = anioInscripcion;
        this.anioEgreso = anioEgreso;
        this.graduado = graduado;
        this.nombreCarrera = nombreCarrera;
        this.luEstudiante = luEstudiante;
    }

    public int getAntiguedad() { return antiguedad; }
    public LocalDate getAnioInscripcion() { return anioInscripcion; }
    public LocalDate getAnioEgreso() { return anioEgreso; }
    public boolean isGraduado() { return graduado; }
    public String getCarrera() { return nombreCarrera; }
    public Long getLuEstudiante() { return luEstudiante; }
    public EstudianteDTO getEstudiante() { return estudiante; }

    @Override
    public String toString() {
        return "InscripcionDTO{" +
                "antiguedad=" + antiguedad +
                ", anioInscripcion=" + anioInscripcion +
                ", anioEgreso=" + anioEgreso +
                ", graduado=" + graduado +
                ", carrera='" + nombreCarrera  +
                ", luEstudiante=" + luEstudiante +
                '}';
    }
}