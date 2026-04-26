package edu.unisabana.tyvs.registry.application.usecase;

import edu.unisabana.tyvs.registry.application.port.out.RegistryRepositoryPort;
import edu.unisabana.tyvs.registry.domain.model.Gender;
import edu.unisabana.tyvs.registry.domain.model.Person;
import edu.unisabana.tyvs.registry.domain.model.RegisterResult;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Pruebas de INTEGRACION con Mockito.
 * El repositorio se simula para aislar el caso de uso y verificar interacciones.
 */
public class RegistryWithMockTest {

    private RegistryRepositoryPort repo;
    private Registry registry;

    @Before
    public void setUp() {
        repo = mock(RegistryRepositoryPort.class);
        registry = new Registry(repo);
    }

    /**
     * Given: repo dice que el ID=7 ya existe.
     * When:  se intenta registrar persona con ID=7.
     * Then:  resultado DUPLICATED y save() NUNCA se llama.
     */
    @Test
    public void shouldReturnDuplicatedWhenRepoSaysExists() throws Exception {
        // Arrange
        when(repo.existsById(7)).thenReturn(true);
        Person p = new Person("Ana", 7, 25, Gender.FEMALE, true);
        // Act
        RegisterResult result = registry.registerVoter(p);
        // Assert
        assertEquals(RegisterResult.DUPLICATED, result);
        verify(repo, never()).save(anyInt(), anyString(), anyInt(), anyBoolean());
    }

    /**
     * Given: repo dice que el ID=10 NO existe.
     * When:  se registra persona valida con ID=10.
     * Then:  resultado VALID y save() se invoca exactamente una vez.
     */
    @Test
    public void shouldCallSaveWhenPersonIsValid() throws Exception {
        // Arrange
        when(repo.existsById(10)).thenReturn(false);
        Person p = new Person("Pedro", 10, 30, Gender.MALE, true);
        // Act
        RegisterResult result = registry.registerVoter(p);
        // Assert
        assertEquals(RegisterResult.VALID, result);
        verify(repo, times(1)).save(10, "Pedro", 30, true);
    }

    /**
     * Given: repo lanza excepcion al llamar existsById.
     * When:  se intenta registrar.
     * Then:  la excepcion se propaga correctamente.
     */
    @Test(expected = Exception.class)
    public void shouldPropagateExceptionFromRepo() throws Exception {
        // Arrange
        when(repo.existsById(anyInt())).thenThrow(new Exception("DB connection failed"));
        Person p = new Person("Error", 99, 25, Gender.MALE, true);
        // Act
        registry.registerVoter(p);
    }

    /**
     * Given: persona fallecida.
     * When:  se intenta registrar.
     * Then:  resultado DEAD, existsById y save NUNCA se llaman.
     */
    @Test
    public void shouldNotInteractWithRepoForDeadPerson() throws Exception {
        // Arrange
        Person dead = new Person("Carlos", 5, 40, Gender.MALE, false);
        // Act
        RegisterResult result = registry.registerVoter(dead);
        // Assert
        assertEquals(RegisterResult.DEAD, result);
        verify(repo, never()).existsById(anyInt());
        verify(repo, never()).save(anyInt(), anyString(), anyInt(), anyBoolean());
    }

    /**
     * Given: persona menor de edad (16 años).
     * When:  se intenta registrar.
     * Then:  resultado UNDERAGE y save() NUNCA se llama.
     */
    @Test
    public void shouldNotSaveUnderagePerson() throws Exception {
        // Arrange
        when(repo.existsById(anyInt())).thenReturn(false);
        Person minor = new Person("Joven", 8, 16, Gender.FEMALE, true);
        // Act
        RegisterResult result = registry.registerVoter(minor);
        // Assert
        assertEquals(RegisterResult.UNDERAGE, result);
        verify(repo, never()).save(anyInt(), anyString(), anyInt(), anyBoolean());
    }
}