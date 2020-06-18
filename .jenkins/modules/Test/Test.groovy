/**
 * Running selenium tests
 */

if( ! CFG.'selenium.hub_url' ) {
  MPLModule('Selenium Deploy', CFG)
}

MPLModule('Selenium Maven Test', CFG)
