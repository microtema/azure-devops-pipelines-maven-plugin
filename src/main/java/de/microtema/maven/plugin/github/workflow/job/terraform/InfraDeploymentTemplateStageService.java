package de.microtema.maven.plugin.github.workflow.job.terraform;

import de.microtema.maven.plugin.github.workflow.PipelineGeneratorMojo;
import de.microtema.maven.plugin.github.workflow.PipelineGeneratorUtil;
import de.microtema.maven.plugin.github.workflow.job.TemplateStageService;
import de.microtema.maven.plugin.github.workflow.model.MetaData;

public class InfraDeploymentTemplateStageService implements TemplateStageService {

    @Override
    public String getTemplateName() {
        return "terraform/infra-deployment";
    }

    @Override
    public String getJobId() {
        return "infra-deployment";
    }

    @Override
    public boolean access(PipelineGeneratorMojo mojo, MetaData metaData) {

        return true;
    }

    @Override
    public String getTemplate(PipelineGeneratorMojo mojo, MetaData metaData) {

        String template = TemplateStageService.super.getTemplate(mojo, metaData);

        return template
                .replace("-${{ lower(variables['APP_NAME']) }}", "")
                .replace("${{ lower(variables['APP_NAME']) }}", "");
    }
}
