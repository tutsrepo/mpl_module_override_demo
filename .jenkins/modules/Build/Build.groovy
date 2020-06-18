/**
 * Additionally build the docker image if Dockerfile is found
 */

MPLPostStep('always') {
  junit 'target/surefire-reports/*.xml'
}

MPLModule('Build', CFG)

if( fileExists('Dockerfile') ) {
  MPLModule('Docker Build', CFG)
}
