package de.microtema.maven.plugin.github.workflow;

import org.apache.commons.io.FileUtils;
import org.apache.maven.project.MavenProject;
import org.junit.jupiter.api.*;
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

    static File packageJson = new File("./package.json");

    @InjectMocks
    PipelineGeneratorMojo sut;

    @Mock
    MavenProject project;

    @Mock
    File basePath;

    Properties properties = new Properties();

    File pipelineFile;

    @BeforeAll
    static void beforeAll() throws Exception {

        packageJson.createNewFile();
    }

    @AfterAll
    static void afterAll() {

        packageJson.delete();
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

    @AfterEach
    void tearDown() {


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
                "  DOCKER_REGISTRY: \"docker.registry.local\"\n" +
                "  DOCKER_REGISTRY_USER: \"docker.user\"\n" +
                "  DOCKER_REGISTRY_PASSWORD: \"docker.password\"\n" +
                "  SERVICE_URL: \"http://localhost:8080\"\n" +
                "  ${{ if eq(variables['Build.SourceBranch'], 'refs/heads/develop') }}:\n" +
                "    STAGE_NAME: \"DEV\"\n" +
                "  ${{ elseif startsWith(variables['Build.SourceBranch'], 'refs/heads/release/') }}:\n" +
                "    STAGE_NAME: \"INT\"\n" +
                "  ${{ elseif eq(variables['Build.SourceBranch'], 'refs/heads/master') }}:\n" +
                "    STAGE_NAME: \"PROD\"\n" +
                "  ${{ else }}:\n" +
                "    STAGE_NAME: \"DEV\"\n" +
                "\n" +
                "stages:\n" +
                "  - stage: initialize\n" +
                "    displayName: 'Initialize'\n" +
                "    jobs:\n" +
                "      - job: initialize\n" +
                "        displayName: 'Initialize'\n" +
                "        pool:\n" +
                "          vmImage: ubuntu-latest\n" +
                "        steps:\n" +
                "          - task: NodeTool@0\n" +
                "            displayName: Install Node.js\n" +
                "            inputs:\n" +
                "              versionSpec: ${{ variables['NODEJS_VERSION'] }}\n" +
                "          - script: npm --version\n" +
                "            displayName: 'Run a one-line script'\n" +
                "  - stage: versioning\n" +
                "    displayName: 'Versioning'\n" +
                "    dependsOn: [ initialize ]\n" +
                "    condition: succeeded('initialize')\n" +
                "    jobs:\n" +
                "      - job: versioning\n" +
                "        displayName: 'Versioning'\n" +
                "        pool:\n" +
                "          vmImage: ubuntu-latest\n" +
                "        steps:\n" +
                "          - script: echo handle semver 2.0\n" +
                "            displayName: 'Run a one-line script'\n" +
                "  - stage: compile\n" +
                "    displayName: 'Compile'\n" +
                "    dependsOn: [ versioning ]\n" +
                "    condition: succeeded('versioning')\n" +
                "    jobs:\n" +
                "      - job: versioning\n" +
                "        displayName: 'Versioning'\n" +
                "        pool:\n" +
                "          vmImage: ubuntu-latest\n" +
                "        steps:\n" +
                "          - task: NodeTool@0\n" +
                "            displayName: 'Install Node.js'\n" +
                "            inputs:\n" +
                "              versionSpec: ${{ variables['NODEJS_VERSION'] }}\n" +
                "          - script: npm install\n" +
                "            displayName: 'Run a one-line script'\n" +
                "  \n" +
                "  - stage: security_check\n" +
                "    displayName: 'Security Check'\n" +
                "    dependsOn: [ compile ]\n" +
                "    condition: succeeded('compile')\n" +
                "    jobs:\n" +
                "      - job: security_check\n" +
                "        displayName: 'Security Check'\n" +
                "        pool:\n" +
                "          vmImage: ubuntu-latest\n" +
                "        steps:\n" +
                "          - task: NodeTool@0\n" +
                "            displayName: 'Install Node.js'\n" +
                "            inputs:\n" +
                "              versionSpec: ${{ variables['NODEJS_VERSION'] }}\n" +
                "          - script: npm install\n" +
                "            displayName: 'Run a one-line script'\n" +
                "          - script: echo npm run security:check\n" +
                "            displayName: 'Run a one-line script'\n" +
                "            timeoutInMinutes: 5\n" +
                "  - stage: unit_test\n" +
                "    displayName: 'Unit Test'\n" +
                "    dependsOn: [ security_check ]\n" +
                "    condition: succeeded('security_check')\n" +
                "    jobs:\n" +
                "      - job: unit_test\n" +
                "        displayName: 'Unit Test'\n" +
                "        pool:\n" +
                "          vmImage: ubuntu-latest\n" +
                "        steps:\n" +
                "          - task: NodeTool@0\n" +
                "            displayName: 'Install Node.js'\n" +
                "            inputs:\n" +
                "              versionSpec: ${{ variables['NODEJS_VERSION'] }}\n" +
                "          - script: npm install\n" +
                "            displayName: 'Run a one-line script'\n" +
                "          - script: CI=true npm test\n" +
                "            displayName: 'Run a one-line script'\n" +
                "  - stage: it_test\n" +
                "    displayName: 'Integration Test'\n" +
                "    dependsOn: [ security_check ]\n" +
                "    condition: succeeded('security_check')\n" +
                "    jobs:\n" +
                "      - job: system_test\n" +
                "        displayName: 'System Test'\n" +
                "        pool:\n" +
                "          vmImage: ubuntu-latest\n" +
                "        steps:\n" +
                "          - task: NodeTool@0\n" +
                "            displayName: 'Install Node.js'\n" +
                "            inputs:\n" +
                "              versionSpec: ${{ variables['NODEJS_VERSION'] }}\n" +
                "          - script: npm install\n" +
                "            displayName: 'Install npm packages'\n" +
                "          - script: npm run test:integration\n" +
                "            displayName: 'Run a one-line script'\n" +
                "  - stage: quality_gate\n" +
                "    displayName: 'Quality Gate'\n" +
                "    dependsOn: [ unit_test, it_test ]\n" +
                "    condition: and(succeeded('unit_test'), succeeded('it_test'))\n" +
                "    jobs:\n" +
                "      - job: quality_gate\n" +
                "        displayName: 'Quality Gate'\n" +
                "        pool:\n" +
                "          vmImage: ubuntu-latest\n" +
                "        steps:\n" +
                "          - script: echo quality_gate\n" +
                "            displayName: 'Run a one-line script'\n" +
                "  - stage: build\n" +
                "    displayName: 'Build'\n" +
                "    dependsOn: [ quality_gate ]\n" +
                "    condition: succeeded('quality_gate')\n" +
                "    jobs:\n" +
                "      - job: build\n" +
                "        displayName: 'Build'\n" +
                "        pool:\n" +
                "          vmImage: ubuntu-latest\n" +
                "        steps:\n" +
                "          - task: NodeTool@0\n" +
                "            displayName: 'Install Node.js'\n" +
                "            inputs:\n" +
                "              versionSpec: ${{ variables['NODEJS_VERSION'] }}\n" +
                "          - script: npm install\n" +
                "            displayName: 'Run a one-line script'\n" +
                "          - script: npm run build\n" +
                "            displayName: 'Run a one-line script'\n" +
                "  - stage: package\n" +
                "    displayName: 'Package'\n" +
                "    dependsOn: [ build ]\n" +
                "    condition: and(succeeded('build'), or(eq(variables.isDevelop, true), eq(variables.isRelease, true), eq(variables.isMaster, true)))\n" +
                "    variables:\n" +
                "      - group: ${{ variables['STAGE_NAME'] }}\n" +
                "    jobs:\n" +
                "      - job: check\n" +
                "        displayName: 'Check Resource Group'\n" +
                "        variables:\n" +
                "          AZURE_RESOURCE_GROUP: ${{ lower(variables['APP_NAME']) }}-${{ lower(variables['STAGE_NAME']) }}-$(AZURE_LOCATION_SHORT)-$(STAGE_COUNT)\n" +
                "        pool:\n" +
                "          vmImage: ubuntu-latest\n" +
                "        steps:\n" +
                "          - task: AzureCLI@2\n" +
                "            name: rg\n" +
                "            displayName: 'Task: Azure CLI'\n" +
                "            inputs:\n" +
                "              azureSubscription: ${{ upper(variables['PROJECT_NAME']) }}-AZURE-SUBSCRIPTION-${{ upper(variables['STAGE_NAME']) }}\n" +
                "              ScriptType: bash\n" +
                "              scriptLocation: inlineScript\n" +
                "              inlineScript: echo \"##vso[task.setvariable variable=exists;isoutput=true]$(az group exists -n $AZURE_RESOURCE_GROUP)\"\n" +
                "      - job: create_resource_group\n" +
                "        displayName: 'Create Resource Group'\n" +
                "        dependsOn: [ check ]\n" +
                "        condition: and(succeeded('check'), eq(dependencies.check.outputs['rg.exists'], 'false'))\n" +
                "        variables:\n" +
                "          AZURE_RESOURCE_GROUP: ${{ lower(variables['APP_NAME']) }}-${{ lower(variables['STAGE_NAME']) }}-$(AZURE_LOCATION_SHORT)-$(STAGE_COUNT)\n" +
                "        pool:\n" +
                "          vmImage: ubuntu-latest\n" +
                "        steps:\n" +
                "          - task: AzureCLI@2\n" +
                "            displayName: 'Create Resource-Group'\n" +
                "            inputs:\n" +
                "              azureSubscription: ${{ upper(variables['PROJECT_NAME']) }}-AZURE-SUBSCRIPTION-${{ upper(variables['STAGE_NAME']) }}\n" +
                "              ScriptType: bash\n" +
                "              scriptLocation: inlineScript\n" +
                "              inlineScript: |\n" +
                "                az group create \\\n" +
                "                --location $(AZURE_LOCATION) \\\n" +
                "                --name $(AZURE_RESOURCE_GROUP)\n" +
                "                \n" +
                "                az group wait --created \\\n" +
                "                --resource-group $(AZURE_RESOURCE_GROUP)\n" +
                "      - job: create_storage_account\n" +
                "        displayName: 'Create Storage Account'\n" +
                "        dependsOn: create_resource_group\n" +
                "        condition: and(succeeded('create_resource_group'), eq(dependencies.check.outputs['rg.exists'], 'false'))\n" +
                "        variables:\n" +
                "          AZURE_RESOURCE_GROUP: ${{ lower(variables['APP_NAME']) }}-${{ lower(variables['STAGE_NAME']) }}-$(AZURE_LOCATION_SHORT)-$(STAGE_COUNT)\n" +
                "        pool:\n" +
                "          vmImage: ubuntu-latest\n" +
                "        steps:\n" +
                "          - task: AzureCLI@2\n" +
                "            displayName: 'Create Storage Account'\n" +
                "            inputs:\n" +
                "              azureSubscription: ${{ upper(variables['PROJECT_NAME']) }}-AZURE-SUBSCRIPTION-${{ upper(variables['STAGE_NAME']) }}\n" +
                "              ScriptType: bash\n" +
                "              scriptLocation: inlineScript\n" +
                "              inlineScript: |\n" +
                "                az storage account create \\\n" +
                "                --name ${AZURE_RESOURCE_GROUP//[^[:alnum:]]/\"\"} \\\n" +
                "                --resource-group $(AZURE_RESOURCE_GROUP) \\\n" +
                "                --allow-blob-public-access true\n" +
                "      - job: create_storage_container\n" +
                "        displayName: 'Create Storage Container'\n" +
                "        dependsOn: [ create_storage_account ]\n" +
                "        condition: and(succeeded('check'), eq(dependencies.check.outputs['rg.exists'], 'false'))\n" +
                "        variables:\n" +
                "          AZURE_RESOURCE_GROUP: ${{ lower(variables['APP_NAME']) }}-${{ lower(variables['STAGE_NAME']) }}-$(AZURE_LOCATION_SHORT)-$(STAGE_COUNT)\n" +
                "        pool:\n" +
                "          vmImage: ubuntu-latest\n" +
                "        steps:\n" +
                "          - task: AzureCLI@2\n" +
                "            displayName: 'Create Container'\n" +
                "            inputs:\n" +
                "              azureSubscription: ${{ upper(variables['PROJECT_NAME']) }}-AZURE-SUBSCRIPTION-${{ upper(variables['STAGE_NAME']) }}\n" +
                "              ScriptType: bash\n" +
                "              scriptLocation: inlineScript\n" +
                "              inlineScript: |\n" +
                "                az storage container create \\\n" +
                "                  --name scm-releases \\\n" +
                "                  --resource-group $(AZURE_RESOURCE_GROUP) \\\n" +
                "                  --account-name ${AZURE_RESOURCE_GROUP//[^[:alnum:]]/\"\"}\n" +
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
                "  - stage: deployment\n" +
                "    displayName: 'Deployment'\n" +
                "    dependsOn: [ package ]\n" +
                "    condition: and(succeeded('package'), or(eq(variables.isDevelop, true), eq(variables.isRelease, true), eq(variables.isMaster, true)))\n" +
                "    variables:\n" +
                "      - group: ${{ variables['STAGE_NAME'] }}\n" +
                "    jobs:\n" +
                "      - job: iac\n" +
                "        displayName: 'Infra as Code'\n" +
                "        variables:\n" +
                "          AZURE_RESOURCE_GROUP: ${{ lower(variables['APP_NAME']) }}-${{ lower(variables['STAGE_NAME']) }}-$(AZURE_LOCATION_SHORT)-$(STAGE_COUNT)\n" +
                "        pool:\n" +
                "          vmImage: ubuntu-latest\n" +
                "        steps:\n" +
                "          - task: AzureCLI@2\n" +
                "            displayName: 'Terraform Init'\n" +
                "            inputs:\n" +
                "              azureSubscription: ${{ upper(variables['PROJECT_NAME']) }}-AZURE-SUBSCRIPTION-${{ upper(variables['STAGE_NAME']) }}\n" +
                "              ScriptType: bash\n" +
                "              scriptLocation: inlineScript\n" +
                "              workingDirectory: terraform\n" +
                "              addSpnToEnvironment: true\n" +
                "              inlineScript: |\n" +
                "                export ARM_SUBSCRIPTION_ID=\"$(az account show --query id --output tsv)\"\n" +
                "                export ARM_TENANT_ID=\"$tenantId\"\n" +
                "                export ARM_CLIENT_ID=\"$servicePrincipalId\"\n" +
                "                export ARM_CLIENT_SECRET=\"$servicePrincipalKey\"\n" +
                "                \n" +
                "                terraform init \\\n" +
                "                  -backend-config=\"resource_group_name=$(AZURE_RESOURCE_GROUP)\" \\\n" +
                "                  -backend-config=\"storage_account_name=${AZURE_RESOURCE_GROUP//[^[:alnum:]]/\"\"}\"\n" +
                "          - task: AzureCLI@2\n" +
                "            displayName: 'Terraform Plan'\n" +
                "            inputs:\n" +
                "              azureSubscription: ${{ upper(variables['PROJECT_NAME']) }}-AZURE-SUBSCRIPTION-${{ upper(variables['STAGE_NAME']) }}\n" +
                "              ScriptType: bash\n" +
                "              scriptLocation: inlineScript\n" +
                "              workingDirectory: terraform\n" +
                "              addSpnToEnvironment: true\n" +
                "              inlineScript: |\n" +
                "                export ARM_SUBSCRIPTION_ID=\"$(az account show --query id --output tsv)\"\n" +
                "                export ARM_TENANT_ID=\"$tenantId\"\n" +
                "                export ARM_CLIENT_ID=\"$servicePrincipalId\"\n" +
                "                export ARM_CLIENT_SECRET=\"$servicePrincipalKey\"\n" +
                "                \n" +
                "                terraform plan -out=tfplan \\\n" +
                "                  -input=false \\\n" +
                "                  -var=commit_id=$(GIT_COMMIT) \\\n" +
                "                  -var=branch=$(VERSION) \\\n" +
                "                  -var=env=${{ lower(variables['STAGE_NAME']) }} \\\n" +
                "                  -var=counter=$(STAGE_COUNT) \\\n" +
                "                  -var=location=$(AZURE_LOCATION) \\\n" +
                "                  -var=location_short=$(AZURE_LOCATION_SHORT)\n" +
                "          - task: AzureCLI@2\n" +
                "            displayName: 'Terraform Apply'\n" +
                "            inputs:\n" +
                "              azureSubscription: ${{ upper(variables['PROJECT_NAME']) }}-AZURE-SUBSCRIPTION-${{ upper(variables['STAGE_NAME']) }}\n" +
                "              ScriptType: bash\n" +
                "              scriptLocation: inlineScript\n" +
                "              workingDirectory: terraform\n" +
                "              addSpnToEnvironment: true\n" +
                "              inlineScript: |\n" +
                "                export ARM_SUBSCRIPTION_ID=\"$(az account show --query id --output tsv)\"\n" +
                "                export ARM_TENANT_ID=\"$tenantId\"\n" +
                "                export ARM_CLIENT_ID=\"$servicePrincipalId\"\n" +
                "                export ARM_CLIENT_SECRET=\"$servicePrincipalKey\"\n" +
                "                \n" +
                "                terraform apply tfplan\n" +
                "      - job: func_deployment\n" +
                "        displayName: 'Deploy Azure Function App'\n" +
                "        dependsOn: [ iac ]\n" +
                "        variables:\n" +
                "          AZURE_RESOURCE_GROUP: ${{ lower(variables['APP_NAME']) }}-${{ lower(variables['STAGE_NAME']) }}-$(AZURE_LOCATION_SHORT)-$(STAGE_COUNT)\n" +
                "        pool:\n" +
                "          vmImage: ubuntu-latest\n" +
                "        steps:\n" +
                "          - task: FuncToolsInstaller@0\n" +
                "            displayName: 'Install Azure Function CLI'\n" +
                "            inputs:\n" +
                "              version: 'latest'\n" +
                "          - task: NodeTool@0\n" +
                "            displayName: 'Install Node.js'\n" +
                "            inputs:\n" +
                "              versionSpec: ${{ variables['NODEJS_VERSION'] }}\n" +
                "          - script: npm install\n" +
                "            displayName: 'Install npm packages'\n" +
                "          - script: npm run build:production\n" +
                "            displayName: 'Build production artifact'\n" +
                "          - task: AzureCLI@2\n" +
                "            displayName: 'Deploy Azure Functions'\n" +
                "            inputs:\n" +
                "              azureSubscription: ${{ upper(variables['PROJECT_NAME']) }}-AZURE-SUBSCRIPTION-${{ upper(variables['STAGE_NAME']) }}\n" +
                "              ScriptType: bash\n" +
                "              scriptLocation: inlineScript\n" +
                "              inlineScript: |\n" +
                "                func azure functionapp publish $(AZURE_RESOURCE_GROUP) --typescript\n" +
                "      - job: register_process\n" +
                "        displayName: 'Register Runtime View'\n" +
                "        dependsOn: [ func_deployment ]\n" +
                "        variables:\n" +
                "          REPORTING_SERVER: \"http://dcs-process-reporting-euw-04.westeurope.azurecontainer.io:8080\"\n" +
                "        pool:\n" +
                "          vmImage: ubuntu-latest\n" +
                "        steps:\n" +
                "          - task: NodeTool@0\n" +
                "            displayName: 'Install Node.js'\n" +
                "            inputs:\n" +
                "              versionSpec: ${{ variables['NODEJS_VERSION'] }}\n" +
                "          - script: npm install\n" +
                "            displayName: 'NPM Install'\n" +
                "          - script: npm run build\n" +
                "            displayName: 'Build production artifact'\n" +
                "          - script: npm run register:process\n" +
                "            displayName: 'Register BPMN Process'\n" +
                "  - stage: readiness\n" +
                "    displayName: 'Readiness'\n" +
                "    dependsOn: [ deployment ]\n" +
                "    condition: and(succeeded('deployment'), or(eq(variables.isDevelop, true), eq(variables.isRelease, true), eq(variables.isMaster, true)))\n" +
                "    variables:\n" +
                "      SERVICE_URL: \"https://$(APP_NAME)-${{ lower(variables['STAGE_NAME']) }}-$(AZURE_LOCATION_SHORT)-$(STAGE_COUNT).azurewebsites.net/rest/api/git/info\"\n" +
                "    jobs:\n" +
                "      - job: Readiness\n" +
                "        displayName: 'Readiness'\n" +
                "        pool:\n" +
                "          vmImage: ubuntu-latest\n" +
                "        steps:\n" +
                "          - script: |\n" +
                "              echo check the commit id [$(GIT_COMMIT)] from $(SERVICE_URL)\n" +
                "              while [[ \"$(curl -H X-API-KEY:API_KEY -s $SERVICE_URL | jq -r '.commitId')\" != \"$GIT_COMMIT\" ]]; do sleep 10; done\n" +
                "            displayName: 'Run a one-line script'\n" +
                "            timeoutInMinutes: 5\n" +
                "  - stage: system_test\n" +
                "    displayName: 'System Test'\n" +
                "    dependsOn: [ readiness ]\n" +
                "    condition: and(succeeded('readiness'), or(eq(variables.isDevelop, true), eq(variables.isRelease, true)))\n" +
                "    variables:\n" +
                "      SERVICE_URL: \"https://$(APP_NAME)-${{ lower(variables['STAGE_NAME']) }}-$(AZURE_LOCATION_SHORT)-$(STAGE_COUNT).azurewebsites.net\"\n" +
                "    jobs:\n" +
                "      - job: system_test\n" +
                "        displayName: 'System Test'\n" +
                "        pool:\n" +
                "          vmImage: ubuntu-latest\n" +
                "        steps:\n" +
                "          - task: NodeTool@0\n" +
                "            displayName: 'Install Node.js'\n" +
                "            inputs:\n" +
                "              versionSpec: ${{ variables['NODEJS_VERSION'] }}\n" +
                "          - script: npm install\n" +
                "            displayName: 'Install npm packages'\n" +
                "          - script: npm run test:system\n" +
                "            displayName: 'Run a one-line script'\n" +
                "  - stage: downstream\n" +
                "    displayName: 'E2E Test'\n" +
                "    dependsOn: [ system_test ]\n" +
                "    condition: and(succeeded('performance_test'), or(eq(variables.isDevelop, true), eq(variables.isRelease, true)))\n" +
                "    jobs:\n" +
                "      - job: downstream\n" +
                "        displayName: 'Downstream'\n" +
                "        pool:\n" +
                "          vmImage: ubuntu-latest\n" +
                "        variables:\n" +
                "          SERVICE_URL: \"http://$(APP_NAME)-${{ lower(variables['STAGE_NAME']) }}-$(AZURE_LOCATION_SHORT)-$(STAGE_COUNT).westeurope.azurecontainer.io:$(APP_PORT)\"\n" +
                "        steps:\n" +
                "          - script: echo downstream -e SERVICE_URL=$(SERVICE_URL) -e STAGE_NAME=$(STAGE_NAME)\n" +
                "            displayName: 'Run a one-line script'\n" +
                "  - stage: documentation\n" +
                "    displayName: 'Documentation'\n" +
                "    dependsOn: [ downstream ]\n" +
                "    condition: and(succeeded('downstream'), or(eq(variables.isDevelop, true), eq(variables.isRelease, true), eq(variables.isMaster, true)))\n" +
                "    variables:\n" +
                "      - group: ${{ variables['STAGE_NAME'] }}\n" +
                "    jobs:\n" +
                "      - job: documentation\n" +
                "        displayName: 'Documentation'\n" +
                "        pool:\n" +
                "          vmImage: ubuntu-latest\n" +
                "        steps:\n" +
                "          - script: |\n" +
                "              git clone https://$(REPO_TOKEN)@$(REPO_HOST)/$(REPO_ORGANISATION)/_git/doc-as-code\n" +
                "              cd doc-as-code \n" +
                "              git checkout $(BRANCH_NAME)\n" +
                "            displayName: 'Git: checkout'\n" +
                "          - script: |\n" +
                "              mkdir -p doc-as-code/.docs/$(APP_DISPLAY_NAME)/deleteme\n" +
                "              rm -r doc-as-code/.docs/$(APP_DISPLAY_NAME)/*\n" +
                "              cp -R docs/. doc-as-code/.docs/$(APP_DISPLAY_NAME)\n" +
                "            displayName: 'Copy Docu'\n" +
                "          - script: |\n" +
                "              if [ -d \"sdd\" ]; then\n" +
                "                mkdir -p doc-as-code/.sdd/$(APP_DISPLAY_NAME)/deleteme\n" +
                "                rm -r doc-as-code/.sdd/$(APP_DISPLAY_NAME)/*\n" +
                "                cp -R sdd/. doc-as-code/.sdd/$(APP_DISPLAY_NAME)\n" +
                "              fi\n" +
                "            displayName: 'Copy SDD'\n" +
                "          - script: |\n" +
                "              cd doc-as-code\n" +
                "              git config --global user.email mario.tema@ascent.io\n" +
                "              git config --global user.name mario.tema\n" +
                "              git add --all\n" +
                "              git status\n" +
                "              if [[ `git status --porcelain` ]]; then\n" +
                "                 git commit -am \"Update DOC_AS_CODE repo within docs from $(REPO_NAME) commit $(GIT_COMMIT)\"\n" +
                "                 git push https://$(REPO_TOKEN)@$(REPO_HOST)/$(REPO_ORGANISATION)/_git/doc-as-code\n" +
                "              fi\n" +
                "            displayName: 'Git: Commit'\n" +
                "  - stage: notification\n" +
                "    displayName: 'Notification'\n" +
                "    dependsOn: [ documentation ]\n" +
                "    condition: and(succeeded('documentation'), or(eq(variables.isRelease, true), eq(variables.isMaster, true)))\n" +
                "    jobs:\n" +
                "      - job: notification\n" +
                "        displayName: 'Notification'\n" +
                "        pool:\n" +
                "          vmImage: ubuntu-latest\n" +
                "        steps:\n" +
                "          - script: echo Notification\n" +
                "            displayName: 'Run a one-line script'\n" +
                "\n", answer);
    }
}
