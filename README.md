 # Lagom with MySql CRUD application
 
 To start the Lagom Scala Application first we need to make sure we are exporting SQL username and password correctly.
 
 `export USERNAME=<SQL_USERNAME>`

 `export PASSWORD=<SQL_PASSWORD>`
 
 ### Start Lagom Service:
 `sbt clean compile runAll`
 
 ### Start MySQL Server:
 `mysql -u root -p`
 
 ### Sample Service Requests:
 * ##### ADD USER  
 **POST** `http://localhost:9000/user/add`
 
 ````
 {
   	"orgId":1,
   	"email":"charmy@gmail.com",
   	"name":"Charmy"
 }
 ````
 
* ##### GET USER  
  **GET** `http://localhost:9000/user/get?orgId=1`
  
  
* ##### UPDATE USER  
  **PUT** `http://localhost:9000/user/update`
  
  ````
  {
  	"orgId":1,
  	"name":"Charmy Garg"
  }
  ````
* ##### DELETE USER  
  **DELETE** `http://localhost:9000/user/delete?orgId=1`
  
