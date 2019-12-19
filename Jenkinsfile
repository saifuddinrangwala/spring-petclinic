try {
    def appName=env.APP_NAME
    def gitSourceUrl=env.GIT_SOURCE_URL
    def gitSourceRef=env.GIT_SOURCE_REF
    def project=""
    def projectVersion=""
    def quayUser=env.QUAY_USER
    def quayPassword=env.QUAY_PASSWORD
    def ocpUser=env.OCP_USER
    def ocpPassword=env.OCP_PASSWORD
    node("maven") {
        stage("Initialize") {
            project = env.PROJECT_NAME
            echo "appName: ${appName}"
            echo "gitSourceUrl: ${gitSourceUrl}"
            echo "gitSourceRef: ${gitSourceRef}"
        }
        stage("Checkout") {
            echo "Checkout source."
            git url: "${gitSourceUrl}", branch: "${gitSourceRef}"
            echo "Read POM info."
            pom = readMavenPom file: 'pom.xml'
            projectVersion = pom.version
        }
        stage("Build JAR") {
            echo "Build the app."
            sh "mvn clean package"
        }
        stage("Quality Check") {
   			sh "mvn clean org.jacoco:jacoco-maven-plugin:prepare-agent install -Dmaven.test.failure.ignore=false"
            sh "mvn sonar:sonar -Dsonar.jacoco.reportPaths=target/coverage-reports/jacoco-unit.exec -Dsonar.host.url=http://sonarqube.cicd.svc:9000"
            // sh "mvn org.cyclonedx:cyclonedx-maven-plugin:makeBom"
            //dependencyTrackPublisher(artifact: 'target/bom.xml', artifactType: 'bom', projectName: "${appName}", projectVersion: "${projectVersion}", synchronous: false)
        }
        stage("Build Image") {
            echo "Build container image."
            // unstash name:"jar"
            openshift.withCluster() {
                openshift.withProject('cicd') {
                    sh "oc start-build ${appName}-s2i-build --from-file=target/app.jar -n cicd --follow"
                }
            }
        }
    }
    node('jenkins-slave-skopeo') {
        
        stage('Clair Container Vulnerability Scan') {
            sh "oc login -u $ocpUser -p $ocpPassword --insecure-skip-tls-verify https://api.cluster-ottawa-57ac.ottawa-57ac.example.opentlc.com:6443 2>&1"
            sh 'skopeo --debug copy --src-creds="$(oc whoami)":"$(oc whoami -t)" --src-tls-verify=false --dest-tls-verify=false' + " --dest-creds=$quayUser:$quayPassword docker://docker-registry.default.svc:5000/cicd/spring-petclinic:latest docker://quay.io/$quayUser/spring-petclinic:latest"
        }
        
        stage("Tag DEV") {
            echo "Tag image to DEV"
            openshift.withCluster() {
                openshift.withProject('cicd') {
                    openshift.tag("${appName}:latest", "${appName}:dev")
                }
            }
        }
        stage("Deploy DEV") {
            echo "Deploy to DEV."
            openshift.withCluster() {
                openshift.withProject("${appName}-dev") {
                    echo "Rolling out to DEV."
                    def dc = openshift.selector('dc', "${appName}")
                    dc.rollout().latest()
                    dc.rollout().status()
                }
            }
        }
        stage("Tag for QA") {
            echo "Tag to UAT"
            openshift.withCluster() {
                openshift.withProject('cicd') {
                    openshift.tag("${appName}:dev", "${appName}:uat")
                }
            }
        }
        stage("Deploy UAT") {
            echo "Deploy to UAT."
            openshift.withCluster() {
                openshift.withProject("${appName}-uat") {
                    echo "Rolling out to UAT."
                    def dc = openshift.selector('dc', "${appName}")
                    dc.rollout().latest()
                    dc.rollout().status()
                }
            }
        }
    }
} catch (err) {
    echo "in catch block"
    echo "Caught: ${err}"
    currentBuild.result = 'FAILURE'
    throw err
}
