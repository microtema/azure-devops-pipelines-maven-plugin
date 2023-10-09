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

        assertEquals("############## Created by de.microtema:azure-devops-pipelines-maven-plugin ############\n" +
                "#++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++#\n" +
                "# azure-pipelines.yml is generated and should not be edited. #\n" +
                "################################################################################\n" +
                "\n" +
                "trigger: [bugfix/*, develop, feature/*, master, release/*]\n" +
                "\n" +
                "variables:\n" +
                "  APP_NAME: \"azure-devops-pipelines-maven-plugin\"\n" +
                "  APP_DISPLAY_NAME: \"azure-devops-pipelines-maven-plugin\"\n" +
                "  VERSION: \"1.1.0-SNAPSHOT\"\n" +
                "  GIT_COMMIT: \"$(Build.SourceVersion)\"\n" +
                "  REPO_NAME: \"$(Build.Repository.Name)\"\n" +
                "  BRANCH_NAME: \"$[replace(variables['Build.SourceBranch'], 'refs/heads/', '')]\"\n" +
                "  isDevelop: \"$[eq(variables['Build.SourceBranch'], 'refs/heads/develop')]\"\n" +
                "  isRelease: \"$[startsWith(variables['Build.SourceBranch'], 'refs/heads/release/')]\"\n" +
                "  isMaster: \"$[eq(variables['Build.SourceBranch'], 'refs/heads/master')]\"\n" +
                "  REPO_ORGANISATION: \"mariotema\"\n" +
                "  REPO_PROJECT: \"microtema\"\n" +
                "\n" +
                "stages:\n" +
                "  - stage: initialize\n" +
                "    displayName: Initialize\n" +
                "    jobs:\n" +
                "      - job: initialize\n" +
                "        displayName: Initialize\n" +
                "        pool:\n" +
                "          vmImage: ubuntu-latest\n" +
                "        steps:\n" +
                "          - script: echo dotnet --version\n" +
                "            displayName: 'Run a one-line script'\n" +
                "  - stage: versioning\n" +
                "    displayName: Versioning\n" +
                "    dependsOn: [ initialize ]\n" +
                "    condition: succeeded('initialize')\n" +
                "    jobs:\n" +
                "      - job: versioning\n" +
                "        displayName: Versioning\n" +
                "        pool:\n" +
                "          vmImage: ubuntu-latest\n" +
                "        steps:\n" +
                "          - script: echo handle semver 2.0\n" +
                "            displayName: 'Run a one-line script'\n" +
                "  - stage: compile\n" +
                "    displayName: Compile\n" +
                "    dependsOn: [ versioning ]\n" +
                "    condition: succeeded('versioning')\n" +
                "    jobs:\n" +
                "      - job: versioning\n" +
                "        displayName: versioning\n" +
                "        pool:\n" +
                "          vmImage: ubuntu-latest\n" +
                "        steps:\n" +
                "          - script: echo dotnet restore\n" +
                "            displayName: 'Run a one-line script'\n" +
                "  - stage: security_check\n" +
                "    displayName: Security Check\n" +
                "    dependsOn: [ compile ]\n" +
                "    condition: succeeded('compile')\n" +
                "    jobs:\n" +
                "      - job: security_check\n" +
                "        displayName: Security Check\n" +
                "        pool:\n" +
                "          vmImage: ubuntu-latest\n" +
                "        steps:\n" +
                "          - script: echo security_check\n" +
                "            displayName: 'Run a one-line script'\n" +
                "  - stage: unit_test\n" +
                "    displayName: Unit Test\n" +
                "    dependsOn: [ security_check ]\n" +
                "    condition: succeeded('security_check')\n" +
                "    jobs:\n" +
                "      - job: unit_test\n" +
                "        displayName: unit test\n" +
                "        pool:\n" +
                "          vmImage: ubuntu-latest\n" +
                "        steps:\n" +
                "          - script: echo dotnet test\n" +
                "            displayName: 'Run a one-line script'\n" +
                "  - stage: it_test\n" +
                "    displayName: Integration Test\n" +
                "    dependsOn: [ security_check ]\n" +
                "    condition: succeeded('security_check')\n" +
                "    jobs:\n" +
                "      - job: it_test\n" +
                "        displayName: Integration Test\n" +
                "        pool:\n" +
                "          vmImage: ubuntu-latest\n" +
                "        steps:\n" +
                "          - script: echo it_test\n" +
                "            displayName: 'Run a one-line script'\n" +
                "  - stage: quality_gate\n" +
                "    displayName: Quality Gate\n" +
                "    dependsOn: [ unit_test, it_test ]\n" +
                "    condition: and(succeeded('unit_test'), succeeded('it_test'))\n" +
                "    jobs:\n" +
                "      - job: quality_gate\n" +
                "        displayName: quality gate\n" +
                "        pool:\n" +
                "          vmImage: ubuntu-latest\n" +
                "        steps:\n" +
                "          - script: echo quality_gate\n" +
                "            displayName: 'Run a one-line script'\n" +
                "  - stage: build\n" +
                "    displayName: Build\n" +
                "    dependsOn: [ quality_gate ]\n" +
                "    condition: succeeded('quality_gate')\n" +
                "    jobs:\n" +
                "      - job: build\n" +
                "        displayName: Build\n" +
                "        pool:\n" +
                "          vmImage: ubuntu-latest\n" +
                "        steps:\n" +
                "          - script: echo build\n" +
                "            displayName: 'Run a one-line script'\n" +
                "  - stage: package\n" +
                "    displayName: Package\n" +
                "    dependsOn: [ build ]\n" +
                "    condition: and(succeeded('build'), or(eq(variables.isDevelop, true), eq(variables.isRelease, true), eq(variables.isMaster, true)))\n" +
                "    jobs:\n" +
                "      - job: package\n" +
                "        displayName: package\n" +
                "        pool:\n" +
                "          vmImage: ubuntu-latest\n" +
                "        steps:\n" +
                "          - script: echo package\n" +
                "            displayName: 'Run a one-line script'\n" +
                "  - stage: tag\n" +
                "    displayName: Tag Release\n" +
                "    dependsOn: [ package ]\n" +
                "    condition: and(succeeded('build'), eq(variables.isMaster, true))\n" +
                "    jobs:\n" +
                "      - job: tag\n" +
                "        displayName: package\n" +
                "        pool:\n" +
                "          vmImage: ubuntu-latest\n" +
                "        steps:\n" +
                "          - script: echo tag release\n" +
                "            displayName: 'Run a one-line script'\n" +
                "  - stage: db_migration\n" +
                "    displayName: DB Migration\n" +
                "    dependsOn: [ tag ]\n" +
                "    condition: and(succeeded('package'), or(eq(variables.isDevelop, true), eq(variables.isRelease, true), eq(variables.isMaster, true)))\n" +
                "    jobs:\n" +
                "      - job: db_migration\n" +
                "        displayName: DB Migration\n" +
                "        pool:\n" +
                "          vmImage: ubuntu-latest\n" +
                "        steps:\n" +
                "          - script: echo db_migration\n" +
                "            displayName: 'Run a one-line script'\n" +
                "  - stage: build\n" +
                "    displayName: Build\n" +
                "    dependsOn: [ quality_gate ]\n" +
                "    condition: succeeded('quality_gate')\n" +
                "    jobs:\n" +
                "      - job: build\n" +
                "        displayName: Build\n" +
                "        pool:\n" +
                "          vmImage: ubuntu-latest\n" +
                "        steps:\n" +
                "          - script: echo build\n" +
                "            displayName: 'Run a one-line script'\n" +
                "  - stage: readiness\n" +
                "    displayName: Readiness\n" +
                "    dependsOn: [ deployment ]\n" +
                "    condition: and(succeeded('deployment'), or(eq(variables.isDevelop, true), eq(variables.isRelease, true), eq(variables.isMaster, true)))\n" +
                "    jobs:\n" +
                "      - job: Readiness\n" +
                "        displayName: Readiness\n" +
                "        pool:\n" +
                "          vmImage: ubuntu-latest\n" +
                "        steps:\n" +
                "          - script: echo Readiness\n" +
                "            displayName: 'Run a one-line script'\n" +
                "  - stage: system_test\n" +
                "    displayName: System Test\n" +
                "    dependsOn: [ readiness ]\n" +
                "    condition: and(succeeded('readiness'), or(eq(variables.isDevelop, true), eq(variables.isRelease, true)))\n" +
                "    jobs:\n" +
                "      - job: system_test\n" +
                "        displayName: System Test\n" +
                "        pool:\n" +
                "          vmImage: ubuntu-latest\n" +
                "        steps:\n" +
                "          - script: echo system_test\n" +
                "            displayName: 'Run a one-line script'\n" +
                "  - stage: build\n" +
                "    displayName: Build\n" +
                "    dependsOn: [ quality_gate ]\n" +
                "    condition: succeeded('quality_gate')\n" +
                "    jobs:\n" +
                "      - job: build\n" +
                "        displayName: Build\n" +
                "        pool:\n" +
                "          vmImage: ubuntu-latest\n" +
                "        steps:\n" +
                "          - script: echo build\n" +
                "            displayName: 'Run a one-line script'\n" +
                "  - stage: downstream\n" +
                "    displayName: E2E Test\n" +
                "    dependsOn: [ performance_test ]\n" +
                "    condition: and(succeeded('performance_test'), or(eq(variables.isDevelop, true), eq(variables.isRelease, true)))\n" +
                "    jobs:\n" +
                "      - job: downstream\n" +
                "        displayName: downstream\n" +
                "        pool:\n" +
                "          vmImage: ubuntu-latest\n" +
                "        steps:\n" +
                "          - script: echo downstream\n" +
                "            displayName: 'Run a one-line script'\n" +
                "  - stage: documentation\n" +
                "    displayName: Documentation\n" +
                "    dependsOn: [ downstream ]\n" +
                "    condition: and(succeeded('readiness'), or(eq(variables.isDevelop, true), eq(variables.isRelease, true), eq(variables.isMaster, true)))\n" +
                "    jobs:\n" +
                "      - job: documentation\n" +
                "        displayName: Documentation\n" +
                "        pool:\n" +
                "          vmImage: ubuntu-latest\n" +
                "        steps:\n" +
                "          - script: |\n" +
                "              git clone https://$(REPO_TOKEN)@dev.azure.com/$(REPO_ORGANISATION)/$(REPO_PROJECT)/_git/doc-as-code\n" +
                "              cd doc-as-code \n" +
                "              git checkout $(BRANCH_NAME)\n" +
                "            displayName: 'Git: checkout'\n" +
                "          - script: |\n" +
                "              mkdir -p doc-as-code/.docs/$(APP_NAME)/deleteme\n" +
                "              rm -r doc-as-code/.docs/$(APP_NAME)/*\n" +
                "              cp -R docs/. doc-as-code/.docs/$(APP_NAME)\n" +
                "              cp README.md doc-as-code/docs/$(APP_NAME)/$(APP_NAME).md\n" +
                "            displayName: 'Copy Docu'\n" +
                "          - script: |\n" +
                "              cd doc-as-code\n" +
                "              git config --global user.email mario.tema@ascent.io\n" +
                "              git config --global user.name mario.tema\n" +
                "              git add --all\n" +
                "              git status\n" +
                "              if [[ `git status --porcelain` ]]; then\n" +
                "                 git commit -am \"Update DOC_AS_CODE repo within docs from $(REPO_NAME) commit $(GIT_COMMIT)\"\n" +
                "                 git push https://$(REPO_TOKEN)@dev.azure.com/$(REPO_ORGANISATION)/$(REPO_PROJECT)/_git/doc-as-code\n" +
                "              fi\n" +
                "            displayName: 'Git: Commit'\n" +
                "  - stage: notification\n" +
                "    displayName: Notification\n" +
                "    dependsOn: [ documentation ]\n" +
                "    condition: and(succeeded('readiness'), or(eq(variables.isDevelop, true), eq(variables.isRelease, true), eq(variables.isMaster, true)))\n" +
                "    jobs:\n" +
                "      - job: notification\n" +
                "        displayName: Notification\n" +
                "        pool:\n" +
                "          vmImage: ubuntu-latest\n" +
                "        steps:\n" +
                "          - script: echo Notification\n" +
                "            displayName: 'Run a one-line script'\n", answer);
    }
}
