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

- Copy the file hanger.war to webapps directory of Apache Tomcat.


# Functional


## User
An User is requeied to operate the tool.
- In the side menu, access the option ***User***.

##### ADD USER
- Click in the button Add User, represented with **+** icon.
- Define the user e-mail in the field **E-mail**.
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

