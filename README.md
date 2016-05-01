# RESTful-CRUD-Service
A REST Server that supports CRUD services &amp; A Client that implements a CRUD lifecycle

In this project client and server communicate using HTTP verbs (POST, GET, PUT, DELETE) and JSON format for request/response body.
Client interface allows professor to create, read, update, delete gradebook entries for given student.
You can find the code [here](https://github.com/rajeshsurana/RESTful-CRUD-Service).

Gradebook structure is explained in following diagram->
![Gradebook Structure](https://raw.githubusercontent.com/rajeshsurana/RESTful-CRUD-Service/master/images/gradebook_datastructure.png)

Here is self explanatory image of client interface ->

![client interface](https://raw.githubusercontent.com/rajeshsurana/RESTful-CRUD-Service/master/images/client_interface_with_explanation.png) 

**URI for Student resource->**

http://localhost:8080/CSE564_CRUD_RESTws/webresources/gradebooks/{gradebook}/gradeditems/{category}/{itemId}/students/{studentId}

**Some important points to know about project before you execute it->**

1. Client-main program is in edu.asu.cse564.samples.crud.restcl.ui.Gradebook_REST_UI.java
2. All client-server communication is done in JSON format.
3. Before you start creating graded items you need to create gradebook.
4. Server can maintain multiple gradebooks at a time. There could be multiple graded items inside gradebook. Graded Item contains multiple students’ info.
5. Item consists of category and Item id.
6. Before you start creating student you need to create graded item.
7. Student update action will update his score and feedback.
8. To access Student resource you need gradebook, category, Item id and Student id.
9. To access graded item resource you need gradebook, category, and Item id.
10. All gradebook data is saved in JSON file on server side. So, if you stop and start server it will be there.
11. You can see log statements while client and server are communicating to get idea what is happening behind the scene
