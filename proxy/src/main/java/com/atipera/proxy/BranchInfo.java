package com.atipera.proxy;

import com.fasterxml.jackson.annotation.JsonProperty;

record BranchInfo(@JsonProperty("name") String name, @JsonProperty("lastCommitSha") String lastCommitSha) {}