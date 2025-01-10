package de.microtema.maven.plugin.github.workflow.job.terraform;

import de.microtema.maven.plugin.github.workflow.PipelineGeneratorMojo;
import de.microtema.maven.plugin.github.workflow.job.TemplateStageService;
import de.microtema.maven.plugin.github.workflow.model.MetaData;

public class PromoteTemplateStageService implements TemplateStageService {

    @Override
    public String getTemplateName() {
        return "promote";
    }

    @Override
    public String getJobId() {
        return "promote";
    }

    @Override
    public boolean access(PipelineGeneratorMojo mojo, MetaData metaData) {

        return true;
    }

    @Override
    public String getTemplate(PipelineGeneratorMojo mojo, MetaData metaData) {

        String template =   TemplateStageService.super.getTemplate(mojo, metaData);

        return template
                .replace("[ build ]", "[ versioning ]")
                .replace("succeeded('build')", "succeeded('versioning')")
                .replace("-${{ lower(variables['APP_NAME']) }}", "");
    }
}
