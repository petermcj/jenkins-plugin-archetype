package ${package};

import hudson.model.FreeStyleBuild;
import hudson.model.FreeStyleProject;
import org.junit.jupiter.api.Test;
import org.jvnet.hudson.test.JenkinsRule;
import org.jvnet.hudson.test.junit.jupiter.WithJenkins;

/**
 * Tests for {@link SampleBuilder}.
 *
 * <p>Each test method receives a fresh {@link JenkinsRule} instance injected
 * by the {@code @WithJenkins} JUnit 5 extension — no shared state between tests.
 *
 * <p>TDD cycle:
 * <ol>
 *   <li>Add a failing test here
 *   <li>Implement the feature in {@link SampleBuilder}
 *   <li>Run {@code mvn test} — all tests must pass before committing
 * </ol>
 */
@WithJenkins
class SampleBuilderTest {

    @Test
    void configRoundtrip(JenkinsRule jenkins) throws Exception {
        FreeStyleProject project = jenkins.createFreeStyleProject();
        project.getBuildersList().add(new SampleBuilder("test"));
        project = jenkins.configRoundtrip(project);
        jenkins.assertEqualDataBoundBeans(
                new SampleBuilder("test"),
                project.getBuildersList().get(0));
    }

    @Test
    void greetsByName(JenkinsRule jenkins) throws Exception {
        FreeStyleProject project = jenkins.createFreeStyleProject();
        project.getBuildersList().add(new SampleBuilder("World"));
        FreeStyleBuild build = jenkins.buildAndAssertSuccess(project);
        jenkins.assertLogContains("Hello, World!", build);
    }

    @Test
    void greetsInFrench(JenkinsRule jenkins) throws Exception {
        FreeStyleProject project = jenkins.createFreeStyleProject();
        SampleBuilder builder = new SampleBuilder("World");
        builder.setUseFrench(true);
        project.getBuildersList().add(builder);
        FreeStyleBuild build = jenkins.buildAndAssertSuccess(project);
        jenkins.assertLogContains("Bonjour, World!", build);
    }

    @Test
    void frenchFlagDefaultsToFalse(JenkinsRule jenkins) throws Exception {
        SampleBuilder builder = new SampleBuilder("test");
        org.junit.jupiter.api.Assertions.assertFalse(builder.isUseFrench());
    }
}
