package de.microtema.maven.plugin.github.workflow.job.npm;

import de.microtema.maven.plugin.github.workflow.PipelineGeneratorMojo;
import de.microtema.maven.plugin.github.workflow.PipelineGeneratorUtil;
import de.microtema.maven.plugin.github.workflow.job.TemplateStageService;
import de.microtema.maven.plugin.github.workflow.model.MetaData;

public class InfraPostDeploymentTemplateStageService implements TemplateStageService {

    @Override
    public String getTemplateName() {
        return "terraform/infra-post-deployment";
    }

    @Override
    public String getJobId() {
        return "infra-post-deployment";
    }

    @Override
    public boolean access(PipelineGeneratorMojo mojo, MetaData metaData) {

        return PipelineGeneratorUtil.hasTerraformModules(mojo.getProject());
    }
}
