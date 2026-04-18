# ${artifactId}

A Jenkins plugin generated from the **Jenkins Plugin TDD Archetype**.

---

## Project layout

```
src/main/java/${package}/
  SampleBuilder.java          # Example Freestyle + Pipeline build step
  ai/
    AiAgentBuilder.java       # AI agent build step — override invokeAgent()

src/main/resources/${package}/
  SampleBuilder/config.jelly  # UI form for SampleBuilder
  ai/AiAgentBuilder/config.jelly
  Messages.properties         # Localised display names

src/test/java/${package}/
  SampleBuilderTest.java      # JUnit 5 tests (TDD-first)
  ai/AiAgentBuilderTest.java
```

---

## Build commands

```bash
# Compile, run all tests, and package the .hpi
mvn verify

# Start a local Jenkins instance with the plugin installed (http://localhost:8080)
mvn hpi:run

# Run a single test class
mvn test -Dtest=SampleBuilderTest

# Skip tests (not recommended — use only during rapid iteration)
mvn package -DskipTests
```

---

## TDD workflow

1. Write a **failing** test in `src/test/java/${package}/`
2. Run `mvn test` — confirm the test fails
3. Implement the minimum code to make it pass
4. Run `mvn test` — all tests must be green
5. Refactor; repeat from step 1

Never commit with failing tests.

---

## Adding a new build step

1. Create `MyStep.java` extending `Builder implements SimpleBuildStep`
2. Annotate constructor with `@DataBoundConstructor`, optional fields with `@DataBoundSetter`
3. Add `@Symbol("myStep")` on the inner `DescriptorImpl` for Pipeline DSL support
4. Create `src/main/resources/${package}/MyStep/config.jelly` for the UI
5. Add a display name to `Messages.properties`
6. Write tests in `MyStepTest.java` — config roundtrip test first

---

## AI agent integration

`AiAgentBuilder` exposes a single override point:

```java
@Override
protected String invokeAgent(String prompt, TaskListener listener)
        throws IOException, InterruptedException {
    // Call your LLM API here and return the response text
}
```

### Connecting to Anthropic Claude

```java
// Add to pom.xml: com.squareup.okhttp3:okhttp
@Override
protected String invokeAgent(String prompt, TaskListener listener)
        throws IOException, InterruptedException {
    // See https://docs.anthropic.com/en/api/messages
    // POST https://api.anthropic.com/v1/messages
    // Header: x-api-key: <ANTHROPIC_API_KEY>
    throw new UnsupportedOperationException("implement me");
}
```

Store secrets as **Jenkins credentials** (Secret Text), never in plain text:

```java
StringCredentials creds = CredentialsProvider.findCredentialById(
    credentialId, StringCredentials.class, run);
String apiKey = creds.getSecret().getPlainText();
```

---

## Dependency management

- Versions are managed by the Jenkins BOM declared in `plugin-pom`.  
  Add new plugin dependencies **without** a `<version>` tag whenever possible.
- Dependabot / Renovate will keep `plugin-pom` and the BOM current via weekly PRs.
- Check the latest LTS at https://www.jenkins.io/download/lts/ and update  
  `<jenkins.version>` in `pom.xml` accordingly.

---

## Jenkins resources

- Plugin development guide: https://www.jenkins.io/doc/developer/
- Dependency management: https://www.jenkins.io/doc/developer/plugin-development/dependency-management/
- Jenkins test harness (JUnit 5): https://github.com/jenkinsci/jenkins-test-harness
- Plugin BOM: https://github.com/jenkinsci/bom
