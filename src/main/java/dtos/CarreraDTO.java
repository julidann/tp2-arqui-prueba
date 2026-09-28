package dtos;

import entities.Inscripcion;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CarreraDTO {
    private String nombre;
    private List<Inscripcion> inscripciones = new ArrayList<>();

    public CarreraDTO() {}
    public CarreraDTO(String nombre) { this.nombre = nombre; }

    public String getNombre() { return nombre; }
    public List<Inscripcion> getInscripciones() { return new ArrayList<>(inscripciones); }

    public void addInscripcion(Inscripcion inscripcion) {
        inscripciones.add(inscripcion);
    }

    public List<ResumenAnualDTO> getResumenPorAnio() {
        Map<Integer, List<Inscripcion>> porAnio = inscripciones.stream()
                .filter(i -> i.getAnioInscripcion() != null)
                .collect(Collectors.groupingBy(i -> i.getAnioInscripcion().getYear()));

        return porAnio.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(e -> new ResumenAnualDTO(
                        e.getKey(),
                        e.getValue().size(),
                        e.getValue().stream().filter(Inscripcion::isGraduado).count()))
                .toList();
    }

    @Override
    public String toString() {
        return nombre + " -> " + getResumenPorAnio();
    }
}