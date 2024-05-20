1. There are two profile, one is default and another is dev. Please select application-dev.yml during development
2. To run this project without IDE: 
   1. mvn clean install
   2. docker build -f Dockerfile -t library-syetem-v1.jar .
   3. docker compose up
   4. Now open your browser and you can see the API's:
   http://localhost:8080/swagger-ui/index.html#/