import jetbrains.buildServer.configs.kotlin.v2019_2.*
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.PowerShellStep
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.maven
import jetbrains.buildServer.configs.kotlin.v2019_2.buildSteps.powerShell
import jetbrains.buildServer.configs.kotlin.v2019_2.triggers.vcs

/*
The settings script is an entry point for defining a TeamCity
project hierarchy. The script should contain a single call to the
project() function with a Project instance or an init function as
an argument.

VcsRoots, BuildTypes, Templates, and subprojects can be
registered inside the project using the vcsRoot(), buildType(),
template(), and subProject() methods respectively.

To debug settings scripts in command-line, run the

    mvnDebug org.jetbrains.teamcity:teamcity-configs-maven-plugin:generate

command and attach your debugger to the port 8000.

To debug in IntelliJ Idea, open the 'Maven Projects' tool window (View
-> Tool Windows -> Maven Projects), find the generate task node
(Plugins -> teamcity-configs -> teamcity-configs:generate), the
'Debug' option is available in the context menu for the task.
*/

version = "2021.2"

project {
    buildType(GitHubTest)
    buildType(BuildPetClinic)
}

object BuildPetClinic : BuildType({
    name = "Build-PetClinic"

    vcs {
        root(DslContext.settingsRoot)
    }

    steps {
        maven {
            goals = "clean test"
            runnerArgs = "-Dmaven.test.failure.ignore=true"
        }
    }

    triggers {
        vcs {
        }
    }
})

object GitHubTest : BuildType({
    name = "GitHubTest"

    params {
        password("GitHubToken", "credentialsJSON:7e037c7f-d428-47ee-92dd-790365c8b5be")
    }

    vcs {
        root(DslContext.settingsRoot)
    }

    triggers {
        vcs {
        }
    }

    steps {
        powerShell {
            name = "GetPullRequest"
            edition = PowerShellStep.Edition.Core
            scriptMode = script {
                content = """
                ${'$'}ErrorActionPreference = "Stop"
                Write-Host %
                Get-GitHubPullRequest -Owner WSStudios -RepositoryName tdp1 -Number 5736 -Token ${'$'}(ConvertTo-SecureString -AsPlainText -Force -String "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")
                """.trimIndent()
            }
        }
    }
})
