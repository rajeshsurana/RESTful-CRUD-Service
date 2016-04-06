/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.asu.cse564.samples.crud.restcl;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import javax.ws.rs.core.MediaType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Rajesh Surana
 */
public class GradeBook_CRUD_cl {
    private static final Logger LOG = LoggerFactory.getLogger(GradeBook_CRUD_cl.class);
    
    final private WebResource webResource;
    final private Client client;
    private static final String BASE_URI = "http://localhost:8080/CSE564_CRUD_RESTws/webresources";

    public GradeBook_CRUD_cl() {
        LOG.info("Creating a Gradebook_CRUD REST client");

        ClientConfig config = new DefaultClientConfig();
        client = Client.create(config);
        webResource = client.resource(BASE_URI).path("gradebooks");
        LOG.debug("webResource = {}", webResource.getURI());
    }
    
    public ClientResponse createGradebook(Object requestEntity) throws UniformInterfaceException {
        LOG.info("Initiating a Create request");
        LOG.debug("JSON String = {}", (String) requestEntity);
        
        return webResource.type(MediaType.APPLICATION_JSON).post(ClientResponse.class, requestEntity);
    }
    
    public <T> T retrieveGradedbook(Class<T> responseType, String gradebook) throws UniformInterfaceException {
        LOG.info("Initiating a Retrieve request");
        LOG.debug("responseType = {}", responseType.getClass());
        LOG.debug("Gradebook = {}", gradebook);

        return webResource.path(gradebook).accept(MediaType.APPLICATION_JSON).get(responseType);
    }
    
    public ClientResponse deleteGradedbook(String gradebook) throws UniformInterfaceException {
        LOG.info("Initiating a Delete request");
        LOG.debug("Gradebook = {}", gradebook);
        
        return webResource.path(gradebook).delete(ClientResponse.class);
    }
    
    public ClientResponse createGradedItem(Object requestEntity, String gradebook, String category) throws UniformInterfaceException {
        LOG.info("Initiating a Create request");
        LOG.debug("Gradebook = {}", gradebook);
        LOG.debug("JSON String = {}", (String) requestEntity);
        
        return webResource.path(gradebook).path("gradeditems").path(category).type(MediaType.APPLICATION_JSON).post(ClientResponse.class, requestEntity);
    }
    
    public <T> T retrieveGradedItem(Class<T> responseType, String gradebook, String category, String itemId, String studentId) throws UniformInterfaceException {
        LOG.info("Initiating a Retrieve request");
        LOG.debug("responseType = {}", responseType.getClass());
        LOG.debug("Gradebook = {}", gradebook);
        LOG.debug("Item Id = {}", itemId);
        LOG.debug("Student Id = {}", studentId);

        return webResource.path(gradebook).path("gradeditems").path(category).path(itemId).path("students").path(studentId).accept(MediaType.APPLICATION_JSON).get(responseType);
    }
    
    public ClientResponse updateGradedItem(Object requestEntity, String gradebook, String category, String itemId, String studentId) throws UniformInterfaceException {
        LOG.info("Initiating an Update request");
        LOG.debug("JSON String = {}", (String) requestEntity);
        LOG.debug("Gradebook = {}", gradebook);
        LOG.debug("Item Id = {}", itemId);
        LOG.debug("Student Id = {}", studentId);
        
        return webResource.path(gradebook).path("gradeditems").path(category).path(itemId).path("students").path(studentId).type(MediaType.APPLICATION_JSON).put(ClientResponse.class, requestEntity);
    }
    
    public ClientResponse deleteGradedItem(String gradebook, String category, String itemId, String studentId) throws UniformInterfaceException {
        LOG.info("Initiating a Delete request");
        LOG.debug("Gradebook = {}", gradebook);
        LOG.debug("Item Id = {}", itemId);
        LOG.debug("Student Id = {}", studentId);
        
        return webResource.path(gradebook).path("gradeditems").path(category).path(itemId).path("students").path(studentId).delete(ClientResponse.class);
    }
    public void close() {
        LOG.info("Closing the REST Client");
        
        client.destroy();
    }
    
}
