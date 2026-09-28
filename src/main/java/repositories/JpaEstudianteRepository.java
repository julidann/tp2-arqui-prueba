package repositories;

import dtos.EstudianteDTO;
import entities.Estudiante;
import jakarta.persistence.*;
import repositories.interfaces.RepositoryEstudiante;
import java.util.List;

public class JpaEstudianteRepository implements RepositoryEstudiante {
    private final EntityManager em;

    public JpaEstudianteRepository(EntityManager em) {
        this.em = em;
    }

    @Override
    public void save(Estudiante estudiante) {
        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();
            em.persist(estudiante);
            tx.commit();

        } catch (Exception e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            e.printStackTrace();
        }
    }

    @Override
    public EstudianteDTO selectById(int dni) {
        List<EstudianteDTO> result = em.createQuery(
                "SELECT new dtos.EstudianteDTO(e.nombres,e.apellido,e.edad,e.genero,e.dni,e.ciudadResidencia,e.lu) " +
                "FROM Estudiante e WHERE e.dni = :dni",
                EstudianteDTO.class)
                .setParameter("dni", dni)
                .getResultList();

        return result.isEmpty() ? null : result.get(0);
    }

    @Override
    public List<EstudianteDTO> selectAll() {
        return obtenerEstudiantesOrdenadosPorNombre();
    }

    @Override
    public boolean delete(int dni) {
        EntityTransaction tx = em.getTransaction();

        try {
            Estudiante estudiante = em.find(Estudiante.class, dni);

            if (estudiante == null) {
                return false;
            }

            tx.begin();
            em.remove(estudiante);
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
    public List<EstudianteDTO> obtenerEstudiantesOrdenadosPorNombre() {
        return em.createQuery(
                "SELECT new dtos.EstudianteDTO(e.nombres,e.apellido,e.edad,e.genero,e.dni,e.ciudadResidencia,e.lu) " +
                "FROM Estudiante e ORDER BY e.nombres ASC",
                EstudianteDTO.class
        ).getResultList();
    }

    @Override
    public EstudianteDTO buscarPorLibreta(Long lu) {
        List<EstudianteDTO> result = em.createQuery(
                "SELECT new dtos.EstudianteDTO(e.nombres,e.apellido,e.edad,e.genero,e.dni,e.ciudadResidencia,e.lu) " +
                "FROM Estudiante e WHERE e.lu = :lu",
                EstudianteDTO.class)
                .setParameter("lu", lu)
                .getResultList();

        return result.isEmpty() ? null : result.get(0);
    }

    @Override
    public List<EstudianteDTO> buscarPorGenero(String genero) {
        return em.createQuery(
                "SELECT new dtos.EstudianteDTO(e.nombres,e.apellido,e.edad,e.genero,e.dni,e.ciudadResidencia,e.lu) " +
                "FROM Estudiante e WHERE e.genero = :genero ORDER BY e.apellido ASC, e.nombres ASC",
                EstudianteDTO.class)
                .setParameter("genero", genero)
                .getResultList();
    }
}