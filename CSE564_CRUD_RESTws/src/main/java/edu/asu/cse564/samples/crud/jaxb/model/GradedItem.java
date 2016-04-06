/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.asu.cse564.samples.crud.jaxb.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Rajesh Surana
 */
public class GradedItem {
    
    private static final Logger LOG = LoggerFactory.getLogger(GradedItem.class);
    
    private String category;
    private int id;
    private int studentId;
    private float marks;
    private String feedback;
    
    public GradedItem(){
        LOG.info("Creating graded-item object");
    }

    public GradedItem(int id, String category, int studentId, float marks, String feedback) {
        LOG.info("Creating graded-item object");
        this.id = id;
        this.category = category;
        this.studentId = studentId;
        this.marks = marks;
        this.feedback = feedback;
        LOG.debug("Created graded-item = {}", this);
    }

    public GradedItem(GradedItem item){
        LOG.info("Creating graded-item object");
        this.id = item.id;
        this.category = item.category;
        this.studentId = item.studentId;
        this.marks = item.marks;
        this.feedback = item.feedback;
        LOG.debug("Created graded-item = {}", this);   
    }
    
    public int getId() {
        return id;
    }

    public void setId(int id) {
        LOG.info("Setting graded-item id");
        this.id = id;
        LOG.debug("Updated graded-item = {}", this);
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        LOG.info("Setting graded-item category");
        this.category = category;
        LOG.debug("Updated graded-item = {}", this);
    }

    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        LOG.info("Setting graded-item student id");
        this.studentId = studentId;
        LOG.debug("Updated graded-item = {}", this);
    }

    public float getMarks() {
        return marks;
    }

    public void setMarks(float marks) {
        LOG.info("Setting graded-item marks");
        this.marks = marks;
        LOG.debug("Updated graded-item = {}", this);
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        LOG.info("Setting graded-item feedback");
        this.feedback = feedback;
        LOG.debug("Updated graded-item = {}", this);
    }

    @Override
    public String toString() {
        return "GradedItem{" + "category=" + category + ", id=" + id + ", studentId=" + studentId + ", marks=" + marks + ", feedback=" + feedback + '}';
    }
}
