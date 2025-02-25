package de.microtema.maven.plugin.github.workflow;

import de.microtema.maven.plugin.github.workflow.job.VersioningTemplateStageService;
import de.microtema.maven.plugin.github.workflow.job.terraform.InfraSecurityCheckStageService;
import de.microtema.maven.plugin.github.workflow.job.terraform.*;
import de.microtema.maven.plugin.github.workflow.model.MetaData;
import de.microtema.model.converter.util.ClassUtil;

import java.io.File;
import java.io.PrintWriter;
import java.util.*;

import static de.microtema.maven.plugin.github.workflow.PipelineGeneratorUtil.*;

public class TerraformPipelineGeneratorMojo extends PipelineGeneratorMojo {

    public TerraformPipelineGeneratorMojo(PipelineGeneratorMojo mojo) {
        this.project = mojo.project;
        this.downStreams = mojo.downStreams;
        this.stages = mojo.stages;
        this.variables = mojo.variables;
        this.pipelineFileName = mojo.pipelineFileName;
    }

    public void execute() {

        appName = getAppDisplayName();

        injectTemplateStageServices();

        applyDefaultVariables();

        List<MetaData> workflows = getWorkflowFiles(project, stages, downStreams);

        for (MetaData metaData : workflows) {
            executeImpl(metaData, workflows);
        }
    }

    void injectTemplateStageServices() {

        templateStageServices.add(ClassUtil.createInstance(VersioningTemplateStageService.class));
        templateStageServices.add(ClassUtil.createInstance(InfraSecurityCheckStageService.class));
        templateStageServices.add(ClassUtil.createInstance(PromoteRootTemplateStageService.class));
        templateStageServices.add(ClassUtil.createInstance(InfraRootDeploymentTemplateStageService.class));
        templateStageServices.add(ClassUtil.createInstance(TagTemplateStageService.class));
        templateStageServices.add(ClassUtil.createInstance(DocumentationTemplateStageService.class));
    }

    void executeImpl(MetaData metaData, List<MetaData> workflows) {

        String rootPath = PipelineGeneratorUtil.getRootPath(project);

        File dir = new File(rootPath, pipelineFileName);

        String version = project.getVersion();

        switch (metaData.getBranchName()) {
            case "feature":
            case "develop":
                break;
            case "release":
                version = version.replace("-SNAPSHOT", "-RC");
                break;
            case "hotfix":
                version = version.replace("-SNAPSHOT", "");
                break;
            case "master":
                version = version.replace("-SNAPSHOT", "");
                version = version.replace("-RC", "");
                version = version.replace("-FIX", "");
                break;
        }

        defaultVariables.put("VERSION", version);

        String pipeline = PipelineGeneratorUtil.getTemplate("pipeline");

        String serviceConnection = defaultVariables.get("SERVICE_CONNECTION");

        if (Objects.nonNull(serviceConnection)) {

            pipeline = pipeline.replaceAll("PROJECT_NAME_SUBSCRIPTION_DEV", serviceConnection);
            pipeline = pipeline.replaceAll("PROJECT_NAME_SUBSCRIPTION_INT", serviceConnection);
            pipeline = pipeline.replaceAll("PROJECT_NAME_SUBSCRIPTION_PRD", serviceConnection);
        } else {
            pipeline = pipeline.replaceAll("PROJECT_NAME", project.getParent().getArtifactId().toUpperCase());
        }

        pipeline = pipeline
                .replace("%TRIGGERS%", String.join(", ", getBranches(this.stages)))
                .replace("%VERSION%", version)
                .replace("%VARIABLES%", getVariablesTemplate(defaultVariables))
                .replace("%STAGES%", getStagesTemplate(metaData, templateStageServices));

        pipeline = PipelineGeneratorUtil.removeEmptyLines(pipeline);

        try (PrintWriter out = new PrintWriter(dir)) {
            out.println(pipeline);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }
}
