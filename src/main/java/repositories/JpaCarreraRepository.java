package repositories;

import dtos.CarreraDTO;
import dtos.ReporteCarreraDTO;
import entities.Carrera;
import entities.Estudiante;
import entities.Inscripcion;
import jakarta.persistence.*;
import repositories.interfaces.RepositoryCarrera;
import java.time.LocalDate;
import java.util.*;

public class JpaCarreraRepository implements RepositoryCarrera {
    private final EntityManager em;

    public JpaCarreraRepository(EntityManager em) {
        this.em = em;
    }

    @Override
    public void save(Carrera carrera) {
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.persist(carrera);
            tx.commit();
        } catch (RuntimeException e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        }
    }

    @Override
    public CarreraDTO selectById(int id) {
        Carrera c = em.find(Carrera.class, id);
        if (c == null) return null;
        CarreraDTO dto = new CarreraDTO(c.getNombre());
        c.getInscripciones().forEach(dto::addInscripcion);
        return dto;
    }

    @Override
    public List<CarreraDTO> selectAll() {
        List<Carrera> carreras = em.createQuery(
                "SELECT c FROM Carrera c ORDER BY c.nombre ASC", Carrera.class).getResultList();
        return carreras.stream().map(c -> {
            CarreraDTO dto = new CarreraDTO(c.getNombre());
            c.getInscripciones().forEach(dto::addInscripcion);
            return dto;
        }).toList();
    }

    @Override
    public boolean delete(int id) {
        EntityTransaction tx = em.getTransaction();
        try {
            Carrera c = em.find(Carrera.class, id);
            if (c == null) return false;
            tx.begin();
            em.remove(c);
            tx.commit();
            return true;
        } catch (RuntimeException e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        }
    }

    @Override
    public void matricularEstudianteEnCarrera(Long lu, String nombreCarrera) {
        Estudiante estudiante = em.createQuery(
                "SELECT e FROM Estudiante e WHERE e.lu = :lu", Estudiante.class)
                .setParameter("lu", lu)
                .getSingleResult();

        Carrera carrera = em.createQuery(
                "SELECT c FROM Carrera c WHERE c.nombre = :nombre", Carrera.class)
                .setParameter("nombre", nombreCarrera)
                .getSingleResult();

        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.persist(new Inscripcion(carrera, estudiante));
            tx.commit();
        } catch (RuntimeException e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        }
    }

    @Override
    public List<CarreraDTO> generarReporteCarreras() {
        List<Carrera> carreras = em.createQuery(
                "SELECT DISTINCT c FROM Carrera c LEFT JOIN FETCH c.inscripciones i ORDER BY c.nombre ASC",
                Carrera.class).getResultList();

        return carreras.stream().map(c -> {
            CarreraDTO dto = new CarreraDTO(c.getNombre());
            c.getInscripciones().stream()
                    .sorted(Comparator.comparing(Inscripcion::getAnioInscripcion,
                            Comparator.nullsLast(Comparator.naturalOrder())))
                    .forEach(dto::addInscripcion);
            return dto;
        }).toList();
    }

    @Override
    public List<ReporteCarreraDTO> reporteCarreras() {
        List<Carrera> carreras = em.createQuery(
                "SELECT DISTINCT c FROM Carrera c LEFT JOIN FETCH c.inscripciones i ORDER BY c.nombre ASC",
                Carrera.class).getResultList();

        List<ReporteCarreraDTO> reporte = new ArrayList<>();

        for (Carrera carrera : carreras) {
            Map<Integer, Long> inscriptosPorAnio = new TreeMap<>();
            Map<Integer, Long> egresadosPorAnio = new TreeMap<>();

            for (Inscripcion i : carrera.getInscripciones()) {
                if (i.getAnioInscripcion() != null) {
                    int anio = i.getAnioInscripcion().getYear();
                    inscriptosPorAnio.merge(anio, 1L, Long::sum);
                }
                if (i.isGraduado() && i.getAnioEgreso() != null) {
                    int anio = i.getAnioEgreso().getYear();
                    egresadosPorAnio.merge(anio, 1L, Long::sum);
                }
            }

            Set<Integer> anios = new TreeSet<>(inscriptosPorAnio.keySet());
            anios.addAll(egresadosPorAnio.keySet());

            for (Integer anio : anios) {
                reporte.add(new ReporteCarreraDTO(
                        carrera.getNombre(),
                        anio,
                        inscriptosPorAnio.getOrDefault(anio, 0L),
                        egresadosPorAnio.getOrDefault(anio, 0L)));
            }
        }
        return reporte;
    }
}