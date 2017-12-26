# shoppingcart-microservice
Shopping Cart microservice implemented using WildFly Swarm and Infinispan

Build Status: [![Build Status](https://travis-ci.org/redhat-msa-3/shoppingcart-microservice.svg?branch=master)](https://travis-ci.org/redhat-msa-3/shoppingcart-microservice)

Execute shoppingcart locally
----------------------------

1. Open a command prompt and navigate to the root directory of this microservice.

2. Type this command to generate the application package:

        mvn package

3. Type this command to test the application:

        mvn test -DskipTests=false

4. Type this command to execute the application:

        java -jar target/shoppingcart-swarm.jar

5. This will execute `shoppingcart-swarm.jar` .
6. The application will be running at the following URL: <http://localhost:8080/session>

Execute catalog in OpenShift
-----------------------------

Make sure that you are logged in.

Execute:

    mvn fabric8:deploy
