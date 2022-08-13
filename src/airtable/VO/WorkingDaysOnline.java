package airtable.VO;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.*;
import java.util.function.Predicate;

public class WorkingDaysOnline {
    public static void main(String[] args) {
        LocalDate today = LocalDate.of(2020, 5, 5);

        List<String> list = Arrays.asList("2020-05-11", "2020-05-1");
        Collections.sort(list);

        //Add one holiday for testing
        List<LocalDate> holidays = new ArrayList<>();
        holidays.add(LocalDate.of(2020, 5, 11));
        holidays.add(LocalDate.of(2020, 5, 1));

        System.out.println(addBusinessDays(today, 8, Optional.empty()));        // 2020-05-15
        System.out.println(addBusinessDays(today, 8, Optional.of(holidays)));           // 2020-05-18
        System.out.println();
        Set<String> set = new HashSet<>();
        set.addAll(list);

        String startDate = "2020-05-05";
        System.out.println(endWorkingDate(startDate, 8, new HashSet<>()));
        System.out.println(endWorkingDate(startDate, 8, set));
    }

    public static String endWorkingDate(String startDate, int offset, Set<String> holidays) {
        String[] array = startDate.split("-");
        int year = Integer.parseInt(array[0]), month = Integer.parseInt(array[1]), day = Integer.parseInt(array[2]);
        LocalDate date = LocalDate.of(year, month, day);

        while (offset > 0) {
            date = date.plusDays(1);
            if (isHoliday(date, holidays) || isWeekend(date)) {
                continue;
            }
            offset--;
        }
        return date.toString();
    }

    private static boolean isWeekend(LocalDate date) {
        return date.getDayOfWeek() == DayOfWeek.SATURDAY || date.getDayOfWeek() == DayOfWeek.SUNDAY;
    }

    public static boolean isHoliday(LocalDate date, Set<String> holidays) {
        return holidays.contains(date.toString());
    }

    public static String endDate(String startDate, int offset, List<String> holidays) {
        offset = (offset / 5) * 7 + offset % 5;
        String[] array = startDate.split("-");
        int year = Integer.parseInt(array[0]), month = Integer.parseInt(array[1]), day = Integer.parseInt(array[2]);

        int[] toDate = findTodate(year, month, day, offset);
        int endY = toDate[0], endM = toDate[1], endDay = toDate[2];
        LocalDate date = LocalDate.of(endY, endM, endDay);
        int totalHolidays = countHolidays(startDate, date.toString(), holidays);

        Set<String> set = new HashSet<>();
        set.addAll(holidays);
        while (totalHolidays > 0) {
            date = date.plusDays(1);
            if (isHoliday(date, set) || isWeekend(date)) {
                continue;
            }
            totalHolidays--;
        }
        return date.toString();
    }

    private static int countHolidays(String startDate, String endDate, List<String> holidays) {
        int pos1 = findPos(startDate, holidays), pos2 = findPos(endDate, holidays);

        return pos2 - pos1;
    }

    private static int findPos(String date, List<String> holidays) {
        int l = 0, r = holidays.size() - 1;
        while (l + 1 < r) {
            int mid = l + (r - l) / 2;
            if (date.compareTo(holidays.get(mid)) == 0) {
                return mid;
            } else if (date.compareTo(holidays.get(mid)) < 0){
                r = mid;
            } else {
                l = mid;
            }
        }
        if (date.compareTo(holidays.get(r)) > 0) {
            return r;
        }
        return l;
    }

    private static int[] findTodate(int year, int month, int day, int offset) {
        int pastedDays = calPastedDays(year, month, day);
        int yearDays = isLeap(year) ? 366 : 365;
        int restDays = yearDays - pastedDays;
        if (restDays <= offset) {
            return finalDate(year, offset + pastedDays);
        } else {
            year++;
            offset -= restDays;
            yearDays = isLeap(year) ? 366 : 365;
            while (offset > yearDays) {
                year++;
                offset -= yearDays;
                yearDays = isLeap(year) ? 366 : 365;
            }
            return finalDate(year, offset);
        }
    }

    private static int[] finalDate(int year, int offset) {
        int[] month = new int[]{0, 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};

        int m = 1;
        while (m <= 12 && offset > month[m]) {
            offset -= month[m];
            m++;
        }
        return new int[]{year, m, offset};
    }

    private static int calPastedDays(int year, int month, int day) {
        int pastedDays = day;

        if (month - 1 == 1) {
            pastedDays += 31;
        } else if (month - 1 == 2) {
            pastedDays += 31 + 28;
        } else if (month - 1 == 3) {
            pastedDays += 31 + 28 + 31;
        } else if (month - 1 == 4) {
            pastedDays += 31 + 28 + 31 + 30;
        } else if (month - 1 == 5) {
            pastedDays += 31 + 28 + 31 + 30 + 31;
        } else if (month - 1 == 6) {
            pastedDays += 31 + 28 + 31 + 30 + 31 + 30;
        } else if (month - 1 == 7) {
            pastedDays += 31 + 28 + 31 + 30 + 31 + 30 + 31;
        } else if (month - 1 == 8) {
            pastedDays += 31 + 28 + 31 + 30 + 31 + 30 + 31 + 31;
        } else if (month - 1 == 9) {
            pastedDays += 31 + 28 + 31 + 30 + 31 + 30 + 31 + 31 + 30;
        } else if (month - 1 == 10) {
            pastedDays += 31 + 28 + 31 + 30 + 31 + 30 + 31 + 31 + 30 + 31;
        } else if (month - 1 == 11) {
            pastedDays += 31 + 28 + 31 + 30 + 31 + 30 + 31 + 31 + 30 + 31 + 30;
        }

        if (isLeap(year) && month > 2) {
            pastedDays++;
        }
        return pastedDays;
    }

    private static boolean isLeap(int y) {
        if (y % 100 != 0 && y % 4 == 0 || y % 400 == 0)
            return true;

        return false;
    }


    // https://howtodoinjava.com/java/date-time/add-subtract-business-days/
    private static LocalDate addBusinessDays(LocalDate localDate, int days,
                                             Optional<List<LocalDate>> holidays){
        if(localDate == null || days <= 0 || holidays == null) {
            throw new IllegalArgumentException("Invalid method argument(s) "
                    + "to addBusinessDays("+localDate+","+days+","+holidays+")");
        }

        Predicate<LocalDate> isHoliday =
                date -> holidays.isPresent() ? holidays.get().contains(date) : false;

        Predicate<LocalDate> isWeekend = date
                -> date.getDayOfWeek() == DayOfWeek.SATURDAY
                || date.getDayOfWeek() == DayOfWeek.SUNDAY;

        LocalDate result = localDate;
        while (days > 0) {
            result = result.plusDays(1);
            if (isHoliday.or(isWeekend).negate().test(result)) {
//                System.out.println(result);
                days--;
            }
        }
        return result;
    }
}
