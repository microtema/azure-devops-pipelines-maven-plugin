# Azure DevOps pipelines generator
Reducing Boilerplate Code with DevOps pipelines maven plugin
> More Time for Feature and functionality
  Through a simple set of github workflows templates and saving 60% of development time 

## Key Features
* Auto generate by maven compile phase
* Auto JUnit Tests detector by adding "JUnit Tests" stage
* Auto Integration Tests detector by adding "Integration Tests" stage
* Auto Dockerfile detector by adding "Build Docker" stage
* Auto Maven artifact detector by adding "Deploy Maven Artifact" stage
* Auto Sonar report detector by adding "Sonar Report" stage
* Auto Deployment to Cloud Platform by adding "Deployment" stage


## How to use

```
    <profiles>
        <profile>
            <id>dev</id>
            <build>
                <plugins>
                    <plugin>
                        <groupId>de.microtema</groupId>
                        <artifactId>azure-devops-pipelines-maven-plugin</artifactId>
                        <version>${project.version}</version>
                        <configuration>
                            <variables>
                                <DOCKER_REGISTRY>docker.io</DOCKER_REGISTRY>
                                <STAGE_NAME>NAME_$STAGE_NAME</STAGE_NAME>
                            </variables>
                            <stages>
                                <none>feature/*,bugfix/*</none>
                                <develop>develop</develop>
                                <release>release/*</release>
                                <master>master</master>
                            </stages>
                        </configuration>
                        <executions>
                            <execution>
                                <id>azure-devops-pipelines</id>
                                <phase>validate</phase>
                                <goals>
                                    <goal>generate</goal>
                                </goals>
                            </execution>
                        </executions>
                    </plugin>
                </plugins>
            </build>
        </profile>
```

### maven command

```
mvn validate -P dev
```

## Output

> azure-pipelines.yml
> NOTE: This is an example file.

