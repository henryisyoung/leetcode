package uber;

public class PrintCalendar {
    public void printCalendar(int year){
        System.out.print(String.format("         Calendar - %d\n\n", year));
        int days;

        // Index of the day from 0 to 6
        int current = dayNumber (1, 1, year);

        // i --> Iterate through all the months
        // j --> Iterate through all the days of the
        //       month - i
        for (int i = 0; i < 12; i++){
            days = numberOfDays (i, year);
            System.out.print(String.format("\n  ------------%s-------------\n", getMonthName(i)));
            System.out.print("  Sun  Mon  Tue  Wed  Thu  Fri  Sat\n");
            int k;
            for (k = 0; k < current; k++)
                System.out.print("     ");

            for (int j = 1; j <= days; j++) {
                System.out.print(String.format("%5d", j));

                if (++k > 6) {
                    k = 0;
                    System.out.print("\n");
                }
            }

            if (k > 0)
                System.out.print("\n");

            current = k;
        }
    }

    private String getMonthName(int monthNumber) {
        String months[] = {"January", "February", "March",
                "April", "May", "June",
                "July", "August", "September",
                "October", "November", "December"
        };

        return (months[monthNumber]);
    }

    private int numberOfDays(int monthNumber, int year) {
        // January
        if (monthNumber == 0)
            return (31);

        // February
        if (monthNumber == 1) {
            // If the year is leap then February has
            // 29 days
            if (year % 400 == 0 ||
                    (year % 4 == 0 && year % 100 != 0))
                return (29);
            else
                return (28);
        }

        // March
        if (monthNumber == 2)
            return (31);

        // April
        if (monthNumber == 3)
            return (30);

        // May
        if (monthNumber == 4)
            return (31);

        // June
        if (monthNumber == 5)
            return (30);

        // July
        if (monthNumber == 6)
            return (31);

        // August
        if (monthNumber == 7)
            return (31);

        // September
        if (monthNumber == 8)
            return (30);

        // October
        if (monthNumber == 9)
            return (31);

        // November
        if (monthNumber == 10)
            return (30);

        // December
        if (monthNumber == 11)
            return (31);
        return -1;
    }

    //Here is a formula for finding the day of the week for ANY date.
    // N = d + 2m + [3(m+1)/5] + y + [y/4] - [y/100] + [y/400] + 2

    private int dayNumber(int day, int month, int year) {
        int t[] = { 0, 3, 2, 5, 0, 3, 5, 1, 4, 6, 2, 4 };
        year -= month < 3 ? 1 : 0;
        return ( year + year/4 - year/100 + year/400 + t[month-1] + day) % 7;
    }

    public static void main(String[] args) {
        int year = 2016;
        PrintCalendar solver = new PrintCalendar();
        solver.printCalendar(year);
    }
}
