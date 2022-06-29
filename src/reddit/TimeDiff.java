package reddit;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeDiff {
    static void
    findDifference(String start_date,
                   String end_date)
    {

        // SimpleDateFormat converts the
        // string format to date object
        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy");

        // Try Block
        try {

            // parse method is used to parse
            // the text from a string to
            // produce the date
            Date d1 = sdf.parse(start_date);
            Date d2 = sdf.parse(end_date);

            // Calucalte time difference
            // in milliseconds
            long difference_In_Time
                    = d2.getTime() - d1.getTime();

            long days = difference_In_Time / 1000 / 60 / 60 / 24;
            System.out.println("days diff " + days);
        }

        // Catch the Exception
        catch (ParseException e) {
            e.printStackTrace();
        }
    }

    // Driver Code
    public static void main(String[] args)
    {
        // Given start Date
        String start_date
                = "10-01-2020";

        // Given end Date
        String end_date
                = "10-06-2020";

        // Function Call
        findDifference(start_date, end_date);
    }
}
