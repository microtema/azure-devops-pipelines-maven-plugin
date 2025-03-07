package de.microtema.maven.plugin.github.workflow.job.npm;

import de.microtema.maven.plugin.github.workflow.PipelineGeneratorMojo;
import de.microtema.maven.plugin.github.workflow.PipelineGeneratorUtil;
import de.microtema.maven.plugin.github.workflow.job.TemplateStageService;
import de.microtema.maven.plugin.github.workflow.model.MetaData;

public class InfraPostDeploymentTemplateStageService implements TemplateStageService {

    @Override
    public String getTemplateName() {
        return "terraform/infra-deployment";
    }

    @Override
    public String getJobId() {
        return "infra-post-deployment";
    }

    @Override
    public boolean access(PipelineGeneratorMojo mojo, MetaData metaData) {

        return PipelineGeneratorUtil.hasTerraformModules(mojo.getProject());
    }

    @Override
    public String getTemplate(PipelineGeneratorMojo mojo, MetaData metaData) {

        if(!access(mojo, metaData)) {
            return null;
        }

        String template =  TemplateStageService.super.getTemplate(mojo, metaData);

        return template
                .replace("infra_deployment", "infra_post_deployment")
                .replace("Infra Deployment", "Infra Post Deployment")
                .replace("[ infra_precondition ]", "[ readiness ]")
                .replace("succeeded('infra_precondition')", "succeeded('readiness')")
                .replace("terraform plan", "terraform plan -target module.post")
                .replace("- publish: ./.env", "")
                .replace("artifact: shared-files", "").trim();
    }
}
