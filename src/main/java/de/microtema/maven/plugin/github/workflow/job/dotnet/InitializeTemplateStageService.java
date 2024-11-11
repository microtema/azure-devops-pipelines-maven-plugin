package de.microtema.maven.plugin.github.workflow.job.dotnet;

import de.microtema.maven.plugin.github.workflow.PipelineGeneratorMojo;
import de.microtema.maven.plugin.github.workflow.job.TemplateStageService;
import de.microtema.maven.plugin.github.workflow.model.MetaData;

public class InitializeTemplateStageService implements TemplateStageService {

    @Override
    public String getTemplateName() {
        return "dotnet/initialize";
    }

    @Override
    public boolean access(PipelineGeneratorMojo mojo, MetaData metaData) {

        return true;
    }
}
