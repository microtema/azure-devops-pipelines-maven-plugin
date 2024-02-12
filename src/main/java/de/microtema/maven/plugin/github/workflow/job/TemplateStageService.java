package de.microtema.maven.plugin.github.workflow.job;

import de.microtema.maven.plugin.github.workflow.PipelineGeneratorMojo;
import de.microtema.maven.plugin.github.workflow.PipelineGeneratorUtil;
import de.microtema.maven.plugin.github.workflow.model.JobData;
import de.microtema.maven.plugin.github.workflow.model.MetaData;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.WordUtils;

import java.util.List;
import java.util.stream.Collectors;

public interface TemplateStageService {

    String regex = "([a-z0-9])([A-Z])";
    String replacement = "$1-$2";

    default String getTemplateName() {

        return getClass().getSimpleName()
                .replace("TemplateStageService", StringUtils.EMPTY)
                .replaceAll(regex, replacement).toLowerCase();
    }

    default String getTemplate(PipelineGeneratorMojo mojo, MetaData metaData) {

        if (!access(mojo, metaData)) {
            return null;
        }

        return PipelineGeneratorUtil.getTemplate(getTemplateName());
    }

    default String getJobId(){

        String templatePath = getTemplateName();

        int indexOf = templatePath.lastIndexOf("/");

        String templateName = templatePath.substring(indexOf+1);

        return templateName.replaceAll("-", "_");
    }

    default boolean access(PipelineGeneratorMojo mojo, MetaData metaData) {

        return false;
    }
}
