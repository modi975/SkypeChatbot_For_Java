package SkypeBot;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.StringTokenizer;
import java.util.regex.Pattern;

/**
 *
 * @author zain
 */
public class ReadRecord
{
    // fullPath is "./src/SkypeBot/" when project run from netbeans IDE, fullPath is QA_DB.filesPath() when project run from .jar file
    private String fullPath="./src/SkypeBot/";  
    
    private ArrayList<QA_DB> records=new ArrayList<>();
    
    
    // read question answer from text file.
    public ReadRecord() 
    {
        Scanner readFile;
        
        try
        {
            // read database file from resources folder
           
            
            readFile = new Scanner(new FileReader(new File(fullPath+"QA_Data.zdb"))); // fullPath provide full path of file when run using .jar
            
            while (readFile.hasNext())
            {
                String line = "";
                line = readFile.nextLine();

                // sperate question and answer using # 
                StringTokenizer strTok = new StringTokenizer(line, "#");

                // save question in pattern 
                Pattern qustion = Pattern.compile(strTok.nextToken());
                
                // replace answer '~' with '\n'
                String answer = strTok.nextToken().replace('~', '\n');

                
                QA_DB tempRecord=new QA_DB(999,qustion, answer);
                
                // test data read from file
//                System.out.println(tempRecord.getDistance() + " " + qustion + " " + answer);

                // rank, qustion and answer in record list
                records.add(tempRecord);
            }
        }
        catch (FileNotFoundException ex)
        {
        }
        
        
    }



    ArrayList<QA_DB> getRecord()
    {
        return records;
    }
    
   
    
}
