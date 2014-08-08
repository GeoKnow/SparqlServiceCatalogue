echo "Stopping tomcat"
sudo service tomcat7 stop

echo "Purging debs"
sudo apt-get purge datacat-tomcat-common datacat-tomcat7

echo "Removing bower components"
rm -rf datacat-webapp/src/main/webapp/bower_components/

echo "Running mvn clean install"
mvn clean install

echo "Installing debs"
sudo dpkg -i `find datacat-debian-tomcat-common/target -name *.deb`
sudo dpkg -i `find datacat-debian-tomcat7/target -name *.deb`

