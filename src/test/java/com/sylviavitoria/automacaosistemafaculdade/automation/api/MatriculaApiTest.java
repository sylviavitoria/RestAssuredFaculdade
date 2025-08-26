package com.sylviavitoria.automacaosistemafaculdade.automation.api;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

class MatriculaApiTest {

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
                .body("accessToken", notNullValue())
                .extract()
                .path("accessToken");
    }

    @Test
    void adminDeveCadastrarMatriculaComSucesso() {
        String tokenAdmin = loginComoAdmin();

        given()
                .header("Authorization", "Bearer " + tokenAdmin)
                .contentType(ContentType.JSON)
                .body("{ \"alunoId\": 1, \"disciplinaId\": 1 }")
                .when()
                .post("/api/v1/matriculas")
                .then()
                .statusCode(201)
                .body("id", notNullValue())
                .body("aluno.id", equalTo(1))
                .body("disciplina.id", equalTo(1));
    }


    @Test
    void naoDeveCadastrarMatriculaDuplicada() {
        String tokenAdmin = loginComoAdmin();

        given()
                .header("Authorization", "Bearer " + tokenAdmin)
                .contentType(ContentType.JSON)
                .body("{ \"alunoId\": 1, \"disciplinaId\": 1 }")
                .when()
                .post("/api/v1/matriculas")
                .then()
                .statusCode(409);
    }
    @Test
    void professorNaoDeveCadastrarMatricula() {
        String tokenProfessor = loginComoProfessor();

        given()
                .header("Authorization", "Bearer " + tokenProfessor)
                .contentType(ContentType.JSON)
                .body("{ \"alunoId\": 2, \"disciplinaId\": 1 }")
                .when()
                .post("/api/v1/matriculas")
                .then()
                .statusCode(403);
    }

    @Test
    void naoDeveCadastrarMatriculaSemAlunoId() {
        String tokenAdmin = loginComoAdmin();

        given()
                .header("Authorization", "Bearer " + tokenAdmin)
                .contentType(ContentType.JSON)
                .body("{ \"disciplinaId\": 1 }")
                .when()
                .post("/api/v1/matriculas")
                .then()
                .statusCode(400);
    }

    @Test
    void naoDeveCadastrarMatriculaSemDisciplinaId() {
        String tokenAdmin = loginComoAdmin();

        given()
                .header("Authorization", "Bearer " + tokenAdmin)
                .contentType(ContentType.JSON)
                .body("{ \"alunoId\": 1 }")
                .when()
                .post("/api/v1/matriculas")
                .then()
                .statusCode(400);
    }

}
