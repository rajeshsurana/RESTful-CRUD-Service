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
import edu.asu.cse564.samples.crud.jaxb.utils.Converter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 * REST Web Service
 *
 * @author Rajesh Surana
 */
@Path("gradebooks")
public class GradebookResource {
    
    private static final Logger LOG = LoggerFactory.getLogger(GradebookResource.class);
    private static final String FILENAME = "gradebook.json";
    
    private static Gradebook gradebook;


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
        gradebook = null;
        
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
                List<Gradebook> gradebookList = GradebookIO.readFromGradebook(FILENAME); 
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

                    //String jsonString = Converter.convertFromObjectToJSON(gradebook, Gradebook.class);
                    LOG.info("Writing gradebook to gradebook.json");
                    gradebookList.add(gradebook);
                    GradebookIO.writeToGradebook(gradebookList, FILENAME);

                    URI locationURI = URI.create(context.getAbsolutePath() + "/" + gradebook.getGradebookName());

                    response = Response.status(Response.Status.NO_CONTENT).location(locationURI).type(MediaType.APPLICATION_JSON).build();

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
     * @param gradebookName
     * @return an HTTP response with content of the updated or created resource.
     */
    @GET
    @Path("{gradebook}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getGradebookResource(@PathParam("gradebook") String gradebookName) {
        LOG.info("Retrieving the Gradebook Resource {}", gradebookName);
        LOG.debug("GET request");
        LOG.debug("PathParam gradebook = {}", gradebookName);
        
        Response response;
        String errMessage;
        gradebook = null;
              
        try{

            LOG.info("Reading gradebook data from file");
            List<Gradebook> gradebookList = GradebookIO.readFromGradebook(FILENAME); 
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
     * @param gradebookName
     * @return an HTTP response with content of the deleted resource.
     */
    @DELETE
    @Path("{gradebook}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteGradebookResource(@PathParam("gradebook") String gradebookName) {
        LOG.debug("DELETE request");
        LOG.debug("PathParam gradebook = {}", gradebookName);

        
        LOG.info("Attempting to remove the Gradebook Resource");
        
        Response response;
        String errMessage;
        gradebook = null;
        
        try{ 
            LOG.info("Reading gradebook data from file");
            List<Gradebook> gradebookList = GradebookIO.readFromGradebook(FILENAME); 
            if(gradebookList == null || gradebookList.isEmpty()){
                gradebookList = new ArrayList<Gradebook>();
            }
            Gradebook storedGradebook = null;
            for(Gradebook tempGradebook: gradebookList){
                if(tempGradebook.getGradebookName().equals(gradebookName)){
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
                GradebookIO.writeToGradebook(gradebookList, FILENAME);
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
    
}