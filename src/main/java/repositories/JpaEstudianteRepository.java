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
        } catch (RuntimeException e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        }
    }

    private List<EstudianteDTO> query(String jpql, Object... params) {
        TypedQuery<EstudianteDTO> q = em.createQuery(jpql, EstudianteDTO.class);
        for (int i = 0; i < params.length; i += 2) {
            q.setParameter(params[i].toString(), params[i + 1]);
        }
        return q.getResultList();
    }

    @Override
    public EstudianteDTO selectById(int dni) {
        List<EstudianteDTO> result = query(
                "SELECT new dtos.EstudianteDTO(e.nombres,e.apellido,e.edad,e.genero,e.dni,e.ciudadResidencia,e.lu) " +
                "FROM Estudiante e WHERE e.dni = :dni", "dni", dni);
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
            Estudiante e = em.find(Estudiante.class, dni);
            if (e == null) return false;
            tx.begin();
            em.remove(e);
            tx.commit();
            return true;
        } catch (RuntimeException ex) {
            if (tx.isActive()) tx.rollback();
            throw ex;
        }
    }

    @Override
    public List<EstudianteDTO> obtenerEstudiantesOrdenadosPorNombre() {
        return query(
                "SELECT new dtos.EstudianteDTO(e.nombres,e.apellido,e.edad,e.genero,e.dni,e.ciudadResidencia,e.lu) " +
                "FROM Estudiante e ORDER BY e.nombres ASC");
    }

    @Override
    public EstudianteDTO buscarPorLibreta(Long lu) {
        List<EstudianteDTO> result = query(
                "SELECT new dtos.EstudianteDTO(e.nombres,e.apellido,e.edad,e.genero,e.dni,e.ciudadResidencia,e.lu) " +
                "FROM Estudiante e WHERE e.lu = :lu", "lu", lu);
        return result.isEmpty() ? null : result.get(0);
    }

    @Override
    public List<EstudianteDTO> buscarPorGenero(String genero) {
        return query(
                "SELECT new dtos.EstudianteDTO(e.nombres,e.apellido,e.edad,e.genero,e.dni,e.ciudadResidencia,e.lu) " +
                "FROM Estudiante e WHERE e.genero = :genero ORDER BY e.apellido ASC, e.nombres ASC",
                "genero", genero);
    }
}