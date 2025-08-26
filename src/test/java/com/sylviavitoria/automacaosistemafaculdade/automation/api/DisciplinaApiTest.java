package com.sylviavitoria.automacaosistemafaculdade.automation.api;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

class DisciplinaApiTest {

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

    private String loginComoAluno() {
        return given()
                .contentType(ContentType.JSON)
                .body("{ \"email\": \"luanasilva@universidade.com\", \"senha\": \"123\" }")
                .when()
                .post("/api/v1/auth/login")
                .then()
                .statusCode(200)
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
    void adminDeveCadastrarDisciplinaComSucesso() {
        String tokenAdmin = loginComoAdmin();

        given()
                .header("Authorization", "Bearer " + tokenAdmin)
                .contentType(ContentType.JSON)
                .body("{ \"nome\": \"Banco de Dados\", \"codigo\": \"BD104\", \"professorId\": 1 }")
                .when()
                .post("/api/v1/disciplinas")
                .then()
                .statusCode(201)
                .body("id", notNullValue())
                .body("nome", equalTo("Banco de Dados"))
                .body("codigo", equalTo("BD104"));
    }

    @Test
    void naoDeveCadastrarDisciplinaComCodigoDuplicado() {
        String tokenAdmin = loginComoAdmin();

        given()
                .header("Authorization", "Bearer " + tokenAdmin)
                .contentType(ContentType.JSON)
                .body("{ \"nome\": \"Banco de Dados\", \"codigo\": \"BD2025\", \"professorId\": 1 }")
                .when()
                .post("/api/v1/disciplinas")
                .then()
                .statusCode(201);

        given()
                .header("Authorization", "Bearer " + tokenAdmin)
                .contentType(ContentType.JSON)
                .body("{ \"nome\": \"Banco de Dados Avançado\", \"codigo\": \"BD2025\", \"professorId\": 1 }")
                .when()
                .post("/api/v1/disciplinas")
                .then()
                .statusCode(409);
    }


    @Test
    void adminDeveListarDisciplinas() {
        String tokenAdmin = loginComoAdmin();

        given()
                .header("Authorization", "Bearer " + tokenAdmin)
                .when()
                .get("/api/v1/disciplinas")
                .then()
                .statusCode(200)
                .body("content", not(empty()));
    }

    @Test
    void professorDeveListarDisciplinas() {
        String tokenProfessor = loginComoProfessor();

        given()
                .header("Authorization", "Bearer " + tokenProfessor)
                .when()
                .get("/api/v1/disciplinas")
                .then()
                .statusCode(200)
                .body("content", not(empty()));
    }

    @Test
    void alunoDeveListarDisciplinas() {
        String tokenAluno = loginComoAluno();

        given()
                .header("Authorization", "Bearer " + tokenAluno)
                .when()
                .get("/api/v1/disciplinas")
                .then()
                .statusCode(200)
                .body("content", not(empty()));
    }

    @Test
    void naoDeveCadastrarDisciplinaSemNome() {
        String tokenAdmin = loginComoAdmin();

        given()
                .header("Authorization", "Bearer " + tokenAdmin)
                .contentType(ContentType.JSON)
                .body("{ \"codigo\": \"BD300\", \"professorId\": 1 }")
                .when()
                .post("/api/v1/disciplinas")
                .then()
                .statusCode(400);
    }

    @Test
    void naoDeveCadastrarDisciplinaSemCodigo() {
        String tokenAdmin = loginComoAdmin();

        given()
                .header("Authorization", "Bearer " + tokenAdmin)
                .contentType(ContentType.JSON)
                .body("{ \"nome\": \"Engenharia de Software\", \"professorId\": 1 }")
                .when()
                .post("/api/v1/disciplinas")
                .then()
                .statusCode(400);
    }

    @Test
    void naoDeveCadastrarDisciplinaSemProfessorId() {
        String tokenAdmin = loginComoAdmin();

        given()
                .header("Authorization", "Bearer " + tokenAdmin)
                .contentType(ContentType.JSON)
                .body("{ \"nome\": \"Redes de Computadores\", \"codigo\": \"RC101\" }")
                .when()
                .post("/api/v1/disciplinas")
                .then()
                .statusCode(400);
    }

    @Test
    void professorNaoDeveCadastrarDisciplina() {
        String tokenProfessor = loginComoProfessor();

        given()
                .header("Authorization", "Bearer " + tokenProfessor)
                .contentType(ContentType.JSON)
                .body("{ \"nome\": \"Arquitetura de Software\", \"codigo\": \"AS101\", \"professorId\": 1 }")
                .when()
                .post("/api/v1/disciplinas")
                .then()
                .statusCode(403);
    }

    @Test
    void alunoNaoDeveCadastrarDisciplina() {
        String tokenAluno = loginComoAluno();

        given()
                .header("Authorization", "Bearer " + tokenAluno)
                .contentType(ContentType.JSON)
                .body("{ \"nome\": \"Segurança da Informação\", \"codigo\": \"SI101\", \"professorId\": 1 }")
                .when()
                .post("/api/v1/disciplinas")
                .then()
                .statusCode(403);
    }
}
