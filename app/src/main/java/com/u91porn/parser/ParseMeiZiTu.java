package com.u91porn.parser;

import android.text.TextUtils;

import com.orhanobut.logger.Logger;
import com.u91porn.data.model.BaseResult;
import com.u91porn.data.model.MeiZiTu;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

/**
 * @author flymegoc
 * @date 2018/1/25
 */

public class ParseMeiZiTu {
    private static final String TAG = ParseMeiZiTu.class.getSimpleName();

    public static BaseResult<List<MeiZiTu>> parseMeiZiTuList(String html, int page) {
        BaseResult<List<MeiZiTu>> baseResult = new BaseResult<>();
        baseResult.setTotalPage(1);
        Document doc = Jsoup.parse(html);
        Element ulPins = doc.getElementById("pins");
        Elements lis = ulPins.select("li");
        List<MeiZiTu> meiZiTuList = new ArrayList<>();
        for (Element li : lis) {
            MeiZiTu meiZiTu = new MeiZiTu();
            String contentUrl = li.select("a").first().attr("href");
            //meiZiTu.setContentUrl(contentUrl);
            int index = contentUrl.lastIndexOf("/");
            if (index >= 0 && index + 1 < contentUrl.length()) {
                String idStr = contentUrl.substring(index + 1, contentUrl.length());
                Logger.t(TAG).d(idStr);
                if (!TextUtils.isEmpty(idStr) && TextUtils.isDigitsOnly(idStr)) {
                    meiZiTu.setId(Integer.parseInt(idStr));
                }
            }
            Element imageElement = li.selectFirst("img");
            String name = imageElement.attr("alt");
            meiZiTu.setName(name);
            String thumbUrl = imageElement.attr("data-original");
            meiZiTu.setThumbUrl(thumbUrl);
            Logger.t(TAG).d(thumbUrl);
            int height = Integer.parseInt(imageElement.attr("height"));
            meiZiTu.setHeight(height);
            int width = Integer.parseInt(imageElement.attr("width"));
            meiZiTu.setWidth(width);
            String date = li.getElementsByClass("time").first().text();
            meiZiTu.setDate(date);
            String viewCount = li.getElementsByClass("view").first().text();
            meiZiTu.setViewCount(viewCount);
            meiZiTuList.add(meiZiTu);
        }
        Logger.t(TAG).d("size::" + meiZiTuList.size());
        if (page == 1) {
            Elements pageElements = doc.getElementsByClass("page-numbers");
            if (pageElements != null && pageElements.size() > 3) {
                String pageStr = pageElements.get(pageElements.size() - 2).text();
                Logger.t(TAG).d("totalPage::" + pageStr);
                if (!TextUtils.isEmpty(pageStr) && TextUtils.isDigitsOnly(pageStr)) {
                    baseResult.setTotalPage(Integer.parseInt(pageStr));
                }
            }
        }

        baseResult.setData(meiZiTuList);
        return baseResult;
    }

    public static BaseResult<List<String>> parsePicturePage(String html) {
        BaseResult<List<String>> baseResult = new BaseResult<>();

        Document doc = Jsoup.parse(html);

        Element pageElement = doc.getElementsByClass("pagenavi").first();

        Elements aElements = pageElement.select("a");
        int totalPage = 1;
        if (aElements != null && aElements.size() > 3) {
            String pageStr = aElements.get(aElements.size() - 2).text();
            if (!TextUtils.isEmpty(pageStr) && TextUtils.isDigitsOnly(pageStr)) {
                totalPage = Integer.parseInt(pageStr);
            }
        }

        List<String> imageUrlList = new ArrayList<>();

        String imageUrl = doc.getElementsByClass("main-image").first().selectFirst("img").attr("src");
        if (totalPage == 1) {
            imageUrlList.add(imageUrl);
        }
        for (int i = 1; i < totalPage + 1; i++) {
            String tmp;
            if (i < 10) {
                tmp = imageUrl.replace("01.", "0" + i + ".");
            } else {
                tmp = imageUrl.replace("01.", "" + i + ".");
            }
            imageUrlList.add(tmp);
        }
        baseResult.setData(imageUrlList);
        return baseResult;
    }
}
