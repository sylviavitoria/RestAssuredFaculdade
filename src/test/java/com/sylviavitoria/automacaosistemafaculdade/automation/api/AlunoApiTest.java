package com.sylviavitoria.automacaosistemafaculdade.automation.api;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

class AlunoApiTest {

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

    @Test
    void adminDeveCadastrarAlunoComSucesso() {
        String tokenAdmin = loginComoAdmin();

        given()
                .header("Authorization", "Bearer " + tokenAdmin)
                .contentType(ContentType.JSON)
                .body("{ \"nome\": \"Carlos Souza\", \"email\": \"carlos@gmail.com\", \"matricula\": \"2025004\", \"senha\": \"123\" }")
                .when()
                .post("/api/v1/alunos")
                .then()
                .statusCode(201)
                .body("id", notNullValue())
                .body("nome", equalTo("Carlos Souza"));
    }

    @Test
    void naoDeveCadastrarAlunoComEmailDuplicado() {
        String tokenAdmin = loginComoAdmin();

        given()
                .header("Authorization", "Bearer " + tokenAdmin)
                .contentType(ContentType.JSON)
                .body("{ \"nome\": \"Joao Carlos Souza\", \"email\": \"luanasilva@universidade.com\", \"matricula\": \"2025999\", \"senha\": \"123\" }")
                .when()
                .post("/api/v1/alunos")
                .then()
                .statusCode(409);
    }

    @Test
    void adminDeveListarAlunosPaginados() {
        String tokenAdmin = loginComoAdmin();

        given()
                .header("Authorization", "Bearer " + tokenAdmin)
                .when()
                .get("/api/v1/alunos?page=0&size=10")
                .then()
                .statusCode(200)
                .body("content", not(empty()));
    }

    @Test
    void alunoNaoDeveCadastrarDisciplina() {
        String tokenAluno = loginComoAluno();

        given()
                .header("Authorization", "Bearer " + tokenAluno)
                .contentType(ContentType.JSON)
                .body("{ \"nome\": \"Fabrica de Software\", \"codigo\": \"DT101\", \"professorId\": 1 }")
                .when()
                .post("/api/v1/disciplinas")
                .then()
                .statusCode(403);
    }

    @Test
    void alunoNaoDeveCadastrarProfessor() {
        String tokenAluno = loginComoAluno();

        given()
                .header("Authorization", "Bearer " + tokenAluno)
                .contentType(ContentType.JSON)
                .body("{ \"nome\": \"Mariana Souza\", \"email\": \"marianas@gmail.com\", \"senha\": \"123\" }")
                .when()
                .post("/api/v1/professores")
                .then()
                .statusCode(403);
    }

    @Test
    void alunoNaoDeveCadastrarAluno() {
        String tokenAluno = loginComoAluno();

        given()
                .header("Authorization", "Bearer " + tokenAluno)
                .contentType(ContentType.JSON)
                .body("{ \"nome\": \"Alan Vieria\", \"email\": \"matins@gmail.com\", \"matricula\": \"2025003\", \"senha\": \"123\" }")
                .when()
                .post("/api/v1/alunos")
                .then()
                .statusCode(403);
    }


    @Test
    void naoDeveCadastrarAlunoSemNome() {
        String tokenAdmin = loginComoAdmin();

        given()
                .header("Authorization", "Bearer " + tokenAdmin)
                .contentType(ContentType.JSON)
                .body("{ \"email\": \"marioo@gmail.com\", \"matricula\": \"2025004\", \"senha\": \"123\" }")
                .when()
                .post("/api/v1/alunos")
                .then()
                .statusCode(400);
    }

    @Test
    void naoDeveCadastrarAlunoSemEmail() {
        String tokenAdmin = loginComoAdmin();

        given()
                .header("Authorization", "Bearer " + tokenAdmin)
                .contentType(ContentType.JSON)
                .body("{ \"nome\": \"Gabriel Souza\", \"matricula\": \"2025005\", \"senha\": \"123\" }")
                .when()
                .post("/api/v1/alunos")
                .then()
                .statusCode(400);
    }

    @Test
    void naoDeveCadastrarAlunoComMatriculaDuplicada() {
        String tokenAdmin = loginComoAdmin();

        given()
                .header("Authorization", "Bearer " + tokenAdmin)
                .contentType(ContentType.JSON)
                .body("{ \"nome\": \"Eduardo Silva\", \"email\": \"eduardosilva@gmail.com\", \"matricula\": \"2025010\", \"senha\": \"123\" }")
                .when()
                .post("/api/v1/alunos")
                .then()
                .statusCode(201);

        given()
                .header("Authorization", "Bearer " + tokenAdmin)
                .contentType(ContentType.JSON)
                .body("{ \"nome\": \"Jonas Carlos Mario\", \"email\": \"mariosilva@gmail.com\", \"matricula\": \"2025010\", \"senha\": \"123\" }")
                .when()
                .post("/api/v1/alunos")
                .then()
                .statusCode(409);
    }


    @Test
    void naoDeveCadastrarAlunoSemMatricula() {
        String tokenAdmin = loginComoAdmin();

        given()
                .header("Authorization", "Bearer " + tokenAdmin)
                .contentType(ContentType.JSON)
                .body("{ \"nome\": \"Julia Viana\", \"email\": \"juliav@gmail.com\", \"senha\": \"123\" }")
                .when()
                .post("/api/v1/alunos")
                .then()
                .statusCode(400);
    }

    @Test
    void naoDeveCadastrarAlunoSemSenha() {
        String tokenAdmin = loginComoAdmin();

        given()
                .header("Authorization", "Bearer " + tokenAdmin)
                .contentType(ContentType.JSON)
                .body("{ \"nome\": \"Emanuelesilva\", \"email\": \"emanuelesilva@gmail.com\", \"matricula\": \"2025006\" }")
                .when()
                .post("/api/v1/alunos")
                .then()
                .statusCode(400);
    }
}