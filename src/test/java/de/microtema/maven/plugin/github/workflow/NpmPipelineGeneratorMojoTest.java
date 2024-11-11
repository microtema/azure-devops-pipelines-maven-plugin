package de.microtema.maven.plugin.github.workflow;

import org.apache.commons.io.FileUtils;
import org.apache.maven.project.MavenProject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NpmPipelineGeneratorMojoTest {


    NpmPipelineGeneratorMojo sut;

    @InjectMocks
    PipelineGeneratorMojo mojo;

    @Mock
    MavenProject project;

    @Mock
    File basePath;

    File pipelineFile;

    @BeforeEach
    void setUp() {

        mojo.project = project;

        mojo.pipelineFileName = "target/azure-pipelines.yml";

        mojo.variables.put("SERVICE_URL", "http://localhost:8080");

        mojo.variables.put("REPO_ORGANISATION", "MICROTEMA");
        mojo.variables.put("REPO_PROJECT", "DX");

        mojo.stages.put("none", "feature/*,bugfix/*");
        mojo.stages.put("dev", "develop");
        mojo.stages.put("int", "release/*");
        mojo.stages.put("prod", "master");

        sut = new NpmPipelineGeneratorMojo(mojo);
    }

    @Test
    void execute() throws Exception{

        when(project.getBasedir()).thenReturn(basePath);
        when(basePath.getPath()).thenReturn(".");
        when(project.getName()).thenReturn("azure-devops-pipelines-maven-plugin");
        when(project.getArtifactId()).thenReturn("azure-devops-pipelines-maven-plugin");
        when(project.getName()).thenReturn("azure-devops-pipelines-maven-plugin");
        when(project.getVersion()).thenReturn("1.1.0-SNAPSHOT");

        pipelineFile = new File("target/azure-pipelines.yml");

        sut.execute();

        String answer = FileUtils.readFileToString(pipelineFile, "UTF-8");

        assertNotNull(answer);

        assertEquals("############## Created by de.microtema:azure-devops-pipelines-maven-plugin ############\n" , answer);
    }

    @Test
    void injectTemplateStageServices() {
    }

    @Test
    void applyDefaultVariables() {
    }

    @Test
    void executeImpl() {
    }

    @Test
    void getStagesTemplate() {
    }
}