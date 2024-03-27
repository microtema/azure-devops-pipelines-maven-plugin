package de.microtema.maven.plugin.github.workflow;

import de.microtema.maven.plugin.github.workflow.job.*;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import java.util.*;

import static de.microtema.maven.plugin.github.workflow.PipelineGeneratorUtil.*;

@Mojo(name = "generate", defaultPhase = LifecyclePhase.COMPILE)
public class PipelineGeneratorMojo extends AbstractMojo {

    String pipelineFileName = "azure-pipelines.yml";

    @Parameter(defaultValue = "${project}", required = true, readonly = true)
    MavenProject project;

    @Parameter(property = "variables")
    Map<String, String> variables = new LinkedHashMap<>();

    @Parameter(property = "stages")
    Map<String, String> stages = new LinkedHashMap<>();

    @Parameter(property = "down-streams")
    Map<String, String> downStreams = new LinkedHashMap<>();

    @Parameter(property = "schedule-cron")
    String scheduleCron = "0 12 * * 0"; // Weekly Sunday build

    @Parameter(property = "schedule-branches")
    List<String> scheduleBranches = new ArrayList<>();

    final List<TemplateStageService> templateStageServices = new ArrayList<>();
    final LinkedHashMap<String, String> defaultVariables = new LinkedHashMap<>();

    String appName;

    public void execute() {

        appName = getAppDisplayName();

        // Skip maven sub modules
        if (!PipelineGeneratorUtil.isGitRepo(project)) {

            logMessage("Skip maven module: " + appName + " since it is not a git repo!");

            return;
        }

        if (PipelineGeneratorUtil.isDotNetRepo(project)) {

            new DotnetPipelineGeneratorMojo(this).execute();

            return;
        }

        if (PipelineGeneratorUtil.isNodeJsRepo(project)) {

            NpmPipelineGeneratorMojo npmPipelineGeneratorMojo = new NpmPipelineGeneratorMojo(this);

            npmPipelineGeneratorMojo.execute();

            return;
        }

        throw new RuntimeException("Unsupported project type");
    }

    public Map<String, Object> getVariables() {

        return new LinkedHashMap<>(variables);
    }

    public String getAppDisplayName() {

        return Optional.ofNullable(project.getName()).orElse(project.getArtifactId());
    }

    public MavenProject getProject() {
        return this.project;
    }
}
