package ${package};

import hudson.model.FreeStyleBuild;
import hudson.model.FreeStyleProject;
import hudson.model.TaskListener;
import org.junit.jupiter.api.Test;
import org.jvnet.hudson.test.JenkinsRule;
import org.jvnet.hudson.test.junit.jupiter.WithJenkins;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests for {@link AiAgentBuilder}.
 *
 * <p>The {@link AiAgentBuilder#invokeAgent} hook keeps unit tests fast and
 * provider-independent: inject a stub response instead of hitting a real API.
 *
 * <p>TDD workflow:
 * <ol>
 *   <li>Write a failing test
 *   <li>Implement / override {@code invokeAgent} in a subclass or the real class
 *   <li>All tests must pass before merging
 * </ol>
 */
@WithJenkins
class AiAgentBuilderTest {

    @Test
    void logsPromptDuringBuild(JenkinsRule jenkins) throws Exception {
        FreeStyleProject project = jenkins.createFreeStyleProject();
        project.getBuildersList().add(new AiAgentBuilder("What is 2+2?"));
        FreeStyleBuild build = jenkins.buildAndAssertSuccess(project);
        jenkins.assertLogContains("[AI Agent] Prompt: What is 2+2?", build);
    }

    @Test
    void logsConfiguredModel(JenkinsRule jenkins) throws Exception {
        FreeStyleProject project = jenkins.createFreeStyleProject();
        AiAgentBuilder builder = new AiAgentBuilder("ping");
        builder.setModel("claude-sonnet-4-6");
        project.getBuildersList().add(builder);
        FreeStyleBuild build = jenkins.buildAndAssertSuccess(project);
        jenkins.assertLogContains("[AI Agent] Model:  claude-sonnet-4-6", build);
    }

    @Test
    void logsAgentResponse(JenkinsRule jenkins) throws Exception {
        FreeStyleProject project = jenkins.createFreeStyleProject();
        project.getBuildersList().add(new StubAiAgentBuilder("compute", "42"));
        FreeStyleBuild build = jenkins.buildAndAssertSuccess(project);
        jenkins.assertLogContains("[AI Agent] Response: 42", build);
    }

    @Test
    void configRoundtrip(JenkinsRule jenkins) throws Exception {
        FreeStyleProject project = jenkins.createFreeStyleProject();
        AiAgentBuilder original = new AiAgentBuilder("hello");
        original.setModel("claude-haiku-4-5");
        original.setMaxTokens(512);
        project.getBuildersList().add(original);
        project = jenkins.configRoundtrip(project);
        AiAgentBuilder restored = (AiAgentBuilder) project.getBuildersList().get(0);
        jenkins.assertEqualDataBoundBeans(original, restored);
    }

    @Test
    void maxTokensDefaultsTo1024() {
        assertEquals(1024, new AiAgentBuilder("test").getMaxTokens());
    }

    // ---------------------------------------------------------------------------
    // Stub helper — isolates tests from real AI provider calls
    // ---------------------------------------------------------------------------

    private static final class StubAiAgentBuilder extends AiAgentBuilder {

        private final String stubbedResponse;

        StubAiAgentBuilder(String prompt, String stubbedResponse) {
            super(prompt);
            this.stubbedResponse = stubbedResponse;
        }

        @Override
        protected String invokeAgent(String agentPrompt, TaskListener listener)
                throws IOException, InterruptedException {
            return stubbedResponse;
        }
    }
}