```
############## Created by de.microtema:azure-devops-pipelines-maven-plugin ############
#++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++#
# azure-pipelines.yml is generated and should not be edited. #
################################################################################

trigger: [bugfix/*, develop, feature/*, master, release/*]

variables:
  APP_NAME: "azure-devops-pipelines-maven-plugin"
  APP_DISPLAY_NAME: "azure-devops-pipelines-maven-plugin"
  VERSION: "1.1.0-SNAPSHOT"
  GIT_COMMIT: "$(Build.SourceVersion)"
  REPO_NAME: "$(Build.Repository.Name)"
  BRANCH_NAME: "$[replace(variables['Build.SourceBranch'], 'refs/heads/', '')]"
  isDevelop: "$[eq(variables['Build.SourceBranch'], 'refs/heads/develop')]"
  isRelease: "$[startsWith(variables['Build.SourceBranch'], 'refs/heads/release/')]"
  isMaster: "$[eq(variables['Build.SourceBranch'], 'refs/heads/master')]"
  REPO_ORGANISATION: "mariotema"
  REPO_PROJECT: "microtema"

stages:
  - stage: initialize
    displayName: Initialize
    jobs:
      - job: initialize
        displayName: Initialize
        pool:
          vmImage: ubuntu-latest
        steps:
          - script: echo dotnet --version
            displayName: 'Run a one-line script'
  - stage: versioning
    displayName: Versioning
    dependsOn: [ initialize ]
    condition: succeeded('initialize')
    jobs:
      - job: versioning
        displayName: Versioning
        pool:
          vmImage: ubuntu-latest
        steps:
          - script: echo handle semver 2.0
            displayName: 'Run a one-line script'
  - stage: compile
    displayName: Compile
    dependsOn: [ versioning ]
    condition: succeeded('versioning')
    jobs:
      - job: versioning
        displayName: versioning
        pool:
          vmImage: ubuntu-latest
        steps:
          - script: echo dotnet restore
            displayName: 'Run a one-line script'
  - stage: security_check
    displayName: Security Check
    dependsOn: [ compile ]
    condition: succeeded('compile')
    jobs:
      - job: security_check
        displayName: Security Check
        pool:
          vmImage: ubuntu-latest
        steps:
          - script: echo security_check
            displayName: 'Run a one-line script'
  - stage: unit_test
    displayName: Unit Test
    dependsOn: [ security_check ]
    condition: succeeded('security_check')
    jobs:
      - job: unit_test
        displayName: unit test
        pool:
          vmImage: ubuntu-latest
        steps:
          - script: echo dotnet test
            displayName: 'Run a one-line script'
  - stage: it_test
    displayName: Integration Test
    dependsOn: [ security_check ]
    condition: succeeded('security_check')
    jobs:
      - job: it_test
        displayName: Integration Test
        pool:
          vmImage: ubuntu-latest
        steps:
          - script: echo it_test
            displayName: 'Run a one-line script'
  - stage: quality_gate
    displayName: Quality Gate
    dependsOn: [ unit_test, it_test ]
    condition: and(succeeded('unit_test'), succeeded('it_test'))
    jobs:
      - job: quality_gate
        displayName: quality gate
        pool:
          vmImage: ubuntu-latest
        steps:
          - script: echo quality_gate
            displayName: 'Run a one-line script'
  - stage: build
    displayName: Build
    dependsOn: [ quality_gate ]
    condition: succeeded('quality_gate')
    jobs:
      - job: build
        displayName: Build
        pool:
          vmImage: ubuntu-latest
        steps:
          - script: echo build
            displayName: 'Run a one-line script'
  - stage: package
    displayName: Package
    dependsOn: [ build ]
    condition: and(succeeded('build'), or(eq(variables.isDevelop, true), eq(variables.isRelease, true), eq(variables.isMaster, true)))
    jobs:
      - job: package
        displayName: package
        pool:
          vmImage: ubuntu-latest
        steps:
          - script: echo package
            displayName: 'Run a one-line script'
  - stage: tag
    displayName: Tag Release
    dependsOn: [ package ]
    condition: and(succeeded('build'), eq(variables.isMaster, true))
    jobs:
      - job: tag
        displayName: package
        pool:
          vmImage: ubuntu-latest
        steps:
          - script: echo tag release
            displayName: 'Run a one-line script'
  - stage: db_migration
    displayName: DB Migration
    dependsOn: [ tag ]
    condition: and(succeeded('package'), or(eq(variables.isDevelop, true), eq(variables.isRelease, true), eq(variables.isMaster, true)))
    jobs:
      - job: db_migration
        displayName: DB Migration
        pool:
          vmImage: ubuntu-latest
        steps:
          - script: echo db_migration
            displayName: 'Run a one-line script'
  - stage: build
    displayName: Build
    dependsOn: [ quality_gate ]
    condition: succeeded('quality_gate')
    jobs:
      - job: build
        displayName: Build
        pool:
          vmImage: ubuntu-latest
        steps:
          - script: echo build
            displayName: 'Run a one-line script'
  - stage: readiness
    displayName: Readiness
    dependsOn: [ deployment ]
    condition: and(succeeded('deployment'), or(eq(variables.isDevelop, true), eq(variables.isRelease, true), eq(variables.isMaster, true)))
    jobs:
      - job: Readiness
        displayName: Readiness
        pool:
          vmImage: ubuntu-latest
        steps:
          - script: echo Readiness
            displayName: 'Run a one-line script'
  - stage: system_test
    displayName: System Test
    dependsOn: [ readiness ]
    condition: and(succeeded('readiness'), or(eq(variables.isDevelop, true), eq(variables.isRelease, true)))
    jobs:
      - job: system_test
        displayName: System Test
        pool:
          vmImage: ubuntu-latest
        steps:
          - script: echo system_test
            displayName: 'Run a one-line script'
  - stage: build
    displayName: Build
    dependsOn: [ quality_gate ]
    condition: succeeded('quality_gate')
    jobs:
      - job: build
        displayName: Build
        pool:
          vmImage: ubuntu-latest
        steps:
          - script: echo build
            displayName: 'Run a one-line script'
  - stage: downstream
    displayName: E2E Test
    dependsOn: [ performance_test ]
    condition: and(succeeded('performance_test'), or(eq(variables.isDevelop, true), eq(variables.isRelease, true)))
    jobs:
      - job: downstream
        displayName: downstream
        pool:
          vmImage: ubuntu-latest
        steps:
          - script: echo downstream
            displayName: 'Run a one-line script'
  - stage: documentation
    displayName: Documentation
    dependsOn: [ downstream ]
    condition: and(succeeded('readiness'), or(eq(variables.isDevelop, true), eq(variables.isRelease, true), eq(variables.isMaster, true)))
    jobs:
      - job: documentation
        displayName: Documentation
        pool:
          vmImage: ubuntu-latest
        steps:
          - script: |
              git clone https://$(REPO_TOKEN)@dev.azure.com/$(REPO_ORGANISATION)/$(REPO_PROJECT)/_git/doc-as-code
              cd doc-as-code 
              git checkout $(BRANCH_NAME)
            displayName: 'Git: checkout'
          - script: |
              mkdir -p doc-as-code/.docs/$(APP_NAME)/deleteme
              rm -r doc-as-code/.docs/$(APP_NAME)/*
              cp -R docs/. doc-as-code/.docs/$(APP_NAME)
              cp README.md doc-as-code/docs/$(APP_NAME)/$(APP_NAME).md
            displayName: 'Copy Docu'
          - script: |
              cd doc-as-code
              git config --global user.email mario.tema@ascent.io
              git config --global user.name mario.tema
              git add --all
              git status
              if [[ `git status --porcelain` ]]; then
                 git commit -am "Update DOC_AS_CODE repo within docs from $(REPO_NAME) commit $(GIT_COMMIT)"
                 git push https://$(REPO_TOKEN)@dev.azure.com/$(REPO_ORGANISATION)/$(REPO_PROJECT)/_git/doc-as-code
              fi
            displayName: 'Git: Commit'
  - stage: notification
    displayName: Notification
    dependsOn: [ documentation ]
    condition: and(succeeded('readiness'), or(eq(variables.isDevelop, true), eq(variables.isRelease, true), eq(variables.isMaster, true)))
    jobs:
      - job: notification
        displayName: Notification
        pool:
          vmImage: ubuntu-latest
        steps:
          - script: echo Notification
            displayName: 'Run a one-line script'

```
    
## Technology Stack

* Java 1.8
    * Streams 
    * Lambdas
* Third Party Libraries
    * Commons-BeanUtils (Apache License)
    * Commons-IO (Apache License)
    * Commons-Lang3 (Apache License)
    * Mustache (MIT License)
    * Junit (EPL 1.0 License)
* Code-Analyses
    * Sonar
    * Jacoco
    
## Test Coverage threshold
> 95%
    
## License

MIT (unless noted otherwise)

## Quality Gate Status

[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=github-workflows-maven-plugin&metric=alert_status)](https://sonarcloud.io/dashboard?id=mtema_github-workflows-maven-plugin)

[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=github-workflows-maven-plugin&metric=coverage)](https://sonarcloud.io/dashboard?id=mtema_github-workflows-maven-plugin)

[![Technical Debt](https://sonarcloud.io/api/project_badges/measure?project=github-workflows-maven-plugin&metric=sqale_index)](https://sonarcloud.io/dashboard?id=mtema_github-workflows-maven-plugin)
