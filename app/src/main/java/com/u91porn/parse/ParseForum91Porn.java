package com.u91porn.parse;

import android.text.TextUtils;

import com.orhanobut.logger.Logger;
import com.u91porn.adapter.BaseHeaderAdapter;
import com.u91porn.data.Api;
import com.u91porn.data.model.BaseResult;
import com.u91porn.data.model.Content91Porn;
import com.u91porn.data.model.Forum91PronItem;
import com.u91porn.data.model.PinnedHeaderEntity;
import com.u91porn.utils.AddressHelper;
import com.u91porn.utils.AppUtils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

/**
 * @author flymegoc
 * @date 2018/1/23
 */

public class ParseForum91Porn {
    private static final String TAG = ParseForum91Porn.class.getSimpleName();

    public static BaseResult<List<PinnedHeaderEntity<Forum91PronItem>>> parseIndex(String html) {
        BaseResult<List<PinnedHeaderEntity<Forum91PronItem>>> baseResult = new BaseResult<>();
        Document doc = Jsoup.parse(html);
        Elements tds = doc.getElementsByAttributeValue("background", "images/listbg.gif");
        List<PinnedHeaderEntity<Forum91PronItem>> forum91PornItemSectionList = new ArrayList<>();
        for (Element td : tds) {
            Elements elements = td.select("a");
            if (td.select("a").first().attr("title").contains("最新精华")) {
                PinnedHeaderEntity<Forum91PronItem> pronItemPinnedHeaderEntity = new PinnedHeaderEntity<>(null, BaseHeaderAdapter.TYPE_HEADER, "最新精华");
                forum91PornItemSectionList.add(pronItemPinnedHeaderEntity);
            } else if (td.select("a").first().attr("title").contains("最新回复")) {
                PinnedHeaderEntity<Forum91PronItem> pronItemPinnedHeaderEntity = new PinnedHeaderEntity<>(null, BaseHeaderAdapter.TYPE_HEADER, "最新回复");
                forum91PornItemSectionList.add(pronItemPinnedHeaderEntity);
            } else {
                PinnedHeaderEntity<Forum91PronItem> pronItemPinnedHeaderEntity = new PinnedHeaderEntity<>(null, BaseHeaderAdapter.TYPE_HEADER, "本周热门");
                forum91PornItemSectionList.add(pronItemPinnedHeaderEntity);
            }

            for (Element element : elements) {
                Forum91PronItem forum91PronItem = new Forum91PronItem();
                String allInfo = element.attr("title").replaceAll("\n", "");
                int titleIndex = allInfo.indexOf("主题标题:");
                int authorIndex = allInfo.indexOf("主题作者:");
                int authorPublishTimeIndex = allInfo.indexOf("发表时间:");
                int viewCountIndex = allInfo.indexOf("浏览次数:");
                int replyCountIndex = allInfo.indexOf("回复次数:");
                int lastPostTimeIndex = allInfo.indexOf("最后回复:");
                int lastPostAuthorIndex = allInfo.indexOf("最后发表:");

                String title = allInfo.substring(titleIndex + 5, authorIndex);
                String author = allInfo.substring(authorIndex + 5, authorPublishTimeIndex);
                String authorPublishTime = allInfo.substring(authorPublishTimeIndex + 5, viewCountIndex);
                String viewCount = allInfo.substring(viewCountIndex + 5, replyCountIndex).replace("次", "").trim();
                String replyCount = allInfo.substring(replyCountIndex + 5, lastPostTimeIndex).replace("次", "").trim();
                try {
                    //新主题这部分会缺失
                    String lastPostTime = allInfo.substring(lastPostTimeIndex + 5, lastPostAuthorIndex);
                    String lastPostAuthor = allInfo.substring(lastPostAuthorIndex + 5, allInfo.length());
                    forum91PronItem.setLastPostTime(lastPostTime);
                    forum91PronItem.setLastPostAuthor(lastPostAuthor);
                } catch (Exception e) {
                    forum91PronItem.setLastPostTime("---");
                    forum91PronItem.setLastPostAuthor("---");
                    e.printStackTrace();
                }


                forum91PronItem.setTitle(title);
                forum91PronItem.setAuthor(author);
                forum91PronItem.setViewCount(Long.parseLong(viewCount));
                forum91PronItem.setReplyCount(Long.parseLong(replyCount));
                forum91PronItem.setAuthorPublishTime(authorPublishTime);


                String contentUrl = element.attr("href");
                int starIndex = contentUrl.indexOf("tid=");
                String tidStr = null;
                if (starIndex >= 0) {
                    tidStr = contentUrl.substring(starIndex + 4, contentUrl.length());
                }
                if (!TextUtils.isEmpty(tidStr) && TextUtils.isDigitsOnly(tidStr)) {
                    forum91PronItem.setTid(Long.parseLong(tidStr));
                }

                PinnedHeaderEntity<Forum91PronItem> pronItemPinnedHeaderEntity = new PinnedHeaderEntity<>(forum91PronItem, BaseHeaderAdapter.TYPE_DATA, "");
                forum91PornItemSectionList.add(pronItemPinnedHeaderEntity);
            }
        }
        baseResult.setData(forum91PornItemSectionList);
        return baseResult;
    }

