package airtable.VO;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.*;

public class WorkingDays2 {
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

    public static String addBusinessDays(String startDate, int offset, List<String> holidays) {
        String[] array = startDate.split("-");
        int year = Integer.parseInt(array[0]), month = Integer.parseInt(array[1]), day = Integer.parseInt(array[2]);
        LocalDate date = LocalDate.of(year, month, day);

        while (offset > 0) {
            date = date.plusDays(1);
            if (isHoliday(holidays, date) || isWeekend(date)) {
                continue;
            }
            offset--;
        }
        return date.toString();
    }

    public static String addBusinessDays2(String startDate, int offset, List<String> holidays) {
        String[] array = startDate.split("-");
        int year = Integer.parseInt(array[0]), month = Integer.parseInt(array[1]), day = Integer.parseInt(array[2]);
        String toDate = findToDate(year, month, day, offset);
        array = toDate.split("-");
        year = Integer.parseInt(array[0]); month = Integer.parseInt(array[1]); day = Integer.parseInt(array[2]);

        if (holidays.isEmpty()) {
            return toDate;
        }
        int count = countHolidays(holidays, startDate, toDate);

        LocalDate endDate = LocalDate.of(year, month, day);
        while (count > 0) {
            endDate = endDate.plusDays(1);
            if (isWeekend(endDate)) {
                continue;
            } else if (isHoliday(holidays, endDate)) {
                count++;
            } else {
                count--;
            }
        }
        return endDate.toString();
    }

    private static int countHolidays(List<String> holidays, String startDate, String toDate) {
        int l = 0, r = holidays.size() - 1;
        int leftPos = -1;
        if (holidays.get(l).compareTo(startDate) < 0) {
            leftPos = findLeftPos(holidays, startDate);
        }
        int rightPos = holidays.size();
        if (holidays.get(r).compareTo(startDate) > 0) {
            rightPos = findRightPos(holidays, toDate);
        }
        return rightPos - leftPos - 1;
    }

    private static int findLeftPos(List<String> holidays, String startDate) {
        int l = 0, r = holidays.size() - 1;
        while (l + 1 < r) {
            int mid = l + (r - l) / 2;
            String midDate = holidays.get(mid);
            if (midDate.compareTo(startDate) >= 0) {
                r = mid;
            } else {
                l = mid;
            }
        }
        if (holidays.get(r).compareTo(startDate) < 0) {
            return r;
        }
        return l;
    }

    private static int findRightPos(List<String> holidays, String endDate) {
        int l = 0, r = holidays.size() - 1;
        while (l + 1 < r) {
            int mid = l + (r - l) / 2;
            String midDate = holidays.get(mid);
            if (midDate.compareTo(endDate) <= 0) {
                l = mid;
            } else {
                r = mid;
            }
        }
        if (holidays.get(l).compareTo(endDate) > 0) {
            return l;
        }
        return r;
    }

    private static String findToDate(int year, int month, int day, int offset) {
        offset = offset / 5 * 7 + offset % 5;
        int pastedDays = calPastDays(year, month, day);
        int yearDays = isLeap(year) ? 366 : 365;
        int remDays = yearDays - pastedDays;
        LocalDate endDate = null;
        if (remDays >= offset) {
            offset += pastedDays;
            endDate = calDate(offset, year);
        } else {
            offset -= yearDays;
            year++;
            yearDays = isLeap(year) ? 366 : 365;
            while (offset > yearDays) {
                offset -= yearDays;
                year++;
                yearDays = isLeap(year) ? 366 : 365;
            }
            endDate = calDate(offset, year);
        }
        while (isWeekend(endDate)) {
            endDate = endDate.plusDays(1);
        }

        return endDate.toString();
    }

    private static LocalDate calDate(int offset, int year) {
        int[] month = {0, 31, 28, 31, 30,31,30,31,31,30,31,30,31};
        if (isLeap(year)) {
            month[2]++;
        }
        int m = 1;
        for (; m <= 12; m++) {
            if (month[m] < offset) {
                offset -= month[m];
            } else {
                break;
            }
        }
        return LocalDate.of(year, m, offset);
    }

    private static int calPastDays(int year, int month, int day) {
        int past = day;
        if (month - 1 == 1) {
            past += 31;
        } else if (month - 1 == 2) {
            past += 31 + 28;
        } else if (month - 1 == 3) {
            past += 31 + 28 + 31;
        } else if (month - 1 == 4) {
            past += 31 + 28 + 31 + 30;
        } else if (month - 1 == 5) {
            past += 31 + 28 + 31 + 30 + 31;
        } else if (month - 1 == 6) {
            past += 31 + 28 + 31 + 30 + 31 + 30;
        } else if (month - 1 == 7) {
            past += 31 + 28 + 31 + 30 + 31 + 30 + 31;
        } else if (month - 1 == 8) {
            past += 31 + 28 + 31 + 30 + 31 + 30 + 31 + 31;
        } else if (month - 1 == 9) {
            past += 31 + 28 + 31 + 30 + 31 + 30 + 31 + 31 + 30;
        } else if (month - 1 == 10) {
            past += 31 + 28 + 31 + 30 + 31 + 30 + 31 + 31 + 30 + 31;
        } else if (month - 1 == 11) {
            past += 31 + 28 + 31 + 30 + 31 + 30 + 31 + 31 + 30 + 31 + 30;
        }
        past += isLeap(year) && month > 2 ? 1 : 0;
        return past;
    }

    private static boolean isLeap(int year) {
        return year % 4 == 0;
    }


    private static boolean isWeekend(LocalDate date) {
        return date.getDayOfWeek() == DayOfWeek.SATURDAY || date.getDayOfWeek() == DayOfWeek.SUNDAY;
    }

    private static boolean isHoliday(List<String> holidays, LocalDate date) {
        return holidays.contains(date.toString());
    }

    public static void main(String[] args) {
        LocalDate today = LocalDate.of(2020, 5, 5);

        List<String> list = Arrays.asList("2020-05-11", "2020-05-1");
        Collections.sort(list);

        //Add one holiday for testing
        List<LocalDate> holidays = new ArrayList<>();
        holidays.add(LocalDate.of(2020, 5, 11));
        holidays.add(LocalDate.of(2020, 5, 1));

        System.out.println(addBusinessDays("2020-05-05", 8, Arrays.asList()));        // 2020-05-15
        System.out.println(addBusinessDays("2020-05-05", 8, list));           // 2020-05-18


//        String startDate = "2020-05-05";
//        System.out.println(endWorkingDate(startDate, 8, new HashSet<>()));
//        System.out.println(endWorkingDate(startDate, 8, set));
    }
}
