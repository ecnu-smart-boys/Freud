package org.ecnusmartboys.infrastructure.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Pattern;

public class Validator {
    private static final int[] WEIGHTING = new int[]{7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2};
    private final static int ID_LENGTH = 17;
    private final static int NUMBER_0 = 48;
    private final static char[] CHECK_CODE_LIST = new char[]{'1', '0', 'X', '9', '8', '7', '6', '5', '4', '3', '2'};
    public final static String PATTERN_PHONE_STR = "^(13[0-9]|14[01456879]|15[0-35-9]|16[2567]|17[0-8]|18[0-9]|19[0-35-9])\\d{8}$";
    private final static Pattern PATTERN_PHONE = Pattern.compile(PATTERN_PHONE_STR);

    public static boolean validateID(String id) {
        if (id != null && !id.isEmpty()) {
            id = id.trim();
            if (id.length() != 18) {
                return false;
            } else {
                char[] idCharts = id.toCharArray();
                char validChart = idCharts[ID_LENGTH];
                int sum = 0;

                for (int i = 0; i < 17; ++i) {
                    int value = idCharts[i] - NUMBER_0;
                    sum += WEIGHTING[i] * value;
                }

                return CHECK_CODE_LIST[sum % 11] == validChart;
            }
        } else {
            return false;
        }
    }

    public static int countAge(String idNumber) {
        if (idNumber.length() != 18 && idNumber.length() != 15) {
            try {
                throw new IllegalArgumentException("身份证号长度错误");
            } catch (Exception var10) {
                return 35;
            }
        } else {
            String year;
            String yue;
            String day;
            if (idNumber.length() == 18) {
                year = idNumber.substring(6).substring(0, 4);
                yue = idNumber.substring(10).substring(0, 2);
                day = idNumber.substring(12).substring(0, 2);
            } else {
                year = "19" + idNumber.substring(6, 8);
                yue = idNumber.substring(8, 10);
                day = idNumber.substring(10, 12);
            }

            Date date = new Date();
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            String fyear = format.format(date).substring(0, 4);
            String fyue = format.format(date).substring(5, 7);
            String fday = format.format(date).substring(8, 10);
            int age = 0;
            if (Integer.parseInt(yue) == Integer.parseInt(fyue)) {
                if (Integer.parseInt(day) <= Integer.parseInt(fday)) {
                    age = Integer.parseInt(fyear) - Integer.parseInt(year);
                }
            } else if (Integer.parseInt(yue) < Integer.parseInt(fyue)) {
                age = Integer.parseInt(fyear) - Integer.parseInt(year);
            } else {
                age = Integer.parseInt(fyear) - Integer.parseInt(year) - 1;
            }

            return age;
        }
    }

    public static boolean validatePhone(String phone) {
        if (phone == null || phone.length() != 11) {
            return false;
        }
        return PATTERN_PHONE.matcher(phone).matches();
    }
}
