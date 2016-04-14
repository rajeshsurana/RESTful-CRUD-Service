/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.asu.cse564.samples.crud.restws;

import edu.asu.cse564.samples.crud.io.GradebookIO;
import java.net.URI;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.PathParam;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.asu.cse564.samples.crud.jaxb.model.Gradebook;
import edu.asu.cse564.samples.crud.jaxb.model.GradedItem;
import edu.asu.cse564.samples.crud.jaxb.model.Student;
import edu.asu.cse564.samples.crud.jaxb.utils.Converter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * REST Web Service
 *
 * @author Rajesh Surana
 */
@Path("gradebooks/{gradebook}/gradeditems/{category}/{itemId}/students")
public class StudentResource {
    
    private static final Logger LOG = LoggerFactory.getLogger(StudentResource.class);
    private static final String FILENAME = "gradebook.json";
    
    private GradedItem gradedItem;
    private Student student;

    @Context
    private UriInfo context;

    /**
     * Creates a new instance of StudentResource
     */
    public StudentResource() {
        LOG.info("Creating an Graded Item Resource");
    }
    
    /**
     * POST method for creating an instance of edu.asu.cse564.sample.crud.restws.StudentResource
     * @param gradebook gradebook identifier
     * @param category graded item type
     * @param itemId
     * @param content representation for the resource
     * @return an HTTP response with content of the updated or created resource.
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createStudentResource(@PathParam("gradebook") String gradebook,
                                             @PathParam("category") String category,
                                             @PathParam("itemId") String itemId,
                                             String content) {
        LOG.info("Creating the instance Graded Item Resource {}", gradedItem);
        LOG.debug("POST request");
        LOG.debug("PathParam gradebook = {}", gradebook);
        LOG.debug("PathParam category = {}", category);
        LOG.debug("PathParam itemId = {}", itemId);
        LOG.debug("Request Content = {}", content);
        
        Response response;
        String errMessage;
        gradedItem = null;
        student = null;
        try {
            student = (Student) Converter.convertFromJsonToObject(content, Student.class);
            LOG.debug("The JSON {} was converted to the object {}", content, student);
            
            LOG.info("Reading gradebook data from file");
            List<Gradebook> gradebookList = GradebookIO.readFromGradebook(FILENAME); 
            if(gradebookList == null || gradebookList.isEmpty()){
                gradebookList = new ArrayList<Gradebook>();
            }
            Gradebook storedGradebook = null;
            for(Gradebook tempGradebook: gradebookList){
                if(tempGradebook.getGradebookName().equals(gradebook)){
                    storedGradebook = tempGradebook;
                    break;
                }
            }

            if (storedGradebook != null){
                
                List<GradedItem> gradedItemList = storedGradebook.getItems();
                
                if(gradedItemList != null && gradedItemList.size()>=1){
                    
                    for(GradedItem giTemp: gradedItemList){
                        if(giTemp.getCategory().equals(category) && giTemp.getId() == Integer.parseInt(itemId)){
                            gradedItem = giTemp;
                            break;
                        }
                    }
                    if(gradedItem != null){
                        
                        if(gradedItem.getStudents() == null){
                            List<Student> studentList = new ArrayList<Student>();
                            gradedItem.setStudents(studentList);
                        }
                        List<Student> studentList = gradedItem.getStudents();
                        boolean studentExist = false;
                        for(Student s: studentList){
                            if(s.getStudentId() == student.getStudentId()){
                                studentExist = true;
                                break;
                            }
                        }
                        if(studentExist){
                            LOG.info("Creating a {} {} Status Response", Response.Status.CONFLICT.getStatusCode(), Response.Status.CONFLICT.getReasonPhrase());
                            LOG.debug("Cannot create Student Resource as it already exits: {}", student);
                            errMessage = "{\"error\": \"Student Resource already exists\"}";
                            response = Response.status(Response.Status.CONFLICT).entity(errMessage).build();
                        } else {
                            LOG.debug("Attempting to create Student Resource and setting it to {}", content);
                            gradedItem.addStudent(student);
                            LOG.info("Creating a {} {} Status Response", Response.Status.CREATED.getStatusCode(), Response.Status.CREATED.getReasonPhrase());

                            String jsonString = Converter.convertFromObjectToJSON(student, Student.class);
                            LOG.info("Writing student data to gradebook.json");

                            GradebookIO.writeToGradebook(gradebookList, FILENAME);

                            URI locationURI = URI.create(context.getAbsolutePath() + "/" + student.getStudentId());

                            response = Response.status(Response.Status.CREATED).location(locationURI).entity(jsonString).build();
                        }
                        
                    } else {
                        LOG.info("Creating a {} {} Status Response", Response.Status.CONFLICT.getStatusCode(), Response.Status.CONFLICT.getReasonPhrase());
                        LOG.debug("Cannot create Student Resource as gradeItem with category: {} & id: {} does not exist", category, itemId);

                        errMessage = "{\"error\": \"Graded Item does not exist\"}";
                        response = Response.status(Response.Status.CONFLICT).entity(errMessage).build(); 
                    }                      
                }else{
                    LOG.info("Creating a {} {} Status Response", Response.Status.CONFLICT.getStatusCode(), Response.Status.CONFLICT.getReasonPhrase());
                    LOG.debug("Cannot create Student Resource as gradeItem with category: {} & id: {} does not exist", category, itemId);

                    errMessage = "{\"error\": \"Graded Item does not exist\"}";
                    response = Response.status(Response.Status.CONFLICT).entity(errMessage).build();    
                }   
            } else {
                LOG.info("Creating a {} {} Status Response", Response.Status.CONFLICT.getStatusCode(), Response.Status.CONFLICT.getReasonPhrase());
                LOG.debug("Cannot create Graded Item Resource as gradebook: {} does not exists", gradebook);

                errMessage = "{\"error\": \"Gradebook does not exist\"}";
                response = Response.status(Response.Status.CONFLICT).entity(errMessage).build();
            }
        } catch (IOException e) {
            LOG.info("Creating a {} {} Status Response", Response.Status.BAD_REQUEST.getStatusCode(), Response.Status.BAD_REQUEST.getReasonPhrase());
            LOG.debug("JSON is {} is incompatible with Student Resource", content);

            response = Response.status(Response.Status.BAD_REQUEST).entity(content).build();
        } catch (RuntimeException e) {
            e.printStackTrace();
            LOG.debug("Catch All exception");

            LOG.info("Creating a {} {} Status Response", Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), Response.Status.INTERNAL_SERVER_ERROR.getReasonPhrase());

            response = Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(content).build();
        }
                   
        LOG.debug("Generated response {}", response);
        
        return response;
    }
    
   
    /**
     * Retrieves representation of an instance of edu.asu.cse564.sample.crud.restws.StudentResource
     * @param gradebook
     * @param category
     * @param itemId
     * @param studentId
     * @return an HTTP response with content of the updated or created resource.
     */
    @GET
    @Path("{studentId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getStudentResource(@PathParam("gradebook") String gradebook,
                                @PathParam("category") String category,
                                @PathParam("itemId") String itemId,
                                @PathParam("studentId") String studentId) {
        LOG.info("Retrieving the GradedItem Resource {}", gradedItem);
        LOG.debug("GET request");
        LOG.debug("PathParam gradebook = {}", gradebook);
        LOG.debug("PathParam category = {}", category);
        LOG.debug("PathParam itemId = {}", itemId);
        LOG.debug("PathParam studentId = {}", studentId);
        
        Response response;
        String errMessage;
        gradedItem = null;
        student = null;
        
        try{

            LOG.info("Reading gradebook data from file");
            List<Gradebook> gradebookList = GradebookIO.readFromGradebook(FILENAME); 
            if(gradebookList == null || gradebookList.isEmpty()){
                gradebookList = new ArrayList<Gradebook>();
            }
            Gradebook storedGradebook = null;
            for(Gradebook tempGradebook: gradebookList){
                if(tempGradebook.getGradebookName().equals(gradebook)){
                    storedGradebook = tempGradebook;
                    break;
                }
            }
            if (storedGradebook == null){
                LOG.info("Creating a {} {} Status Response", Response.Status.GONE.getStatusCode(), Response.Status.GONE.getReasonPhrase());
                LOG.debug("No Graded Item Resource to return");
                
                errMessage = "{\"error\": \"Gradebook does not exist\"}";
                response = Response.status(Response.Status.GONE).entity(errMessage).build();
            } else {
                List<GradedItem> gradedItemList = storedGradebook.getItems();
                if (gradedItemList.isEmpty()){
                    LOG.info("Creating a {} {} Status Response", Response.Status.GONE.getStatusCode(), Response.Status.GONE.getReasonPhrase());
                    LOG.debug("No Graded Item Resource to return");

                    errMessage = "{\"error\":\"Graded Item Resource does not exist\"}";
                    response = Response.status(Response.Status.GONE).entity(errMessage).build();
                } else {
                    for(GradedItem item: gradedItemList){
                        if (item.getCategory().equals(category) && 
                            item.getId() == Integer.parseInt(itemId)){
                            gradedItem = item;
                            break;
                        }
                    }

                    if (gradedItem == null){
                        LOG.info("Creating a {} {} Status Response", Response.Status.NOT_FOUND.getStatusCode(), Response.Status.NOT_FOUND.getReasonPhrase());
                        LOG.debug("Retrieving the Graded Item Resource {}", gradedItem);

                        errMessage = "{\"error\":\"Graded Item Resource not found\"}";
                        response = Response.status(Response.Status.NOT_FOUND).entity(errMessage).build();
                    } else {
                        List<Student> studentList = gradedItem.getStudents();
                        if(studentList == null || studentList.isEmpty()){
                            LOG.info("Creating a {} {} Status Response", Response.Status.NOT_FOUND.getStatusCode(), Response.Status.NOT_FOUND.getReasonPhrase());
                            LOG.debug("Retrieving the Student Resource {}", student);

                            errMessage = "{\"error\":\"Student Resource not found\"}";
                            response = Response.status(Response.Status.NOT_FOUND).entity(errMessage).build();
                        }else {
                            for(Student s: studentList){
                                if(s.getStudentId() == Integer.parseInt(studentId)){
                                    student = s;
                                    break;
                                }
                            }
                            if (student == null){
                                LOG.info("Creating a {} {} Status Response", Response.Status.NOT_FOUND.getStatusCode(), Response.Status.NOT_FOUND.getReasonPhrase());
                                LOG.debug("Retrieving the Student Resource {}", student);

                                errMessage = "{\"error\":\"Student Resource not found\"}";
                                response = Response.status(Response.Status.NOT_FOUND).entity(errMessage).build();
                            } else {
                                LOG.info("Creating a {} {} Status Response", Response.Status.OK.getStatusCode(), Response.Status.OK.getReasonPhrase());
                                LOG.debug("Retrieving the Student Resource {}", gradedItem);

                                String jsonString = Converter.convertFromObjectToJSON(student, Student.class);
                                response = Response.status(Response.Status.OK).entity(jsonString).build();            
                            } 
                        }  
                    }
                }
            }
        } catch (IOException e) {
            LOG.info("Creating a {} {} Status Response", Response.Status.BAD_REQUEST.getStatusCode(), Response.Status.BAD_REQUEST.getReasonPhrase());

            response = Response.status(Response.Status.BAD_REQUEST).entity("{\"error\":\"Could not understand request\"}").build();
        }catch (RuntimeException e) {
            e.printStackTrace();
            LOG.debug("Catch All exception");
            LOG.info("Creating a {} {} Status Response", Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), Response.Status.INTERNAL_SERVER_ERROR.getReasonPhrase());

            response = Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("{\"error\":\"Internal Server Error occured\"}").build();
        }
        
