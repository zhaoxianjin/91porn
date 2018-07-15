package com.u91porn.parser;

import android.text.TextUtils;

import com.orhanobut.logger.Logger;
import com.u91porn.data.model.BaseResult;
import com.u91porn.data.model.PigAv;
import com.u91porn.utils.StringUtils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

/**
 * @author flymegoc
 * @date 2018/1/22
 */

public class ParsePigAv {
    /**
     * @param html 原网页
     * @return json===
     */
    public static BaseResult<List<PigAv>> parserVideoUrl(String html) {
        BaseResult<List<PigAv>> baseResult = new BaseResult<>();
        Document document = Jsoup.parse(html);
        Element videoWrapper = document.getElementsByClass("td-post-content td-pb-padding-side").first();
        String videoHtml = videoWrapper.html();
        Logger.d(videoHtml);
        int index = videoHtml.indexOf("setup") + 6;
        int endIndexV = videoHtml.indexOf(");");
        String videoUrl = videoHtml.substring(index, endIndexV);
        Logger.d(videoUrl);

        Elements items = document.getElementsByClass("td-block-span12");
        List<PigAv> pigAvList = new ArrayList<>();
        for (Element element : items) {
            PigAv pigAv = new PigAv();
            Element a = element.selectFirst("a");
            String title = a.attr("title");
            pigAv.setTitle(title);
            String contentUrl = a.attr("href");
            pigAv.setContentUrl(contentUrl);
            Element img = element.selectFirst("img");
            String imgUrl = img.attr("src");
            int beginIndex = imgUrl.lastIndexOf("/");
            int endIndex = imgUrl.indexOf("-");
            String bigImg = StringUtils.subString(imgUrl, 0, endIndex);
            if (TextUtils.isEmpty(bigImg)) {
                pigAv.setImgUrl(imgUrl);
            } else {
                pigAv.setImgUrl(bigImg + ".jpg");
            }
            String pId = StringUtils.subString(imgUrl, beginIndex + 1, endIndex);
            Logger.d(pId);
            pigAv.setpId(pId);

            int imgWidth = Integer.parseInt(img.attr("width"));
            pigAv.setImgWidth(imgWidth);
            int imgHeight = Integer.parseInt(img.attr("height"));
            pigAv.setImgHeight(imgHeight);
            pigAvList.add(pigAv);
        }
        baseResult.setData(pigAvList);
        baseResult.setMessage(videoUrl);
        return baseResult;
    }

    public static BaseResult<List<PigAv>> videoList(String html) {
        BaseResult<List<PigAv>> baseResult = new BaseResult<>();
        baseResult.setTotalPage(1);

        Document doc = Jsoup.parse(html);
        Elements items = doc.getElementsByClass("td-block-span4");
        List<PigAv> pigAvList = new ArrayList<>();
        for (Element element : items) {
            PigAv pigAv = new PigAv();
            Element a = element.selectFirst("a");
            String title = a.attr("title");
            pigAv.setTitle(title);
            String contentUrl = a.attr("href");
            pigAv.setContentUrl(contentUrl);
            Element img = element.selectFirst("img");
            String imgUrl = img.attr("src");
            int beginIndex = imgUrl.lastIndexOf("/");
            int endIndex = imgUrl.lastIndexOf("-");
            String bigImg = StringUtils.subString(imgUrl, 0, endIndex);
            if (TextUtils.isEmpty(bigImg)) {
                pigAv.setImgUrl(imgUrl);
            } else {
                pigAv.setImgUrl(bigImg + ".jpg");
            }
            String pId = StringUtils.subString(imgUrl, beginIndex + 1, endIndex);
            Logger.d(pId);
            pigAv.setpId(pId);

            int imgWidth = Integer.parseInt(img.attr("width"));
            pigAv.setImgWidth(imgWidth);
            int imgHeight = Integer.parseInt(img.attr("height"));
            pigAv.setImgHeight(imgHeight);
            pigAvList.add(pigAv);
        }
        baseResult.setData(pigAvList);
        return baseResult;
    }

    public static BaseResult<List<PigAv>> moreVideoList(String html) {
        BaseResult<List<PigAv>> baseResult = new BaseResult<>();
        baseResult.setTotalPage(1);

        Document doc = Jsoup.parse(html);
        Elements items = doc.getElementsByClass("td-block-span4");
        List<PigAv> pigAvList = new ArrayList<>();
        for (Element element : items) {
            PigAv pigAv = new PigAv();
            Element a = element.selectFirst("a");
            String title = a.attr("title");
            pigAv.setTitle(title);
            String contentUrl = a.attr("href");
            pigAv.setContentUrl(contentUrl);
            Element img = element.selectFirst("img");
            String imgUrl = img.attr("src");
            int beginIndex = imgUrl.lastIndexOf("/");
            int endIndex = imgUrl.lastIndexOf("-");
            String bigImg = StringUtils.subString(imgUrl, 0, endIndex);
            if (TextUtils.isEmpty(bigImg)) {
                pigAv.setImgUrl(imgUrl);
            } else {
                pigAv.setImgUrl(bigImg + ".jpg");
            }
            String pId = StringUtils.subString(imgUrl, beginIndex + 1, endIndex);
            Logger.d(pId);
            pigAv.setpId(pId);

            int imgWidth = Integer.parseInt(img.attr("width"));
            pigAv.setImgWidth(imgWidth);
            int imgHeight = Integer.parseInt(img.attr("height"));
            pigAv.setImgHeight(imgHeight);
            pigAvList.add(pigAv);
        }
        baseResult.setData(pigAvList);
        return baseResult;
    }
}
