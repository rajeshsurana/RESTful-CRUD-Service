/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.asu.cse564.samples.crud.jaxb.model;

import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Rajesh Surana
 */
public class Gradebook {
    private static final Logger LOG = LoggerFactory.getLogger(Gradebook.class);
    private String gradebookName;
    private List<GradedItem> items;

    public Gradebook() {
        LOG.info("Creating gradebook object");
        items = new ArrayList<GradedItem>();
    }

    public Gradebook(String name) {
        LOG.info("Creating gradebook object");
        this.gradebookName = name;
        items = new ArrayList<GradedItem>();
        LOG.debug("Created gradebook object = {}", this);
    }

    
    public Gradebook(List<GradedItem> items) {
        LOG.info("Creating gradebook object");
        this.items = items;
        LOG.debug("Created gradebook object = {}", this);
    }

    public List<GradedItem> getItems() {
        return items;
    }

    public void setItems(List<GradedItem> items) {
        LOG.info("Updating gradebook object");
        this.items = items;
        LOG.info("Updated gradebook object = {}", this);
    }    

    public String getGradebookName() {
        return gradebookName;
    }

    public void setGradebookName(String name) {
        LOG.info("Updating gradebook object");
        this.gradebookName = name;
        LOG.info("Updated gradebook object = {}", this);
    }
    
    public void addItem(GradedItem item){
        LOG.info("Updating gradebook object");
        items.add(item);
        LOG.info("Updated gradebook object = {}", this);
    }

    @Override
    public String toString() {
        return "Gradebook{" + "gradebookName=" + gradebookName + ", items=" + items + '}';
    }    
}
