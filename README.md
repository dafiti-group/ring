# Ring [![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
### A new solution for user generated external data
###### Every day many companies are generating and processing new data, whether it is structured, semi-structured or non structured. All these data are used for analytics and decision taken at any moment, and in the mostly we need to join data from differents sources to improve our analysis and the best way to achieve this is through BigData.
###### Sometimes when we need to create a KIP or a new Dashboard, we may need some data not avalable in our data sources or for any reason it's not possible to extract, so we generate new files to integrate with our tools but it's hard to keep a pattern and define the best way to consume the files in the processes.
###### **That's why Ring was created!** Ring is a tool to keep track of user generated NoSQL data, where you can define a template, upload files and validate uploaded data. This way Ring becomes a new source you can integrate with your tools extracting data via API, and Ring will take care of providing data in a more fault-tolerant way to your processes

## Instalation

##### REQUIREMENTS

- Tomcat 8 +
- MySQL 5.7 +
- Java 8 +

##### BUILD
Using [Maven](https://maven.apache.org/):

- Access the directory where Ring source code is placed.
- Use the command **mvn package**.
- The file ring.war will be created in subdirecotry *target*.

##### CONFIGURATION

- Create the temporary directory where system will use to handle the files

```shell
mkdir -p ~/tmp_files/ring/
```

- Create the file ~/.ring/ring.properties with the following content

```properties
####### DATABASE CONFIGURATION #######

# Ring MySQL
spring.datasource.url=jdbc:mysql://<host>:<port>/<db>
spring.datasource.driver-class-name=com.mysql.jdbc.Driver
spring.datasource.username=<user>
spring.datasource.password=<password>
spring.datasource.validationQuery=SELECT 1

# Ring Secret Key
ring.encrypt.key=<Any key>
 
# Ring Anonymous Access
ring.anonymous.access=true
 
# Log
logging.level.root=INFO
logging.level.org.springframework.web=WARN
logging.level.org.hibernate=INFO
logging.level.org.hibernate.SQL=WARN
 
# Timezone
spring.jackson.time-zone=America/Sao_Paulo

server.port=8080

# Ring Enable native validation
ring.enable.native.data.validation=true
```

##### DEPLOY
Using [Apache Tomcat](http://tomcat.apache.org/):

- Copy the file ring.war to webapps directory of Apache Tomcat.


# User Manual
this section explain the main funcionalities of Ring


## Home
Shows a quicky text about how to use the system
- In the side menu, access the option ***Home***.

## Manual Input
*Manual Input* is a template that says the pattern the user file should follow to upload in the system
- In the side menu, access the option ***Manual Input***.

##### CREATE MANUAL INPUT
- Click in the button Create Manual Input, represented with **+** icon.
- Define the manual input name in the field ***Manual Input Name***, the field should match the pattern **[0-9a-z_]***.
- Define the group of manual input in the field ***Group*** if you have role LORD, or else the group is setted to the same group of logged user.
- Define the checkbox ***Can be modified*** (Default: checked). If this option is not checked, nobody can make modification in metadata, only an user with role LORD can check again
- Define a description in the field ***Description***. This field is very important to understand it's purpose.
- The fields ***Delimiter***, ***Quote***, ***Escape*** and ***Line Separator*** have a default value and you change change as you wish.
- Define the the name of each column in metadata for your manual input in the field ***Field Name***. It defines what header is expected coming file to upload. It's not allowed 2 fields with the same name.
- Define the the data type of each column in metadata for your manual input in the field ***Data Type***.
- Define the the validation of each column in metadata for your manual input in the field ***Validation***.
- Define the the Threshold of each column in metadata for your manual input in the field ***Threshold***. It defines what value to campare for validation.
- Define a combination of field to create a business key checking the box ***Business Key***. A business key field combination defines uniq id for the data. Ring don't updates the records, but you may identity version of a record to work with in an integration process
    - ###### ADD FIELD
        > You may add a new field in your metadata template, indication that a field with that name in file header is required. (click in the button represented by **+**)
    - ###### REMOVE FIELD
        > You may remove a field from your template (click in the button represented by **-**) identicating that a field with that name in file header is not required anymore. However, in the backend the field keep saved as part of metadata of your manual input.
        If in the future you try to add a field with the name of a field you have ever removed, then the removed field is activated and take place of new field.
        However, if you try to add a field with the name of a field you have ever removed but with a different data type, it will be created a new version of the field, being renamed with pos-fix ***_v2***, and will be required your file to have a field with pos-fix ***_v2***.
    - ###### HOW TO SET METADATA
        > If you have any doubt about to set up your metadata you can click in button "how to set metadata?" represented by ***?*** icon to get some hints.

##### EDIT MANUAL INPUT
Allows alter a manual input if user has role LORD or ADMIN in the same group
- Click in the edit button represented by a ***pencil*** icon.

##### DELETE MANUAL INPUT
Removes a group from systen
- Click in the delete button represented by a ***trash*** icon.

##### VIEW MANUAL INPUT
Shows the group configuration in view mode
- Click in the view button represented by a ***eye*** icon.
In the view mode you can
- Edit Manual Input: Click in the edit button represented by a ***pencil*** icon.
- Delete Manual Input: Click in the delete button represented by a ***trash*** icon.
- Upload File: Click in the upload button represented by a ***up arrow*** icon.
- See historical log of last 6 uploads in manual input: Click in the log history button represented by a ***clock*** icon.
    - Click in row of log to see the full text output


## Group
A group relate a set of users with a set of manual inputs, so an user can work only with manual inputs related to his group
- In the side menu, access the option ***Group***.

##### CREATE GROUP
- Click in the button Create Group, represented with **+** icon.
- Define the group name in the field ***Group Name***.
- Define the group description in the field ***Description***.
- click in button ***Save***.

##### EDIT GROUP
Allows alter a group if user has role LORD
- Click in the edit button represented by a ***pencil*** icon.

##### VIEW GROUP
Shows the group configuration in view mode
- Click in the view button represented by a ***eye*** icon.

##### DELETE GROUP
Removes a group from systen
- Click in the trash button represented by a ***trash*** icon.


## Search

*Search* is the fastest way to find a manual input in Ring

- In the side menu, access the option *Search*.
- Will be shown the search page with a text box and a button with a ***magnifying glass*** icon.
- Type the content to be searched and click enter or click in the button with a ***magnifying glass*** icon.

## User
An User is requeied to operate the tool.
- In the side menu, access the option ***User***.

##### ADD USER
- Click in the button Add User, represented with **+** icon.
- Define the user e-mail in the field ***E-mail***.
- Define the user name in the system in the field ***Username***.
- Define the first name of user in the field ***First Name***.
- Define the last name of user in the field ***Last Name***.
- Define an access profile in the field ***Role***.
- Define an user group in the field ***Group***.

**LORD:** This User is the integral administrator of the system and can effect any operation.

**ADMIN:** This User has permission to manage (create, delete, edit) new manual input and upload files to his group.

**USER:** This User has permission only to upload files in a manual input of his group.

- It's possible to define whether the user is active or not in the system with the button ***Enabled***.
- click in button ***Save***.

##### EDIT
Allows alter an User and, for LORD, redefine the password of others users:
- Click in the edit button represented by a **pencil** icon.
- Click in the button ***Reset password***.
- An e-mail will be sent to the user with the new password.

##### DELETE
Removes a user from system.

##### CHANGE PASSWORD
Allows the user to alter his own password.

## Configuration
*Configuration* contains the global settings of Ring.

- In the side menu, access the option ***Configuration***
- Enter with the server used to sent e-mails in the field ***Host***.
- Enter with the port of e-mail server in the field ***Port***.
- Enter with the e-mail address in the field ***Address***. This e-mail address will be used to sent system e-mails.
- Enter with the e-mail password in the field ***Password***.
- In the field ***Log Retention*** is possible to define in days a clean up of the *logs of import files*
- Click in the button ***Upload Logo*** to alter the logo of the system with any image file.

