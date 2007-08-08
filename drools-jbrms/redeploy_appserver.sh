mvn -o  package -Dmaven.test.skip=true
cp ./target/drools-jbrms.war /usr/local/share/jboss-4.2.0.GA/server/default/deploy


