ant gwt-compile
mvn -o  package -DskipTests
cp -v ./target/drools-guvnor.war /usr/local/share/java/jetty-6.1.0/webapps



