package de.microtema.maven.plugin.github.workflow.job.npm;

import de.microtema.maven.plugin.github.workflow.PipelineGeneratorMojo;
import de.microtema.maven.plugin.github.workflow.job.TemplateStageService;
import de.microtema.maven.plugin.github.workflow.model.MetaData;

public class SmokeTestTemplateStageService implements TemplateStageService {

    @Override
    public String getTemplateName() {
        return "npm/smoke-test";
    }

    @Override
    public boolean access(PipelineGeneratorMojo mojo, MetaData metaData) {

        return true;
    }
}
