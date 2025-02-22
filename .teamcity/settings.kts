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

object Secrets {
    val githubToken = "credentialsJSON:8565735a-8b84-4926-a24c-b1fca4a9d060"
}

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
        password("GitHubToken", Secrets.githubToken)
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
            scriptMode = file {
                path = "Test-GitHub.ps1"
            }
            param("jetbrains_powershell_scriptArguments", "-TeamCityToken %GitHubToken%")
        }
    }
})
