package com.huaxi.hailuo.util;


import android.content.Context;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;

import com.huaxi.hailuo.R;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtil {


    public static String getTianShenCardNum(String cardNum) {
        int content = 4;
        StringBuilder sb = new StringBuilder();
        char[] cs = cardNum.toCharArray();
        for (int i = 0; i < cs.length; i++) {
            System.out.println(cs[i]);
            if (i != 0 && i % content == 0) {
                sb.append(" ");
            }
            sb.append(cs[i]);
        }
        return sb.toString();
    }


    /**
     * 给手机号显示
     */
    public static String encryptPhoneNum(String phoneNum) {
        char[] array = phoneNum.toCharArray();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < array.length; i++) {
            if (i == 3 || i == 4 || i == 5 || i == 6) {
                sb.append("*");
            } else {
                sb.append(array[i]);
            }
        }
        return sb.toString();
    }

    public static String getEndBankCard(String cardNum) {
        char[] array = cardNum.toCharArray();
        StringBuilder sb = new StringBuilder();
        for (int i = array.length - 4; i < array.length; i++) {
            sb.append(array[i]);
        }
        return sb.toString();
    }


    //提取字符串中的数字
    public static String getNumFromStr(String str) {
        String regEx = "[^0-9]";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(str);
        return m.replaceAll("").trim();
    }

    //截取数字
    public static String getNumbers(String content) {
        Pattern pattern = Pattern.compile("\\d+");
        Matcher matcher = pattern.matcher(content);
        while (matcher.find()) {
            return matcher.group(0);
        }
        return "";
    }

    public static CharSequence setColor(Context context, String text, String text1, String text2) {
        SpannableStringBuilder style = new SpannableStringBuilder(text);
        style.setSpan(new ForegroundColorSpan(context.getResources().getColor(R.color.colorPrimary)), 0, text1.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        style.setSpan(new ForegroundColorSpan(context.getResources().getColor(R.color.colorAccent)), text1.length() + 6, text1.length() + 6 + text2.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return style;
    }

}


