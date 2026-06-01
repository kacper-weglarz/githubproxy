# GitHub Repositories Proxy API

A simple proxy service that integrates with the GitHub API to list non-fork repositories for a given user, including branch names and last commit SHAs.

---

## Features
* **Fork Filtering:** Lists only non-fork repositories.
* **Data Aggregation:** Includes branch names and the last commit SHA for each repository.
* **Custom Error Handling:** Returns a clean, 404 JSON response if the GitHub user is not found.
* **Clean Response:** Follows strict requirements - no unnecessary fields in the JSON output.

## Technical Stack
* **Java:** 25
* **Spring Boot:** 4.1.0-SNAPSHOT
* **Build Tool:** Gradle (Kotlin DSL)
* **Testing:** Wiremock (Integration Tests)

## API Usage
Once the application is running, you can access the proxy via:

`GET http://localhost:8080/api/repositories/{username}`

### Example Success Response:
```json
[
  {
    "repositoryName": "my-cool-project",
    "ownerLogin": "octocat",
    "branches": [
      {
        "name": "main",
        "lastCommitSha": "7fd1a60b01f91b314f59955a4e4d4e80d8edf11d"
      }
    ]
  }
]
