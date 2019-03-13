#OVERVIEW

A simple application to determine if there is a route between 2 locations. Roads connect locations, and a route means there are roads from the origin to the destination. The relevant classes are Location and Graph.

This is of course an implementation of a graph, where locations are nodes and roads are edges.

I prefer business relevant names (location instead of node, road instead of edge), as that promotes a common language between business and technology. The exception in this implementation is the class Graph. MapService was the original name, but the name collision with the Java Collection "Map" is too annoying, so it became Graph.

#HOW TO RUN

You can use download RouteFinder.jar located in the /compiled directory. Make sure you have a Java 8 JRE on the path and run the following comand in the directory containing the jar file:
java -jar compiled/RouteFinder.jar

In the command prompt you should see that the jar has been loaded and is running. You can also see the logging as the application runs.

You can now go to: http://localhost:8080 The swagger is available here, and you can run any of the available API from the swagger. Note that although the main API is the GET connected API, there are also some graph API and a POST connected API that allow you to manage and modify the Graph.

You can run the GET connected command directly in your browser.
For instance each of the following 3 commands work using the default map. The first 2 should return "yes" and the last should return "no"
http://localhost:8080/connected?origin=Boston&destination=Newark
http://localhost:8080/connected?origin=Boston&destination=Philadelphia
http://localhost:8080/connected?origin=Philadelphia&destination=Albany

For further information on running, go to https://docs.spring.io/spring-boot/docs/current/reference/html/using-boot-running-your-application.html

#HOW TO COMPILE

This is a Java 8, Spring Boot application that uses Maven. Clone the application using your Git client, or download the application. Compile using a Java 8 compliant JDK, Maven, and optionally the IDE of your choice.





