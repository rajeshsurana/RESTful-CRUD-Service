/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.asu.cse564.samples.crud.jaxb.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
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
    
    public static Object convertFromJsonToObject(Object jsonString, Class type){
        LOG.info("Converting from JSON to an Object");
        LOG.debug("Object jsonString = {}", jsonString);
        LOG.debug("Class type = {}", type);
        if(((String)jsonString).trim().equals(""))
            return null;
        ObjectMapper mapper = new ObjectMapper();
        Object ob;
        try{
            ob = mapper.readValue((String)jsonString, type);
        }catch (Exception e){
            throw new RuntimeException(e);
        }
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
    
    public static String convertFromObjectToJSON(Object source, Class type){
        LOG.info("Converting from and Object to JSON");
        LOG.debug("Object source = {}", source);
        LOG.debug("Class type = {}", type);
        
        ObjectMapper mapper = new ObjectMapper();
        String result;
        try{
            //result = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(source);
            result = mapper.writeValueAsString(source);
            LOG.debug("Object to JSON = {}", result);
        }catch (Exception e) {
            throw new RuntimeException(e);
        }
        return result;
    } 
}

