package com.u91porn.parser;

import android.text.TextUtils;

import com.orhanobut.logger.Logger;
import com.u91porn.data.model.BaseResult;
import com.u91porn.data.model.UnLimit91PornItem;
import com.u91porn.data.model.User;
import com.u91porn.data.model.VideoComment;
import com.u91porn.data.model.VideoResult;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

/**
 * @author flymegoc
 * @date 2017/11/15
 * @describe
 */

public class Parse91PronVideo {

    private static final String TAG = Parse91PronVideo.class.getSimpleName();

    /**
     * 解析主页
     *
     * @param html 主页html
     * @return 视频列表
     */
    public static List<UnLimit91PornItem> parseIndex(String html) {
        List<UnLimit91PornItem> unLimit91PornItemList = new ArrayList<>();
        Document doc = Jsoup.parse(html);
        Element body = doc.getElementById("tab-featured");
        Elements itms = body.select("p");
        for (Element element : itms) {
            UnLimit91PornItem unLimit91PornItem = new UnLimit91PornItem();

            String title = element.getElementsByClass("title").first().text();
            unLimit91PornItem.setTitle(title);
            // Logger.d(title);

            String imgUrl = element.select("img").first().attr("src");
            unLimit91PornItem.setImgUrl(imgUrl);
            // Logger.d(imgUrl);

            String duration = element.getElementsByClass("duration").first().text();
            unLimit91PornItem.setDuration(duration);
            //Logger.d(duration);

            String contentUrl = element.select("a").first().attr("href");
            String viewKey = contentUrl.substring(contentUrl.indexOf("=") + 1);
            unLimit91PornItem.setViewKey(viewKey);
            // Logger.d(viewKey);

            String allInfo = element.text();
            int start = allInfo.indexOf("添加时间");
            String info = allInfo.substring(start);

            unLimit91PornItem.setInfo(info);
            // Logger.d(info);
            unLimit91PornItemList.add(unLimit91PornItem);
        }
        return unLimit91PornItemList;
    }

    /**
     * 解析其他类别
     *
     * @param html 类别
     * @return 列表
     */
    public static BaseResult<List<UnLimit91PornItem>> parseHot(String html) {
        int totalPage = 1;
        List<UnLimit91PornItem> unLimit91PornItemList = new ArrayList<>();
        Document doc = Jsoup.parse(html);
        Element body = doc.getElementById("fullside");

        Elements listchannel = body.getElementsByClass("listchannel");
        for (Element element : listchannel) {
            UnLimit91PornItem unLimit91PornItem = new UnLimit91PornItem();
            String contentUrl = element.select("a").first().attr("href");
            //Logger.d(contentUrl);
            contentUrl = contentUrl.substring(0, contentUrl.indexOf("&"));
            // Logger.d(contentUrl);
            String viewKey = contentUrl.substring(contentUrl.indexOf("=") + 1);
            unLimit91PornItem.setViewKey(viewKey);

            String imgUrl = element.select("a").first().select("img").first().attr("src");
            //  Logger.d(imgUrl);
            unLimit91PornItem.setImgUrl(imgUrl);

            String title = element.select("a").first().select("img").first().attr("title");
            //  Logger.d(title);
            unLimit91PornItem.setTitle(title);


            String allInfo = element.text();

            int sindex = allInfo.indexOf("时长");

            String duration = allInfo.substring(sindex + 3, sindex + 8);
            unLimit91PornItem.setDuration(duration);

            int start = allInfo.indexOf("添加时间");
            String info = allInfo.substring(start);
            unLimit91PornItem.setInfo(info.replace("还未被评分", ""));
            //  Logger.d(info);

            unLimit91PornItemList.add(unLimit91PornItem);
        }
        //总页数
        Element pagingnav = body.getElementById("paging");
        Elements a = pagingnav.select("a");
        if (a.size() > 2) {
            String ppp = a.get(a.size() - 2).text();
            if (TextUtils.isDigitsOnly(ppp)) {
                totalPage = Integer.parseInt(ppp);
                //    Logger.d("总页数：" + totalPage);
            }
        }
        BaseResult<List<UnLimit91PornItem>> baseResult = new BaseResult<>();
        baseResult.setTotalPage(totalPage);
        baseResult.setData(unLimit91PornItemList);
        return baseResult;
    }

