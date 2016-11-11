# shiro-poc
shiro-poc

<h3>Configuration info</h3>
https://shiro.apache.org/spring.html#Spring-WebApplications

<h3>Docker</h3>

<h5>Build image</h5>
`mvn package docker:build`

<h5>Run</h5>
`docker run -it -p 8080:8080 bsferreira/shiro-poc`

<h3>Endpoints examples:</h3>

<h5>admin user<h5> 
http://localhost:8080/login?username=bruno&password=123456

<h5>read only user<h5>
http://localhost:8080/login?username=ricardo&password=123456

<h5>authorized only to admin role<h5>
http://localhost:8080/admin

<h5>authorized to admin and read role<h5>
http://localhost:8080/read

<h5>logout<h5>
http://localhost:8080/logout