        LOG.debug("Generated response {}", response);
        
        return response;
    }


    /**
     * PUT method for updating an instance of edu.asu.cse564.sample.crud.restws.StudentResource
     * @param gradebook
     * @param category
     * @param itemId
     * @param studentId
     * @param content representation for the resource
     * @return an HTTP response with content of the updated or created resource.
     */
    @PUT
    @Path("{studentId}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateStudentResource(@PathParam("gradebook") String gradebook,
                                   @PathParam("category") String category,
                                   @PathParam("itemId") String itemId,
                                   @PathParam("studentId") String studentId, 
                                   String content) {
        LOG.info("Updating the GradedItem Resource with {}", content);
        LOG.debug("PUT request");
        LOG.debug("PathParam gradebook = {}", gradebook);
        LOG.debug("PathParam category = {}", category);
        LOG.debug("PathParam itemId = {}", itemId);
        LOG.debug("PathParam studentId = {}", studentId);
        LOG.debug("Request Content = {}", content);
        
        Response response;
        String errMessage;
        gradedItem = null;
        student = null;
        
        LOG.debug("Attempting to update the Graded Item Resource");
        
        try{ 
            LOG.info("Reading gradebook data from file");
            List<Gradebook> gradebookList = GradebookIO.readFromGradebook(FILENAME); 
            if(gradebookList == null || gradebookList.isEmpty()){
                gradebookList = new ArrayList<Gradebook>();
            }
            Gradebook storedGradebook = null;
            for(Gradebook tempGradebook: gradebookList){
                if(tempGradebook.getGradebookName().equals(gradebook)){
                    storedGradebook = tempGradebook;
                    break;
                }
            }
            if (storedGradebook == null){
                LOG.info("Creating a {} {} Status Response", Response.Status.CONFLICT.getStatusCode(), Response.Status.CONFLICT.getReasonPhrase());
                LOG.debug("Cannot update Student Resource {} as it has not yet been created", content);

                errMessage = "{\"error\": \"Gradebook does not exist\"}";
                response = Response.status(Response.Status.CONFLICT).entity(errMessage).build();
            } else {
                List<GradedItem> gradedItemList = storedGradebook.getItems();
                if (gradedItemList.isEmpty()){
                    LOG.info("Creating a {} {} Status Response", Response.Status.CONFLICT.getStatusCode(), Response.Status.CONFLICT.getReasonPhrase());
                    LOG.debug("Cannot update Student Resource {} as it has not yet been created", content);
                    
                    errMessage = "{\"error\": \"Graded Item does not exist\"}";
                    response = Response.status(Response.Status.CONFLICT).entity(errMessage).build();
                } else {
                    for(GradedItem item: gradedItemList){
                        if (item.getCategory().equals(category) && 
                            item.getId() == Integer.parseInt(itemId)){
                            gradedItem = item;
                            break;
                        }
                    }

                    if (gradedItem == null){
                        LOG.info("Creating a {} {} Status Response", Response.Status.CONFLICT.getStatusCode(), Response.Status.CONFLICT.getReasonPhrase());
                        LOG.debug("Cannot update Student Resource {} as it has not yet been created", content);

                        errMessage = "{\"error\": \"Graded Item does not exist\"}";
                        response = Response.status(Response.Status.CONFLICT).entity(errMessage).build();
                    } else {
                        List<Student> studentList = gradedItem.getStudents();
                        if(studentList == null || studentList.isEmpty()){
                            LOG.info("Creating a {} {} Status Response", Response.Status.CONFLICT.getStatusCode(), Response.Status.CONFLICT.getReasonPhrase());
                            LOG.debug("Cannot update Student Resource {} as it has not yet been created", content);

                            errMessage = "{\"error\": \"Student does not exist\"}";
                            response = Response.status(Response.Status.CONFLICT).entity(errMessage).build();
                        } else {
                            for(Student s: studentList){
                                if(s.getStudentId() == Integer.parseInt(studentId)){
                                    student = s;
                                    break;
                                }
                            }
                            if(student == null){
                                LOG.info("Creating a {} {} Status Response", Response.Status.CONFLICT.getStatusCode(), Response.Status.CONFLICT.getReasonPhrase());
                                LOG.debug("Cannot update Student Resource {} as it has not yet been created", content);

                                errMessage = "{\"error\": \"Student does not exist\"}";
                                response = Response.status(Response.Status.CONFLICT).entity(errMessage).build();
                            } else {
                                Student tempStudent = (Student) Converter.convertFromJsonToObject(content, Student.class);
                                student.setScore(tempStudent.getScore());
                                student.setFeedback(tempStudent.getFeedback());
                                GradebookIO.writeToGradebook(gradebookList, FILENAME);
                                LOG.debug("The JSON {} was converted to the object {}", content, student);         
                                LOG.debug("Updated Student Resource to {}", student);
                                LOG.info("Creating a {} {} Status Response", Response.Status.OK.getStatusCode(), Response.Status.OK.getReasonPhrase());

                                response = Response.status(Response.Status.OK).entity(content).build();
                            }                          
                        }                        
                    }
                }
            }
        
        } catch (IOException e) {
            LOG.info("Creating a {} {} Status Response", Response.Status.BAD_REQUEST.getStatusCode(), Response.Status.BAD_REQUEST.getReasonPhrase());

            response = Response.status(Response.Status.BAD_REQUEST).entity("{\"error\":\"Could not understand request\"}").build();
        }catch (RuntimeException e) {
            e.printStackTrace();
            LOG.debug("Catch All exception");
            LOG.info("Creating a {} {} Status Response", Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), Response.Status.INTERNAL_SERVER_ERROR.getReasonPhrase());

            response = Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("{\"error\":\"Internal Server Error occured\"}").build();
        }
        
        LOG.debug("Generated response {}", response);
        
        return response;
    }
    
    /**
     * Retrieves representation of an instance of edu.asu.cse564.sample.crud.restws.StudentResource
     * @param gradebook
     * @param category
     * @param itemId
     * @param studentId
     * @return an HTTP response with content of the deleted resource.
     */
    @DELETE
    @Path("{studentId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteStudentResource(@PathParam("gradebook") String gradebook,
                                          @PathParam("category") String category,
                                          @PathParam("itemId") String itemId,
                                          @PathParam("studentId") String studentId) {
        LOG.debug("DELETE request");
        LOG.debug("PathParam gradebook = {}", gradebook);
        LOG.debug("PathParam category = {}", category);
        LOG.debug("PathParam itemId = {}", itemId);
        LOG.debug("PathParam studentId = {}", studentId);
        
        LOG.info("Attempting to remove the Graded Item Resource");
        
        Response response;
        String errMessage;
        gradedItem = null;
        student = null;
        
        try{ 
            LOG.info("Reading gradebook data from file");
            List<Gradebook> gradebookList = GradebookIO.readFromGradebook(FILENAME); 
            if(gradebookList == null || gradebookList.isEmpty()){
                gradebookList = new ArrayList<Gradebook>();
            }
            Gradebook storedGradebook = null;
            for(Gradebook tempGradebook: gradebookList){
                if(tempGradebook.getGradebookName().equals(gradebook)){
                    storedGradebook = tempGradebook;
                    break;
                }
            }
            if (storedGradebook == null){
            LOG.info("Creating a {} {} Status Response", Response.Status.GONE.getStatusCode(), Response.Status.GONE.getReasonPhrase());
            LOG.debug("No Graded Item Resource to delete");
            
            errMessage = "{\"error\": \"Gradebook does not exist\"}";
            response = Response.status(Response.Status.GONE).entity(errMessage).build();
            } else {
                List<GradedItem> gradedItemList = storedGradebook.getItems();
                if (gradedItemList.isEmpty()){
                    LOG.info("Creating a {} {} Status Response", Response.Status.GONE.getStatusCode(), Response.Status.GONE.getReasonPhrase());
                    LOG.debug("No Student Resource to delete");

                    errMessage = "{\"error\":\"Graded Item resouce does not exist\"}";
                    response = Response.status(Response.Status.GONE).entity(errMessage).build();
                } else {
                    for(GradedItem item: gradedItemList){
                        if (item.getCategory().equals(category) && 
                            item.getId() == Integer.parseInt(itemId)){
                            gradedItem = item;
                            break;
                        }
                    }
                    if (gradedItem == null){
                        LOG.info("Creating a {} {} Status Response", Response.Status.NOT_FOUND.getStatusCode(), Response.Status.NOT_FOUND.getReasonPhrase());

                        errMessage = "{\"error\":\"Graded Item resouce does not exist\"}";
                        response = Response.status(Response.Status.NOT_FOUND).entity(errMessage).build();
                    } else {
                        List<Student> studentList = gradedItem.getStudents();
                        if(studentList == null || studentList.isEmpty()){
                            LOG.info("Creating a {} {} Status Response", Response.Status.NOT_FOUND.getStatusCode(), Response.Status.NOT_FOUND.getReasonPhrase());

                            errMessage = "{\"error\":\"Could not found the Student resource\"}";
                            response = Response.status(Response.Status.NOT_FOUND).entity(errMessage).build();
                        } else {
                            for(Student s: studentList){
                                if(s.getStudentId() == Integer.parseInt(studentId)){
                                    student = s;
                                    break;
                                }
                            }
                            if(student == null){
                                LOG.info("Creating a {} {} Status Response", Response.Status.NOT_FOUND.getStatusCode(), Response.Status.NOT_FOUND.getReasonPhrase());

                                errMessage = "{\"error\":\"Could not found the Student resource\"}";
                                response = Response.status(Response.Status.NOT_FOUND).entity(errMessage).build();
                            } else {
                                LOG.info("Creating a {} {} Status Response", Response.Status.OK.getStatusCode(), Response.Status.OK.getReasonPhrase());
                                LOG.debug("Deleting the Student Resource {}", student);
                                String jsonString =  "{\"success\": \"Student is deleted successfully\"}";
                                studentList.remove(student);
                                GradebookIO.writeToGradebook(gradebookList, FILENAME);
                                response = Response.status(Response.Status.OK).entity(jsonString).build();
                            } 
                        }                     
                    }
                }
            }
        } catch (RuntimeException e) {
            e.printStackTrace();
            LOG.debug("Catch All exception");
            LOG.info("Creating a {} {} Status Response", Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), Response.Status.INTERNAL_SERVER_ERROR.getReasonPhrase());

            response = Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("{\"error\":\"Internal Server Error occured\"}").build();
        }      
        
        LOG.debug("Generated response {}", response);
        
        return response;
    }
    
}



