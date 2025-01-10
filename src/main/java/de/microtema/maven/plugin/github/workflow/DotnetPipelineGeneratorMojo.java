package de.microtema.maven.plugin.github.workflow;

import de.microtema.maven.plugin.github.workflow.job.dotnet.*;
import de.microtema.maven.plugin.github.workflow.model.MetaData;
import de.microtema.model.converter.util.ClassUtil;

import java.io.File;
import java.io.PrintWriter;
import java.util.List;
import java.util.stream.Collectors;

import static de.microtema.maven.plugin.github.workflow.PipelineGeneratorUtil.*;

public class DotnetPipelineGeneratorMojo extends PipelineGeneratorMojo {

    public DotnetPipelineGeneratorMojo(PipelineGeneratorMojo mojo) {
        this.project = mojo.project;
        this.downStreams = mojo.downStreams;
        this.stages = mojo.stages;
        this.variables = mojo.variables;
        this.pipelineFileName = mojo.pipelineFileName;
        this.appName = mojo.getAppDisplayName();
    }

    public void execute() {

        injectTemplateStageServices();

        applyDefaultVariables();

        List<MetaData> workflows = getWorkflowFiles(project, stages, downStreams);

        for (MetaData metaData : workflows) {
            executeImpl(metaData, workflows);
        }
    }

    void injectTemplateStageServices() {
        templateStageServices.add(ClassUtil.createInstance(InitializeTemplateStageService.class));
        templateStageServices.add(ClassUtil.createInstance(VersioningTemplateStageService.class));
        templateStageServices.add(ClassUtil.createInstance(CompileTemplateStageService.class));
        templateStageServices.add(ClassUtil.createInstance(SecurityTemplateStageService.class));
        templateStageServices.add(ClassUtil.createInstance(UnitTestTemplateStageService.class));
        templateStageServices.add(ClassUtil.createInstance(IntegrationTestTemplateStageService.class));
        templateStageServices.add(ClassUtil.createInstance(QualityGateTemplateStageService.class));
        templateStageServices.add(ClassUtil.createInstance(BuildTemplateStageService.class));
        templateStageServices.add(ClassUtil.createInstance(PackageTemplateStageService.class));
        templateStageServices.add(ClassUtil.createInstance(TagTemplateStageService.class));
        templateStageServices.add(ClassUtil.createInstance(DbMigrationTemplateStageService.class));
        templateStageServices.add(ClassUtil.createInstance(DeploymentTemplateStageService.class));
        templateStageServices.add(ClassUtil.createInstance(ReadinessTemplateStageService.class));
        templateStageServices.add(ClassUtil.createInstance(SystemTestTemplateStageService.class));
        templateStageServices.add(ClassUtil.createInstance(PerformanceTestTemplateStageService.class));
        templateStageServices.add(ClassUtil.createInstance(DownstreamTemplateStageService.class));
        templateStageServices.add(ClassUtil.createInstance(DocuTemplateStageService.class));
        templateStageServices.add(ClassUtil.createInstance(NotificationTemplateStageService.class));
    }

    void executeImpl(MetaData metaData, List<MetaData> workflows) {

        defaultVariables.putAll(variables);

        String pipeline = PipelineGeneratorUtil.getTemplate("dotnet/pipeline");

        List<MetaData> workflowFiles = getWorkflowFiles(project, stages, downStreams);

        List<String> branches = workflowFiles.stream()
                .map(MetaData::getBranchPattern)
                .collect(Collectors.toList()).stream()
                .sorted().collect(Collectors.toList());

        pipeline = pipeline
                .replace("%TRIGGER%", String.join(", ", branches))
                .replace("%VARIABLES%", getVariablesTemplate(defaultVariables))
                .replace("%STAGES%", getStagesTemplate(metaData, templateStageServices));

        File githubWorkflow = new File(pipelineFileName);

        logMessage("Generate Azure DevOps pipelines for " + appName + " -> " + pipelineFileName);

        pipeline = PipelineGeneratorUtil.removeEmptyLines(pipeline);

        try (PrintWriter out = new PrintWriter(githubWorkflow)) {
            out.println(pipeline);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }
}
