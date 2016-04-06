/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.asu.cse564.samples.crud.io;

import com.fasterxml.jackson.core.type.TypeReference;
import edu.asu.cse564.samples.crud.jaxb.model.Gradebook;
import edu.asu.cse564.samples.crud.jaxb.utils.Converter;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Rajesh Surana
 */
public class GradebookIO {
    
    private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(GradebookIO.class);
    
    public static void writeToGradebook(List<Gradebook> gradebookList, String filename){
        //GradebookIO gbio = new GradebookIO();
        try{
            //String path = gbio.getPath(filename);
            String path = filename;
            File file = new File(path);
        
            // if file doesnt exists, then create it
            if (!file.exists()) {
                LOG.info("Creating file as it does not exist");
                file.createNewFile();
                LOG.debug("Created file = {}", file.getAbsolutePath());
            }
            String jsonString = null;
            jsonString = Converter.convertFromObjectToJSON(gradebookList, gradebookList.getClass());
            LOG.info("File path = {}", file.getAbsolutePath());
            FileWriter fw = new FileWriter(file.getAbsoluteFile());
            try (BufferedWriter bw = new BufferedWriter(fw)) {
                bw.write(jsonString);
            }
        }catch(Exception e){
            e.printStackTrace();;
        }
    }
    
    public static List<Gradebook> readFromGradebook(String filename){
        //GradebookIO gbio = new GradebookIO();
        //String path = gbio.getPath(filename);
        String path = filename;
        File file = new File(filename);
        
        // if file doesnt exists, then create it
        if (!file.exists()) {
            LOG.info("Creating file as it does not exits");
            try {
                file.createNewFile();
            } catch (IOException ex) {
                Logger.getLogger(GradebookIO.class.getName()).log(Level.SEVERE, null, ex);
            }
            LOG.debug("Created file = {}", file.getAbsolutePath());
            return null;
        }
        LOG.info("File path = {}", file.getAbsolutePath());
        try (BufferedReader br = new BufferedReader(new FileReader(path)))
        {
            StringBuilder sb = new StringBuilder();
            String sCurrentLine;

            while ((sCurrentLine = br.readLine()) != null) {
                sb.append(sCurrentLine);
            }
            if(sb.toString() != null && !sb.toString().trim().equals("")){
                List<Gradebook> gblist = new ArrayList<Gradebook>();
                gblist = (List<Gradebook> )Converter.convertFromJsonToObject(sb.toString(), new TypeReference<List<Gradebook>>(){});
                return gblist;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } 
        return null;
    }
    
    public String getPath(String fileName){
        String reponsePath = "";
        try {
            String path = this.getClass().getClassLoader().getResource("").getPath();
            String fullPath = URLDecoder.decode(path, "UTF-8");
            System.out.println(fullPath);
            
            // to read a file from webcontent
            String[] pathElements = fullPath.split("/");
            for(String ele: pathElements){
                if(!ele.equals("CSE564_CRUD_RESTws")){
                    if(ele.length()>1){
                        reponsePath += ele + File.separatorChar;
                    }
                }
                else{
                    break;
                }
            }
            reponsePath += "CSE564_CRUD_RESTws" + File.separatorChar + fileName;
            
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(GradebookIO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return reponsePath;
    }
    
}

