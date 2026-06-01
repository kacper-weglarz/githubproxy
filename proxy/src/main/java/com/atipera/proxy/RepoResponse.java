package com.atipera.proxy;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

record RepoResponse(@JsonProperty("repositoryName") String repositoryName,
                    @JsonProperty("ownerLogin") String ownerLogin, List<BranchInfo> branches) {}