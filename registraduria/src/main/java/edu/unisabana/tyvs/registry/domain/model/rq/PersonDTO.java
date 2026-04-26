package edu.unisabana.tyvs.registry.domain.model.rq;

public class PersonDTO {
    private String  name;
    private int     id;
    private int     age;
    private String  gender;
    private boolean alive;

    public PersonDTO() {}

    public String  getName()             { return name; }
    public void    setName(String name)  { this.name = name; }
    public int     getId()               { return id; }
    public void    setId(int id)         { this.id = id; }
    public int     getAge()              { return age; }
    public void    setAge(int age)       { this.age = age; }
    public String  getGender()           { return gender; }
    public void    setGender(String g)   { this.gender = g; }
    public boolean isAlive()             { return alive; }
    public void    setAlive(boolean a)   { this.alive = a; }
}