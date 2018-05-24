# JEE-MIF

### How to start project with IntelliJ

1. Add project from VCS https://git.mif.vu.lt/tama2088/jee-mif.git -> maven project -> check "Search recursive") -> name the direcotory or leave default
2. Wait till IntelliJ ends his magic
3. Right click on parent project -> Open Module Settings -> Global libraries -> + -> From Maven... -> spring-boot-starter-tomcat -> select with version ...:1.5.2.RELEASE
4. Add Spring Boot Configuration (Run -> Edit Configuration... -> select Spring Boot -> Name it, select "Use classpath of module" and "Main class" -> Applay -> Okey
5. Run

If that won't help contact Tadas. 

### Using database

Create local postgresql database with user `jee`, password `password` and database `jee`

Or connect to prod database using SSH tunnel
* Host `194.135.80.233`
* User `jee`
* Password _in chat_
* Source port `5433`
* Destination `localhost:5433`