    public static BaseResult<List<UnLimit91PornItem>> parseSearchVideos(String html) {
        int totalPage = 1;
        List<UnLimit91PornItem> unLimit91PornItemList = new ArrayList<>();
        Document doc = Jsoup.parse(html);
        Element body = doc.getElementById("fullside");
        if (body == null) {
            String errorMsg = parseErrorInfo(html);
            Logger.t(TAG).d(errorMsg);
            BaseResult baseResult = new BaseResult();

            baseResult.setCode(BaseResult.ERROR_CODE);
            baseResult.setMessage(errorMsg);
            return baseResult;
        }
        Elements listchannel = body.getElementsByClass("listchannel");
        for (Element element : listchannel) {
            UnLimit91PornItem unLimit91PornItem = new UnLimit91PornItem();
            String contentUrl = element.select("a").first().attr("href");
            //Logger.d(contentUrl);
            contentUrl = contentUrl.substring(0, contentUrl.indexOf("&"));
            //Logger.d(contentUrl);

            String viewKey = contentUrl.substring(contentUrl.indexOf("=") + 1);
            unLimit91PornItem.setViewKey(viewKey);
            //Logger.d(viewKey);

            String imgUrl = element.select("a").first().select("img").first().attr("src");
            //Logger.d(imgUrl);
            unLimit91PornItem.setImgUrl(imgUrl);

            String title = element.select("span.title").text();
            //Logger.d(title);
            unLimit91PornItem.setTitle(title);

            String duration = element.select("span.duration").text();
            unLimit91PornItem.setDuration(duration);

            String allInfo = element.text();

            int start = allInfo.indexOf("添加时间");
            String info = allInfo.substring(start);
            unLimit91PornItem.setInfo(info.replace("还未被评分", ""));
            //Logger.d(info);

            unLimit91PornItemList.add(unLimit91PornItem);
        }
        //总页数
        Element pagingnav = body.getElementById("paging");
        Elements a = pagingnav.select("a");
        if (a.size() > 2) {
            String ppp = a.get(a.size() - 2).text();
            if (TextUtils.isDigitsOnly(ppp)) {
                totalPage = Integer.parseInt(ppp);
                //Logger.d("总页数：" + totalPage);
            }
        }
        BaseResult<List<UnLimit91PornItem>> baseResult = new BaseResult<>();
        baseResult.setTotalPage(totalPage);
        baseResult.setData(unLimit91PornItemList);
        return baseResult;
    }

    /**
     * 解析视频播放连接
     *
     * @param html 视频页
     * @return 视频连接
     */
    public static VideoResult parseVideoPlayUrl(String html) {
        VideoResult videoResult = new VideoResult();
        if (html.contains("你每天只可观看10个视频")) {
            Logger.d("已经超出观看上限了");
            //设置标志位,用于上传日志
            videoResult.setId(VideoResult.OUT_OF_WATCH_TIMES);
            return videoResult;
        }
        Document doc = Jsoup.parse(html);
        String videoUrl = doc.select("video").first().select("source").first().attr("src");
        videoResult.setVideoUrl(videoUrl);
        Logger.t(TAG).d("视频链接：" + videoUrl);

        int startIndex = videoUrl.lastIndexOf("/");
        int endIndex = videoUrl.indexOf(".mp4");
        String videoId = videoUrl.substring(startIndex + 1, endIndex);
        videoResult.setVideoId(videoId);
        Logger.t(TAG).d("视频Id：" + videoId);

        String ownnerUrl = doc.select("a[href*=UID]").first().attr("href");
        String ownnerId = ownnerUrl.substring(ownnerUrl.indexOf("=") + 1, ownnerUrl.length());
        videoResult.setOwnnerId(ownnerId);
        Logger.t(TAG).d("作者Id：" + ownnerId);

        String ownnerName = doc.select("a[href*=UID]").first().text();
        videoResult.setOwnnerName(ownnerName);
        Logger.t(TAG).d("作者：" + ownnerName);

        String allInfo = doc.getElementById("videodetails-content").text();
        String addDate = allInfo.substring(allInfo.indexOf("添加时间"), allInfo.indexOf("作者"));
        videoResult.setAddDate(addDate);
        Logger.t(TAG).d("添加时间：" + addDate);

        String otherInfo = allInfo.substring(allInfo.indexOf("注册"), allInfo.indexOf("简介"));
        videoResult.setUserOtherInfo(otherInfo);
        Logger.t(TAG).d(otherInfo);

        String thumImg = doc.getElementById("vid").attr("poster");
        videoResult.setThumbImgUrl(thumImg);
        Logger.t(TAG).d("缩略图：" + thumImg);

        String videoName = doc.getElementById("viewvideo-title").text();
        videoResult.setVideoName(videoName);
        Logger.t(TAG).d("视频标题：" + videoName);

        return videoResult;
    }

