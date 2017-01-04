# Monithor
Monithor is a basic HTTP health checking tool, built as a learning project using Vue.js and Spring Boot. 

Monithor will periodically make a HTTP request to fetch the configured URL and do a pattern match to validate the response. Besides that, it will try to extract version information from the response.

 A couple of screenshots are worth a thousand words.
 
 
 **Defining a job**
 
 ![Alt text](monithor-client/docs/addjob.png?raw=true "Add")
 
 
 **Searching**
 
 ![Alt text](monithor-client/docs/search.png?raw=true "Search")
 
 
 **Monitoring**
 
 ![Alt text](monithor-client/docs/monitor.png?raw=true "Monitor")

# Running
## Server
Start the Spring Boot application `Monithor` from your IDE. The server will start listening on port 8090. 
## Client
`npm install` and `npm run dev` to start the dev-server on port 8080, which is configured to forward API calls to port 8090.
# Packaging
```
Client: npm run build
Server: mvn clean package
```
and run the executable jar with 
```
java -jar monithor-server/target/monithor-server-1.0-SNAPSHOT.jar
```
