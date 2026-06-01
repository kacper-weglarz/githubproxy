package com.atipera.proxy;

import org.springframework.stereotype.Service;
import java.util.List;

@Service
class GithubProxyService {

    private final GithubClient githubClient;

    GithubProxyService(GithubClient githubClient) {
        this.githubClient = githubClient;
    }

    List<RepoResponse> getUserRepositories(String username) {
        List<GithubRepoDTO> allRepos = githubClient.getUserRepositories(username);
        return allRepos.stream()
                .filter(repo -> !repo.fork())
                .map(repo -> {
                    List<GithubBranchDTO> branches = githubClient.getBranches(username, repo.name());
                    List<BranchInfo> branchInfos = branches.stream()
                            .map(branch -> new BranchInfo(branch.name(), branch.commit().sha()))
                            .toList();
                    return new RepoResponse(repo.name(), repo.owner().login(), branchInfos);
                })
                .toList();
    }
}