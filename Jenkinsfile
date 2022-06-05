@Library('slack') _

pipeline {
  agent any

environment {
		DOCKERHUB_CREDENTIALS=credentials('docker-hub')
		applicationURL = "http://swotitup.southeastasia.cloudapp.azure.com"
        applicationURI = "/customerses"
        deploymentName = "swotitup-secops"
        containerName = "swotitup-secops"
        serviceName = "swotitup-secops-svc"
        imageName = "venmaum/secops-app:${BUILD_NUMBER}"

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

//     stage('SonarQube - SAST') {
//           steps {
//             withSonarQubeEnv('SonarQube') {
//               sh "mvn sonar:sonar -Dsonar.projectKey=swotitup_secops -Dsonar.host.url=http://swotitup.southeastasia.cloudapp.azure.com:9000 -Dsonar.login=2cc8403c8b8d98685979d2d10818e2fb03f28ab8"
//             }
//             timeout(time: 2, unit: 'MINUTES') {
//               script {
//                 waitForQualityGate abortPipeline: true
//               }
//             }
//           }
//         }
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
//  stage('Vulnerability Scan - Kubernetes') {
//       steps {
//         sh 'docker run --rm -v $(pwd):/project openpolicyagent/conftest test --policy opa-k8s-security.rego k8s_deployment_service.yaml'
//       }
//     }
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
        stages {
            stage('Testing Slack') {
              steps {
                sh 'exit 1'
              }
            }

          }
//      stage('OWASP ZAP - DAST') {
//                steps {
//                  withKubeConfig([credentialsId: 'kubeconfig']) {
//                    sh 'bash zap.sh'
//                  }
//                }
//              }
//   }
  post {
      always {
       publishHTML([allowMissing:false, alwaysLinkToLastBuild: true, keepAll: true, reportDir: 'owasp-zap-report', reportFiles: 'zap_report.html', reportName: 'OWASP ZAP HTML Report', reportTitles: 'OWASP ZAP'])
        send_notification currentBuild.result
      }

      // success {

      // }

      // failure {

      // }
    }
}