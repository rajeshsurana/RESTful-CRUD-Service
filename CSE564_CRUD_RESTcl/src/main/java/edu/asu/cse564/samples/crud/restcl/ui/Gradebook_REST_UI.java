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
import edu.asu.cse564.samples.crud.jaxb.model.Student;
import edu.asu.cse564.samples.crud.jaxb.utils.Converter;
import edu.asu.cse564.samples.crud.restcl.GradeBook_CRUD_cl;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Random;
import javax.swing.JOptionPane;

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
    public Gradebook_REST_UI(String title) {
        LOG.info("Creating a Appointment_REST_UI object");
        initComponents();
        
        gradebook_CRUD_rest_client = new GradeBook_CRUD_cl();
        jRadioButton_create_gradebook.setSelected(true);
        this.enableDiableFlag = true;
        this.formatCheckingFlag = true;
        super.setTitle(title);
        super.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        this.textFieldOperator(true, false, false, false, false, false);
        super.addWindowListener( new WindowAdapter()
        {
            @Override
            public void windowClosing(WindowEvent e)
            {
                JFrame frame = (JFrame)e.getSource();

                int result = JOptionPane.showConfirmDialog(
                    frame,
                    "Are you sure you want to exit the application?",
                    "Exit Application",
                    JOptionPane.YES_NO_OPTION);

                if (result == JOptionPane.YES_OPTION){
                    gradebook_CRUD_rest_client.close();
                    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                }
            }
        });
    }
    
    private String convertFormToJSONString(boolean stu){
        GradedItem gradedItem = new GradedItem();
        String jsonString = null;
         
        
        
        if(stu){
            Student student = new Student();
            if (!jTextField_studentId.getText().trim().equals("")){
                student.setStudentId(Integer.parseInt(jTextField_studentId.getText()));
            }

            if (!jTextField_score.getText().trim().equals("")){
                student.setScore(Float.parseFloat(jTextField_score.getText()));
            }

            if (!jTextField_feedback.getText().trim().equals("")){
                student.setFeedback(jTextField_feedback.getText());
            }
            jsonString = Converter.convertFromObjectToJSON(student, student.getClass());
            //gradedItem.addStudent(student);
        } else {
            if (!jTextField_category.getText().trim().equals("")){
            gradedItem.setCategory(jTextField_category.getText());
            }

            if (!jTextField_itemId.getText().trim().equals("")){
                gradedItem.setId(Integer.parseInt(jTextField_itemId.getText()));
            }
            jsonString = Converter.convertFromObjectToJSON(gradedItem, gradedItem.getClass());
        }
        return jsonString;
    }
    
    private void populateForm(ClientResponse clientResponse){
        this.populateForm(clientResponse, true);
    }
    
    private void populateForm(ClientResponse clientResponse, boolean populateStudent ){
        LOG.info("Populating the UI with the Graded Item info");
        LOG.info("Response status code = {}", clientResponse.getStatus());
        String entity=null;
        if(!(clientResponse.getStatus() == Response.Status.NO_CONTENT.getStatusCode())){
            
            try{
                entity = clientResponse.getEntity(String.class);
                jTextPane_response.setText(entity);
                // Populate HTTP Header Information
                jTextField_media.setText(clientResponse.getType().toString());

            } catch(Exception e){
                e.printStackTrace();
            }
        }else{
            jTextPane_response.setText("");
            jTextField_media.setText("");
        }
        try{
            if (((clientResponse.getStatus() == Response.Status.OK.getStatusCode()) ||
                (clientResponse.getStatus() == Response.Status.CREATED.getStatusCode()))&&
                 populateStudent){
                
                LOG.debug("The Client Response entity is {}", entity);
                Student student = (Student)Converter.convertFromJsonToObject(entity, Student.class);
                LOG.debug("The Client Response student object is {}", student);
                
                // Populate Student info
                jTextField_studentId.setText(Integer.toString(student.getStudentId()));
                jTextField_score.setText(Float.toString(student.getScore()));
                jTextField_feedback.setText(student.getFeedback());
            } else {
                //jTextField_category.setText("");
                //jTextField_itemId.setText("");
                //jTextField_studentId.setText("");
                jTextField_score.setText("");
                jTextField_feedback.setText("");
            }
           
            
            // The Location filed is only populated when a Resource is created
            if ((clientResponse.getStatus() == Response.Status.CREATED.getStatusCode()) ||
                    (clientResponse.getStatus() == Response.Status.NO_CONTENT.getStatusCode())){
                jTextField_location.setText(clientResponse.getLocation().toString());
            } else {
                jTextField_location.setText("");
            }
            jTextField_code.setText(Integer.toString(clientResponse.getStatus()));
            
            jTextField_phrase.setText(clientResponse.getResponseStatus().getReasonPhrase());
            
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
    
    private boolean checkFormat(boolean item, boolean student, boolean scoref){
        if (!this.formatCheckingFlag){
            return true;
        }
        
        if(gradebook == null || gradebook.trim().equals("")){
            jTextField_help.setForeground(new java.awt.Color(255, 0, 0));
            jTextField_help.setText("Gradebook Name can not be only whitespaces");
            return false;
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
        if(scoref){
            if(!this.isFloat(score)){
                jTextField_help.setForeground(new java.awt.Color(255, 0, 0));
                jTextField_help.setText("Score must in Integer/Float format");
                return false;
            }
        }
        Random randomcolor = new Random();
        jTextField_help.setForeground(new java.awt.Color(randomcolor.nextInt(100), 255 - randomcolor.nextInt(40), randomcolor.nextInt(100)));
        jTextField_help.setText("Request sent");
        return true;
    }
    
    
    private void textFieldOperator(boolean gradebook, 
                                   boolean category,
                                   boolean itemId,
                                   boolean studentId,
                                   boolean score,
                                   boolean feedback){
        if(!enableDiableFlag)
            return;

        if(gradebook){
            jTextField_gradebook.setEnabled(true);
        }else{
            //jTextField_gradebook.setText("");
            jTextField_gradebook.setEnabled(false);
        }
        if(category){
            jTextField_category.setEnabled(true);
        }else{
            //jTextField_category.setText("");
            jTextField_category.setEnabled(false);
        }
        if(itemId){
            jTextField_itemId.setEnabled(true);
        }else{
            //jTextField_itemId.setText("");
            jTextField_itemId.setEnabled(false);
        }
        if(studentId){
            jTextField_studentId.setEnabled(true);
        }else{
            //jTextField_studentId.setText("");
            jTextField_studentId.setEnabled(false);
        }
        if(score){
            jTextField_score.setEnabled(true);
        }else{
            //jTextField_score.setText("");
            jTextField_score.setEnabled(false);
        }
        if(feedback){
            jTextField_feedback.setEnabled(true);
        }else{
            //jTextField_feedback.setText("");
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
        jRadioButton_create_student = new javax.swing.JRadioButton();
        jRadioButton_read_student = new javax.swing.JRadioButton();
        jRadioButton_update_student = new javax.swing.JRadioButton();
        jRadioButton_delete_student = new javax.swing.JRadioButton();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jTextField_studentId = new javax.swing.JTextField();
        jTextField_category = new javax.swing.JTextField();
        jTextField_itemId = new javax.swing.JTextField();
        jButton1 = new javax.swing.JButton();
        jLabel5 = new javax.swing.JLabel();
        jTextField_code = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jTextField_location = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        jTextField_media = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jTextField_score = new javax.swing.JTextField();
        jRadioButton_create_gradebook = new javax.swing.JRadioButton();
        jTextField_gradebook = new javax.swing.JTextField();
        jLabel14 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jTextField_feedback = new javax.swing.JTextField();
        jLabel17 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jTextField_phrase = new javax.swing.JTextField();
        jRadioButton_delete_gradebook = new javax.swing.JRadioButton();
        jLabel18 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextPane_response = new javax.swing.JTextPane();
        jRadioButton_read_gradebook = new javax.swing.JRadioButton();
        jLabel_help = new javax.swing.JLabel();
        jTextField_help = new javax.swing.JTextField();
        jToggleButton_req = new javax.swing.JToggleButton();
        jToggleButton_format = new javax.swing.JToggleButton();
        jRadioButton_create_item = new javax.swing.JRadioButton();
        jRadioButton_read_item = new javax.swing.JRadioButton();
        jRadioButton_delete_item = new javax.swing.JRadioButton();
        jLabel20 = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        jSeparator2 = new javax.swing.JSeparator();
        jLabel12 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel1.setFont(new java.awt.Font("Lucida Grande", 1, 18)); // NOI18N
        jLabel1.setText("Input Fields");

        buttonGroup1.add(jRadioButton_create_student);
        jRadioButton_create_student.setText("Create");
        jRadioButton_create_student.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButton_create_studentActionPerformed(evt);
            }
        });

        buttonGroup1.add(jRadioButton_read_student);
        jRadioButton_read_student.setText("Read");
        jRadioButton_read_student.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButton_read_studentActionPerformed(evt);
            }
        });

        buttonGroup1.add(jRadioButton_update_student);
        jRadioButton_update_student.setText("Update");
        jRadioButton_update_student.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButton_update_studentActionPerformed(evt);
            }
        });

        buttonGroup1.add(jRadioButton_delete_student);
        jRadioButton_delete_student.setText("Delete");
        jRadioButton_delete_student.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButton_delete_studentActionPerformed(evt);
            }
        });

        jLabel2.setFont(new java.awt.Font("Lucida Grande", 1, 13)); // NOI18N
        jLabel2.setText("Student");

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

        jTextField_code.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField_codeActionPerformed(evt);
            }
        });

        jLabel7.setText("Location");

        jLabel8.setFont(new java.awt.Font("Lucida Grande", 1, 13)); // NOI18N
        jLabel8.setText("HTTP Header Info");

        jTextField_location.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField_locationActionPerformed(evt);
            }
        });

        jLabel9.setText("Media Type");

        jTextField_media.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField_mediaActionPerformed(evt);
            }
        });

        jLabel10.setText("Gradebook");

        jLabel11.setText("Score");

        buttonGroup1.add(jRadioButton_create_gradebook);
        jRadioButton_create_gradebook.setText("Create");
        jRadioButton_create_gradebook.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButton_create_gradebookActionPerformed(evt);
            }
        });

        jLabel14.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        jLabel14.setForeground(new java.awt.Color(0, 51, 153));
        jLabel14.setText("Restful-CRUD Client");

        jLabel16.setFont(new java.awt.Font("Lucida Grande", 1, 13)); // NOI18N
        jLabel16.setText("Gradebook");

        jLabel15.setText("Feedback");

        jLabel17.setText("Item id");

        jLabel6.setText("Status");

        buttonGroup1.add(jRadioButton_delete_gradebook);
        jRadioButton_delete_gradebook.setText("Delete");
        jRadioButton_delete_gradebook.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButton_delete_gradebookActionPerformed(evt);
            }
        });

        jLabel18.setText("Response");

        jScrollPane1.setViewportView(jTextPane_response);

        buttonGroup1.add(jRadioButton_read_gradebook);
        jRadioButton_read_gradebook.setText("Read");
        jRadioButton_read_gradebook.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButton_read_gradebookActionPerformed(evt);
            }
        });

        jLabel_help.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        jLabel_help.setText("Help:");

        jTextField_help.setEditable(false);
        jTextField_help.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        jTextField_help.setForeground(new java.awt.Color(255, 51, 51));

        jToggleButton_req.setText("Enable all fields for all Actions");
        jToggleButton_req.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButton_reqActionPerformed(evt);
            }
        });

        jToggleButton_format.setText("Disable format checking for input fields");
        jToggleButton_format.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButton_formatActionPerformed(evt);
            }
        });

        buttonGroup1.add(jRadioButton_create_item);
        jRadioButton_create_item.setText("Create");
        jRadioButton_create_item.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButton_create_itemActionPerformed(evt);
            }
        });

        buttonGroup1.add(jRadioButton_read_item);
        jRadioButton_read_item.setText("Read");
        jRadioButton_read_item.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButton_read_itemActionPerformed(evt);
            }
        });

        buttonGroup1.add(jRadioButton_delete_item);
        jRadioButton_delete_item.setText("Delete");
        jRadioButton_delete_item.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButton_delete_itemActionPerformed(evt);
            }
        });

        jLabel20.setFont(new java.awt.Font("Lucida Grande", 1, 13)); // NOI18N
        jLabel20.setText("Graded Item");

        jLabel12.setFont(new java.awt.Font("Lucida Grande", 1, 18)); // NOI18N
        jLabel12.setText("Action");

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(54, 54, 54)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createSequentialGroup()
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                            .add(jLabel6)
                            .add(jLabel18)
                            .add(jLabel7))
                        .add(18, 18, 18)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jTextField_location)
                            .add(jScrollPane1)
                            .add(layout.createSequentialGroup()
                                .add(jTextField_phrase, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 144, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .add(18, 18, 18)
                                .add(jLabel5)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                                .add(jTextField_code, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 86, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .add(18, 18, 18)
                                .add(jLabel9)
                                .add(18, 18, 18)
                                .add(jTextField_media, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 128, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .add(0, 105, Short.MAX_VALUE))))
                    .add(layout.createSequentialGroup()
                        .add(jLabel_help)
                        .add(18, 18, 18)
                        .add(jTextField_help)))
                .add(40, 40, 40))
            .add(layout.createSequentialGroup()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createSequentialGroup()
                        .add(117, 117, 117)
                        .add(jToggleButton_req)
                        .add(88, 88, 88)
                        .add(jToggleButton_format))
                    .add(layout.createSequentialGroup()
                        .add(40, 40, 40)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                            .add(jLabel16)
                            .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                .add(jRadioButton_create_gradebook)
                                .add(jRadioButton_read_gradebook)
                                .add(jRadioButton_delete_gradebook)))
                        .add(18, 18, 18)
                        .add(jSeparator2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(layout.createSequentialGroup()
                                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                    .add(jRadioButton_read_item)
                                    .add(jRadioButton_create_item)
                                    .add(jRadioButton_delete_item))
                                .add(20, 20, 20)
                                .add(jSeparator1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                            .add(jLabel20)
                            .add(jButton1))
                        .add(18, 18, 18)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jRadioButton_create_student)
                            .add(jRadioButton_update_student)
                            .add(jRadioButton_read_student, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 88, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(jRadioButton_delete_student)
                            .add(layout.createSequentialGroup()
                                .add(9, 9, 9)
                                .add(jLabel2)))
                        .add(58, 58, 58)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(org.jdesktop.layout.GroupLayout.TRAILING, jLabel17)
                            .add(org.jdesktop.layout.GroupLayout.TRAILING, jLabel3)
                            .add(org.jdesktop.layout.GroupLayout.TRAILING, jLabel4)
                            .add(org.jdesktop.layout.GroupLayout.TRAILING, jLabel11)
                            .add(org.jdesktop.layout.GroupLayout.TRAILING, jLabel15)
                            .add(org.jdesktop.layout.GroupLayout.TRAILING, jLabel10))
                        .add(30, 30, 30)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                                .add(org.jdesktop.layout.GroupLayout.LEADING, jTextField_feedback, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 272, Short.MAX_VALUE)
                                .add(org.jdesktop.layout.GroupLayout.LEADING, jTextField_gradebook)
                                .add(org.jdesktop.layout.GroupLayout.LEADING, jTextField_category))
                            .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                                .add(org.jdesktop.layout.GroupLayout.LEADING, jTextField_score, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 113, Short.MAX_VALUE)
                                .add(org.jdesktop.layout.GroupLayout.LEADING, jTextField_studentId)
                                .add(org.jdesktop.layout.GroupLayout.LEADING, jTextField_itemId))))
                    .add(layout.createSequentialGroup()
                        .add(385, 385, 385)
                        .add(jLabel8))
                    .add(layout.createSequentialGroup()
                        .add(308, 308, 308)
                        .add(jLabel14))
                    .add(layout.createSequentialGroup()
                        .add(159, 159, 159)
                        .add(jLabel12)
                        .add(365, 365, 365)
                        .add(jLabel1)))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(jLabel14, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 45, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(25, 25, 25)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jToggleButton_req)
                    .add(jToggleButton_format))
                .add(33, 33, 33)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel1)
                    .add(jLabel12))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createSequentialGroup()
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(jLabel2)
                            .add(jLabel20)
                            .add(jLabel16))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jSeparator1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 154, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(layout.createSequentialGroup()
                                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                    .add(jRadioButton_create_student)
                                    .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                                        .add(jRadioButton_create_item)
                                        .add(jRadioButton_create_gradebook)))
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                                    .add(jRadioButton_read_student)
                                    .add(jRadioButton_read_item)
                                    .add(jRadioButton_read_gradebook))
                                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                    .add(layout.createSequentialGroup()
                                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                        .add(jRadioButton_update_student)
                                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                        .add(jRadioButton_delete_student))
                                    .add(layout.createSequentialGroup()
                                        .add(33, 33, 33)
                                        .add(jRadioButton_delete_item))
                                    .add(layout.createSequentialGroup()
                                        .add(34, 34, 34)
                                        .add(jRadioButton_delete_gradebook))))
                            .add(jSeparator2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 154, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                    .add(layout.createSequentialGroup()
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(jLabel10)
                            .add(jTextField_gradebook, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(jLabel4)
                            .add(jTextField_category, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(jTextField_itemId, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(jLabel17))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(jLabel3)
                            .add(jTextField_studentId, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(jLabel11)
                            .add(jTextField_score, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(jTextField_feedback, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(jLabel15))))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jButton1)
                .add(20, 20, 20)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel_help)
                    .add(jTextField_help, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(28, 28, 28)
                .add(jLabel8)
                .add(18, 18, 18)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel5)
                    .add(jTextField_code, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel9)
                    .add(jTextField_media, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jTextField_phrase, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 26, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel6))
                .add(18, 18, 18)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel7)
                    .add(jTextField_location, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createSequentialGroup()
                        .add(jLabel18)
                        .add(24, 24, 24))
                    .add(layout.createSequentialGroup()
                        .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 123, Short.MAX_VALUE)
                        .add(30, 30, 30))))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jRadioButton_create_studentActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButton_create_studentActionPerformed
        LOG.info("Selecting radio button {}", jRadioButton_create_student.getText());
        this.textFieldOperator(true, true, true, true, true, true);
    }//GEN-LAST:event_jRadioButton_create_studentActionPerformed

    private void jRadioButton_read_studentActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButton_read_studentActionPerformed
        LOG.info("Selecting radio button {}", jRadioButton_read_student.getText());
        this.textFieldOperator(true, true, true, true, true, true);
    }//GEN-LAST:event_jRadioButton_read_studentActionPerformed
      
    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        LOG.info("Invoking REST Client based on selection");
        
        gradebook = jTextField_gradebook.getText().trim();
        itemID = jTextField_itemId.getText().trim();
        studentID = jTextField_studentId.getText().trim();
        String category = jTextField_category.getText().trim();
        score = jTextField_score.getText().trim();
        
        if (jRadioButton_create_student.isSelected()){
            if (this.checkFormat(true, true, true)) {
                LOG.debug("Invoking {} action", jRadioButton_create_student.getText());//Create
            
                ClientResponse clientResponse = gradebook_CRUD_rest_client.createStudent(this.convertFormToJSONString(true), gradebook, category, itemID);

                resourceURI = clientResponse.getLocation();
                LOG.debug("Retrieved location {}", resourceURI);

                this.populateForm(clientResponse);
            }        

        } else if (jRadioButton_read_student.isSelected()) {
            if (this.checkFormat(true, true, false)) { 
                LOG.debug("Invoking {} action", jRadioButton_read_student.getText());// Read

                ClientResponse clientResponse = gradebook_CRUD_rest_client.retrieveStudent(ClientResponse.class, gradebook, category, itemID, studentID);

                this.populateForm(clientResponse);  
            }
        } else if (jRadioButton_update_student.isSelected()) {
            if (this.checkFormat(true, true, true)) {
                LOG.debug("Invoking {} action", jRadioButton_update_student.getText());//Update

                ClientResponse clientResponse = gradebook_CRUD_rest_client.updateStudent(this.convertFormToJSONString(true), gradebook, category, itemID, studentID);

                this.populateForm(clientResponse);
            }
        } else if (jRadioButton_delete_student.isSelected()) {
            if (this.checkFormat(true, true, false)) {
                LOG.debug("Invoking {} action", jRadioButton_delete_student.getText());//Delete

                ClientResponse clientResponse = gradebook_CRUD_rest_client.deleteStudent(gradebook, category, itemID, studentID);
                this.populateForm(clientResponse);
            }
        } else if (jRadioButton_create_gradebook.isSelected()){
            if (this.checkFormat(false, false, false)) { 
                LOG.debug("Invoking {} action", jRadioButton_create_gradebook.getText());//Create
            
                Gradebook gradebookObj = new Gradebook(jTextField_gradebook.getText());

                ClientResponse clientResponse = gradebook_CRUD_rest_client.createGradebook(Converter.convertFromObjectToJSON(gradebookObj, Gradebook.class));

                resourceURI = clientResponse.getLocation();
                LOG.debug("Retrieved location {}", resourceURI);

                this.populateForm(clientResponse, false);
            }  
        } else if (jRadioButton_read_gradebook.isSelected()) {
            if (this.checkFormat(false, false, false)) { 
                LOG.debug("Invoking {} action", jRadioButton_read_gradebook.getText());//Read
            
                ClientResponse clientResponse = gradebook_CRUD_rest_client.retrieveGradedbook(ClientResponse.class, gradebook);
                this.populateForm(clientResponse, false);
            }
        } else if (jRadioButton_delete_gradebook.isSelected()) {
            if (this.checkFormat(false, false, false)) { 
                LOG.debug("Invoking {} action", jRadioButton_delete_gradebook.getText());//Delete
            
                ClientResponse clientResponse = gradebook_CRUD_rest_client.deleteGradedbook(jTextField_gradebook.getText());
                this.populateForm(clientResponse, false);
            }    
        } else if (jRadioButton_create_item.isSelected()){
            if (this.checkFormat(true, false, false)) { 
                LOG.debug("Invoking {} action", jRadioButton_create_item.getText());//Create
                GradedItem gradedItemObj = new GradedItem();
                gradedItemObj.setCategory(jTextField_category.getText());
                gradedItemObj.setId(Integer.parseInt(jTextField_itemId.getText()));
                ClientResponse clientResponse = gradebook_CRUD_rest_client.createGradedItem(Converter.convertFromObjectToJSON(gradedItemObj, GradedItem.class), gradebook);

                resourceURI = clientResponse.getLocation();
                LOG.debug("Retrieved location {}", resourceURI);

                this.populateForm(clientResponse, false);
            }       
        } else if (jRadioButton_read_item.isSelected()) {
            if (this.checkFormat(true, false, false)) { 
                LOG.debug("Invoking {} action", jRadioButton_read_item.getText());//Read

                ClientResponse clientResponse = gradebook_CRUD_rest_client.retrieveGradedItem(ClientResponse.class, gradebook, category, itemID);
                this.populateForm(clientResponse, false);
            }
        } else if (jRadioButton_delete_item.isSelected()) {
            if (this.checkFormat(true, false, false)) { 
                LOG.debug("Invoking {} action", jRadioButton_delete_item.getText());//Delete

                ClientResponse clientResponse = gradebook_CRUD_rest_client.deleteGradedItem(gradebook, category, itemID);
                this.populateForm(clientResponse, false);
            }  
        }
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jTextField_studentIdActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField_studentIdActionPerformed
        LOG.info("Selecting text field {}", jTextField_studentId.getText());
    }//GEN-LAST:event_jTextField_studentIdActionPerformed

    private void jRadioButton_update_studentActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButton_update_studentActionPerformed
        LOG.info("Selecting radio button {}", jRadioButton_update_student.getText());
        this.textFieldOperator(true, true, true, true, true, true);
    }//GEN-LAST:event_jRadioButton_update_studentActionPerformed

    private void jRadioButton_delete_studentActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButton_delete_studentActionPerformed
        LOG.info("Selecting radio button {}", jRadioButton_delete_student.getText());
        this.textFieldOperator(true, true, true, true, true, true);
    }//GEN-LAST:event_jRadioButton_delete_studentActionPerformed

    private void jTextField_categoryActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField_categoryActionPerformed
        LOG.info("Selecting text field {}", jTextField_category.getText());
    }//GEN-LAST:event_jTextField_categoryActionPerformed

    private void jTextField_itemIdActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField_itemIdActionPerformed
        LOG.info("Selecting text field {}", jTextField_itemId.getText());
    }//GEN-LAST:event_jTextField_itemIdActionPerformed

    private void jTextField_codeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField_codeActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField_codeActionPerformed

    private void jTextField_locationActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField_locationActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField_locationActionPerformed

    private void jTextField_mediaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField_mediaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField_mediaActionPerformed

    private void jRadioButton_create_gradebookActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButton_create_gradebookActionPerformed
        LOG.info("Selecting radio button {}", jRadioButton_create_gradebook.getText());
        this.textFieldOperator(true, false, false, false, false, false);
    }//GEN-LAST:event_jRadioButton_create_gradebookActionPerformed

    private void jRadioButton_read_gradebookActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButton_read_gradebookActionPerformed
        LOG.info("Selecting radio button {}", jRadioButton_read_gradebook.getText());
        this.textFieldOperator(true, false, false, false, false, false);
    }//GEN-LAST:event_jRadioButton_read_gradebookActionPerformed

    private void jRadioButton_delete_gradebookActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButton_delete_gradebookActionPerformed
        LOG.info("Selecting radio button {}", jRadioButton_delete_gradebook.getText());
        this.textFieldOperator(true, false, false, false, false, false);
    }//GEN-LAST:event_jRadioButton_delete_gradebookActionPerformed

    private void jToggleButton_reqActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jToggleButton_reqActionPerformed
        if(this.enableDiableFlag){
            this.textFieldOperator(true, true, true, true, true, true);
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

    private void jRadioButton_create_itemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButton_create_itemActionPerformed
        LOG.info("Selecting radio button {}", jRadioButton_create_item.getText());
        this.textFieldOperator(true, true, true, false, false, false);
    }//GEN-LAST:event_jRadioButton_create_itemActionPerformed

    private void jRadioButton_read_itemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButton_read_itemActionPerformed
        LOG.info("Selecting radio button {}", jRadioButton_read_item.getText());
        this.textFieldOperator(true, true, true, false, false, false);
    }//GEN-LAST:event_jRadioButton_read_itemActionPerformed

    private void jRadioButton_delete_itemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButton_delete_itemActionPerformed
        LOG.info("Selecting radio button {}", jRadioButton_delete_item.getText());
        this.textFieldOperator(true, true, true, false, false, false);
    }//GEN-LAST:event_jRadioButton_delete_itemActionPerformed

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
                new Gradebook_REST_UI("RESTful-CRUD Client").setVisible(true);
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
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JLabel jLabel_help;
    private javax.swing.JRadioButton jRadioButton_create_gradebook;
    private javax.swing.JRadioButton jRadioButton_create_item;
    private javax.swing.JRadioButton jRadioButton_create_student;
    private javax.swing.JRadioButton jRadioButton_delete_gradebook;
    private javax.swing.JRadioButton jRadioButton_delete_item;
    private javax.swing.JRadioButton jRadioButton_delete_student;
    private javax.swing.JRadioButton jRadioButton_read_gradebook;
    private javax.swing.JRadioButton jRadioButton_read_item;
    private javax.swing.JRadioButton jRadioButton_read_student;
    private javax.swing.JRadioButton jRadioButton_update_student;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JTextField jTextField_category;
    private javax.swing.JTextField jTextField_code;
    private javax.swing.JTextField jTextField_feedback;
    private javax.swing.JTextField jTextField_gradebook;
    private javax.swing.JTextField jTextField_help;
    private javax.swing.JTextField jTextField_itemId;
    private javax.swing.JTextField jTextField_location;
    private javax.swing.JTextField jTextField_media;
    private javax.swing.JTextField jTextField_phrase;
    private javax.swing.JTextField jTextField_score;
    private javax.swing.JTextField jTextField_studentId;
    private javax.swing.JTextPane jTextPane_response;
    private javax.swing.JToggleButton jToggleButton_format;
    private javax.swing.JToggleButton jToggleButton_req;
    // End of variables declaration//GEN-END:variables
    private String gradebook;
    private String studentID;
    private String itemID;
    private String score;
    private boolean enableDiableFlag;
    private boolean formatCheckingFlag;
}
