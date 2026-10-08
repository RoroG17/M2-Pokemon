# M2Pokemon : Pokémon Management API

API REST Spring Boot (Java 17, Spring Boot 4.1.1) qui gère des dresseurs Pokémon et calcule leur niveau.

## Règle métier

Le niveau d'un dresseur est calculé ainsi :

1. Aucun Pokémon : niveau **1**.
2. Sinon, somme des niveaux de ses Pokémon.
3. **+20** s'il possède au moins 3 types distincts (bonus de diversité).
4. **+10** par Pokémon de niveau ≥ 50 (bonus d'élite).

Exemple : Pikachu (Électrik, 55), Salamèche (Feu, 20), Carapuce (Eau, 30) donnent 105 + 20 + 10 = **135**.

## Architecture

```
com.example.m2pokemon
├── entity/       Dresseur, Pokemon
├── repository/   DresseurRepository, PokemonRepository
├── dto/          DresseurDto
├── service/      DresseurService
├── controller/   DresseurController
└── exception/    DresseurNotFoundException, GlobalExceptionHandler
```

## Prérequis

- JDK 17
- Docker (pour MySQL), ou une installation MySQL 8 locale

## Lancer MySQL

```bash
docker compose up -d
```

Cela démarre MySQL 8 sur le port 3306, avec la base `pokemon_db` (utilisateur `root`, mot de passe `root`).

> Si la connexion (DBeaver ou application) échoue avec `Public Key Retrieval is not allowed`, ajouter `?allowPublicKeyRetrieval=true&useSSL=false` à l'URL JDBC.

## Lancer l'application

```bash
./mvnw spring-boot:run
```

Sous Windows PowerShell : `.\mvnw.cmd spring-boot:run`. Les tables sont créées automatiquement (`ddl-auto=update`).

Insérer quelques données pour tester :

```sql
INSERT INTO dresseur (nom, region) VALUES ('Sacha', 'Kanto');
INSERT INTO pokemon (nom, type, niveau, dresseur_id) VALUES ('Pikachu', 'Electrik', 55, 1);
INSERT INTO pokemon (nom, type, niveau, dresseur_id) VALUES ('Salameche', 'Feu', 20, 1);
INSERT INTO pokemon (nom, type, niveau, dresseur_id) VALUES ('Carapuce', 'Eau', 30, 1);
```

## API

### `GET /dresseurs/{id}`

```bash
curl http://localhost:8080/dresseurs/1
```

Réponse `200 OK` :

```json
{
  "nomDresseur": "Sacha",
  "nombrePokemons": 3,
  "niveauDresseur": 135
}
```

Réponse `404 Not Found` si le dresseur n'existe pas :

```json
{ "message": "Dresseur 99 n'existe pas" }
```

Le sujet mentionne « 201 Dresseur n'existe pas ». Nous avons retenu **404**, car 201 signifie *Created*, ce qui ne convient pas pour une ressource introuvable.

## Lancer les tests

```bash
./mvnw clean install
```

MySQL n'est pas nécessaire : les tests utilisent H2 en mémoire (`src/test/resources/application.properties` et `data.sql`).

| Couche | Annotation | Isolation |
|---|---|---|
| Repository | `@DataJpaTest` | Base H2 en mémoire, chargée avec `data.sql` |
| Service | `@ExtendWith(MockitoExtension.class)` | Repository mocké, aucune base ni Spring |
| Controller | `@WebMvcTest` | Service mocké avec `@MockitoBean`, requêtes via `MockMvc` |

Les tests suivent le nommage `should_..._when_...` et la structure Arrange / Act / Assert.
