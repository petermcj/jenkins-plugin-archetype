package ${package}.ai;

import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.AbstractProject;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Builder;
import jenkins.tasks.SimpleBuildStep;
import org.jenkinsci.Symbol;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;

import java.io.IOException;

/**
 * Build step that runs an AI agent with a given prompt.
 *
 * <p>Override {@link #invokeAgent} to connect to your AI provider.
 * Out of the box the step logs the prompt and returns a placeholder response,
 * so all existing tests continue to pass while you wire up a real provider.
 *
 * <p>Supported providers (examples):
 * <ul>
 *   <li>Anthropic Claude  — POST https://api.anthropic.com/v1/messages
 *   <li>OpenAI            — POST https://api.openai.com/v1/chat/completions
 *   <li>Any OpenAI-compatible endpoint
 * </ul>
 *
 * <p>TDD workflow:
 * <ol>
 *   <li>Write a failing test in AiAgentBuilderTest
 *   <li>Override invokeAgent with real API call
 *   <li>Run {@code mvn test} to verify
 * </ol>
 */
public class AiAgentBuilder extends Builder implements SimpleBuildStep {

    private final String prompt;
    private String model = "claude-opus-4-7";
    private int maxTokens = 1024;

    @DataBoundConstructor
    public AiAgentBuilder(String prompt) {
        this.prompt = prompt;
    }

    public String getPrompt() {
        return prompt;
    }

    public String getModel() {
        return model;
    }

    @DataBoundSetter
    public void setModel(String model) {
        this.model = model;
    }

    public int getMaxTokens() {
        return maxTokens;
    }

    @DataBoundSetter
    public void setMaxTokens(int maxTokens) {
        this.maxTokens = maxTokens;
    }

    @Override
    public void perform(Run<?, ?> run, FilePath workspace, Launcher launcher, TaskListener listener)
            throws InterruptedException, IOException {
        listener.getLogger().println("[AI Agent] Model:  " + model);
        listener.getLogger().println("[AI Agent] Prompt: " + prompt);
        String response = invokeAgent(prompt, listener);
        listener.getLogger().println("[AI Agent] Response: " + response);
    }

    /**
     * Invokes the configured AI provider and returns the response text.
     *
     * <p>Override this method to integrate with a real LLM API.
     * The default implementation is a no-op placeholder so tests compile
     * and pass before the integration is wired up.
     */
    protected String invokeAgent(String agentPrompt, TaskListener listener)
            throws IOException, InterruptedException {
        return "AI provider not configured — override invokeAgent() to connect to Claude, OpenAI, etc.";
    }

    @Symbol("aiAgent")
    @Extension
    public static final class DescriptorImpl extends BuildStepDescriptor<Builder> {

        @Override
        public boolean isApplicable(Class<? extends AbstractProject> aClass) {
            return true;
        }

        @Override
        public String getDisplayName() {
            return "Run AI Agent";
        }
    }
}
