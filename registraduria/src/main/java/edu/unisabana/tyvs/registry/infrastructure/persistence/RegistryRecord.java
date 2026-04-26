package edu.unisabana.tyvs.registry.infrastructure.persistence;

public class RegistryRecord {
    private final int     id;
    private final String  name;
    private final int     age;
    private final boolean alive;

    public RegistryRecord(int id, String name, int age, boolean alive) {
        this.id = id; this.name = name; this.age = age; this.alive = alive;
    }

    public int     getId()     { return id; }
    public String  getName()   { return name; }
    public int     getAge()    { return age; }
    public boolean isAlive()   { return alive; }
}