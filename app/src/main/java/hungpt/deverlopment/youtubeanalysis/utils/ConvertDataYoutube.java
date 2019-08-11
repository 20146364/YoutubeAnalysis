package hungpt.deverlopment.youtubeanalysis.utils;

import android.util.Log;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;
import java.util.regex.Pattern;

public class ConvertDataYoutube {

    private SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-d HH:mm:s");
    private SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
    private SimpleDateFormat formatDate = new SimpleDateFormat("MMMM d, yyyy");
    private SimpleDateFormat formats = new SimpleDateFormat("yyyy-MM-d");

    private HashMap<String, String> regexMap = new HashMap<>();
    private String regex2two = "(?<=[^\\d])(\\d)(?=[^\\d])";
    private String two = "0$1";

    private NavigableMap<Long, String> suffixes = new TreeMap<>();

    {
        suffixes.put(1_000L, "K");
        suffixes.put(1_000_000L, "M");
        suffixes.put(1_000_000_000L, "G");
        suffixes.put(1_000_000_000_000L, "T");
        suffixes.put(1_000_000_000_000_000L, "P");
        suffixes.put(1_000_000_000_000_000_000L, "E");
    }


    public String convertNumber(String s) {
        int x = Integer.parseInt(s);
        String number;
        if (x < 1000) {
            number = String.valueOf(x);
        } else if (x >= 1000 && x < 1000000) {
            number = (x / 1000) + " nghìn";
        } else if (x >= 1000000 && x < 1000000000) {
            number = (x / 1000000) + " triệu";
        } else {
            number = (x / 1000000000) + " tỷ";
        }
        return number;
    }

//    public String convertTime(String s) {
//        String result = s.replace("PT", "").replace("H", ":").replace("M", ":").replace("S", "");
//        String arr[] = result.split(":");
//        String timeString;
//        if (arr.length > 2) {
//            timeString = String.format("%d:%02d:%02d", Integer.parseInt(arr[0]), Integer.parseInt(arr[1]), Integer.parseInt(arr[2]));
//        } else if (1 < arr.length && arr.length < 2) {
//            timeString = String.format("%02d:%02d", Integer.parseInt(arr[0]), Integer.parseInt(arr[1]));
//        } else {
//            timeString = String.format("%02d:%02d", 0, Integer.parseInt(arr[0]));
//        }
//        return timeString;
//    }

    public String convertTime(String s) {
        regexMap.put("PT(\\d\\d)S", "00:$1");
        regexMap.put("PT(\\d\\d)M", "$1:00");
        regexMap.put("PT(\\d\\d)H", "$1:00:00");
        regexMap.put("PT(\\d\\d)M(\\d\\d)S", "$1:$2");
        regexMap.put("PT(\\d\\d)H(\\d\\d)S", "$1:00:$2");
        regexMap.put("PT(\\d\\d)H(\\d\\d)M", "$1:$2:00");
        regexMap.put("PT(\\d\\d)H(\\d\\d)M(\\d\\d)S", "$1:$2:$3");

        String d = s.replaceAll(regex2two, two);
        String regex = getRegex(d);
        String newDate = d.replaceAll(regex, regexMap.get(regex));

        return newDate;
    }

    private String getRegex(String date) {
        for (String r : regexMap.keySet())
            if (Pattern.matches(r, date))
                return r;
        return null;
    }

    public String convertTimeUpload(String s) {
        String date = s.substring(0, 9);
        String time = s.substring(11, 18);
        String dateTime = date + " " + time;
        Date d = convertStringtoDate(dateTime);

        String t = format.format(d);
        return t;
    }

    public String convertDate(String s){
        String date = s.substring(0, 9);
        Date d = convertStringDate(date);
        return formatDate.format(d);
    }

    private Date convertStringtoDate(String s) {
        Date d = null;
        try {
            d = formatter.parse(s);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return d;
    }

    private Date convertStringDate(String s){
        Date d = null;
        try {
            d = formats.parse(s);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return d;
    }

    public String getTimeUpload(String x) {
        String t = convertTimeUpload(x);

        String diff = null;
        try {
            Date endDate = format.parse(getcurrentDay());
            Date startDate = format.parse(t);

            long different = endDate.getTime() - startDate.getTime();
            long hours = (different / (1000 * 60 * 60));
            long mins = ((different / (1000 * 60)) % 60);
            long sec = ((different / (1000)) % 60);

            if (hours >= 24) {
                int day = (int) hours / 24;
                if (day < 30) {
                    diff = day + " ngày trước";
                    return diff;
                } else {
                    int month = day / 30;
                    if (month < 12) {
                        diff = month + " tháng trước";
                        return diff;
                    } else {
                        int year = month / 12;
                        diff = year + " năm trước";
                        return diff;
                    }
                }
            } else {
                if (hours > 1) {
                    diff = hours + " giờ trước";
                    return diff;
                } else {
                    if (mins > 0) {
                        diff = mins + " phút trước";
                        return diff;
                    } else {
                        if (sec > 0) {
                            diff = sec + " giây trước";
                            return diff;
                        } else {
                            return "Vừa đăng";
                        }
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            return "Lỗi";
        }

    }

    private String getcurrentDay() {
        String currentDateandTime = format.format(new Date());
        return currentDateandTime;
    }


    public  String format(long value) {
        if (value == Long.MIN_VALUE) return format(Long.MIN_VALUE + 1);
        if (value < 0) return "-" + format(-value);
        if (value < 1000) return Long.toString(value); //deal with easy case

        Map.Entry<Long, String> e = suffixes.floorEntry(value);
        Long divideBy = e.getKey();
        String suffix = e.getValue();

        long truncated = value / (divideBy / 10); //the number part of the output times 10
        boolean hasDecimal = truncated < 100 && (truncated / 10d) != (truncated / 10);
        return hasDecimal ? (truncated / 10d) + suffix : (truncated / 10) + suffix;
    }


    public String formatNumber(String s) {
        NumberFormat formatter = new DecimalFormat("#,###,###,###");
        String output = formatter.format(Double.valueOf(s));
        return output;
    }


}
