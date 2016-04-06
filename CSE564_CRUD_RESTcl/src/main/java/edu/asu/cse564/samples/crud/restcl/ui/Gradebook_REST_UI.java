/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.asu.cse564.samples.crud.restcl.ui;

import com.sun.jersey.api.client.ClientResponse;

import javax.ws.rs.core.Response;

import javax.swing.JFrame;


import java.net.URI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.asu.cse564.samples.crud.jaxb.model.Gradebook;
import edu.asu.cse564.samples.crud.jaxb.model.GradedItem;
import edu.asu.cse564.samples.crud.jaxb.utils.Converter;
import edu.asu.cse564.samples.crud.restcl.GradeBook_CRUD_cl;

/**
 *
 * @author Rajesh Surana
 */
public class Gradebook_REST_UI extends JFrame {
    
    private static final Logger LOG = LoggerFactory.getLogger(Gradebook_REST_UI.class);
    
    private GradeBook_CRUD_cl gradebook_CRUD_rest_client;
    
    private URI resourceURI;

    /**
     * Creates new form Gradebook_REST_UI
     */
    public Gradebook_REST_UI() {
        LOG.info("Creating a Appointment_REST_UI object");
        initComponents();
        
        gradebook_CRUD_rest_client = new GradeBook_CRUD_cl();
        jRadioButton_create2.setSelected(true);
        this.enableDiableFlag = false;
        this.formatCheckingFlag = true;
    }
    
    private String convertFormToJSONString(){
        GradedItem gradedItem = new GradedItem();
         
        if (!jTextField_category.getText().trim().equals("")){
            gradedItem.setCategory(jTextField_category.getText());
        }
        
        if (!jTextField_itemId.getText().trim().equals("")){
            gradedItem.setId(Integer.parseInt(jTextField_itemId.getText()));
        }
        
        if (!jTextField_studentId.getText().trim().equals("")){
            gradedItem.setStudentId(Integer.parseInt(jTextField_studentId.getText()));
        }
        
        if (!jTextField_score.getText().trim().equals("")){
            gradedItem.setMarks(Float.parseFloat(jTextField_score.getText()));
        }

        if (!jTextField_feedback.getText().trim().equals("")){
            gradedItem.setFeedback(jTextField_feedback.getText());
        }
        String jsonString = Converter.convertFromObjectToJSON(gradedItem, gradedItem.getClass());
        
        return jsonString;
    }
    
    private void populateForm(ClientResponse clientResponse){
        this.populateForm(clientResponse, true);
    }
    
    private void populateForm(ClientResponse clientResponse, boolean populateGradedItem){
        LOG.info("Populating the UI with the Graded Item info");
        
        String entity=null;
        try{
            entity = clientResponse.getEntity(String.class);
            jTextPane_response.setText(entity);
        } catch(Exception e){
            e.printStackTrace();
        }
        
        try{
            if (((clientResponse.getStatus() == Response.Status.OK.getStatusCode()) ||
                (clientResponse.getStatus() == Response.Status.CREATED.getStatusCode()))&&
                 populateGradedItem){
                
                LOG.debug("The Client Response entity is {}", entity);
                GradedItem gradedItem = (GradedItem)Converter.convertFromJsonToObject(entity, GradedItem.class);
                LOG.debug("The Client Response gradedItem object is {}", gradedItem);
                
                // Populate Graded Item info
                jTextField_category.setText(gradedItem.getCategory());
                jTextField_itemId.setText(String.valueOf(gradedItem.getId()));
                jTextField_studentId.setText(Integer.toString(gradedItem.getStudentId()));
                jTextField_score.setText(Float.toString(gradedItem.getMarks()));
                jTextField_feedback.setText(gradedItem.getFeedback());
            } else {
                jTextField_category.setText("");
                jTextField_itemId.setText("");
                jTextField_studentId.setText("");
                jTextField_score.setText("");
                jTextField_feedback.setText("");
            }
           
            // Populate HTTP Header Information
            jTextField3.setText(Integer.toString(clientResponse.getStatus()));
            jTextField6.setText(clientResponse.getType().toString());
            jTextField_date.setText(clientResponse.getResponseStatus().getReasonPhrase());
            // The Location filed is only populated when a Resource is created
            if (clientResponse.getStatus() == Response.Status.CREATED.getStatusCode()){
                jTextField5.setText(clientResponse.getLocation().toString());
            } else {
                jTextField5.setText("");
            }
            
        } catch (Exception e){
            e.printStackTrace();
        }
    }
    