    public static BaseResult<List<Forum91PronItem>> parseForumList(String html, int currentPage) {
        BaseResult<List<Forum91PronItem>> baseResult = new BaseResult<>();
        baseResult.setTotalPage(1);
        Document doc = Jsoup.parse(html);
        Element table = doc.getElementsByClass("datatable").first();
        Elements tbodys = table.select("tbody");
        List<Forum91PronItem> forum91PronItemList = new ArrayList<>();
        boolean contentStart = false;
        for (Element tbody : tbodys) {
            Forum91PronItem forum91PronItem = new Forum91PronItem();

            Element th = tbody.select("th").first();

            if (!contentStart && currentPage == 1) {
                if (th.text().contains("版块主题")) {
                    contentStart = true;
                }
                continue;
            }

            if (th != null) {
                String title = th.select("a").first().text();
                forum91PronItem.setTitle(title);
                String contentUrl = th.select("a").first().attr("href");
                int starIndex = contentUrl.indexOf("tid=");
                int endIndex = contentUrl.indexOf("&");
                String tidStr = null;
                if (starIndex >= 0 && endIndex <= contentUrl.length()) {
                    tidStr = contentUrl.substring(starIndex + 4, endIndex);
                    Logger.t(TAG).d("contentUrl:" + contentUrl);
                    Logger.t(TAG).d("tidStr:" + tidStr);
                }
                if (!TextUtils.isEmpty(tidStr) && TextUtils.isDigitsOnly(tidStr)) {
                    forum91PronItem.setTid(Long.parseLong(tidStr));
                }
                Elements imageElements = th.select("img");
                List<String> stringList = null;
                for (Element element : imageElements) {
                    if (stringList == null) {
                        stringList = new ArrayList<>();
                    }
                    stringList.add(element.attr("src"));
                }
                forum91PronItem.setImageList(stringList);

                Elements agreeElements = th.select("font");
                if (agreeElements != null && agreeElements.size() >= 1) {
                    String agreeCount = th.select("font").last().text();
                    forum91PronItem.setAgreeCount(agreeCount);
                }
            }

            Elements tds = tbody.select("td");
            for (Element td : tds) {
                switch (td.className()) {
                    case "folder":
                        String folder = td.select("img").attr("src");
                        forum91PronItem.setFolder(folder);
                        break;
                    case "icon":
                        Element iconElement = td.select("img").first();
                        if (iconElement != null) {
                            String icon = iconElement.attr("src");
                            forum91PronItem.setIcon(icon);
                        }
                        break;
                    case "author":
                        String author = td.select("a").first().text();
                        String authorPubishTime = td.select("em").first().text();
                        forum91PronItem.setAuthor(author);
                        forum91PronItem.setAuthorPublishTime(authorPubishTime);
                        break;
                    case "nums":
                        String replyCount = td.select("strong").first().text();
                        String viewCount = td.select("em").first().text();
                        if (!TextUtils.isEmpty(replyCount) && TextUtils.isDigitsOnly(replyCount)) {
                            forum91PronItem.setReplyCount(Long.parseLong(replyCount));
                        }
                        if (!TextUtils.isEmpty(viewCount) && TextUtils.isDigitsOnly(viewCount)) {
                            forum91PronItem.setViewCount(Long.parseLong(viewCount));
                        }
                        break;
                    case "lastpost":
                        String lastPostAuthor = td.select("a").first().text();
                        String lastPostTime = td.select("em").first().text();
                        forum91PronItem.setLastPostAuthor(lastPostAuthor);
                        forum91PronItem.setLastPostTime(lastPostTime);
                        break;
                    default:
                }

            }
            forum91PronItemList.add(forum91PronItem);
        }
        if (currentPage == 1) {
            Element pageElement = doc.getElementsByClass("pages").first();
            String page = pageElement.getElementsByClass("last").first().text().replace("...", "").trim();
            Logger.t(TAG).d("totalPage:::" + page);
            if (!TextUtils.isEmpty(page) && TextUtils.isDigitsOnly(page)) {
                baseResult.setTotalPage(Integer.parseInt(page));
            }
        }
        baseResult.setData(forum91PronItemList);
        return baseResult;
    }

    public static BaseResult<Content91Porn> parseContent(String html) {
        BaseResult<Content91Porn> baseResult = new BaseResult<>();
        Document doc = Jsoup.parse(html);
        Element content = doc.getElementsByClass("t_msgfontfix").first();

        if (content == null) {
            List<String> stringList = new ArrayList<>();
            Content91Porn content91Porn = new Content91Porn();
            content91Porn.setImageList(stringList);
            content91Porn.setContent(AppUtils.buildHtml("暂不支持解析该网页类型"));
            baseResult.setData(content91Porn);
            return baseResult;
        }

        Elements attachPopups = doc.getElementsByClass("imgtitle");
        if (attachPopups != null) {
            for (Element element : attachPopups) {
                element.html("");
            }
        }

        Elements imagesElements = content.select("img");
        List<String> stringList = new ArrayList<>();
        for (Element element : imagesElements) {
            String imgUrl = null;
            if (!TextUtils.isEmpty(element.attr("file"))) {
                imgUrl = AddressHelper.getInstance().getForum91PornAddress() + element.attr("file");
                element.attr("src", imgUrl);
            }
            element.attr("width", "100%");
            element.attr("style", "margin-top: 1em;");
            element.attr("onclick", "HostApp.toast(\"" + imgUrl + "\")");
            if (!TextUtils.isEmpty(imgUrl)) {
                stringList.add(imgUrl);
            }
        }
        Logger.d(content);
        Content91Porn content91Porn = new Content91Porn();
        String contentStr = AppUtils.buildHtml(content.html().replace("<dd", "<dt").replace("</dd>", "</dt>"));
        content91Porn.setContent(contentStr);
        content91Porn.setImageList(stringList);
        baseResult.setData(content91Porn);
        return baseResult;
    }
}
