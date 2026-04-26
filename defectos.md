# Registro de Defectos - Taller Pruebas de Integracion

## Defecto 01
- **Caso:** Persona duplicada no era rechazada con BD H2
- **Esperado:** `DUPLICATED`
- **Obtenido:** `VALID` (el caso de uso no consultaba el repo antes de persistir)
- **Causa probable:** Faltaba llamar `existsById` antes de `save`.
- **Estado:** Cerrado ✅

## Defecto 02
- **Caso:** Persona fallecida era persistida en la BD
- **Esperado:** No debe guardarse, resultado DEAD
- **Obtenido:** Se guardaba igual en la BD
- **Causa probable:** La validacion de `isAlive()` estaba despues del `repo.save()`.
- **Estado:** Cerrado ✅

## Defecto 03
- **Caso:** Excepcion SQL no manejada en el controlador
- **Esperado:** HTTP 500 con mensaje claro
- **Obtenido:** NullPointerException sin respuesta HTTP coherente
- **Causa probable:** Falta de try-catch en RegistryController.
- **Estado:** Cerrado ✅