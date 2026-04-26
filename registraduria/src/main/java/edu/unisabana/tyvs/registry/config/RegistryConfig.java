package edu.unisabana.tyvs.registry.config;

import edu.unisabana.tyvs.registry.application.port.out.RegistryRepositoryPort;
import edu.unisabana.tyvs.registry.application.usecase.Registry;
import edu.unisabana.tyvs.registry.infrastructure.persistence.RegistryRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RegistryConfig {

    @Bean
    public RegistryRepositoryPort registryRepositoryPort() throws Exception {
        String jdbc = "jdbc:h2:mem:registrodb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE";
        RegistryRepository repo = new RegistryRepository(jdbc);
        repo.initSchema();
        return repo;
    }

    @Bean
    public Registry registry(RegistryRepositoryPort port) {
        return new Registry(port);
    }
}