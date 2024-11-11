package de.microtema.maven.plugin.github.workflow.job.npm;

import de.microtema.maven.plugin.github.workflow.PipelineGeneratorMojo;
import de.microtema.maven.plugin.github.workflow.PipelineGeneratorUtil;
import de.microtema.maven.plugin.github.workflow.job.TemplateStageService;
import de.microtema.maven.plugin.github.workflow.model.MetaData;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class BuildTemplateStageService implements TemplateStageService {

    private final List<TemplateStageService> templateStageServices = new ArrayList<>();

    public BuildTemplateStageService(UnitTestTemplateStageService unitTestTemplateStageService,
                                     IntegrationTestTemplateStageService integrationTestTemplateStageService) {

        templateStageServices.add(unitTestTemplateStageService);
        templateStageServices.add(integrationTestTemplateStageService);
    }

    @Override
    public String getTemplateName() {

        return "npm/build";
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

        String defaultTemplate = PipelineGeneratorUtil.getTemplate(getTemplateName());

        List<String> jobIds = templateStageServices.stream()
                .filter(e -> e.access(mojo, metaData))
                .map(e -> e.getJobIds(metaData, metaData.getStageName())).collect(Collectors.toList());

        String template = PipelineGeneratorUtil.applyProperties(defaultTemplate, metaData.getStageName(), mojo.getVariables());

        return template
                .replace("%DEPENDS_ON%", String.join(", ", jobIds))
                .replace("%CONDITIONS%", jobIds.stream().map(it -> "not(failed('" + it + "'))").collect(Collectors.joining(", ")));
    }
}
