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
public class GradedItem {
    
    private static final Logger LOG = LoggerFactory.getLogger(GradedItem.class);
    
    private String category;
    private int id;
    private List<Student> students;
    
    public GradedItem(){
        LOG.info("Creating graded-item object");
        students = new ArrayList<Student>();
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

    public List<Student> getStudents() {
        return students;
    }

    public void setStudents(List<Student> students) {
        LOG.info("Setting graded-item students");
        this.students = students;
        LOG.debug("Updated graded-item = {}", this);
    }
    
    public void addStudent(Student student){
        this.students.add(student);
    }

    @Override
    public String toString() {
        return "GradedItem{" + "category=" + category + ", id=" + id + ", students=" + students + '}';
    }
}
