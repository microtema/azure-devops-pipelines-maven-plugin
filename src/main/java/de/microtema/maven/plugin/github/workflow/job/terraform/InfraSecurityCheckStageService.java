package de.microtema.maven.plugin.github.workflow.job.terraform;

import de.microtema.maven.plugin.github.workflow.PipelineGeneratorMojo;
import de.microtema.maven.plugin.github.workflow.PipelineGeneratorUtil;
import de.microtema.maven.plugin.github.workflow.job.TemplateStageService;
import de.microtema.maven.plugin.github.workflow.model.MetaData;

public class InfraSecurityCheckStageService implements TemplateStageService {

    @Override
    public String getTemplateName() {
        return "infra-security-check";
    }

    @Override
    public String getJobId() {
        return "infra_security_check";
    }

    @Override
    public boolean access(PipelineGeneratorMojo mojo, MetaData metaData) {

        return true;
    }

    @Override
    public String getTemplate(PipelineGeneratorMojo mojo, MetaData metaData) {

        String template =   TemplateStageService.super.getTemplate(mojo, metaData);

        if (PipelineGeneratorUtil.isTerraformRepo(mojo.getProject())) {
            return template
                    .replace("[ build ]", "[ versioning ]")
                    .replace("succeeded('build')", "succeeded('versioning')");
        }

        return template;
    }

}
