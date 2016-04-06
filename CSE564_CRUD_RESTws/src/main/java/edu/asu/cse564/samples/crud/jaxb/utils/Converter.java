/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.asu.cse564.samples.crud.jaxb.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.StringWriter;
import java.io.StringReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Rajesh Surana
 */
public class Converter {
    
    private static final Logger LOG = LoggerFactory.getLogger(Converter.class);
    
    public static Object convertFromXmlToObject(Object xmlString, Class type) throws JAXBException{
        LOG.info("Converting from XML to an Object");
        LOG.debug("Object xmlString = {}", xmlString);
        LOG.debug("Class type = {}", type);
        
        Object result;

        StringReader sr = null;
        
        if (xmlString instanceof String){
            sr = new StringReader((String)xmlString);
        }

        JAXBContext context         = JAXBContext.newInstance(type);
        Unmarshaller unmarshaller   = context.createUnmarshaller();
        result = unmarshaller.unmarshal(sr);
        
        return result;
    }
    
    public static Object convertFromJsonToObject(Object jsonString, Class type) throws IOException{
        LOG.info("Converting from JSON to an Object");
        LOG.debug("Object jsonString = {}", jsonString);
        LOG.debug("Class type = {}", type);
        
        ObjectMapper mapper = new ObjectMapper();
        Object ob;
        ob = mapper.readValue((String)jsonString, type);
        return ob;
    }
    
    public static Object convertFromJsonToObject(Object jsonString, TypeReference reference) throws IOException{
        LOG.info("Converting from JSON to an Object");
        LOG.debug("Object jsonString = {}", jsonString);
        LOG.debug("Class reference = {}", reference);
        
        ObjectMapper mapper = new ObjectMapper();
        Object ob;
        ob = mapper.readValue((String)jsonString, reference);
        return ob;
    }
    
    public static String convertFromObjectToXml(Object source, Class type) {
        LOG.info("Converting from and Object to XML");
        LOG.debug("Object source = {}", source);
        LOG.debug("Class type = {}", type);
        
        String result;
        StringWriter sw = new StringWriter();
        try {
            JAXBContext context     = JAXBContext.newInstance(type);
            Marshaller  marshaller  = context.createMarshaller();
            marshaller.marshal(source, sw);
            result = sw.toString();
        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }

        return result;
    }
    
    public static String convertFromObjectToJSON(Object source, Class type) throws JsonProcessingException{
        LOG.info("Converting from and Object to JSON");
        LOG.debug("Object source = {}", source);
        LOG.debug("Class type = {}", type);
        
        ObjectMapper mapper = new ObjectMapper();
        String result;

        result = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(source);
        //result = mapper.writeValueAsString(source);
        LOG.debug("Object to JSON = {}", result);
        return result;
    } 
}

