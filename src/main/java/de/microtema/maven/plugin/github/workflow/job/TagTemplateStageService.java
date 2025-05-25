package de.microtema.maven.plugin.github.workflow.job;

import de.microtema.maven.plugin.github.workflow.PipelineGeneratorMojo;
import de.microtema.maven.plugin.github.workflow.PipelineGeneratorUtil;
import de.microtema.maven.plugin.github.workflow.model.MetaData;

public class TagTemplateStageService implements TemplateStageService {

    @Override
    public String getTemplateName() {
        return "tag";
    }

    @Override
    public boolean access(PipelineGeneratorMojo mojo, MetaData metaData) {

        return true;
    }

    @Override
    public String getTemplate(PipelineGeneratorMojo mojo, MetaData metaData) {

        String template = TemplateStageService.super.getTemplate(mojo, metaData);

        if (PipelineGeneratorUtil.isTerraformRepo(mojo.getProject())) {

            return template
                    .replace("[ smoke_test ]", "[ infra_deployment ]")
                    .replace("and(succeeded('readiness'), not(failed('smoke_test')), eq(variables.isMaster, true))", "and(succeeded('infra_deployment'), eq(variables.isMaster, true))");
        }

        return template;
    }
}
