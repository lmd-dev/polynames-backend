package env;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;

public class Env
{
    private static Env instance = null;

    HashMap<String, String> properties = new HashMap<>();

    private Env(String envFilePath)
    {
        extractEnvProperties(envFilePath);
    }

    public static void init()
    {
        Env.init(".env");
    }

    public static void init(String envFilePath)
    {
        if(Env.instance == null)
            Env.instance = new Env(envFilePath);
        
    }

    public static String get(String propertyName)
    {
        if(Env.instance == null)
            Env.init();

        return Env.instance.properties.get(propertyName);
    }

    public static int getInt(String propertyName)
    {
        try
        {
            return Integer.parseInt(get(propertyName));
        }
        catch (Exception e)
        {
            return 0;
        }
    }

    private void extractEnvProperties(String fileName)
    {
        try
        {
            final BufferedReader br = new BufferedReader(new FileReader(fileName));
            String line = br.readLine();

            while (line != null)
            {
                extractProperty(line);
                line = br.readLine();
            }

            br.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void extractProperty(String line)
    {
        int equalIndex = line.indexOf("=");

        if (equalIndex == -1)
            return;

        try
        {
            String constantName = line.substring(0, equalIndex).trim();
            String constantValue = removeStringQuotes(line.substring(equalIndex + 1).trim());

            properties.put(constantName, constantValue);
        }
        catch (Exception e)
        {

        }
    }

    private String removeStringQuotes(String str)
    {
        int firstQuoteIndex = str.indexOf("\"");
        int lastQuoteIndex = str.lastIndexOf("\"");

        if(firstQuoteIndex != -1 && lastQuoteIndex != -1)
            return str.substring(firstQuoteIndex + 1, lastQuoteIndex);

        return str;
    }
}
