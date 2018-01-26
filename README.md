# TicketService

### Assumptions
This Ticket Service is supposed to be used as a library by another
program that needs the management of seats.<p>
It involves operations:
* Seats finding
* Seats holding
* Seats reserving

### Prerequisites

[JDK 1.8+](http://www.oracle.com/technetwork/java/javase/downloads/index.html),
[Maven 3.1.0+](https://maven.apache.org/download.cgi)

### Build
Command to build a jar file.
```
mvn clean install
```

### Test
Command to run all unit tests.
```
mvn test
```

### Use
In your project, add the dependency below to your pom.xml file.
```
<dependency>
  <groupId>com.yangx01123.app</groupId>
  <artifactId>ticketservice</artifactId>
  <version>1.0-SNAPSHOT</version>
</dependency>
```

### Config
After build, 2 configure files can be used for application and log settings.
```
<project_root>/target/test-classes/config.properties
<project_root>/target/test-classes/log4j2.xml
```

### Log
After build, a log file can be found at.
```
<project_root>/all.log
```