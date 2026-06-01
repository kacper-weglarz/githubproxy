package com.atipera.proxy;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/api/repositories")
class GithubProxyController {

    private final GithubProxyService service;

    GithubProxyController(GithubProxyService service) {
        this.service = service;
    }

    @GetMapping(value = "/{username}")
    List<RepoResponse> getRepositories(@PathVariable String username) {

        return service.getUserRepositories(username);
    }
}