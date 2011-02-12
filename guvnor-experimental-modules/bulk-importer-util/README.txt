Guvnor-importer-v1.0
---------------------

The Guvnor-Importer is a maven build tool that recurses your rules directory structure and constructs an
xml import file that can be manually imported into the Drools-Guvnor web interface via the import/export 
administration feature.

It is designed for larger scale projects where in the development phases it's far easier for each developer 
to maintain their own set of rule files, however when the project reaches the staging/pre-production phase 
and the final drools-guvnor solution would be more appropriate to use/test then this tool enables you to 
make that transition.

How to run it
-------------

Included example rules ("my_rules" folder) and example domain objects (sample-model project) used by the example rules. 

If you run "mvn clean install" in the guvnor-importer folder then it should build the example showing the compilation (and percentage complete) as it goes (including a sample dependency failure). 

two files are generated : 
1) kagent-changeset.xml - drools 5 formatted change-set file for the knowledge agent 
2) guvnor-import.xml - the file that can be imported into the Guvnor interface in the Administration section. 


from the guvnor-importer folder:
[mallen@mallen guvnor-importer]$ mvn clean install

from your super.pom further up in the folder hierarchy (ie where guvnor-importer is a sub directory of project1
[mallen@mallen project1]$ mvn clean install clean install -f guvnor-importer/pom.xml

What this tool does
-------------------

This tool: 
- can be executed from maven using exec-maven-plugin 1.1
- assumes a directory structure containing your drl/xls files mapping to your intended package structure
- generates a knowledge agent changeset xml file to work in conjunction with the import package file
- is template based so you can change the guvnor category, status name etc... reasonably easily

