package de.microtema.maven.plugin.github.workflow.job;

import de.microtema.maven.plugin.github.workflow.PipelineGeneratorMojo;
import de.microtema.maven.plugin.github.workflow.PipelineGeneratorUtil;
import de.microtema.maven.plugin.github.workflow.model.MetaData;

public class DocumentationTemplateStageService implements TemplateStageService {

    @Override
    public String getTemplateName() {
        return "documentation";
    }

    @Override
    public boolean access(PipelineGeneratorMojo mojo, MetaData metaData) {

        return true;
    }

    @Override
    public String getTemplate(PipelineGeneratorMojo mojo, MetaData metaData) {

        String template = TemplateStageService.super.getTemplate(mojo, metaData);

        return template.replace("succeeded('readiness')", "succeeded('infra_deployment')");
    }
}
