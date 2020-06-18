/**
 * Running selenium tests using maven
 */

MPLPostStep('always') {
  junit 'target/failsafe-reports/*.xml'
}

withEnv(["PATH+MAVEN=${tool(CFG.'maven.tool_version' ?: 'Maven 3')}/bin"]) {
  def settings = CFG.'maven.settings_path' ? "-s '${CFG.'maven.settings_path'}'" : ''
  sh """mvn -B ${settings} -DargLine='-Xmx1024m -XX:MaxPermSize=1024m' verify -P ${CFG.'selenium.maven_profile'} -DargLine=" """+
    " -Dtest.selenium.hub.url=${CFG.'selenium.hub_url' ?: 'http://selenium-hub:4444/wd/hub'}"+
    " -Dtest.target.server.url=${CFG.'selenium.target_url' ?: 'http://target-host:8080'}"+
    ' -Dtest.run.htmlunit=true'+
    ' -Dtest.run.ie=false'+
    ' -Dtest.run.firefox=true'+
    ' -Dtest.run.chrome=true'+
    ' -Dtest.run.opera=false"'
}
