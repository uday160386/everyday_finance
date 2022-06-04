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
              sh "sed -i 's#replace#venmaum/secops-app:${""$BUILD_NUMBER""}#g' K8_secops_deployment.yaml"
              sh "kubectl apply -f K8_secops_deployment.yaml"
            }
          }
        }
  }
}