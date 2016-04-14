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

/**
 * REST Web Service
 *
 * @author Rajesh Surana
 */
@Path("gradebooks/{gradebook}/gradeditems")
public class GradedItemResource {
    
    private static final Logger LOG = LoggerFactory.getLogger(GradedItemResource.class);
    private static final String FILENAME = "gradebook.json";
    
    private GradedItem gradedItem;

    @Context
    private UriInfo context;

    /**
     * Creates a new instance of GradedItemResource
     */
    public GradedItemResource() {
        LOG.info("Creating an Graded Item Resource");
    }
    
        
    /**
     * POST method for creating an instance of edu.asu.cse564.sample.crud.restws.GradedItemResource
     * @param gradebook gradebook identifier
     * @param content representation for the resource
     * @return an HTTP response with content of the updated or created resource.
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createGradedItemResource(@PathParam("gradebook") String gradebook,
                                             String content) {
        LOG.info("Creating the instance Graded Item Resource {}", gradedItem);
        LOG.debug("POST request");
        LOG.debug("PathParam gradebook = {}", gradebook);
        LOG.debug("Request Content = {}", content);
        
        Response response;
        String errMessage;
        gradedItem = null;
        
        try {
            gradedItem = (GradedItem) Converter.convertFromJsonToObject(content, GradedItem.class);
            if (gradedItem.getCategory().trim().equals("")){
                LOG.info("Creating a {} {} Status Response", Response.Status.NOT_ACCEPTABLE.getStatusCode(), Response.Status.NOT_ACCEPTABLE.getReasonPhrase());
                LOG.debug("JSON is {} is incompatible with Gradebook Resource", content);

                errMessage = "{\"error\": \"Category of graded item can not only be whitespaces\"}";
                response = Response.status(Response.Status.NOT_ACCEPTABLE).entity(errMessage).build();
            } else {
                LOG.debug("The JSON {} was converted to the object {}", content, gradedItem);
            
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
                    LOG.debug("Attempting to create an Graded Item Resource and setting it to {}", content);

                    LOG.info("Creating a {} {} Status Response", Response.Status.CREATED.getStatusCode(), Response.Status.CREATED.getReasonPhrase());

                    if(storedGradebook.getItems() == null){
                        List<GradedItem> gradedItemList = new ArrayList<GradedItem>();
                        storedGradebook.setItems(gradedItemList);
                    }
                    List<GradedItem> gradedItemList = storedGradebook.getItems();
                    boolean itemExist = false;
                    for(GradedItem gitemp: gradedItemList){
                        if (gitemp.getCategory().equals(gradedItem.getCategory()) && gitemp.getId() == gradedItem.getId()){
                            itemExist = true;
                            break;
                        }
                    }
                    if (itemExist){
                        LOG.info("Creating a {} {} Status Response", Response.Status.CONFLICT.getStatusCode(), Response.Status.CONFLICT.getReasonPhrase());
                        LOG.debug("Cannot create Graded Item Resource as it already exists: {} ", gradedItem);

                        errMessage = "{\"error\": \"Graded Item already exists\"}";
                        response = Response.status(Response.Status.CONFLICT).entity(errMessage).build();
                    } else {
                        LOG.info("Writing graded item to gradebook.json");
                        storedGradebook.addItem(gradedItem);
                        GradebookIO.writeToGradebook(gradebookList, FILENAME);

                        URI locationURI = URI.create(context.getAbsolutePath() +"/"+gradedItem.getCategory()+ "/" + gradedItem.getId());

                        response = Response.status(Response.Status.NO_CONTENT).location(locationURI).build();
                    }
                } else {
                    LOG.info("Creating a {} {} Status Response", Response.Status.CONFLICT.getStatusCode(), Response.Status.CONFLICT.getReasonPhrase());
                    LOG.debug("Cannot create Graded Item Resource as gradebook: {} does not exists", gradebook);

                    errMessage = "{\"error\": \"Gradebook does not exist\"}";
                    response = Response.status(Response.Status.CONFLICT).entity(errMessage).build();
                }
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
     * Retrieves representation of an instance of edu.asu.cse564.sample.crud.restws.GradedItemResource
     * @param gradebook
     * @param category
     * @param itemId
     * @return an HTTP response with content of the updated or created resource.
     */
    @GET
    @Path("{category}/{itemId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getGradedItemResource(@PathParam("gradebook") String gradebook,
                                @PathParam("category") String category,
                                @PathParam("itemId") String itemId) {
        LOG.info("Retrieving the GradedItem Resource {}", gradedItem);
        LOG.debug("GET request");
        LOG.debug("PathParam gradebook = {}", gradebook);
        LOG.debug("PathParam category = {}", category);
        LOG.debug("PathParam itemId = {}", itemId);
        
        Response response;
        String errMessage;
        gradedItem = null;   
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

                    errMessage = "{\"error\":\"No Graded Item Resource to return\"}";
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
     * Retrieves representation of an instance of edu.asu.cse564.sample.crud.restws.GradedItemResource
     * @param gradebook
     * @param category
     * @param itemId
     * @return an HTTP response with content of the deleted resource.
     */
    @DELETE
    @Path("{category}/{itemId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteGradedItemResource(@PathParam("gradebook") String gradebook,
                                             @PathParam("category") String category,
                                             @PathParam("itemId") String itemId) {
        LOG.debug("DELETE request");
        LOG.debug("PathParam gradebook = {}", gradebook);
        LOG.debug("PathParam category = {}", category);
        LOG.debug("PathParam itemId = {}", itemId);
        
        LOG.info("Attempting to remove the Graded Item Resource");
        
        Response response;
        String errMessage;
        gradedItem = null;
        
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
                    LOG.debug("No Graded Item Resource to delete");

                    errMessage = "{\"error\":\"Could not found the Graded Item resource\"}";
                    response = Response.status(Response.Status.GONE).entity(errMessage).build();
                } else {
                    for(GradedItem item: gradedItemList){
                        if (item.getCategory().equals(category) && 
                            item.getId() == Integer.parseInt(itemId) ){
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
                        String jsonString =  "{\"success\": \"Graded Item is deleted successfully\"}";
                        gradedItemList.remove(gradedItem);
                        GradebookIO.writeToGradebook(gradebookList, FILENAME);
                        response = Response.status(Response.Status.OK).entity(jsonString).build();
                    }
                }
            }
        }  catch (RuntimeException e) {
            e.printStackTrace();
            LOG.debug("Catch All exception");
            LOG.info("Creating a {} {} Status Response", Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), Response.Status.INTERNAL_SERVER_ERROR.getReasonPhrase());

            response = Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("{\"error\":\"Internal Server Error occured\"}").build();
        }      
        
        LOG.debug("Generated response {}", response);
        
        return response;
    }
}



