/**
 * Simple Dockerfile build
 */

CFG.'docker.deploy_image' = CFG.'docker.deploy_image' ?: env.NODE_NAME

MPLPostStep('always') {
  node('master') {
    withEnv(["PATH+DOCKER=${tool(CFG.'docker.tool_version' ?: 'Docker')}/bin"]) {
      // Remove the docker image
      sh "docker rmi -f '${CFG.'docker.deploy_image'}'"
    }
  }
}

// Stash the artifacts to build image on master
stash name: 'docker_build',
      includes: (['Dockerfile'] + CFG.'docker.artifacts' ?: []).join(',')

node('master') {
  withEnv(["PATH+DOCKER=${tool(CFG.'docker.tool_version' ?: 'Docker')}/bin"]) {
    // Unstash artifacts & build the docker image
    unstash 'docker_build'
    sh "docker build -t ${CFG.'docker.deploy_image'} ."
  }
}