    /**
     * 解析登录用户信息
     *
     * @return 用户
     */
    public static User parseUserInfo(String html) {
        User user = new User();
        Document doc = Jsoup.parse(html);
        //新帐号注册成功登录后信息不一样，导致无法解析
        Element element = doc.getElementById("userinfo-title");
        if (element == null) {
            return null;
        }
        String userLinks = element.select("a").first().attr("href");
        String userAccountStatus = doc.getElementById("userinfo-title").select("font").first().text();
        String userName = doc.getElementById("userinfo-title").select("a").first().text();
        Logger.t(TAG).d(userLinks);
        Logger.t(TAG).d(userAccountStatus);
        Logger.t(TAG).d(userName);

        int uid = Integer.parseInt(userLinks.substring(userLinks.indexOf("=") + 1, userLinks.length()));
        user.setUserId(uid);
        user.setStatus(userAccountStatus);
        user.setUserName(userName);
        Logger.t(TAG).d(uid);

        String userContent = doc.getElementById("userinfo-content").text();
        Logger.t(TAG).d(userContent);

        String lastLoginTime = userContent.substring(userContent.indexOf("最后登录"), userContent.indexOf("IP:"));
        String lastLoginIP = userContent.substring(userContent.indexOf("IP:"), userContent.indexOf("点此查看"));
        user.setLastLoginTime(lastLoginTime);
        user.setLastLoginIP(lastLoginIP);

        Logger.t(TAG).d(lastLoginTime);
        Logger.t(TAG).d(lastLoginIP);

        return user;
    }

    /**
     * 解析我的收藏
     *
     * @param html html
     * @return list
     */
    public static BaseResult<List<UnLimit91PornItem>> parseMyFavorite(String html) {
        int totalPage = 1;
        List<UnLimit91PornItem> unLimit91PornItemList = new ArrayList<>();
        Document doc = Jsoup.parse(html);
        Element body = doc.getElementById("leftside");

        Elements videos = doc.select("div.myvideo");

        for (Element element : videos) {

            UnLimit91PornItem unLimit91PornItem = new UnLimit91PornItem();

            String contentUrl = element.select("a").first().attr("href");

            String viewKey = contentUrl.substring(contentUrl.indexOf("=") + 1, contentUrl.length());
            unLimit91PornItem.setViewKey(viewKey);
            Logger.t(TAG).d(viewKey);

            String title = element.select("strong").first().text();
            unLimit91PornItem.setTitle(title);
            Logger.t(TAG).d(title);

            String imgUrl = element.select("img").first().attr("src");
            unLimit91PornItem.setImgUrl(imgUrl);
            Logger.t(TAG).d(imgUrl);

            String allInfo = element.text();
            Logger.t(TAG).d(allInfo);

            String duration = allInfo.substring(allInfo.indexOf("时长") + 3, allInfo.indexOf("Views") - 3);
            unLimit91PornItem.setDuration(duration);
            Logger.t(TAG).d(duration);

            String info = allInfo.substring(allInfo.indexOf("添加时间"), allInfo.length());
            unLimit91PornItem.setInfo(info);
            Logger.t(TAG).d(info);

            String rvid = element.select("input").first().attr("value");
            Logger.t(TAG).d("rvid::" + rvid);
            VideoResult videoResult = new VideoResult();
            videoResult.setId(VideoResult.OUT_OF_WATCH_TIMES);
            videoResult.setVideoId(rvid);
            unLimit91PornItem.setVideoResult(videoResult);

            unLimit91PornItemList.add(unLimit91PornItem);
        }

        //总页数
        Element pagingnav = body.getElementById("paging");
        Elements a = pagingnav.select("a");
        if (a.size() >= 2) {
            String ppp = a.get(a.size() - 2).text();
            if (TextUtils.isDigitsOnly(ppp)) {
                totalPage = Integer.parseInt(ppp);
                Logger.d("总页数：" + totalPage);
            }
        }
        BaseResult<List<UnLimit91PornItem>> baseResult = new BaseResult<>();
        //尝试解析删除信息
        Elements msgElements = doc.select("div.msgbox");
        if (msgElements != null) {
            String msgInfo = msgElements.text();
            if (!TextUtils.isEmpty(msgInfo)) {
                baseResult.setCode(BaseResult.SUCCESS_CODE);
                baseResult.setMessage(msgInfo);
            }
        } else {
            String errorMsg = parseErrorInfo(html);
            if (!TextUtils.isEmpty(errorMsg)) {
                baseResult.setMessage(errorMsg);
                baseResult.setCode(BaseResult.ERROR_CODE);
            }
        }

        baseResult.setTotalPage(totalPage);
        baseResult.setData(unLimit91PornItemList);

        return baseResult;
    }

