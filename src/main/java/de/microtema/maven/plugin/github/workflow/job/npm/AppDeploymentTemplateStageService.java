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
        return "app-deployment";
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

        String inlineScript = "func azure functionapp publish $RESOURCE_GROUP_NAME";

        String web = "";

        if (PipelineGeneratorUtil.isSPA(mojo.getProject())) {
            inlineScript = "|\n" +
                    "              az storage blob delete-batch --source '$web'\n" +
                    "              az storage blob upload-batch --destination '$web' --source dist";

            web = "web";
        }

        String template = PipelineGeneratorUtil.getTemplate(getTemplateName());

        return template
                .replace("%INLINE_SCRIPT%", inlineScript)
                .replace("%WEB%", web);
    }
}
