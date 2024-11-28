package de.microtema.maven.plugin.github.workflow.job.npm;

import de.microtema.maven.plugin.github.workflow.PipelineGeneratorMojo;
import de.microtema.maven.plugin.github.workflow.PipelineGeneratorUtil;
import de.microtema.maven.plugin.github.workflow.job.TemplateStageService;
import de.microtema.maven.plugin.github.workflow.model.MetaData;

public class ReadinessTemplateStageService implements TemplateStageService {


    @Override
    public String getTemplateName() {

        return "npm/readiness";
    }

    @Override
    public String getJobId() {

        return "readiness";
    }

    @Override
    public boolean access(PipelineGeneratorMojo mojo, MetaData metaData) {

        return true;
    }

    @Override
    public String getTemplate(PipelineGeneratorMojo mojo, MetaData metaData) {

        if (!access(mojo, metaData)) {
            return null;
        }

        if (PipelineGeneratorUtil.isSPA(mojo.getProject())) {
            return PipelineGeneratorUtil.getTemplate("npm/readiness-spa");
        }

        return PipelineGeneratorUtil.getTemplate(getTemplateName());
    }
}
