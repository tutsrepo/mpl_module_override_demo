FROM tomcat:8-jre8-alpine

COPY target/petclinic.war /usr/local/tomcat/webapps
