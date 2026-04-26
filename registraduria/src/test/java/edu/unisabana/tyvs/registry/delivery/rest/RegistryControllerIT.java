package edu.unisabana.tyvs.registry.delivery.rest;

import edu.unisabana.tyvs.registry.application.port.out.RegistryRepositoryPort;
import edu.unisabana.tyvs.registry.application.usecase.Registry;
import edu.unisabana.tyvs.registry.infrastructure.persistence.RegistryRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.http.*;
import org.springframework.test.context.junit4.SpringRunner;
import static org.junit.Assert.*;

/**
 * Pruebas de SISTEMA (caja negra) sobre el endpoint HTTP /register.
 * Valida el flujo completo sin conocer detalles de implementacion interna.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class RegistryControllerIT {

    @TestConfiguration
    static class TestBeans {

        @Bean
        public RegistryRepositoryPort registryRepositoryPort() throws Exception {
            String jdbc = "jdbc:h2:mem:regdb_it;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE";
            RegistryRepository repo = new RegistryRepository(jdbc);
            repo.initSchema();
            repo.deleteAll();
            return repo;
        }

        @Bean
        public Registry registry(RegistryRepositoryPort port) {
            return new Registry(port);
        }
    }

    @Autowired
    private TestRestTemplate rest;

    private ResponseEntity<String> post(String json) {
        HttpHeaders h = new HttpHeaders();
        h.setContentType(MediaType.APPLICATION_JSON);
        return rest.postForEntity("/register", new HttpEntity<>(json, h), String.class);
    }

    /** Given: JSON valido. When: POST /register. Then: 200 VALID. */
    @Test
    public void shouldReturnValidForValidPerson() {
        String json = "{\"name\":\"Ana\",\"id\":501,\"age\":30,\"gender\":\"FEMALE\",\"alive\":true}";
        ResponseEntity<String> resp = post(json);
        assertEquals(HttpStatus.OK, resp.getStatusCode());
        assertEquals("VALID", resp.getBody());
    }

    /** Given: mismo id enviado dos veces. When: POST x2. Then: VALID luego DUPLICATED. */
    @Test
    public void shouldReturnDuplicatedOnSecondRegistration() {
        String json = "{\"name\":\"Pedro\",\"id\":502,\"age\":25,\"gender\":\"MALE\",\"alive\":true}";
        assertEquals("VALID",      post(json).getBody());
        assertEquals("DUPLICATED", post(json).getBody());
    }

    /** Given: JSON con alive=false. When: POST /register. Then: 200 DEAD. */
    @Test
    public void shouldReturnDeadForDeadPerson() {
        String json = "{\"name\":\"Carlos\",\"id\":503,\"age\":45,\"gender\":\"MALE\",\"alive\":false}";
        ResponseEntity<String> resp = post(json);
        assertEquals(HttpStatus.OK, resp.getStatusCode());
        assertEquals("DEAD", resp.getBody());
    }

    /** Given: JSON con age=17. When: POST /register. Then: 200 UNDERAGE. */
    @Test
    public void shouldReturnUnderageForMinor() {
        String json = "{\"name\":\"Joven\",\"id\":504,\"age\":17,\"gender\":\"FEMALE\",\"alive\":true}";
        ResponseEntity<String> resp = post(json);
        assertEquals(HttpStatus.OK, resp.getStatusCode());
        assertEquals("UNDERAGE", resp.getBody());
    }

    /** Given: JSON con gender invalido. When: POST /register. Then: 400. */
    @Test
    public void shouldReturnBadRequestForInvalidGender() {
        String json = "{\"name\":\"Error\",\"id\":505,\"age\":25,\"gender\":\"ALIEN\",\"alive\":true}";
        ResponseEntity<String> resp = post(json);
        assertEquals(HttpStatus.BAD_REQUEST, resp.getStatusCode());
    }
}