package airtable.VO;

import java.util.*;

public class WorkingDays {
    /*
    给起始日期，项目需要的天数，list of holidays，求项目截止日期，要求优化比O（N）要好
    另外有意思的是coding，输入时间，跟offset，和list of holidays， 输出offset之后的work day
    比如7/1/2021, offset 3 应该是7/5，
    优化要求比linear算法更优 要涉及数学算法

    计算working days的题，我的想法是这样
    1. 先只考虑weekends: to_date = from_date + working_days / 5 * 7 + working_days % 5
    2. 计算total_holidays in all years between from_date to to_date. Sort holidays, then for each year, use binary search
    3. to_date = to_date + total_holidays.
    4. Repeat 2 and 3 until total_holidays does not include additional holidays.
    https://www.geeksforgeeks.org/date-after-adding-given-number-of-days-to-the-given-date/
     */

    public String calEndDay(String startDate, int offset, List<String> holidays) {
        offset = offset / 5 * 7 + offset % 5;
        String toDate = calToDate(startDate, offset);
        if (isWeekend(toDate)) {
            toDate = toMonday(toDate);
        }
        Collections.sort(holidays, (a, b) -> {
            String[] array1 = a.split("/"), array2 = b.split("/");
            if (!array1[2].equals(array2[2])) {
                return array1[2].compareTo(array2[2]);
            }
            if (!array1[0].equals(array2[0])) {
                return array1[0].compareTo(array2[0]);
            }
            return array1[1].compareTo(array2[1]);
        });

        String[] array = startDate.split("/");
        String[] array2 = toDate.split("/");
        int startYear = Integer.parseInt(array[2]), endYear = Integer.parseInt(array2[2]);
        int startMonth = Integer.parseInt(array[0]), endMonth = Integer.parseInt(array2[0]);
        int startD = Integer.parseInt(array[0]), endD = Integer.parseInt(array2[0]);

        if (startYear != endYear) {
            int totalHoliday = 0;
            for (int i = startYear; i < endYear; i++) {
                if (i != startYear && i != endYear) {
                    totalHoliday += holidays.size();
                } else if (i != startYear) {
                    totalHoliday += findPos(startMonth + "/" + startD, holidays)[0];
                }
            }
            totalHoliday = totalHoliday / 5 * 7 + totalHoliday % 5;
            toDate = calToDate(toDate, totalHoliday);
            if (isWeekend(toDate)) {
                toDate = toMonday(toDate);
            }
            startDate = 1 + "/" + 1 + "/" + endYear;
        }

        for (String holiday : holidays) {
            if (compareDate(toDate, holiday) && compareDate(holiday, startDate)) {
                toDate = calToDate(toDate, 1);
                if (isWeekend(toDate)) {
                    toDate = toMonday(toDate);
                }
            }
        }
        return toDate;
    }

    private int[] findPos(String s, List<String> holidays) {
        return new int[2];
    }

    private String toMonday(String toDate) {
        return "";
    }

    private boolean isWeekend(String toDate) {
        return true;
    }

    private boolean compareDate(String date1, String date2) {
        // true date1 >= date2 otherwise false
        String[] array1 = date1.split("/"), array2 = date2.split("/");
        if (!array1[2].equals(array2[2])) {
            return Integer.parseInt(array1[2]) >= Integer.parseInt(array2[2]);
        }
        if (!array1[0].equals(array2[0])) {
            return Integer.parseInt(array1[0]) >= Integer.parseInt(array2[0]);
        }
        return Integer.parseInt(array1[1]) >= Integer.parseInt(array2[1]);
    }

    private static String calToDate(String startDay, int offset) {
        String[] array = startDay.split("/");
        int month = Integer.parseInt(array[0]), day = Integer.parseInt(array[1]), year = Integer.parseInt(array[2]);
        int elapsedDays = elapsedDays(month, year, day);

        int yearDays = isLeap(year) ? 366 : 365;
        int remindDays = yearDays - elapsedDays;
        int finalYear = year, finalOffset;

        if (offset <= remindDays) {
            finalYear = year;
            finalOffset = elapsedDays + offset;
        } else {
            finalYear++;
            yearDays = isLeap(finalYear) ? 366 : 365;
            offset -= remindDays;

            while (offset >= yearDays) {
                finalYear++;
                offset -= yearDays;
                yearDays = isLeap(finalYear) ? 366 : 365;
            }
            finalOffset = offset;
        }
        return buildFinalDate(finalOffset, finalYear);
    }

    private static String buildFinalDate(int offset, int year) {
        int []month={ 0, 31, 28, 31, 30, 31, 30,
                31, 31, 30, 31, 30, 31 };

        if (isLeap(year))
            month[2] = 29;
        int i = 1;
        for (; i <= 12; i++) {
            if(offset > month[i]) {
                offset -= month[i];
            } else {
                break;
            }
        }
        return i + "/" + offset + "/" + year;
    }

    private static int elapsedDays(int m, int y, int d) {
        int offset = d;

        if(m - 1 == 11)
            offset += 335;
        if(m - 1 == 10)
            offset += 304;
        if(m - 1 == 9)
            offset += 273;
        if(m - 1 == 8)
            offset += 243;
        if(m - 1 == 7)
            offset += 212;
        if(m - 1 == 6)
            offset += 181;
        if(m - 1 == 5)
            offset += 151;
        if(m - 1 == 4)
            offset += 120;
        if(m - 1 == 3)
            offset += 90;
        if(m - 1 == 2)
            offset += 59;
        if(m - 1 == 1)
            offset += 31;

        if (isLeap(y) && m > 2)
            offset += 1;

        return offset;
    }

    private static boolean isLeap(int y) {
        if (y % 100 != 0 && y % 4 == 0 || y % 400 == 0)
            return true;

        return false;
    }

    public static void main(String[] args) {
        String date = "3/14/2015";
        int offset = 366;
        System.out.println(calToDate(date, offset));
    }
}
