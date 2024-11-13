package de.microtema.maven.plugin.github.workflow.job.npm;

import de.microtema.maven.plugin.github.workflow.PipelineGeneratorMojo;
import de.microtema.maven.plugin.github.workflow.job.TemplateStageService;
import de.microtema.maven.plugin.github.workflow.model.MetaData;

public class InfraDeploymentTemplateStageService implements TemplateStageService {

    @Override
    public String getTemplateName() {
        return "npm/infra-deployment";
    }

    @Override
    public String getJobId() {
        return "infra-deployment";
    }

    @Override
    public boolean access(PipelineGeneratorMojo mojo, MetaData metaData) {

        return true;
    }
}