    private boolean isInteger(String number){
        try{
            Integer.parseInt(number);
        } catch (NumberFormatException e){
            return false;
        }
        return true;
    }
    
    private boolean isFloat(String number){
        try{
            Float.parseFloat(number);
        } catch (NumberFormatException e){
            return false;
        }
        return true;
    }
    
    private boolean checkFormat(boolean item, boolean student, boolean score){
        if (!this.formatCheckingFlag){
            return true;
        }
        if(item){
            if(!this.isInteger(itemID)){
                jTextField_help.setForeground(new java.awt.Color(255, 0, 0));
                jTextField_help.setText("Item ID must be in Integer format");
                return false;
            }
        }
        if(student){
            if(!this.isInteger(studentID)){
                jTextField_help.setForeground(new java.awt.Color(255, 0, 0));
                jTextField_help.setText("Student ID must be in Integer format");
                return false;
            }
        }
        if(score){
            if(!this.isFloat(marks)){
                jTextField_help.setForeground(new java.awt.Color(255, 0, 0));
                jTextField_help.setText("Score must in Integer/Float format");
                return false;
            }
        }
        jTextField_help.setForeground(new java.awt.Color(0, 255, 0));
        jTextField_help.setText("Request sent");
        return true;
    }
    
    
    private void textFieldOperator(boolean gradebook1, 
                                   boolean gradebook2, 
                                   boolean category,
                                   boolean itemId,
                                   boolean studentId,
                                   boolean score,
                                   boolean feedback){
        if(!enableDiableFlag)
            return;
        if(gradebook1){
            jTextField_gradebook1.setEnabled(true);
        }else{
            jTextField_gradebook1.setText("");
            jTextField_gradebook1.setEnabled(false);
        }
        if(gradebook2){
            jTextField_gradebook2.setEnabled(true);
        }else{
            jTextField_gradebook2.setText("");
            jTextField_gradebook2.setEnabled(false);
        }
        if(category){
            jTextField_category.setEnabled(true);
        }else{
            jTextField_category.setText("");
            jTextField_category.setEnabled(false);
        }
        if(itemId){
            jTextField_itemId.setEnabled(true);
        }else{
            jTextField_itemId.setText("");
            jTextField_itemId.setEnabled(false);
        }
        if(studentId){
            jTextField_studentId.setEnabled(true);
        }else{
            jTextField_studentId.setText("");
            jTextField_studentId.setEnabled(false);
        }
        if(score){
            jTextField_score.setEnabled(true);
        }else{
            jTextField_score.setText("");
            jTextField_score.setEnabled(false);
        }
        if(feedback){
            jTextField_feedback.setEnabled(true);
        }else{
            jTextField_feedback.setText("");
            jTextField_feedback.setEnabled(false);
        }         
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        jLabel1 = new javax.swing.JLabel();
        jRadioButton_create = new javax.swing.JRadioButton();
        jRadioButton_read = new javax.swing.JRadioButton();
        jRadioButton_update = new javax.swing.JRadioButton();
        jRadioButton_delete = new javax.swing.JRadioButton();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jTextField_studentId = new javax.swing.JTextField();
        jTextField_category = new javax.swing.JTextField();
        jTextField_itemId = new javax.swing.JTextField();
        jButton1 = new javax.swing.JButton();
        jLabel5 = new javax.swing.JLabel();
        jTextField3 = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jTextField5 = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        jTextField6 = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        jTextField_gradebook1 = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        jTextField_score = new javax.swing.JTextField();
        jLabel12 = new javax.swing.JLabel();
        jRadioButton_create2 = new javax.swing.JRadioButton();
        jLabel13 = new javax.swing.JLabel();
        jTextField_gradebook2 = new javax.swing.JTextField();
        jLabel14 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jTextField_feedback = new javax.swing.JTextField();
        jLabel17 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jTextField_date = new javax.swing.JTextField();
        jRadioButton_delete2 = new javax.swing.JRadioButton();
        jLabel18 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextPane_response = new javax.swing.JTextPane();
        jRadioButton_read2 = new javax.swing.JRadioButton();
        jLabel_help = new javax.swing.JLabel();
        jTextField_help = new javax.swing.JTextField();
        jToggleButton_req = new javax.swing.JToggleButton();
        jToggleButton_format = new javax.swing.JToggleButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel1.setFont(new java.awt.Font("Lucida Grande", 1, 13)); // NOI18N
        jLabel1.setText("Action");

        buttonGroup1.add(jRadioButton_create);
        jRadioButton_create.setText("Create");
        jRadioButton_create.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButton_createActionPerformed(evt);
            }
        });

        buttonGroup1.add(jRadioButton_read);
        jRadioButton_read.setText("Read");
        jRadioButton_read.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButton_readActionPerformed(evt);
            }
        });

        buttonGroup1.add(jRadioButton_update);
        jRadioButton_update.setText("Update");
        jRadioButton_update.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButton_updateActionPerformed(evt);
            }
        });

        buttonGroup1.add(jRadioButton_delete);
        jRadioButton_delete.setText("Delete");
        jRadioButton_delete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButton_deleteActionPerformed(evt);
            }
        });

        jLabel2.setFont(new java.awt.Font("Lucida Grande", 1, 13)); // NOI18N
        jLabel2.setText("Graded Item");

        jLabel3.setText("Student id");

        jLabel4.setText("Graded item category");

        jTextField_studentId.setName("IdField"); // NOI18N
        jTextField_studentId.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField_studentIdActionPerformed(evt);
            }
        });

        jTextField_category.setName("TitleField"); // NOI18N
        jTextField_category.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField_categoryActionPerformed(evt);
            }
        });

        jTextField_itemId.setToolTipText("dd/MM/yyyy HH:mm:ss");
        jTextField_itemId.setName("PriorityField"); // NOI18N
        jTextField_itemId.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField_itemIdActionPerformed(evt);
            }
        });

        jButton1.setText("Submit");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jLabel5.setText("HTTP Status Code");

        jTextField3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField3ActionPerformed(evt);
            }
        });

        jLabel7.setText("Location");

        jLabel8.setFont(new java.awt.Font("Lucida Grande", 1, 13)); // NOI18N
        jLabel8.setText("HTTP Header Info");

        jTextField5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField5ActionPerformed(evt);
            }
        });

        jLabel9.setText("Media Type");

        jTextField6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField6ActionPerformed(evt);
            }
        });

        jLabel10.setText("Gradebook");

        jLabel11.setText("Score");

        jLabel12.setFont(new java.awt.Font("Lucida Grande", 1, 13)); // NOI18N
        jLabel12.setText("Action");

        buttonGroup1.add(jRadioButton_create2);
        jRadioButton_create2.setText("Create");
        jRadioButton_create2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButton_create2ActionPerformed(evt);
            }
        });

        jLabel13.setText("Gradebook");

        jLabel14.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        jLabel14.setForeground(new java.awt.Color(0, 51, 153));
        jLabel14.setText("Restful-CRUD Client");

        jLabel16.setFont(new java.awt.Font("Lucida Grande", 1, 13)); // NOI18N
        jLabel16.setText("Gradebook");

        jLabel15.setText("Feedback");

        jLabel17.setText("Item id");

        jLabel6.setText("Status");

        buttonGroup1.add(jRadioButton_delete2);
        jRadioButton_delete2.setText("Delete");
        jRadioButton_delete2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButton_delete2ActionPerformed(evt);
            }
        });

        jLabel18.setText("Response");

        jScrollPane1.setViewportView(jTextPane_response);

        buttonGroup1.add(jRadioButton_read2);
        jRadioButton_read2.setText("Read");
        jRadioButton_read2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButton_read2ActionPerformed(evt);
            }
        });

        jLabel_help.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        jLabel_help.setText("Help:");

        jTextField_help.setEditable(false);
        jTextField_help.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        jTextField_help.setForeground(new java.awt.Color(255, 51, 51));

        jToggleButton_req.setText("Disable non-required fields for each Action");
        jToggleButton_req.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButton_reqActionPerformed(evt);
            }
        });

        jToggleButton_format.setText("Disable format checking for each Action");
        jToggleButton_format.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButton_formatActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, layout.createSequentialGroup()
                        .add(31, 31, 31)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                                .add(jLabel11)
                                .add(jLabel15)
                                .add(jButton1)
                                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                    .add(jRadioButton_read2)
                                    .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                                        .add(layout.createSequentialGroup()
                                            .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                                .add(jRadioButton_update)
                                                .add(jRadioButton_read)
                                                .add(jRadioButton_delete))
                                            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                            .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                                .add(org.jdesktop.layout.GroupLayout.TRAILING, jLabel4)
                                                .add(org.jdesktop.layout.GroupLayout.TRAILING, jLabel17)
                                                .add(org.jdesktop.layout.GroupLayout.TRAILING, jLabel3)))
                                        .add(layout.createSequentialGroup()
                                            .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                                .add(layout.createSequentialGroup()
                                                    .add(jRadioButton_create)
                                                    .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                                .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                                                    .add(jRadioButton_create2)
                                                    .add(136, 136, 136)))
                                            .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                                .add(jLabel13)
                                                .add(jLabel10)))
                                        .add(org.jdesktop.layout.GroupLayout.LEADING, jRadioButton_delete2))))
                            .add(layout.createSequentialGroup()
                                .add(18, 18, 18)
                                .add(jLabel1)
                                .add(179, 179, 179)
                                .add(jLabel2)))
                        .add(0, 0, 0)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jTextField_category)
                            .add(org.jdesktop.layout.GroupLayout.TRAILING, jTextField_gradebook2)
                            .add(jTextField_feedback)
                            .add(layout.createSequentialGroup()
                                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                    .add(jTextField_itemId, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 201, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                    .add(jTextField_studentId, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 201, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                    .add(jTextField_score, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 203, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                                .add(0, 0, Short.MAX_VALUE))
                            .add(jTextField_gradebook1)))
                    .add(org.jdesktop.layout.GroupLayout.LEADING, layout.createSequentialGroup()
                        .add(54, 54, 54)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(layout.createSequentialGroup()
                                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                                    .add(jLabel6)
                                    .add(jLabel18)
                                    .add(jLabel7))
                                .add(18, 18, 18)
                                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                    .add(jTextField5)
                                    .add(jScrollPane1)
                                    .add(layout.createSequentialGroup()
                                        .add(jTextField_date, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 144, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                        .add(18, 18, 18)
                                        .add(jLabel5)
                                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                                        .add(jTextField3, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 86, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                        .add(18, 18, 18)
                                        .add(jLabel9)
                                        .add(18, 18, 18)
                                        .add(jTextField6, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 128, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                        .add(0, 0, Short.MAX_VALUE))))
                            .add(layout.createSequentialGroup()
                                .add(jLabel_help)
                                .add(18, 18, 18)
                                .add(jTextField_help)))))
                .add(40, 40, 40))
            .add(layout.createSequentialGroup()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createSequentialGroup()
                        .add(274, 274, 274)
                        .add(jLabel8))
                    .add(layout.createSequentialGroup()
                        .add(49, 49, 49)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(layout.createSequentialGroup()
                                .add(jLabel12)
                                .add(190, 190, 190)
                                .add(jLabel16))
                            .add(layout.createSequentialGroup()
                                .add(20, 20, 20)
                                .add(jToggleButton_req)
                                .add(18, 18, 18)
                                .add(jToggleButton_format))))
                    .add(layout.createSequentialGroup()
                        .add(253, 253, 253)
                        .add(jLabel14)))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(jLabel14, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 45, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(30, 30, 30)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jToggleButton_req)
                    .add(jToggleButton_format))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel12)
                    .add(jLabel16))
                .add(18, 18, 18)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jRadioButton_create2)
                    .add(jLabel13)
                    .add(jTextField_gradebook1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(1, 1, 1)
                .add(jRadioButton_read2)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jRadioButton_delete2)
                .add(29, 29, 29)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel1)
                    .add(jLabel2))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jRadioButton_create)
                    .add(jLabel10)
                    .add(jTextField_gradebook2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jRadioButton_read)
                    .add(jLabel4)
                    .add(jTextField_category, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jRadioButton_update)
                    .add(jTextField_itemId, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel17))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jRadioButton_delete)
                    .add(jLabel3)
                    .add(jTextField_studentId, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel11)
                    .add(jTextField_score, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jTextField_feedback, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel15))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(jButton1)
                .add(20, 20, 20)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel_help)
                    .add(jTextField_help, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(30, 30, 30)
                .add(jLabel8)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel5)
                    .add(jTextField3, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel9)
                    .add(jTextField6, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jTextField_date, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 26, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel6))
                .add(18, 18, 18)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel7)
                    .add(jTextField5, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createSequentialGroup()
                        .add(jLabel18)
                        .add(24, 24, 24))
                    .add(layout.createSequentialGroup()
                        .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 103, Short.MAX_VALUE)
                        .add(30, 30, 30))))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jRadioButton_createActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButton_createActionPerformed
        LOG.info("Selecting radio button {}", jRadioButton_create.getText());
        this.textFieldOperator(false, true, true, false, true, true, true);
    }//GEN-LAST:event_jRadioButton_createActionPerformed

    private void jRadioButton_readActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButton_readActionPerformed
        LOG.info("Selecting radio button {}", jRadioButton_read.getText());
        this.textFieldOperator(false, true, true, true, true, false, false);
    }//GEN-LAST:event_jRadioButton_readActionPerformed
      
    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        LOG.info("Invoking REST Client based on selection");
        
        String gradebook = jTextField_gradebook2.getText();
        itemID = jTextField_itemId.getText();
        studentID = jTextField_studentId.getText();
        String category = jTextField_category.getText();
        marks = jTextField_score.getText();
        
        if (jRadioButton_create.isSelected()){
            if (this.checkFormat(false, true, true)) {
                LOG.debug("Invoking {} action", jRadioButton_create.getText());//Create
            
                ClientResponse clientResponse = gradebook_CRUD_rest_client.createGradedItem(this.convertFormToJSONString(), gradebook, category);

                resourceURI = clientResponse.getLocation();
                LOG.debug("Retrieved location {}", resourceURI);

                this.populateForm(clientResponse);
            }        

        } else if (jRadioButton_read.isSelected()) {
            if (this.checkFormat(true, true, false)) { 
                LOG.debug("Invoking {} action", jRadioButton_read.getText());// Read

                ClientResponse clientResponse = gradebook_CRUD_rest_client.retrieveGradedItem(ClientResponse.class, gradebook, category, itemID, studentID);

                this.populateForm(clientResponse);  
            }
        } else if (jRadioButton_update.isSelected()) {
            if (this.checkFormat(true, true, true)) {
                LOG.debug("Invoking {} action", jRadioButton_update.getText());//Update

                ClientResponse clientResponse = gradebook_CRUD_rest_client.updateGradedItem(this.convertFormToJSONString(), gradebook, category, itemID, studentID);

                this.populateForm(clientResponse);
            }
        } else if (jRadioButton_delete.isSelected()) {
            if (this.checkFormat(true, true, false)) {
                LOG.debug("Invoking {} action", jRadioButton_delete.getText());//Delete

                ClientResponse clientResponse = gradebook_CRUD_rest_client.deleteGradedItem(gradebook, category, itemID, studentID);
                this.populateForm(clientResponse, false);
            }
        } else if (jRadioButton_create2.isSelected()){
            LOG.debug("Invoking {} action", jRadioButton_create2.getText());//Create
            
            Gradebook gradebookObj = new Gradebook(jTextField_gradebook1.getText());
            
            ClientResponse clientResponse = gradebook_CRUD_rest_client.createGradebook(Converter.convertFromObjectToJSON(gradebookObj, Gradebook.class));
            
            resourceURI = clientResponse.getLocation();
            LOG.debug("Retrieved location {}", resourceURI);
            
            this.populateForm(clientResponse, false);
        } else if (jRadioButton_read2.isSelected()) {
            LOG.debug("Invoking {} action", jRadioButton_read2.getText());//Read
            
            ClientResponse clientResponse = gradebook_CRUD_rest_client.retrieveGradedbook(ClientResponse.class, jTextField_gradebook1.getText());
            this.populateForm(clientResponse, false);
        } else if (jRadioButton_delete2.isSelected()) {
            LOG.debug("Invoking {} action", jRadioButton_delete2.getText());//Delete
            
            ClientResponse clientResponse = gradebook_CRUD_rest_client.deleteGradedbook(jTextField_gradebook1.getText());
            this.populateForm(clientResponse, false);
        }
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jTextField_studentIdActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField_studentIdActionPerformed
        LOG.info("Selecting text field {}", jTextField_studentId.getText());
    }//GEN-LAST:event_jTextField_studentIdActionPerformed

    private void jRadioButton_updateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButton_updateActionPerformed
        LOG.info("Selecting radio button {}", jRadioButton_update.getText());
        this.textFieldOperator(false, true, true, true, true, true, true);
    }//GEN-LAST:event_jRadioButton_updateActionPerformed

    private void jRadioButton_deleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButton_deleteActionPerformed
        LOG.info("Selecting radio button {}", jRadioButton_delete.getText());
        this.textFieldOperator(false, true, true, true, true, false, false);
    }//GEN-LAST:event_jRadioButton_deleteActionPerformed

    private void jTextField_categoryActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField_categoryActionPerformed
        LOG.info("Selecting text field {}", jTextField_category.getText());
    }//GEN-LAST:event_jTextField_categoryActionPerformed

    private void jTextField_itemIdActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField_itemIdActionPerformed
        LOG.info("Selecting text field {}", jTextField_itemId.getText());
    }//GEN-LAST:event_jTextField_itemIdActionPerformed

    private void jTextField3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField3ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField3ActionPerformed

    private void jTextField5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField5ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField5ActionPerformed

    private void jTextField6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField6ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField6ActionPerformed

    private void jRadioButton_create2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButton_create2ActionPerformed
        LOG.info("Selecting radio button {}", jRadioButton_create2.getText());
        this.textFieldOperator(true, false, false, false, false, false, false);
    }//GEN-LAST:event_jRadioButton_create2ActionPerformed

    private void jRadioButton_read2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButton_read2ActionPerformed
        LOG.info("Selecting radio button {}", jRadioButton_read2.getText());
        this.textFieldOperator(true, false, false, false, false, false, false);
    }//GEN-LAST:event_jRadioButton_read2ActionPerformed

    private void jRadioButton_delete2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButton_delete2ActionPerformed
        LOG.info("Selecting radio button {}", jRadioButton_delete2.getText());
        this.textFieldOperator(true, false, false, false, false, false, false);
    }//GEN-LAST:event_jRadioButton_delete2ActionPerformed

    private void jToggleButton_reqActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jToggleButton_reqActionPerformed
        if(this.enableDiableFlag){
            this.textFieldOperator(true, true, true, true, true, true, true);
            this.enableDiableFlag = false;
        }else {
            this.enableDiableFlag = true;
        }
        jTextField_help.setForeground(new java.awt.Color(0, 255, 0));
        jTextField_help.setText("Now click on any Action button to see effect");
    }//GEN-LAST:event_jToggleButton_reqActionPerformed

    private void jToggleButton_formatActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jToggleButton_formatActionPerformed
        if (this.formatCheckingFlag){
            this.formatCheckingFlag = false;
        } else {
            this.formatCheckingFlag = true;
        }
    }//GEN-LAST:event_jToggleButton_formatActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Gradebook_REST_UI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Gradebook_REST_UI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Gradebook_REST_UI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Gradebook_REST_UI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Gradebook_REST_UI().setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JLabel jLabel_help;
    private javax.swing.JRadioButton jRadioButton_create;
    private javax.swing.JRadioButton jRadioButton_create2;
    private javax.swing.JRadioButton jRadioButton_delete;
    private javax.swing.JRadioButton jRadioButton_delete2;
    private javax.swing.JRadioButton jRadioButton_read;
    private javax.swing.JRadioButton jRadioButton_read2;
    private javax.swing.JRadioButton jRadioButton_update;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextField jTextField3;
    private javax.swing.JTextField jTextField5;
    private javax.swing.JTextField jTextField6;
    private javax.swing.JTextField jTextField_category;
    private javax.swing.JTextField jTextField_date;
    private javax.swing.JTextField jTextField_feedback;
    private javax.swing.JTextField jTextField_gradebook1;
    private javax.swing.JTextField jTextField_gradebook2;
    private javax.swing.JTextField jTextField_help;
    private javax.swing.JTextField jTextField_itemId;
    private javax.swing.JTextField jTextField_score;
    private javax.swing.JTextField jTextField_studentId;
    private javax.swing.JTextPane jTextPane_response;
    private javax.swing.JToggleButton jToggleButton_format;
    private javax.swing.JToggleButton jToggleButton_req;
    // End of variables declaration//GEN-END:variables
    private String studentID;
    private String itemID;
    private String marks;
    private boolean enableDiableFlag;
    private boolean formatCheckingFlag;
}
