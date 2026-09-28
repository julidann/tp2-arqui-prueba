package repositories;

import dtos.CarreraDTO;
import dtos.ReporteCarreraDTO;
import entities.Carrera;
import entities.Estudiante;
import entities.Inscripcion;
import jakarta.persistence.*;
import repositories.interfaces.RepositoryCarrera;
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

        } catch (Exception e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            e.printStackTrace();
        }
    }

    @Override
    public CarreraDTO selectById(int id) {
        Carrera carrera = em.find(Carrera.class, id);

        if (carrera == null) {
            return null;
        }

        CarreraDTO dto = new CarreraDTO(carrera.getNombre());

        for (Inscripcion inscripcion : carrera.getInscripciones()) {
            dto.addInscripcion(inscripcion);
        }

        return dto;
    }

    @Override
    public List<CarreraDTO> selectAll() {
        List<Carrera> carreras = em.createQuery(
                "SELECT c FROM Carrera c ORDER BY c.nombre ASC",
                Carrera.class
        ).getResultList();

        List<CarreraDTO> resultado = new ArrayList<>();

        for (Carrera carrera : carreras) {
            CarreraDTO dto = new CarreraDTO(carrera.getNombre());

            for (Inscripcion inscripcion : carrera.getInscripciones()) {
                dto.addInscripcion(inscripcion);
            }

            resultado.add(dto);
        }

        return resultado;
    }

    @Override
    public boolean delete(int id) {
        EntityTransaction tx = em.getTransaction();

        try {
            Carrera carrera = em.find(Carrera.class, id);

            if (carrera == null) {
                return false;
            }

            tx.begin();
            em.remove(carrera);
            tx.commit();

            return true;

        } catch (Exception e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public void matricularEstudianteEnCarrera(Long lu, String nombreCarrera) {
        Estudiante estudiante = em.createQuery(
                "SELECT e FROM Estudiante e WHERE e.lu = :lu",
                Estudiante.class)
                .setParameter("lu", lu)
                .getSingleResult();

        Carrera carrera = em.createQuery(
                "SELECT c FROM Carrera c WHERE c.nombre = :nombre",
                Carrera.class)
                .setParameter("nombre", nombreCarrera)
                .getSingleResult();

        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();
            em.persist(new Inscripcion(carrera, estudiante));
            tx.commit();

        } catch (Exception e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            e.printStackTrace();
        }
    }

    @Override
    public List<CarreraDTO> generarReporteCarreras() {
        List<Carrera> carreras = em.createQuery(
                "SELECT DISTINCT c FROM Carrera c LEFT JOIN FETCH c.inscripciones i ORDER BY c.nombre ASC",
                Carrera.class
        ).getResultList();

        List<CarreraDTO> resultado = new ArrayList<>();

        for (Carrera carrera : carreras) {
            CarreraDTO dto = new CarreraDTO(carrera.getNombre());

            List<Inscripcion> inscripciones = new ArrayList<>(carrera.getInscripciones());

            inscripciones.sort(Comparator.comparing(
                    Inscripcion::getAnioInscripcion,
                    Comparator.nullsLast(Comparator.naturalOrder())
            ));

            for (Inscripcion inscripcion : inscripciones) {
                dto.addInscripcion(inscripcion);
            }

            resultado.add(dto);
        }

        return resultado;
    }

    @Override
    public List<ReporteCarreraDTO> reporteCarreras() {
        List<Carrera> carreras = em.createQuery(
                "SELECT DISTINCT c FROM Carrera c LEFT JOIN FETCH c.inscripciones i ORDER BY c.nombre ASC",
                Carrera.class
        ).getResultList();

        List<ReporteCarreraDTO> reporte = new ArrayList<>();

        for (Carrera carrera : carreras) {
            Map<Integer, Long> inscriptosPorAnio = new TreeMap<>();
            Map<Integer, Long> egresadosPorAnio = new TreeMap<>();

            for (Inscripcion inscripcion : carrera.getInscripciones()) {
                if (inscripcion.getAnioInscripcion() != null) {
                    int anio = inscripcion.getAnioInscripcion().getYear();

                    if (!inscriptosPorAnio.containsKey(anio)) {
                        inscriptosPorAnio.put(anio, 0L);
                    }

                    inscriptosPorAnio.put(
                            anio,
                            inscriptosPorAnio.get(anio) + 1
                    );
                }

                if (inscripcion.isGraduado() && inscripcion.getAnioEgreso() != null) {
                    int anio = inscripcion.getAnioEgreso().getYear();

                    if (!egresadosPorAnio.containsKey(anio)) {
                        egresadosPorAnio.put(anio, 0L);
                    }

                    egresadosPorAnio.put(
                            anio,
                            egresadosPorAnio.get(anio) + 1
                    );
                }
            }

            Set<Integer> anios = new TreeSet<>(inscriptosPorAnio.keySet());
            anios.addAll(egresadosPorAnio.keySet());

            for (Integer anio : anios) {
                reporte.add(new ReporteCarreraDTO(
                        carrera.getNombre(),
                        anio,
                        inscriptosPorAnio.getOrDefault(anio, 0L),
                        egresadosPorAnio.getOrDefault(anio, 0L)
                ));
            }
        }

        return reporte;
    }
}