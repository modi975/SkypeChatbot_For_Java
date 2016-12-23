/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SkypeBot;

import java.io.File;
import java.net.URISyntaxException;
import java.util.regex.Pattern;

/**
 *
 * @author zain
 */
public class QA_DB
{
    private int distance;
    private Pattern question;
    private String answer;

    public QA_DB()
    {
    }

    
    public QA_DB(int distance,Pattern question, String answer)
    {
        this.distance=distance;
        this.question = question;
        this.answer = answer;
    }

    public int getDistance()
    {
        return distance;
    }

    public void setDistance(int distance)
    {
        this.distance = distance;
    }

    

    public Pattern getQuestion()
    {
        return question;
    }

    public void setQuestion(Pattern question)
    {
        this.question = question;
    }

    public String getAnswer()
    {
        return answer;
    }

    public void setAnswer(String answer)
    {
        this.answer = answer;
    }

    void setQuestion(String string)
    {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    // this path return valid path to read file outside .jar file
    public static String filesPath()
    {
        try
        {
            return new File(SkypeBot.QA_DB.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath()).getParent()+"/";
        }
        catch (URISyntaxException ex)
        {
            
        }
        return null;
    }
    
}
