This covers the instructions on how to build/develop with this module.

It uses maven 2 as the main build system (pom.xml) which will setup the dependencies.
When building into a war, or running unit tests, use

>mvn package

GWT is used to build the UI. This is what the ant build script is for.
If you take a look at build.properties, you may need to change the gwt.home path to be 
relevant for you.

Download GWT if you want to mess with the UI.

In eclipse: this also has a eclipse project file setup for you.
You will need to have the M2_REPO variable setup.

You can run all unit tests from here of course. You can also run GWT in 
"hosted" mode (unless you are on the mac). To do this, there is a JBRMS-launch configuration
for eclipse (when you select Run>Run from the menu, it should provide a JBRMS run config). 
If you are debugging GWT this way, you will need the GWT_HOME variable
set to where you have installed GWT (and if you are on windows, change the jar
to the one for you).

To make a change in GWT - when you are finished debugging, you will need to run
the ant build script to refresh the static web content (all the AJAXY shite).

Any questions, contact michael.neale@gmail.com or find me in IRC.
