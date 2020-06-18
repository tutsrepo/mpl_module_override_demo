/**
 * Deploy a simple selenium cluster via Docker
 */

def network_name = env.NODE_NAME
def container_id = env.DOCKER_CONTAINER_ID

MPLPostStep('always') {
  node('master') {
    withEnv(["PATH+DOCKER=${tool(CFG.'docker.tool_version' ?: 'Docker')}/bin"]) {
      // Disconnecting jenkins slave form the network & destroying all the containers in the network
      sh "docker network disconnect '${network_name}' '${container_id}'"
      sh "for i in \$(docker network inspect -f '{{range .Containers}}{{.Name}} {{end}}' '${network_name}'); do docker rm -f \$i; done"
      sh "docker network rm '${network_name}'"
    }
  }
}

node('master') {
  withEnv(["PATH+DOCKER=${tool(CFG.'docker.tool_version' ?: 'Docker')}/bin"]) {
    // Preparing private network & connect current jenkins slave
    sh "docker network create '${network_name}'"
    sh "docker network connect --alias jenkins-slave '${network_name}' '${container_id}'"

    // Run selenium hub
    sh "docker run -d --name '${network_name}-selenium-hub' --stop-timeout ${CFG.'selenium.timeout' ?: 3600} selenium/hub:3.11.0-dysprosium"
    sh "docker network connect --alias selenium-hub '${network_name}' '${network_name}-selenium-hub'"

    // Run selenium workers
    for( def i = 0; i < (CFG.'selenium.workers.firefox' ?: 1); i++ ) {
      sh "docker run -d --name '${network_name}-selenium-firefox-${i}' --network '${network_name}' --stop-timeout ${CFG.'selenium.timeout' ?: 3600} -e HUB_HOST=selenium-hub -v /dev/shm:/dev/shm selenium/node-firefox:3.11.0-dysprosium"
    }
    for( def i = 0; i < (CFG.'selenium.workers.chrome' ?: 1); i++ ) {
      sh "docker run -d --name '${network_name}-selenium-chrome-${i}' --network '${network_name}' --stop-timeout ${CFG.'selenium.timeout' ?: 3600} -e HUB_HOST=selenium-hub -v /dev/shm:/dev/shm selenium/node-chrome:3.11.0-dysprosium"
    }
  }
}

if( fileExists('Dockerfile') && CFG.'docker.deploy_name' ) {
  node('master') {
    withEnv(["PATH+DOCKER=${tool(CFG.'docker.tool_version' ?: 'Docker')}/bin"]) {
      // Connected deployed application container to the network
      // TODO: Looks like not a good idea - find a way to manage private network over pipeline
      sh """docker network connect --alias target-host '${network_name}' "${CFG.'docker.deploy_name'}" """
    }
  }
}
