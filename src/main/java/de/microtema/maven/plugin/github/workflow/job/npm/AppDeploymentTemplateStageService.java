package de.microtema.maven.plugin.github.workflow.job.npm;

import de.microtema.maven.plugin.github.workflow.PipelineGeneratorMojo;
import de.microtema.maven.plugin.github.workflow.PipelineGeneratorUtil;
import de.microtema.maven.plugin.github.workflow.job.TemplateStageService;
import de.microtema.maven.plugin.github.workflow.model.MetaData;

import java.util.List;
import java.util.stream.Collectors;

public class AppDeploymentTemplateStageService implements TemplateStageService {

    @Override
    public String getTemplateName() {
        return "npm/app-deployment";
    }

    @Override
    public String getJobId() {
        return "app_deployment";
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

            return PipelineGeneratorUtil.getTemplate("npm/spa-deployment");
        }

        return PipelineGeneratorUtil.getTemplate(getTemplateName());
    }
}
