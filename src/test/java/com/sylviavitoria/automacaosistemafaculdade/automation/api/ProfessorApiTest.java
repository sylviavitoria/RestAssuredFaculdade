package com.sylviavitoria.automacaosistemafaculdade.automation.api;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

class ProfessorApiTest {

    private static final String BASE_URI = "http://localhost:8080";

    @BeforeAll
    static void setup() {
        RestAssured.baseURI = BASE_URI;
    }

    private String loginComoAdmin() {
        return given()
                .contentType(ContentType.JSON)
                .body("{ \"email\": \"admin@exemplo.com\", \"senha\": \"123\" }")
                .when()
                .post("/api/v1/auth/login")
                .then()
                .statusCode(200)
                .body("accessToken", notNullValue())
                .extract()
                .path("accessToken");
    }
    private String loginComoProfessor() {
        return given()
                .contentType(ContentType.JSON)
                .body("{ \"email\": \"lucassilva@universidade.com\", \"senha\": \"123\" }")
                .when()
                .post("/api/v1/auth/login")
                .then()
                .statusCode(200)
                .extract()
                .path("accessToken");
    }

    @Test
    void adminDeveCadastrarProfessorComSucesso() {
        String tokenAdmin = loginComoAdmin();

        given()
                .header("Authorization", "Bearer " + tokenAdmin)
                .contentType(ContentType.JSON)
                .body("{ \"nome\": \"Mariana Silva\", \"email\": \"marianal@gmail.com\", \"senha\": \"123\" }")
                .when()
                .post("/api/v1/professores")
                .then()
                .statusCode(201)
                .body("id", notNullValue())
                .body("nome", equalTo("Mariana Silva"));
    }

    @Test
    void naoDeveCadastrarProfessorComEmailDuplicado() {
        String tokenAdmin = loginComoAdmin();

        given()
                .header("Authorization", "Bearer " + tokenAdmin)
                .contentType(ContentType.JSON)
                .body("{ \"nome\": \"João Oliveira\", \"email\": \"lucassilva@universidade.com\", \"senha\": \"123\" }")
                .when()
                .post("/api/v1/professores")
                .then()
                .statusCode(409);
    }

    @Test
    void adminDeveListarProfessoresPaginados() {
        String tokenAdmin = loginComoAdmin();

        given()
                .header("Authorization", "Bearer " + tokenAdmin)
                .when()
                .get("/api/v1/professores?page=0&size=10")
                .then()
                .statusCode(200)
                .body("content", not(empty()));
    }


    @Test
    void professorNaoDeveCadastrarDisciplina() {
        String tokenProfessor = loginComoProfessor();

        given()
                .header("Authorization", "Bearer " + tokenProfessor)
                .contentType(ContentType.JSON)
                .body("{ \"nome\": \"Estruturas de Dados\", \"codigo\": \"ED101\", \"professorId\": 1 }")
                .when()
                .post("/api/v1/disciplinas")
                .then()
                .statusCode(403);
    }

    @Test
    void professorNaoDeveCadastrarAluno() {
        String tokenProfessor = loginComoProfessor();

        given()
                .header("Authorization", "Bearer " + tokenProfessor)
                .contentType(ContentType.JSON)
                .body("{ \"nome\": \"Carlos Souza\", \"email\": \"carlos@email.com\", \"senha\": \"123\", \"matricula\": \"2025001\" }")
                .when()
                .post("/api/v1/alunos")
                .then()
                .statusCode(403);
    }

    @Test
    void professorNaoDeveCadastrarOutroProfessor() {
        String tokenProfessor = loginComoProfessor();

        given()
                .header("Authorization", "Bearer " + tokenProfessor)
                .contentType(ContentType.JSON)
                .body("{ \"nome\": \"Outro Professor\", \"email\": \"outro.prof@gmail.com\", \"senha\": \"123\" }")
                .when()
                .post("/api/v1/professores")
                .then()
                .statusCode(403);
    }

    @Test
    void naoDeveCadastrarProfessorSemNome() {
        String tokenAdmin = loginComoAdmin();

        given()
                .header("Authorization", "Bearer " + tokenAdmin)
                .contentType(ContentType.JSON)
                .body("{ \"email\": \"prof.sem.nome@gmail.com\", \"senha\": \"123\" }")
                .when()
                .post("/api/v1/professores")
                .then()
                .statusCode(400);
    }

    @Test
    void naoDeveCadastrarProfessorSemEmail() {
        String tokenAdmin = loginComoAdmin();

        given()
                .header("Authorization", "Bearer " + tokenAdmin)
                .contentType(ContentType.JSON)
                .body("{ \"nome\": \"Professor Sem Email\", \"senha\": \"123\" }")
                .when()
                .post("/api/v1/professores")
                .then()
                .statusCode(400);
    }

    @Test
    void naoDeveCadastrarProfessorSemSenha() {
        String tokenAdmin = loginComoAdmin();

        given()
                .header("Authorization", "Bearer " + tokenAdmin)
                .contentType(ContentType.JSON)
                .body("{ \"nome\": \"Professor Sem Senha\", \"email\": \"prof.sem.senha@gmail.com\" }")
                .when()
                .post("/api/v1/professores")
                .then()
                .statusCode(400);
    }
}

