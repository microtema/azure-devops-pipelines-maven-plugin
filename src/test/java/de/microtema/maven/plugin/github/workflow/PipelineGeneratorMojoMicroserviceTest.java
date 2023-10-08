package de.microtema.maven.plugin.github.workflow;

import org.apache.commons.io.FileUtils;
import org.apache.maven.project.MavenProject;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.util.Map;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PipelineGeneratorMojoMicroserviceTest {

    static File dbDir = new File("./src/main/resources/db");
    static File dbMigrationDir = new File("./src/main/resources/db/migration");
    static File dbChangelogDir = new File("./src/main/resources/db/changelog");
    static File dockerFileDir = new File("./Dockerfile");

    @InjectMocks
    PipelineGeneratorMojo sut;

    @Mock
    MavenProject project;

    @Mock
    File basePath;

    Properties properties = new Properties();

    File pipelineFile;

    @BeforeAll
    static void beforeAll() {

        dbDir.mkdirs();
        dbMigrationDir.mkdirs();
        dbChangelogDir.mkdirs();

        dockerFileDir.mkdir();
    }

    @AfterAll
    static void afterAll() {

        dbMigrationDir.delete();
        dbChangelogDir.delete();

        dbDir.delete();

        dockerFileDir.delete();
    }

    @BeforeEach
    void setUp() {

        sut.project = project;

        sut.pipelineFileName = "target/azure-pipelines.yml";

        sut.variables.put("DOCKER_REGISTRY", "docker.registry.local");
        sut.variables.put("DOCKER_REGISTRY_USER", "docker.user");
        sut.variables.put("DOCKER_REGISTRY_PASSWORD", "docker.password");

        sut.variables.put("SERVICE_URL", "http://localhost:8080");
        sut.variables.put("ENV_STAGE_NAME", "ENV_$STAGE_NAME");
        sut.variables.put("REPO_ACCESS_TOKEN", "${{ secrets.REPO_ACCESS_TOKEN }}");

        properties.put("sonar.host.url", "http://localhost:9000");

        sut.stages.put("none", "feature/*,bugfix/*");
        sut.stages.put("dev", "develop");
        sut.stages.put("int", "release/*");
        sut.stages.put("prod", "master");
    }

    @Test
    void generatePipelineFile() throws Exception {

        when(project.getBasedir()).thenReturn(basePath);
        when(basePath.getPath()).thenReturn(".");
        when(project.getName()).thenReturn("azure-devops-pipelines-maven-plugin");
        when(project.getArtifactId()).thenReturn("azure-devops-pipelines-maven-plugin");
        when(project.getVersion()).thenReturn("1.1.0-SNAPSHOT");
        when(project.getProperties()).thenReturn(properties);

        properties.put("sonar.host.url", "http://localhost:9000");

        sut.stages.put("dev", "develop");

        pipelineFile = new File("target/azure-pipelines.yml");

        sut.execute();

        String answer = FileUtils.readFileToString(pipelineFile, "UTF-8");

        assertEquals("test", answer);
    }
}
