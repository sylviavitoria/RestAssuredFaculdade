package com.sylviavitoria.automacaosistemafaculdade.automation.api;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.notNullValue;

class AuthApiTest {

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
                .body("{ \"email\": \"joao.silva@email.com\", \"senha\": \"123\" }")
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
                .body("{ \"email\": \"maria.silva@universidade.com\", \"senha\": \"123\" }")
                .when()
                .post("/api/v1/auth/login")
                .then()
                .statusCode(200)
                .body("accessToken", notNullValue())
                .extract()
                .path("accessToken");
    }

    @Test
    void deveFazerLoginAdminERetornarToken() {
        String tokenAdmin = loginComoAdmin();
        System.out.println("Token do Admin: " + tokenAdmin);
    }

    @Test
    void deveFazerLoginAlunoERetornarToken() {
        String tokenAluno = loginComoAluno();
        System.out.println("Token do Aluno: " + tokenAluno);
    }

    @Test
    void deveFazerLoginProfessorERetornarToken() {
        String tokenProfessor = loginComoProfessor();
        System.out.println("Token do Professor: " + tokenProfessor);
    }

    @Test
    void naoDeveFazerLoginComSenhaErrada() {
        given()
                .contentType(ContentType.JSON)
                .body("{ \"email\": \"admin@exemplo.com\", \"senha\": \"errada\" }")
                .when()
                .post("/api/v1/auth/login")
                .then()
                .statusCode(401);
    }

    @Test
    void naoDeveFazerLoginComUsuarioInexistente() {
        given()
                .contentType(ContentType.JSON)
                .body("{ \"email\": \"usuario@inexistente.com\", \"senha\": \"123\" }")
                .when()
                .post("/api/v1/auth/login")
                .then()
                .statusCode(401);
    }

    @Test
    void adminPodeAcessarTodosAlunos() {
        String tokenAdmin = loginComoAdmin();

        given()
                .header("Authorization", "Bearer " + tokenAdmin)
                .when()
                .get("/api/v1/alunos")
                .then()
                .statusCode(200);
    }

    @Test
    void alunoNaoPodeAcessarTodosAlunos() {
        String tokenAluno = loginComoAluno();

        given()
                .header("Authorization", "Bearer " + tokenAluno)
                .when()
                .get("/api/v1/alunos")
                .then()
                .statusCode(403);
    }

    @Test
    void acessoSemTokenRetorna403() {
        given()
                .when()
                .get("/api/v1/alunos")
                .then()
                .statusCode(403);
    }
}
