package edu.unisabana.tyvs.registry.application.usecase;

import edu.unisabana.tyvs.registry.application.port.out.RegistryRepositoryPort;
import edu.unisabana.tyvs.registry.domain.model.Person;
import edu.unisabana.tyvs.registry.domain.model.RegisterResult;

/**
 * Caso de uso: registrar un votante.
 * Solo conoce el puerto de salida (interfaz), nunca la implementacion concreta.
 */
public class Registry {

    private static final int MIN_AGE      = 18;
    private static final int MAX_AGE      = 120;
    private static final int MIN_VALID_ID = 1;

    private final RegistryRepositoryPort repo;

    public Registry(RegistryRepositoryPort repo) {
        this.repo = repo;
    }

    public RegisterResult registerVoter(Person p) throws Exception {
        if (p == null)                               return RegisterResult.INVALID;
        if (p.getId() < MIN_VALID_ID)                return RegisterResult.INVALID;
        if (!p.isAlive())                            return RegisterResult.DEAD;
        if (p.getAge() < 0 || p.getAge() > MAX_AGE) return RegisterResult.INVALID_AGE;
        if (p.getAge() < MIN_AGE)                    return RegisterResult.UNDERAGE;
        if (repo.existsById(p.getId()))              return RegisterResult.DUPLICATED;

        repo.save(p.getId(), p.getName(), p.getAge(), p.isAlive());
        return RegisterResult.VALID;
    }
}