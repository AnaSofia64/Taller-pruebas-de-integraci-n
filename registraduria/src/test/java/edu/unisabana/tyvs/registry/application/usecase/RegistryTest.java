package edu.unisabana.tyvs.registry.application.usecase;

import edu.unisabana.tyvs.registry.application.port.out.RegistryRepositoryPort;
import edu.unisabana.tyvs.registry.domain.model.Gender;
import edu.unisabana.tyvs.registry.domain.model.Person;
import edu.unisabana.tyvs.registry.domain.model.RegisterResult;
import edu.unisabana.tyvs.registry.infrastructure.persistence.RegistryRepository;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Pruebas de INTEGRACION: Registry + RegistryRepository con H2 en memoria.
 * Verifica tanto la regla de negocio como la persistencia real en BD.
 */
public class RegistryTest {

    private RegistryRepositoryPort repo;
    private Registry registry;

    @Before
    public void setup() throws Exception {
        String jdbc = "jdbc:h2:mem:regdb_test;DB_CLOSE_DELAY=-1";
        repo = new RegistryRepository(jdbc);
        repo.initSchema();
        repo.deleteAll();
        registry = new Registry(repo);
    }

    /**
     * Given: persona valida (viva, 30 años, id unico).
     * When:  se registra.
     * Then:  resultado VALID y dato persistido en BD.
     */
    @Test
    public void shouldRegisterValidPerson() throws Exception {
        // Arrange
        Person p = new Person("Ana", 100, 30, Gender.FEMALE, true);
        // Act
        RegisterResult result = registry.registerVoter(p);
        // Assert
        assertEquals(RegisterResult.VALID, result);
        assertTrue("Debe estar en la BD", repo.existsById(100));
    }

    /**
     * Given: dos personas con el mismo ID.
     * When:  se registran una tras otra.
     * Then:  primera VALID, segunda DUPLICATED.
     */
    @Test
    public void shouldPersistValidVoterAndRejectDuplicates() throws Exception {
        // Arrange
        Person p1 = new Person("Ana",    100, 30, Gender.FEMALE, true);
        Person p2 = new Person("AnaDos", 100, 40, Gender.FEMALE, true);
        // Act
        RegisterResult result1 = registry.registerVoter(p1);
        RegisterResult result2 = registry.registerVoter(p2);
        // Assert
        assertEquals(RegisterResult.VALID,      result1);
        assertEquals(RegisterResult.DUPLICATED, result2);
    }

    /**
     * Given: persona fallecida.
     * When:  se intenta registrar.
     * Then:  resultado DEAD y NO se persiste en BD.
     */
    @Test
    public void shouldRejectDeadPersonAndNotPersist() throws Exception {
        // Arrange
        Person dead = new Person("Carlos", 200, 45, Gender.MALE, false);
        // Act
        RegisterResult result = registry.registerVoter(dead);
        // Assert
        assertEquals(RegisterResult.DEAD, result);
        assertFalse("No debe estar en BD", repo.existsById(200));
    }

    /**
     * Given: persona menor de edad (17 años).
     * When:  se intenta registrar.
     * Then:  resultado UNDERAGE y NO se persiste en BD.
     */
    @Test
    public void shouldRejectUnderagePersonAndNotPersist() throws Exception {
        // Arrange
        Person minor = new Person("Sofia", 300, 17, Gender.FEMALE, true);
        // Act
        RegisterResult result = registry.registerVoter(minor);
        // Assert
        assertEquals(RegisterResult.UNDERAGE, result);
        assertFalse("No debe estar en BD", repo.existsById(300));
    }

    /**
     * Given: persona con ID invalido (0).
     * When:  se intenta registrar.
     * Then:  resultado INVALID y NO se persiste en BD.
     */
    @Test
    public void shouldRejectInvalidIdAndNotPersist() throws Exception {
        // Arrange
        Person invalid = new Person("Ghost", 0, 25, Gender.MALE, true);
        // Act
        RegisterResult result = registry.registerVoter(invalid);
        // Assert
        assertEquals(RegisterResult.INVALID, result);
        assertFalse(repo.existsById(0));
    }

    /**
     * Given: persona con edad fuera de rango (121 años).
     * When:  se intenta registrar.
     * Then:  resultado INVALID_AGE y NO se persiste en BD.
     */
    @Test
    public void shouldRejectAgeOver120AndNotPersist() throws Exception {
        // Arrange
        Person tooOld = new Person("Viejo", 400, 121, Gender.MALE, true);
        // Act
        RegisterResult result = registry.registerVoter(tooOld);
        // Assert
        assertEquals(RegisterResult.INVALID_AGE, result);
        assertFalse(repo.existsById(400));
    }
}