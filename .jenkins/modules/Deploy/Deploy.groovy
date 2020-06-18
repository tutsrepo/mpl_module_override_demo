/**
 * Additional docker image deployment
 */

MPLModule('Deploy', CFG)

if( fileExists('Dockerfile') ) {
  MPLModule('Docker Deploy', CFG)
}
