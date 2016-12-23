/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SkypeBot;

import com.skype.ChatMessage;
import com.skype.ChatMessageAdapter;
import com.skype.Friend;
import com.skype.Skype;
import com.skype.SkypeException;
import com.skype.User;
import java.awt.Font;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.Scanner;
import java.util.regex.Matcher;
import javax.swing.DefaultListModel;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import org.apache.commons.lang.StringUtils;
/**
 *
 * @author zain
 */
public class QA_System implements Runnable
{
    // fullPath is "./src/SkypeBot/" when project run from netbeans IDE, fullPath is QA_DB.filesPath() when project run from .jar file
    private String fullPath="./src/SkypeBot/";      
    
    private String question;
    private String answer;
    private String senderID;
    private  QA_DB minimumDistaceRecord;
    
    private ArrayList<QA_DB> QA_Record;
       
    private volatile boolean skypeLogin;
    
    // GUI components
    private JTextArea chatTextArea;
    
    // contact list models
    DefaultListModel<String> listModel;
   
    // file for save user chat and unknownquestions 
    private File outFile;
    private FileWriter fileWriter;
    
    // read last time chat from savechat.log
    private File inFile;
    private Scanner fileReader;
    
    
    public QA_System(JTextArea textArea, DefaultListModel<String> contactListModel) throws FileNotFoundException, SkypeException    // for text interface not use jTextArea
    {
        // Read data from file
        ReadRecord record=new ReadRecord();
        senderID="";
        QA_Record=record.getRecord();
       
        skypeLogin=true;
        this.chatTextArea=textArea;
        
        // clear all contents of listModel
        listModel=contactListModel;
        listModel.removeAllElements();
     
        
        // ----------------------------- load last time chat on chat area -----------------------------   
        inFile=new File(fullPath+"lastChat.log");      // fullPath provide full path of file when run using .jar
        
        if(inFile.exists())
        {
            fileReader=new Scanner(inFile);
            while(fileReader.hasNext())
            {
                chatTextArea.append(fileReader.nextLine());
                chatTextArea.append("\n");
            }
        }
        // ---------------------------------------------------------- 
    }

    @Override
    public void run()
    {
        // if Skye user not login or Skype not installed.
        if(listModel.isEmpty()||(!isSkypeLogin()))
        {
            JOptionPane.showConfirmDialog(null, "1. Please install Skype 6.xx\n   (higher version not working with any 3rd party application)\n2. Login your skype account\n3. Start Skypebot again", "Login message",JOptionPane.PLAIN_MESSAGE,JOptionPane.ERROR_MESSAGE,null);  
            System.exit(0);
        }
        
        else if (isSkypeLogin())
        {
            updateStatus();
            try
            {
                Skype.clearChatHistory();
                Skype.clearCallHistory();
                
                Skype.setDaemon(false);
                Skype.addChatMessageListener(new ChatMessageAdapter()
                {
                    @Override
                    public void chatMessageReceived(ChatMessage received)
                            throws SkypeException
                    {
                        if (received.getType().equals(ChatMessage.Type.SAID)&&isSkypeLogin())
                        {
                            
                            // get sender information
                            User sender = received.getSender();
                            
                            // get sender ID
                            senderID=sender.getId();
                            
                            // get sender question string in lower case
                            question = received.getContent().toLowerCase();
                            
                            // set answer from record
                            answer="";
                            answer = getAnswer(question);

                            // clear CHAT text area lines exceed from 100 lines
                            if(chatTextArea.getLineCount()>100)
                            {
                                chatTextArea.setText("");
                            }
                            
                            // if answer found
//                            System.out.println(sender.getId() + " : " + question);          // use only for text interface

                            if(!answer.equals(""))
                            {
                                // send answer to sender
                                received.getSender().send(answer);
                                
                                 // print sender question and QA answer processed for sender 
//                                 System.out.println("Response : " + answer);    // use only for text interface
                                   
                                chatTextArea.setFont(new Font("Courier New", Font.PLAIN, 11));
                                chatTextArea.append("\n"+sender.getId() + " : " + question+"\nResponse : " + answer+"\n");       // use only for GUI interface
                                
                                
                                // ----------------------------- save user CHAT data in userID.log file -----------------------------  
                                outFile=new File(fullPath+getSenderID()+".log");   // fullPath provide full path of file when run using .jar
                                
                                if(!outFile.exists())
                                {
                                    try
                                    {
                                        outFile.createNewFile();
                                    }
                                    catch (IOException ex)
                                    {
                                        
                                    }
                                }
                                try
                                {
                                    // append data using "true" option
                                    fileWriter=new FileWriter(outFile,true);
                                    fileWriter.append(String.format("%tc\n", new Date()));
                                    fileWriter.append("Question : "+question+"\n");
                                    fileWriter.append("Answer : "+answer+"\n\n");
                                    fileWriter.close();
                                }
                                catch (IOException ex)
                                {
                                    
                                }
                                // ---------------------------------------------------------- 
                            }
                            else if(answer.equals(""))  
                            {      
                                // send answer to sender
                                received.getSender().send("Response : No results found for \"" + question+"\"");
                                
                                 // print sender question and QA answer processed for sender 
//                                 System.out.println("Response : No results found for \"" + question+"\"");  // use only for text interface
                                
                                chatTextArea.setFont(new Font("Courier New", Font.PLAIN, 11));
                                chatTextArea.append("\n"+sender.getId() + " : " + question+"\nResponse : No results found for \"" + question+"\""+"\n");        // use only for GUI interface
                               
                                
                                // ----------------------------- unknown questions in file for future use -----------------------------  
                                outFile=new File(fullPath+"unknownQuestion.txt");      // fullPath provide full path of file when run using .jar
                                
                                if(!outFile.exists())
                                {
                                    try
                                    {
                                        outFile.createNewFile();
                                    }
                                    catch (IOException ex)
                                    {
                                        
                                    }
                                }
                                try
                                {
                                    // append data using "true" option
                                    fileWriter=new FileWriter(outFile,true);
                                    fileWriter.append(String.format("%s\n", question));
                                    fileWriter.close();
                                }
                                catch (IOException ex)
                                {
                                    
                                }
                                // ---------------------------------------------------------- 
                            }
                                
                        }
                    }
                });
            }
            catch (SkypeException ex)
            {
                
            }
        }
    }

