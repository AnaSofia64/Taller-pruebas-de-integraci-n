package edu.unisabana.tyvs.registry.application.port.out;

/** Puerto de salida: contrato de persistencia que la capa de aplicacion conoce. */
public interface RegistryRepositoryPort {
    void    initSchema()                                          throws Exception;
    void    deleteAll()                                           throws Exception;
    boolean existsById(int id)                                    throws Exception;
    void    save(int id, String name, int age, boolean alive)     throws Exception;
}