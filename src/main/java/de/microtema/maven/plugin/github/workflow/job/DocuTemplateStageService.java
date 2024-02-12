package de.microtema.maven.plugin.github.workflow.job;

import de.microtema.maven.plugin.github.workflow.PipelineGeneratorMojo;
import de.microtema.maven.plugin.github.workflow.model.MetaData;
import org.apache.commons.lang3.StringUtils;

import java.util.stream.Stream;

public class DocuTemplateStageService implements TemplateStageService {

    @Override
    public boolean access(PipelineGeneratorMojo mojo, MetaData metaData) {

        return Stream.of("feature", "bugfix").noneMatch(it -> StringUtils.equalsIgnoreCase(metaData.getBranchName(), it));
    }
}
