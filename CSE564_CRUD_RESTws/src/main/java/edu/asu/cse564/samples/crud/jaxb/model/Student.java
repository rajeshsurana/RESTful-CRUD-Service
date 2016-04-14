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
public class Student {
    private static final Logger LOG = LoggerFactory.getLogger(Student.class);
    
    private int studentId;
    private float score;
    private String feedback;

    public Student() {
        LOG.info("Creating student object");
    }

    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        LOG.info("Setting student id");
        this.studentId = studentId;
        LOG.debug("Updated student object = {}", this);
    }

    public float getScore() {
        return score;
    }

    public void setScore(float score) {
        LOG.info("Setting student score");
        this.score = score;
        LOG.debug("Updated student object = {}", this);
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        LOG.info("Setting student score");
        this.feedback = feedback;
        LOG.debug("Updated student object = {}", this);
    }

    @Override
    public String toString() {
        return "Student{" + "studentId=" + studentId + ", score=" + score + ", feedback=" + feedback + '}';
    }
    
}
