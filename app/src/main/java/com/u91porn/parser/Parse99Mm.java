package com.u91porn.parser;

import android.text.TextUtils;

import com.orhanobut.logger.Logger;
import com.u91porn.data.model.BaseResult;
import com.u91porn.data.model.Mm99;
import com.u91porn.utils.StringUtils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

/**
 * @author flymegoc
 * @date 2018/2/1
 */

public class Parse99Mm {
    private static final String TAG = Parse99Mm.class.getSimpleName();

    public static BaseResult<List<Mm99>> parse99MmList(String html, int page) {
        BaseResult<List<Mm99>> baseResult = new BaseResult<>();
        baseResult.setTotalPage(1);
        Logger.t(TAG).d(html);
        Document doc = Jsoup.parse(html);
        Element ul = doc.getElementById("piclist");
        Elements lis = ul.select("li");
        List<Mm99> mm99List = new ArrayList<>();
        for (Element li : lis) {
            Mm99 mm99 = new Mm99();
            Element a = li.selectFirst("dt").selectFirst("a");
            String contentUrl = "http://www.99mm.me" + a.attr("href");
            mm99.setContentUrl(contentUrl);

            int startIndex = contentUrl.lastIndexOf("/");
            int endIndex = contentUrl.lastIndexOf(".");
            String idStr = StringUtils.subString(contentUrl, startIndex + 1, endIndex);

            if (!TextUtils.isEmpty(idStr) && TextUtils.isDigitsOnly(idStr)) {
                mm99.setId(Integer.parseInt(idStr));
            } else {
                Logger.t(TAG).d(idStr);
            }

            Element img = a.selectFirst("img");
            String title = img.attr("alt");
            mm99.setTitle(title);
            String imgUrl = img.attr("src");
            mm99.setImgUrl(imgUrl);
            int imgWidth = Integer.parseInt(img.attr("width"));
            mm99.setImgWidth(imgWidth);

            mm99List.add(mm99);
        }

        if (page == 1) {
            Element pageElement = doc.getElementsByClass("all").first();
            if (pageElement != null) {
                String pageStr = pageElement.text().replace("...", "").trim();
                if (!TextUtils.isEmpty(pageStr) && TextUtils.isDigitsOnly(pageStr)) {
                    baseResult.setTotalPage(Integer.parseInt(pageStr));
                } else {
                    Logger.t(TAG).d(pageStr);
                }
            }
        }

        baseResult.setData(mm99List);
        return baseResult;
    }
}
