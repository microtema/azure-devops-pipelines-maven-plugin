package de.microtema.maven.plugin.github.workflow.job;

import de.microtema.maven.plugin.github.workflow.PipelineGeneratorMojo;
import de.microtema.maven.plugin.github.workflow.PipelineGeneratorUtil;
import de.microtema.maven.plugin.github.workflow.model.MetaData;

import java.util.List;
import java.util.stream.Collectors;

public class DownstreamTemplateStageService implements TemplateStageService {

    private final List<TemplateStageService> templateStageServices;

    public DownstreamTemplateStageService(List<TemplateStageService> templateStageServices) {
        this.templateStageServices = templateStageServices;
    }

    @Override
    public String getTemplate(PipelineGeneratorMojo mojo, MetaData metaData) {

        if (!access(mojo, metaData)) {
            return null;
        }

        String template = PipelineGeneratorUtil.getTemplate(getTemplateName());

        String dependsOn = templateStageServices.stream().filter(it -> it.access(mojo, metaData)).map(TemplateStageService::getJobId).collect(Collectors.joining(", "));

        return template.replace("%DEPENDS_ON%", dependsOn);
    }

    @Override
    public boolean access(PipelineGeneratorMojo mojo, MetaData metaData) {

        return true;
    }
}
