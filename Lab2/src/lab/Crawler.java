package lab;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Created with IntelliJ IDEA.
 * User: ron
 * Date: 1/5/13
 * Time: 9:14 PM
 * To change this template use File | Settings | File Templates.
 */
public class Crawler {
    private int Tasks = 0;
    private int MAX_TASKS = 0;


    private String getDomainName(String URLToParse)
    {
        return null;
    }
    private int createTask(String URLToAdd)
    {
        try {
            InetAddress.getByName(getDomainName(URLToAdd));

        } catch (UnknownHostException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            return -1;
        }
        return -1;
    }

    public int add(String URLToAdd)
    {
        if (Tasks < MAX_TASKS)
        {
            return createTask(URLToAdd);
        } else
        {
            return -1; // TODO: BUGBUG - Reiter to define return error
        }
    }

    public int getStatus()
    {
        if (MAX_TASKS == Tasks)
        {
            return -1; // TODO: BUGBUG - Retier to define return error
        }
        return -1;
    }
}
