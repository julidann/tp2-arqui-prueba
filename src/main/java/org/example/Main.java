package org.example;

import dtos.*;
import factories.JpaMySqlRepositoryFactory;
import factories.RepositoryFactory;
import helpers.CSVreader;
import helpers.DatabaseLoader;
import jakarta.persistence.EntityManager;
import repositories.interfaces.RepositoryCarrera;
import repositories.interfaces.RepositoryEstudiante;
import repositories.interfaces.RepositoryInscripcion;

public class Main {
    public static void main(String[] args) {
        CSVreader reader = new CSVreader();
        RepositoryFactory factory = RepositoryFactory.getDAOFactory(RepositoryFactory.MYSQL_JDBC);

        try {
            DatabaseLoader.cargarDatos(reader, factory);

            EntityManager em = JpaMySqlRepositoryFactory
                    .getEntityManagerFactory()
                    .createEntityManager();

            RepositoryEstudiante estudiantes = factory.getEstudianteRepository(em);
            RepositoryCarrera carreras = factory.getCarreraRepository(em);
            RepositoryInscripcion inscripciones = factory.getInscripcionRepository(em);

            System.out.println("\n=== 2c) ESTUDIANTES ORDENADOS POR NOMBRE ===");
            estudiantes.obtenerEstudiantesOrdenadosPorNombre().forEach(System.out::println);

            System.out.println("\n=== 2d) ESTUDIANTE POR LIBRETA UNIVERSITARIA ===");
            System.out.println(estudiantes.buscarPorLibreta(34978L));

            System.out.println("\n=== 2e) ESTUDIANTES POR GENERO ===");
            estudiantes.buscarPorGenero("Female").forEach(System.out::println);

            System.out.println("\n=== 2f) CARRERAS ORDENADAS POR CANTIDAD DE INSCRIPTOS ===");
            inscripciones.recuperarCarrerasOrdenadasPorCantidadInscriptos()
                    .forEach(System.out::println);

            System.out.println("\n=== 2g) ESTUDIANTES DE UNA CARRERA POR CIUDAD ===");
            inscripciones.estudiantesDeCarreraPorCiudad("TUDAI", "Santiago")
                    .forEach(System.out::println);

            System.out.println("\n=== 3) REPORTE DE CARRERAS ===");
            carreras.reporteCarreras().forEach(System.out::println);

            em.close();
            JpaMySqlRepositoryFactory.getEntityManagerFactory().close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}