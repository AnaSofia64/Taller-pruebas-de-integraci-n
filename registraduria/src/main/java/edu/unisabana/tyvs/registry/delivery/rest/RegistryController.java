package edu.unisabana.tyvs.registry.delivery.rest;

import edu.unisabana.tyvs.registry.application.usecase.Registry;
import edu.unisabana.tyvs.registry.domain.model.Gender;
import edu.unisabana.tyvs.registry.domain.model.Person;
import edu.unisabana.tyvs.registry.domain.model.RegisterResult;
import edu.unisabana.tyvs.registry.domain.model.rq.PersonDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class RegistryController {

    private final Registry registry;

    public RegistryController(Registry registry) {
        this.registry = registry;
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody PersonDTO dto) {
        try {
            Gender gender = Gender.valueOf(dto.getGender().toUpperCase());
            Person person = new Person(dto.getName(), dto.getId(),
                                       dto.getAge(), gender, dto.isAlive());
            RegisterResult result = registry.registerVoter(person);
            return ResponseEntity.ok(result.name());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("INVALID_GENDER");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("ERROR: " + e.getMessage());
        }
    }
}