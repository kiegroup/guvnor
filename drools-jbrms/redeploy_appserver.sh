mvn package -Dmaven.test.skip=true
cp ./target/drools-jbrms.war /usr/local/jboss-4.0.5.GA/server/default/deploy
