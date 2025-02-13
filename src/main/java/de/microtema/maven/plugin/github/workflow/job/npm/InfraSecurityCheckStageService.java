package de.microtema.maven.plugin.github.workflow.job.npm;

import de.microtema.maven.plugin.github.workflow.PipelineGeneratorMojo;
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
}