    /**
     * 解析作者更多视频
     *
     * @param html html
     * @return list
     */
    public static BaseResult<List<UnLimit91PornItem>> parseAuthorVideos(String html) {
        int totalPage = 1;
        List<UnLimit91PornItem> unLimit91PornItemList = new ArrayList<>();
        Document doc = Jsoup.parse(html);
        Element body = doc.getElementById("leftside");

        Elements videos = doc.select("div.myvideo");

        for (Element element : videos) {

            UnLimit91PornItem unLimit91PornItem = new UnLimit91PornItem();

            String contentUrl = element.select("a").first().attr("href");

            String viewKey = contentUrl.substring(contentUrl.indexOf("=") + 1, contentUrl.length());
            unLimit91PornItem.setViewKey(viewKey);
            //Logger.t(TAG).d(viewKey);

            String title = element.select("strong").first().text();
            unLimit91PornItem.setTitle(title);
            //Logger.t(TAG).d(title);

            String imgUrl = element.select("img").first().attr("src");
            unLimit91PornItem.setImgUrl(imgUrl);
            //Logger.t(TAG).d(imgUrl);

            String allInfo = element.text();
            //Logger.t(TAG).d(allInfo);

            String duration = allInfo.substring(allInfo.indexOf("时长") + 3, allInfo.indexOf("查看") - 3);
            unLimit91PornItem.setDuration(duration);
            //Logger.t(TAG).d(duration);

            String info = allInfo.substring(allInfo.indexOf("添加时间"), allInfo.length());
            unLimit91PornItem.setInfo(info);
            //Logger.t(TAG).d(info);

            unLimit91PornItemList.add(unLimit91PornItem);
        }

        //总页数
        Element pagingnav = body.getElementById("paging");
        Elements a = pagingnav.select("a");
        if (a.size() >= 2) {
            String ppp = a.get(a.size() - 2).text();
            if (TextUtils.isDigitsOnly(ppp)) {
                totalPage = Integer.parseInt(ppp);
                //Logger.d("总页数：" + totalPage);
            }
        }

        BaseResult<List<UnLimit91PornItem>> baseResult = new BaseResult<>();
        baseResult.setTotalPage(totalPage);
        baseResult.setData(unLimit91PornItemList);

        return baseResult;
    }

    /**
     * 解析错误提示
     *
     * @param html html
     * @return 错误洗洗脑
     */
    public static String parseErrorInfo(String html) {
        String errorInfo = "";
        Document doc = Jsoup.parse(html);
        Elements errorElements = doc.select("div.errorbox");
        if (errorElements != null) {
            errorInfo = errorElements.text();
        }
        return errorInfo;
    }

    /**
     * 解析视频评论
     *
     * @param html 评论html
     * @return 评论列表
     */
    public static List<VideoComment> parseVideoComment(String html) {
        List<VideoComment> videoCommentList = new ArrayList<>();
        Document doc = Jsoup.parse(html);
        Elements elements = doc.select("table.comment-divider");
        for (Element element : elements) {
            VideoComment videoComment = new VideoComment();

            String ownnerUrl = element.select("a[href*=UID]").first().attr("href");
            String uid = ownnerUrl.substring(ownnerUrl.indexOf("=") + 1, ownnerUrl.length());
            videoComment.setUid(uid);
            //Logger.t(TAG).d(uid);

            String uName = element.select("a[href*=UID]").first().text();
            videoComment.setuName(uName);
            Logger.t(TAG).d(uName);

            String replyTime = element.select("span.comment-info").first().text();
            videoComment.setReplyTime(replyTime.replace("(", "").replace(")", ""));
            // Logger.t(TAG).d(replyTime);

            String tmpreplyId = element.select("div.comment-body").first().attr("id");
            String replyId = tmpreplyId.substring(tmpreplyId.lastIndexOf("_") + 1, tmpreplyId.length());
            videoComment.setReplyId(replyId);
            // Logger.t(TAG).d("replyId:" + replyId);

            String comment = element.select("div.comment-body").first().text().replace("举报", "").replace("Show", "");
//            videoComment.setContentMessage(comment.replace("Show", ""));
            //Logger.t(TAG).d(comment);

            List<String> tmpQuoteList = new ArrayList<>();
            tmpQuoteList.add(comment);
            Elements quotes = element.select("div.comment-body").select("div.comment_quote");
            for (Element element1 : quotes) {
                String quote = element1.text();
                tmpQuoteList.add(quote);
            }

            List<String> quoteList = new ArrayList<>();
            for (int i = 0; i < tmpQuoteList.size(); i++) {
                String quote;
                if (i + 1 >= tmpQuoteList.size()) {
                    quote = tmpQuoteList.get(i);
                    quoteList.add(0, quote.trim());
                    //Logger.t(TAG).d(quote);
                    break;
                }
                quote = tmpQuoteList.get(i).replace(tmpQuoteList.get(i + 1), "");
                quoteList.add(0, quote.trim());
                //Logger.t(TAG).d(quote);
            }

            videoComment.setCommentQuoteList(quoteList);

            String info = element.select("td").first().text();
            String titleInfo = info.substring(0, info.indexOf("("));
            videoComment.setTitleInfo(titleInfo.replace(uName, ""));
            // Logger.t(TAG).d(titleInfo);

            videoCommentList.add(videoComment);
        }
        return videoCommentList;
    }
}
