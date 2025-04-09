package de.microtema.maven.plugin.github.workflow.job;

import de.microtema.maven.plugin.github.workflow.PipelineGeneratorMojo;
import de.microtema.maven.plugin.github.workflow.PipelineGeneratorUtil;
import de.microtema.maven.plugin.github.workflow.model.MetaData;

import java.util.Objects;

public class DownstreamTemplateStageService implements TemplateStageService {

    @Override
    public String getTemplateName() {
        return "downstream";
    }

    @Override
    public boolean access(PipelineGeneratorMojo mojo, MetaData metaData) {

        return Objects.nonNull(PipelineGeneratorUtil.getProperty(mojo.getProject(), "DOWNSTREAM_PIPELINE_ID"));
    }

    @Override
    public String getTemplate(PipelineGeneratorMojo mojo, MetaData metaData) {

        if(!access(mojo, metaData)) {
            return null;
        }

        String template = TemplateStageService.super.getTemplate(mojo, metaData);

        return template.replace("%DOWNSTREAM_PIPELINE_ID%", PipelineGeneratorUtil.getProperty(mojo.getProject(), "DOWNSTREAM_PIPELINE_ID"));
    }
}
