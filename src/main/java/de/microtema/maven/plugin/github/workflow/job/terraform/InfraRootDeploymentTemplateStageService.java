package de.microtema.maven.plugin.github.workflow.job.terraform;

import de.microtema.maven.plugin.github.workflow.PipelineGeneratorMojo;
import de.microtema.maven.plugin.github.workflow.job.TemplateStageService;
import de.microtema.maven.plugin.github.workflow.model.MetaData;

public class InfraRootDeploymentTemplateStageService implements TemplateStageService {

    @Override
    public String getTemplateName() {
        return "terraform/infra-root-deployment";
    }

    @Override
    public String getJobId() {
        return "infra_deployment";
    }

    @Override
    public boolean access(PipelineGeneratorMojo mojo, MetaData metaData) {

        return true;
    }
}
