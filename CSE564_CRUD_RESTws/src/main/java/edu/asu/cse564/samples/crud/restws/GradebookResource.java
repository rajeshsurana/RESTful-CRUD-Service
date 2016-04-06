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
import edu.asu.cse564.samples.crud.jaxb.utils.Converter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * REST Web Service
 *
 * @author fcalliss
 */
@Path("gradebooks")
public class GradebookResource {
    
    private static final Logger LOG = LoggerFactory.getLogger(GradebookResource.class);
    private static final String filename = "gradebook.json";
    
    private static Gradebook gradebook;
    private GradedItem gradedItem;

    @Context
    private UriInfo context;

    /**
     * Creates a new instance of GradebookResource
     */
    public GradebookResource() {
        LOG.info("Creating an Gradebook Resource");
    }

    /**
     * POST method for creating an instance of edu.asu.cse564.sample.crud.restws.GradebookResource
     * @param content representation for the resource
     * @return an HTTP response with content of the updated or created resource.
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createGradebookResource(String content) {
        LOG.info("Creating the instance Gradebook Resource {}", gradebook);
        LOG.debug("POST request");
        LOG.debug("Request Content = {}", content);
        
        Response response;
        String errMessage = null;
        
        try {
            gradebook = (Gradebook) Converter.convertFromJsonToObject(content, Gradebook.class);
            LOG.debug("The JSON {} was converted to the object {}", content, gradebook);
            
            if (gradebook.getGradebookName().trim().equals("")){
                LOG.info("Creating a {} {} Status Response", Response.Status.NOT_ACCEPTABLE.getStatusCode(), Response.Status.NOT_ACCEPTABLE.getReasonPhrase());
                LOG.debug("JSON is {} is incompatible with Gradebook Resource", content);

                errMessage = "{\"error\": \"Name of gradebook can not only be whitespaces\"}";
                response = Response.status(Response.Status.NOT_ACCEPTABLE).entity(errMessage).build();
            } else {
                LOG.info("Reading gradebook data from file");
                List<Gradebook> gradebookList = GradebookIO.readFromGradebook(filename); 
                if(gradebookList == null || gradebookList.isEmpty()){
                    gradebookList = new ArrayList<Gradebook>();
                }
                Gradebook storedGradebook = null;
                for(Gradebook tempGradebook: gradebookList){
                    if(tempGradebook.getGradebookName().equals(gradebook.getGradebookName())){
                        storedGradebook = tempGradebook;
                        break;
                    }
                }

                if (storedGradebook == null || (!gradebook.getGradebookName().equals(storedGradebook.getGradebookName()))){
                    LOG.debug("Attempting to create an Gradebook Resource and setting it to {}", content);

                    LOG.info("Creating a {} {} Status Response", Response.Status.CREATED.getStatusCode(), Response.Status.CREATED.getReasonPhrase());

                    String jsonString = Converter.convertFromObjectToJSON(gradebook, Gradebook.class);
                    LOG.info("Writing gradebook to gradebook.json");
                    gradebookList.add(gradebook);
                    GradebookIO.writeToGradebook(gradebookList, filename);

                    URI locationURI = URI.create(context.getAbsolutePath() + "/" + gradebook.getGradebookName());

                    response = Response.status(Response.Status.CREATED).location(locationURI).entity(jsonString).build();

                } else {
                    LOG.info("Creating a {} {} Status Response", Response.Status.CONFLICT.getStatusCode(), Response.Status.CONFLICT.getReasonPhrase());
                    LOG.debug("Cannot create Gradebook Resource as it already exists = {}", gradebook);

                    errMessage = "{\"error\": \"Gradebook name already exists\"}";
                    response = Response.status(Response.Status.CONFLICT).entity(errMessage).build();
                }
            }
        } catch (IOException e) {
            LOG.info("Creating a {} {} Status Response", Response.Status.BAD_REQUEST.getStatusCode(), Response.Status.BAD_REQUEST.getReasonPhrase());
            LOG.debug("JSON is {} is incompatible with Gradebook Resource", content);

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
     * Retrieves representation of an instance of edu.asu.cse564.sample.crud.restws.GradebookResource
     * @param gradebook
     * @return an HTTP response with content of the updated or created resource.
     */
    @GET
    @Path("{gradebook}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getGradebookResource(@PathParam("gradebook") String gradebookName) {
        LOG.info("Retrieving the GradedItem Resource {}", gradedItem);
        LOG.debug("GET request");
        LOG.debug("PathParam gradebook = {}", gradebookName);
        
        Response response;
        String errMessage;
              
        try{

            LOG.info("Reading gradebook data from file");
            List<Gradebook> gradebookList = GradebookIO.readFromGradebook(filename); 
            if(gradebookList == null || gradebookList.isEmpty()){
                gradebookList = new ArrayList<Gradebook>();
            }
            
            for(Gradebook tempGradebook: gradebookList){
                if(tempGradebook.getGradebookName().equals(gradebookName)){
                    gradebook = tempGradebook;
                    break;
                }
            }
            if (gradebook == null){
                LOG.info("Creating a {} {} Status Response", Response.Status.GONE.getStatusCode(), Response.Status.GONE.getReasonPhrase());
                LOG.debug("No Gradebook Resource to return");
                
                errMessage = "{\"error\": \"Gradebook does not exist\"}";
                response = Response.status(Response.Status.GONE).entity(errMessage).build();
            } else {
                LOG.info("Creating a {} {} Status Response", Response.Status.OK.getStatusCode(), Response.Status.OK.getReasonPhrase());
                LOG.debug("Retrieving the Gradebook Resource {}", gradebook);

                String jsonString = Converter.convertFromObjectToJSON(gradebook, Gradebook.class);
                response = Response.status(Response.Status.OK).entity(jsonString).build();
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
     * Retrieves representation of an instance of edu.asu.cse564.sample.crud.restws.GradebookResource
     * @param gradebook
     * @return an HTTP response with content of the deleted resource.
     */
    @DELETE
    @Path("{gradebook}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteGradebookResource(@PathParam("gradebook") String gradebook) {
        LOG.debug("DELETE request");
        LOG.debug("PathParam gradebook = {}", gradebook);

        
        LOG.info("Attempting to remove the Gradebook Resource");
        
        Response response;
        String errMessage;
        
        try{ 
            LOG.info("Reading gradebook data from file");
            List<Gradebook> gradebookList = GradebookIO.readFromGradebook(filename); 
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
            LOG.debug("No Gradebook Resource to delete");
            
            errMessage = "{\"error\":\"Could not found the Gradebook to be deleted\"}";
            response = Response.status(Response.Status.GONE).entity(errMessage).build();
            } else {
                LOG.info("Creating a {} {} Status Response", Response.Status.OK.getStatusCode(), Response.Status.OK.getReasonPhrase());
                LOG.debug("Deleting the Gradebook Resource {}", storedGradebook);
                String jsonString = "{\"success\": \"Gradebook is deleted successfully\"}";
                gradebookList.remove(storedGradebook);
                GradebookIO.writeToGradebook(gradebookList, filename);
                response = Response.status(Response.Status.OK).entity(jsonString).build();

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
    
    /**
     * POST method for creating an instance of edu.asu.cse564.sample.crud.restws.GradebookResource
     * @param gradebook gradebook identifier
     * @param category graded item type
     * @param content representation for the resource
     * @return an HTTP response with content of the updated or created resource.
     */
    @POST
    @Path("{gradebook}/gradeditems/{category}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createGradedItemResource(@PathParam("gradebook") String gradebook,
                                             @PathParam("category") String category,
                                             String content) {
        LOG.info("Creating the instance Graded Item Resource {}", gradedItem);
        LOG.debug("POST request");
        LOG.debug("PathParam gradebook = {}", gradebook);
        LOG.debug("PathParam category = {}", category);
        LOG.debug("Request Content = {}", content);
        
        Response response;
        String errMessage;
        
        try {
            gradedItem = (GradedItem) Converter.convertFromJsonToObject(content, GradedItem.class);
            LOG.debug("The JSON {} was converted to the object {}", content, gradedItem);
            
            LOG.info("Reading gradebook data from file");
            List<Gradebook> gradebookList = GradebookIO.readFromGradebook(filename); 
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
                LOG.debug("Attempting to create an Graded Item Resource and setting it to {}", content);

                LOG.info("Creating a {} {} Status Response", Response.Status.CREATED.getStatusCode(), Response.Status.CREATED.getReasonPhrase());

                // Id for newly created resource
                Random randomGenerator = new Random();
                int gradedItemId = Math.abs(randomGenerator.nextInt(1000));
                gradedItem.setId(gradedItemId);
                
                String jsonString = Converter.convertFromObjectToJSON(gradedItem, GradedItem.class);
                LOG.info("Writing graded item to gradebook.json");
                if(storedGradebook.getItems() == null){
                    List<GradedItem> gradedItemList = new ArrayList<GradedItem>();
                    storedGradebook.setItems(gradedItemList);
                }
                storedGradebook.addItem(gradedItem);
                GradebookIO.writeToGradebook(gradebookList, filename);

                URI locationURI = URI.create(context.getAbsolutePath() + "/" + gradedItem.getId() + "/students/" + gradedItem.getStudentId());

                response = Response.status(Response.Status.CREATED).location(locationURI).entity(jsonString).build();

            } else {
                LOG.info("Creating a {} {} Status Response", Response.Status.CONFLICT.getStatusCode(), Response.Status.CONFLICT.getReasonPhrase());
                LOG.debug("Cannot create Graded Item Resource as gradebook: {} does not exists", gradebook);

                errMessage = "{\"error\": \"Gradebook does not exist\"}";
                response = Response.status(Response.Status.CONFLICT).entity(errMessage).build();
            }
        } catch (IOException e) {
            LOG.info("Creating a {} {} Status Response", Response.Status.BAD_REQUEST.getStatusCode(), Response.Status.BAD_REQUEST.getReasonPhrase());
            LOG.debug("JSON is {} is incompatible with Graded Item Resource", content);

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
     * Retrieves representation of an instance of edu.asu.cse564.sample.crud.restws.GradebookResource
     * @param gradebook
     * @param category
     * @param itemId
     * @param studentId
     * @return an HTTP response with content of the updated or created resource.
     */
    @GET
    @Path("{gradebook}/gradeditems/{category}/{itemId}/students/{studentId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getResource(@PathParam("gradebook") String gradebook,
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
              
        try{

            LOG.info("Reading gradebook data from file");
            List<Gradebook> gradebookList = GradebookIO.readFromGradebook(filename); 
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

                    errMessage = "{\"error\":\"No Graded Item Resource to return\"}";
                    response = Response.status(Response.Status.GONE).entity(errMessage).build();
                } else {
                    for(GradedItem item: gradedItemList){
                        if (item.getCategory().equals(category) && 
                            item.getId() == Integer.parseInt(itemId) && 
                            item.getStudentId() == Integer.parseInt(studentId) ){
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
                        LOG.info("Creating a {} {} Status Response", Response.Status.OK.getStatusCode(), Response.Status.OK.getReasonPhrase());
                        LOG.debug("Retrieving the Graded Item Resource {}", gradedItem);

                        String jsonString = Converter.convertFromObjectToJSON(gradedItem, GradedItem.class);
                        response = Response.status(Response.Status.OK).entity(jsonString).build();
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
     * PUT method for updating an instance of edu.asu.cse564.sample.crud.restws.GradebookResource
     * @param gradebook
     * @param category
     * @param itemId
     * @param studentId
     * @param content representation for the resource
     * @return an HTTP response with content of the updated or created resource.
     */
    @PUT
    @Path("{gradebook}/gradeditems/{category}/{itemId}/students/{studentId}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateResource(@PathParam("gradebook") String gradebook,
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
        
        LOG.debug("Attempting to update the Graded Item Resource");
        
        try{ 
            LOG.info("Reading gradebook data from file");
            List<Gradebook> gradebookList = GradebookIO.readFromGradebook(filename); 
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
                LOG.debug("Cannot update Graded Item Resource {} as it has not yet been created", content);

                errMessage = "{\"error\": \"Gradebook does not exist\"}";
                response = Response.status(Response.Status.CONFLICT).entity(errMessage).build();
            } else {
                List<GradedItem> gradedItemList = storedGradebook.getItems();
                if (gradedItemList.isEmpty()){
                    LOG.info("Creating a {} {} Status Response", Response.Status.CONFLICT.getStatusCode(), Response.Status.CONFLICT.getReasonPhrase());
                    LOG.debug("Cannot update Graded Item Resource {} as it has not yet been created", content);
                    
                    errMessage = "{\"error\": \"Graded Item does not exist\"}";
                    response = Response.status(Response.Status.CONFLICT).entity(errMessage).build();
                } else {
                    for(GradedItem item: gradedItemList){
                        if (item.getCategory().equals(category) && 
                            item.getId() == Integer.parseInt(itemId) && 
                            item.getStudentId() == Integer.parseInt(studentId) ){
                            gradedItem = item;
                            break;
                        }
                    }

                    if (gradedItem == null){
                        LOG.info("Creating a {} {} Status Response", Response.Status.CONFLICT.getStatusCode(), Response.Status.CONFLICT.getReasonPhrase());
                        LOG.debug("Cannot update Graded Item Resource {} as it has not yet been created", content);

                        errMessage = "{\"error\": \"Graded Item does not exist\"}";
                        response = Response.status(Response.Status.CONFLICT).entity(errMessage).build();
                    } else {
                        GradedItem tempItem = (GradedItem) Converter.convertFromJsonToObject(content, GradedItem.class);
                        gradedItem.setMarks(tempItem.getMarks());
                        gradedItem.setFeedback(tempItem.getFeedback());
                        GradebookIO.writeToGradebook(gradebookList, filename);
                        LOG.debug("The JSON {} was converted to the object {}", content, gradedItem);         
                        LOG.debug("Updated Graded Item Resource to {}", gradedItem);
                        LOG.info("Creating a {} {} Status Response", Response.Status.OK.getStatusCode(), Response.Status.OK.getReasonPhrase());

                        response = Response.status(Response.Status.OK).entity(content).build();
                        
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
     * Retrieves representation of an instance of edu.asu.cse564.sample.crud.restws.GradebookResource
     * @param gradebook
     * @param category
     * @param itemId
     * @param studentId
     * @return an HTTP response with content of the deleted resource.
     */
    @DELETE
    @Path("{gradebook}/gradeditems/{category}/{itemId}/students/{studentId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteResource(@PathParam("gradebook") String gradebook,
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
        
        try{ 
            LOG.info("Reading gradebook data from file");
            List<Gradebook> gradebookList = GradebookIO.readFromGradebook(filename); 
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
                    LOG.debug("No Graded Item Resource to delete");

                    errMessage = "{\"error\":\"Could not found the Graded Item resource\"}";
                    response = Response.status(Response.Status.GONE).entity(errMessage).build();
                } else {
                    for(GradedItem item: gradedItemList){
                        if (item.getCategory().equals(category) && 
                            item.getId() == Integer.parseInt(itemId) && 
                            item.getStudentId() == Integer.parseInt(studentId) ){
                            gradedItem = item;
                            break;
                        }
                    }

                    if (gradedItem == null){
                        LOG.info("Creating a {} {} Status Response", Response.Status.NOT_FOUND.getStatusCode(), Response.Status.NOT_FOUND.getReasonPhrase());

                        errMessage = "{\"error\":\"Could not found the Graded Item resource\"}";
                        response = Response.status(Response.Status.NOT_FOUND).entity(errMessage).build();
                    } else {
                        LOG.info("Creating a {} {} Status Response", Response.Status.OK.getStatusCode(), Response.Status.OK.getReasonPhrase());
                        LOG.debug("Deleting the Graded Item Resource {}", gradedItem);
                        String jsonString = Converter.convertFromObjectToJSON(gradedItem, GradedItem.class);
                        gradedItemList.remove(gradedItem);
                        GradebookIO.writeToGradebook(gradebookList, filename);
                        response = Response.status(Response.Status.OK).entity(jsonString).build();
                    }
                }
            }
        } catch (IOException e) {
            LOG.info("Creating a {} {} Status Response", Response.Status.BAD_REQUEST.getStatusCode(), Response.Status.BAD_REQUEST.getReasonPhrase());

            response = Response.status(Response.Status.BAD_REQUEST).entity("{\"error\":\"Could not understand request\"}").build();
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

