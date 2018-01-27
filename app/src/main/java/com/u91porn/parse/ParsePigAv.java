package com.u91porn.parse;

/**
 * @author flymegoc
 * @date 2018/1/22
 */

public class ParsePigAv {
    /**
     *
     * @param html 原网页
     * @return json===
     */
    public static String parserVideoUrl(String html) {
        int startIndex = html.indexOf("setup");
        int endIndex = html.indexOf(".mp4");
        return html.substring(startIndex + 5, endIndex + 7);
    }
}
