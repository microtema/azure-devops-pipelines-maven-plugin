package de.microtema.maven.plugin.github.workflow.job.terraform;

import de.microtema.maven.plugin.github.workflow.PipelineGeneratorMojo;
import de.microtema.maven.plugin.github.workflow.job.TemplateStageService;
import de.microtema.maven.plugin.github.workflow.model.MetaData;

public class PromoteRootTemplateStageService implements TemplateStageService {

    @Override
    public String getTemplateName() {
        return "promote-root";
    }

    @Override
    public String getJobId() {
        return "promote";
    }

    @Override
    public boolean access(PipelineGeneratorMojo mojo, MetaData metaData) {

        return true;
    }
}
