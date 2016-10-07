# CollaborateUltraPortlet
uPortal portlet providing integration with Collaborate Ultra

# Important!

This portlet will only work with a valid Collaborate Ultra licencse and setup. If you have a valid setup you will have been given API information which you will need to configure the portlet.

# Requirements

* You will need a database set up which this portlet will use to cache collaborate ultra session information locally. A sample table creation script can be found at /src/test/resources/create-tables-vc-ultra.sql.

# Configuration

The main configuration file is at **/src/main/resources/application.properties**. You will need to provide information on the following:

* The database (as per requirements)
* The LDAP directory to look up users from
* The mail host to send email via

There is also a test configuration file at **/src/test/resources/testCollaborateApi.properties**, where you should put credentials for a Test API. These tests are set to auto-skip so that you can build the portlet regardless.

Finally, there are settings in **/src/main/webapp/WEB-INF/portlet.xml** controlling who can access the full/cut-down collaborate session screens, and a group who has administrative control. Please ensure you have set these as per your uPortal installation.

# Compilation

* Build is using Maven, e.g. **mvn clean package**

#Installation

Install the portlet as per uPortal instructions: https://wiki.jasig.org/display/UPM40/Deploy+and+Publish+a+Portlet

