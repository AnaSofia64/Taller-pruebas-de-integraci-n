# Taller Pruebas de Integracion - Registraduria

**Curso:** Testing y Validacion de Software  
**Universidad de La Sabana - Maestria en Ingenieria de Software**

## Integrantes
*Ana Sofia Rodriguez y Juan Camilo Silva*

## Estructura
src/main/java/edu/unisabana/tyvs/registry/
├─ domain/model/        → Person, Gender, RegisterResult, rq/PersonDTO
├─ application/
│   ├─ port/out/        → RegistryRepositoryPort (interfaz)
│   └─ usecase/         → Registry (caso de uso)
├─ infrastructure/
│   └─ persistence/     → RegistryRepository (JDBC/H2), RegistryRecord
├─ delivery/rest/        → RegistryController
├─ config/              → RegistryConfig
└─ RegistryApplication.java
src/test/java/edu/unisabana/tyvs/registry/
├─ application/usecase/
│   ├─ RegistryTest.java          (6 tests - H2 real)
│   └─ RegistryWithMockTest.java  (5 tests - Mockito)
└─ delivery/rest/
└─ RegistryControllerIT.java  (5 tests - sistema HTTP)
## Ejecucion
```bash
mvn clean test      # H2 + Mockito (sin IT)
mvn clean verify    # todo + pruebas HTTP + JaCoCo
mvn spring-boot:run # levantar el servidor
```

## Probar el endpoint manualmente
```bash
curl -X POST http://localhost:8080/register \
  -H "Content-Type: application/json" \
  -d '{"name":"Ana","id":1,"age":30,"gender":"FEMALE","alive":true}'
```

## Matriz de pruebas

| Caso               | Entrada              | Resultado   | Tipo   | Test                                      |
|--------------------|----------------------|-------------|--------|-------------------------------------------|
| Persona valida     | age=30, id=100       | VALID       | H2     | shouldRegisterValidPerson                 |
| Duplicado          | mismo id x2          | DUPLICATED  | H2     | shouldPersistValidVoterAndRejectDuplicates|
| Persona muerta     | alive=false          | DEAD        | H2     | shouldRejectDeadPersonAndNotPersist       |
| Menor de edad      | age=17               | UNDERAGE    | H2     | shouldRejectUnderagePersonAndNotPersist   |
| ID invalido        | id=0                 | INVALID     | H2     | shouldRejectInvalidIdAndNotPersist        |
| Edad > 120         | age=121              | INVALID_AGE | H2     | shouldRejectAgeOver120AndNotPersist       |
| Duplicado (mock)   | existsById=true      | DUPLICATED  | Mock   | shouldReturnDuplicatedWhenRepoSaysExists  |
| Valido (mock)      | existsById=false     | VALID       | Mock   | shouldCallSaveWhenPersonIsValid           |
| Excepcion SQL      | repo lanza Exception | Exception   | Mock   | shouldPropagateExceptionFromRepo          |
| Muerto (mock)      | alive=false          | DEAD        | Mock   | shouldNotInteractWithRepoForDeadPerson    |
| Menor (mock)       | age=16               | UNDERAGE    | Mock   | shouldNotSaveUnderagePerson               |
| HTTP valido        | POST valido          | 200 VALID   | HTTP   | shouldReturnValidForValidPerson           |
| HTTP duplicado     | POST x2              | 200 DUPLICATED | HTTP| shouldReturnDuplicatedOnSecondRegistration|
| HTTP muerto        | alive=false          | 200 DEAD    | HTTP   | shouldReturnDeadForDeadPerson             |
| HTTP menor         | age=17               | 200 UNDERAGE| HTTP   | shouldReturnUnderageForMinor              |
| HTTP gender malo   | gender=ALIEN         | 400         | HTTP   | shouldReturnBadRequestForInvalidGender    |