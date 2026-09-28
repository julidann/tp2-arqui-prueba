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

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

public class Main {
    public static void main(String[] args) {
        System.setOut(new PrintStream(System.out, true, StandardCharsets.UTF_8));

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

            // --- 2a) AGREGAR UN ESTUDIANTE ---
            /*
            Estudiante nuevoEstudiante = new Estudiante(
                    39550725,
                    "Julieta",
                    "D'Annunzio",
                    24,
                    "Female",
                    "Tandil",
                    40000L
            );
            estudiantes.save(nuevoEstudiante);
            */

            // --- 2b) INSCRIBIR ESTUDIANTE EN UNA CARRERA ---
            /*
            carreras.matricularEstudianteEnCarrera(34978L, "TUDAI");
            */

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
            inscripciones.estudiantesDeCarreraPorCiudad("TUDAI", "Rauch")
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
