package main.austin;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Project: DriveBackup
 * File: main.austin.Test
 * User: Austin
 * Date: 5/9/2019
 * Time: 12:01 PM
 */
public class Test {

    public static void main(String[] args) {

        String[] files = {"'Backup-survival1_nether-'yyyy-M-d--HH-mm'.zip'"};

        for (String file : files){

            System.out.println(file);

            //if (file.endsWith(".zip")) {
                String dateString = "Backup-survival1_nether-2019-5-7--20-26.zip";
                DateFormat format = new SimpleDateFormat(file, Locale.ENGLISH);
                try {
                    Date date = format.parse(dateString);
                    System.out.println(date);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            //}

        }

    }

}
