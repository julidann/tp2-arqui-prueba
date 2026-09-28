package repositories;

import dtos.CarreraConCantInscriptosDTO;
import dtos.InscripcionDTO;
import entities.Inscripcion;
import jakarta.persistence.*;
import repositories.interfaces.RepositoryInscripcion;
import java.util.List;

public class JpaInscripcionRepository implements RepositoryInscripcion {
    private final EntityManager em;

    public JpaInscripcionRepository(EntityManager em) {
        this.em = em;
    }

    @Override
    public void save(Inscripcion inscripcion) {
        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();
            em.persist(inscripcion);
            tx.commit();

        } catch (Exception e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            e.printStackTrace();
        }
    }

    @Override
    public InscripcionDTO selectById(int id) {
        List<InscripcionDTO> result = em.createQuery(
                "SELECT new dtos.InscripcionDTO(i.antiguedad,i.anioInscripcion,i.anioEgreso," +
                "i.graduado,c.nombre,e.lu) FROM Inscripcion i " +
                "JOIN i.carrera c JOIN i.estudiante e WHERE i.id = :id",
                InscripcionDTO.class)
                .setParameter("id", id)
                .getResultList();

        return result.isEmpty() ? null : result.get(0);
    }

    @Override
    public List<InscripcionDTO> selectAll() {
        return em.createQuery(
                "SELECT new dtos.InscripcionDTO(i.antiguedad,i.anioInscripcion,i.anioEgreso," +
                "i.graduado,c.nombre,e.lu) FROM Inscripcion i " +
                "JOIN i.carrera c JOIN i.estudiante e ORDER BY c.nombre, e.lu",
                InscripcionDTO.class
        ).getResultList();
    }

    @Override
    public boolean delete(int id) {
        EntityTransaction tx = em.getTransaction();

        try {
            Inscripcion inscripcion = em.find(Inscripcion.class, id);

            if (inscripcion == null) {
                return false;
            }

            tx.begin();
            em.remove(inscripcion);
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
    public List<CarreraConCantInscriptosDTO> recuperarCarrerasOrdenadasPorCantidadInscriptos() {
        return em.createQuery(
                "SELECT new dtos.CarreraConCantInscriptosDTO(c.nombre, COUNT(i)) " +
                "FROM Carrera c JOIN c.inscripciones i " +
                "GROUP BY c.id, c.nombre ORDER BY COUNT(i) DESC, c.nombre ASC",
                CarreraConCantInscriptosDTO.class
        ).getResultList();
    }

    @Override
    public List<InscripcionDTO> estudiantesDeCarreraPorCiudad(String nombreCarrera, String ciudad) {
        return em.createQuery(
                "SELECT new dtos.InscripcionDTO(i.antiguedad,i.anioInscripcion,i.anioEgreso," +
                "i.graduado,c.nombre,e.lu) " +
                "FROM Inscripcion i JOIN i.carrera c JOIN i.estudiante e " +
                "WHERE c.nombre = :carrera AND e.ciudadResidencia = :ciudad " +
                "ORDER BY e.apellido ASC, e.nombres ASC",
                InscripcionDTO.class)
                .setParameter("carrera", nombreCarrera)
                .setParameter("ciudad", ciudad)
                .getResultList();
    }
}