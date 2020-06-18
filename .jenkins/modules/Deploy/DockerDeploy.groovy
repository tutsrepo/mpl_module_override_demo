/**
 * Deploying the docker image locally
 */

CFG.'docker.deploy_image' = CFG.'docker.deploy_image' ?: env.NODE_NAME

MPLPostStep('always') {
  node('master') {
    withEnv(["PATH+DOCKER=${tool(CFG.'docker.tool_version' ?: 'Docker')}/bin"]) {
      // Destroying the deployed container
      sh "docker rm -f '${CFG.'docker.deploy_id'}'"
    }
  }
}

node('master') {
  withEnv(["PATH+DOCKER=${tool(CFG.'docker.tool_version' ?: 'Docker')}/bin"]) {
    // Run docker image as container
    CFG.'docker.deploy_id' = sh(script: """docker run -d --name "${CFG.'docker.deploy_name' ?: ''}" --stop-timeout ${CFG.'docker.timeout' ?: 3600} ${CFG.'docker.deploy_image'}""", returnStdout: true).trim()
  }
}
