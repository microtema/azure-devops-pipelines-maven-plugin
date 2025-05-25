package de.microtema.maven.plugin.github.workflow.job.npm;

import de.microtema.maven.plugin.github.workflow.PipelineGeneratorMojo;
import de.microtema.maven.plugin.github.workflow.PipelineGeneratorUtil;
import de.microtema.maven.plugin.github.workflow.job.TemplateStageService;
import de.microtema.maven.plugin.github.workflow.model.MetaData;

public class SystemTestTemplateStageService implements TemplateStageService {

    private final InfraPostDeploymentTemplateStageService infraPostDeploymentTemplateStageService;

    public SystemTestTemplateStageService(InfraPostDeploymentTemplateStageService infraPostDeploymentTemplateStageService) {
        this.infraPostDeploymentTemplateStageService = infraPostDeploymentTemplateStageService;
    }

    @Override
    public String getTemplateName() {
        return "npm/system-test";
    }

    @Override
    public boolean access(PipelineGeneratorMojo mojo, MetaData metaData) {

        return true;
    }

    @Override
    public String getTemplate(PipelineGeneratorMojo mojo, MetaData metaData) {

        String template = TemplateStageService.super.getTemplate(mojo, metaData);

        if (infraPostDeploymentTemplateStageService.access(mojo, metaData)) {
            return template
                    .replace("[ readiness ]", "[ infra_post_deployment ]")
                    .replace("succeeded('readiness')", "succeeded('infra_post_deployment')")
                    .replace("artifact: shared-files", "artifact: post-shared-files")
                    .replace("script: mv $(Pipeline.Workspace)/shared-files/.env .", "script: mv $(Pipeline.Workspace)/post-shared-files/.env .");
        }

        return template;
    }
}
