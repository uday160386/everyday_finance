pipeline {
  agent any

environment {
		DOCKERHUB_CREDENTIALS=credentials('docker-hub')

	}
  stages {

    stage('Build Artifact - Maven') {
      steps {
        sh "mvn clean package -DskipTests=true"
        archive 'target/*.jar'
      }
    }

    stage('Unit Tests - JUnit and Jacoco') {
      steps {
        sh "mvn test"
      }
      post {
        always {
          junit 'target/surefire-reports/*.xml'
          jacoco execPattern: 'target/jacoco.exec'
        }
      }
    }
// stage('Mutation Tests - PIT') {
//       steps {
//         sh "mvn org.pitest:pitest-maven:mutationCoverage"
//       }
//       post {
//         always {
//           pitmutation mutationStatsFile: '**/target/pit-reports/**/mutations.xml'
//         }
//       }
//     }

    stage('SonarQube - SAST') {
          steps {
            withSonarQubeEnv('SonarQube') {
              sh "mvn sonar:sonar -Dsonar.projectKey=swotitup_secops -Dsonar.host.url=http://swotitup.southeastasia.cloudapp.azure.com:9000 -Dsonar.login=9b22a188940e36f5298c819e7e90960e3278d233"
            }
            timeout(time: 2, unit: 'MINUTES') {
              script {
                waitForQualityGate abortPipeline: true
              }
            }
          }
        }
// stage('Vulnerability Scan - Docker ') {
//       steps {
//         sh "mvn dependency-check:check"
//       }
//       post {
//         always {
//           dependencyCheckPublisher pattern: 'target/dependency-check-report.xml'
//         }
//       }
//     }
 stage('Vulnerability Scan - Kubernetes') {
      steps {
        sh 'docker run --rm -v $(pwd):/project openpolicyagent/conftest test --policy opa-k8s-security.rego k8s_deployment_service.yaml'
      }
    }
    stage('Docker Build and Push') {
      steps {

          sh 'printenv'

          sh 'docker build -t venmaum/secops-app:""$BUILD_NUMBER"" .'
          sh 'echo $DOCKERHUB_CREDENTIALS_PSW | docker login -u $DOCKERHUB_CREDENTIALS_USR --password-stdin'
          sh 'docker push venmaum/secops-app:""$BUILD_NUMBER""'
          sh 'docker logout'

      }
    }
     stage('Kubernetes Deployment - DEV') {
          steps {
            withKubeConfig([credentialsId: 'kubeconfig']) {
              sh 'sed -i "s#replace#venmaum/secops-app:""$BUILD_NUMBER""#g" K8_secops_deployment.yaml'
              sh "kubectl apply -f K8_secops_deployment.yaml"
            }
          }
        }
  }
}