    // get answer of question
    private String getAnswer(String userQuestion)
    {
        // set all record distance maximum 999 by default       
        for(int i=0;i<QA_Record.size();i++)
            QA_Record.get(i).setDistance(999);

        // match user qustion with DB question, if find any question match then find minimum distance question 
        // and return answer of minimum distance question.
        for(int i=0;i<QA_Record.size();i++)
        {
            Matcher m=QA_Record.get(i).getQuestion().matcher(userQuestion);
            
            // find all matched question records
            if(m.find())
            {
                // set minimum distance of each matched record.
                QA_Record.get(i).setDistance(StringUtils.getLevenshteinDistance(userQuestion, m.group()));
//                System.out.println(QA_Record.get(i).getDistance()+" "+QA_Record.get(i).getAnswer());
            }
        }
        
        // return valid answer if distance >=0 and distance < 999
        if((getSmallestDistance(QA_Record).getDistance()>=0)&&(getSmallestDistance(QA_Record).getDistance()<999))
        {
            // return smallest distance object answer.
            return getSmallestDistance(QA_Record).getAnswer();
        }
    
        return "";
    }
    
    //get smallest distance object
    private QA_DB getSmallestDistance(ArrayList<QA_DB> record)
    {
        QA_DB currentRecord;
        
        // minimum distance is 1st index of record in start;
        minimumDistaceRecord=record.get(0);
        Iterator<QA_DB> it=QA_Record.iterator();
        while(it.hasNext())
        {
            currentRecord=it.next();
            if(currentRecord.getDistance()<=minimumDistaceRecord.getDistance())
            {
                minimumDistaceRecord=currentRecord;
            }
        }
        return minimumDistaceRecord;
    }

    public void updateStatus()
    {
        try
        {
            //clear list 
            listModel.removeAllElements();
            //get id of all friends and put in listModel
            
            for(Friend Friends:Skype.getContactList().getAllFriends())
            {
                // get id of online friends
                if(Friends.getStatus()==User.Status.ONLINE)
                    listModel.addElement("[ ONLINE ] "+Friends.getId());
                
                // get id of offline friends
                else if(Friends.getStatus()==User.Status.OFFLINE)
                    listModel.addElement("[ OFFLINE ] "+Friends.getId());
            }   
        }
        catch (SkypeException ex)
        {
        }
        
    }
    
    public DefaultListModel<String> getListModel()
    {
        return listModel;
    }

    
    public boolean isSkypeLogin()
    {
        return skypeLogin;
    }

    public void stopSystem()
    {
        this.skypeLogin = false;
    }

    public void startSystem()
    {
        this.skypeLogin=true;
    }
    
    public String getQuestion()
    {
        return question;
    }

    public String getAnswer()
    {
        return answer;
    }

    public String getSenderID()
    {
        return senderID;
    }

    
}
