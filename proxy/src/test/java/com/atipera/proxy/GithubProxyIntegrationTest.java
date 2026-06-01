package com.atipera.proxy;

import com.github.tomakehurst.wiremock.WireMockServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import java.util.List;
import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class GithubProxyIntegrationTest {

    @LocalServerPort
    private int port;

    private static WireMockServer wireMockServer;

    private RestClient restClient;

    @BeforeAll
    static void startWiremock() {
        wireMockServer = new WireMockServer(0);
        wireMockServer.start();
    }

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("base-url", () -> wireMockServer.baseUrl());
    }

    @AfterAll
    static void stopWiremock() {
        wireMockServer.stop();
    }

    @BeforeEach
    void setUp() {
        restClient = RestClient.builder()
                .baseUrl("http://localhost:" + port)
                .build();
    }

    @Test
    void shouldReturnNotFoundWhenUserDoesNotExist() {
        wireMockServer.stubFor(get(urlEqualTo("/users/unknownUser/repos"))
                .willReturn(aResponse()
                        .withStatus(404)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"message\": \"Not Found\"}")));
        try {
            restClient.get()
                    .uri("/api/repositories/unknownUser")
                    .retrieve()
                    .toBodilessEntity();
        } catch (HttpClientErrorException ex) {
            assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        }
    }

    @Test
    void shouldReturnOnlyNonForkRepositoriesWithBranches() {
        wireMockServer.stubFor(get(urlEqualTo("/users/testUser/repos"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody("""
                        [
                          {"name": "repo-1", "fork": false, "owner": {"login": "testUser"}},
                          {"name": "repo-2", "fork": true, "owner": {"login": "testUser"}}
                        ]
                        """)));
        wireMockServer.stubFor(get(urlEqualTo("/repos/testUser/repo-1/branches"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody("[{\"name\": \"main\", \"commit\": {\"sha\": \"12345\"}}]")));
        List<RepoResponse> result = restClient.get()
                .uri("/api/repositories/testUser")
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});

        assertThat(result).hasSize(1);
        assertThat(result.get(0).repositoryName()).isEqualTo("repo-1");
        assertThat(result.get(0).branches()).hasSize(1);
        assertThat(result.get(0).branches().get(0).name()).isEqualTo("main");
    }
}