package de.microtema.maven.plugin.github.workflow;

import de.microtema.maven.plugin.github.workflow.job.*;
import de.microtema.maven.plugin.github.workflow.model.MetaData;
import de.microtema.model.converter.util.ClassUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.io.PrintWriter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

    @Parameter(property = "undeploy")
    boolean undeploy;

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

        if (PipelineGeneratorUtil.isTerraformRepo(project)) {

            TerraformPipelineGeneratorMojo terraformPipelineGeneratorMojo = new TerraformPipelineGeneratorMojo(this);

            terraformPipelineGeneratorMojo.execute();

            return;
        }
    }

    public void applyDefaultVariables() {

        defaultVariables.put("APP_NAME", project.getArtifactId());
        defaultVariables.put("APP_DISPLAY_NAME", appName);

        defaultVariables.put("GIT_COMMIT", "$(Build.SourceVersion)");
        defaultVariables.put("REPO_NAME", "$(Build.Repository.Name)");
        defaultVariables.put("BRANCH_NAME", "$[replace(variables['Build.SourceBranch'], 'refs/heads/', '')]");

        defaultVariables.put("isDevelop", "$[eq(variables['Build.SourceBranch'], 'refs/heads/develop')]");
        defaultVariables.put("isRelease", "$[startsWith(variables['Build.SourceBranch'], 'refs/heads/release/')]");
        defaultVariables.put("isMaster", "$[eq(variables['Build.SourceBranch'], 'refs/heads/master')]");

        // apply all custom variables
        defaultVariables.putAll(variables);
    }

    public String getStagesTemplate(MetaData metaData, List<TemplateStageService> templateStageServices) {

        return templateStageServices.stream()
                .map(it -> it.getTemplate(this, metaData))
                .filter(Objects::nonNull)
                .map(it -> PipelineGeneratorUtil.trimEmptyLines(it, 2))
                .collect(Collectors.joining("\n\n"));
    }

    public MavenProject getProject() {

        return project;
    }

    public Map<String, String> getDownStreams() {

        return new LinkedHashMap<>(downStreams);
    }

    public Map<String, Object> getVariables() {

        return new LinkedHashMap<>(variables);
    }

    public String getAppDisplayName() {

        return Optional.ofNullable(project.getName()).orElse(project.getArtifactId());
    }

    public boolean isUnDeploy() {

        return undeploy;
    }
}
