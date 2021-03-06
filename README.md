UML-Testing-Tool [![Build Status](https://travis-ci.org/timezra/UML-Testing-Tool.png)](https://travis-ci.org/timezra/UML-Testing-Tool)
========================================================================

The UML Testing Tool is a graphical workbench for building models based on the UML Testing Profile (http://utp.omg.org/) based on the Eclipse Papyrus platform (http://eclipse.org/papyrus/).

This project is based on this maven archetype: http://github.com/timezra/tycho_new_plugin_project

This project can be built from the root pom with standard Maven commands, e.g.,

    $> mvn clean verify

If you would like to build a p2 repository for this project with signed jars use the following commands (the first to generate a self-signed cert with a 1-year validity if you do not have a cert from a CA; the second to activate the maven profile to sign the jars that are packaged in the p2 repository):

    $> keytool -genkey -alias _keystore_alias_ -keystore /path/to/keystore -validity 365
    $> mvn -Psign -Djarsigner.keystore=/path/to/keystore -Djarsigner.storepass=_keystore_password_ -Djarsigner.alias=_keystore_alias_ clean package integration-test

When importing these projects into Eclipse, you should be able to import them as Existing Maven Projects (with the m2e plugin). This should create the appropriate .classpath and .project files.
You may need to delete the timezra.uml.testing.resources.tests project from the workspace (but not from disk) and then re-import it as a General -> Existing Project after the .classpath and .project files have been created. This might have something to do with its being a fragment.

Because of an m2e bug (https://issues.sonatype.org/browse/MNGECLIPSE-966), you may need to set the JRE for timezra.uml.testing.resources and timezra.uml.testing.resources.tests to 1.7.

This plug-in can also be installed from the following p2 repository: https://timezra.github.com/UML-Testing-Tool
