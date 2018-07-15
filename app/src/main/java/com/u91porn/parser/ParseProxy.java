package com.u91porn.parser;

import android.text.TextUtils;

import com.orhanobut.logger.Logger;
import com.u91porn.data.model.BaseResult;
import com.u91porn.data.model.ProxyModel;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

/**
 * 代理抓取
 *
 * @author flymegoc
 * @date 2018/1/20
 */

public class ParseProxy {
    private static final String TAG = ParseProxy.class.getSimpleName();

    public static BaseResult<List<ProxyModel>> parseGouBanJia(String html) {
        BaseResult<List<ProxyModel>> baseResult = new BaseResult<>();
        baseResult.setTotalPage(1);
        Document doc = Jsoup.parse(html);
        Element element = doc.getElementById("list");
        //解析列表
        List<ProxyModel> proxyModelList = new ArrayList<>();
        Elements trs = element.select("tbody").select("tr");
        for (Element tr : trs) {
            Elements tds = tr.select("td");
            ProxyModel proxyModel = new ProxyModel();
            for (int i = 0; i < tds.size(); i++) {
                Element td = tds.get(i);
                switch (i) {
                    case 0:
                        StringBuilder ip = new StringBuilder();
                        Elements childs = td.getAllElements();
                        String lastStr = null;
                        for (int j = 0; j < childs.size() - 1; j++) {
                            if (!childs.get(j).html().contains("display: none;")) {
                                String iii = childs.get(j).text().trim();
                                Logger.t(TAG).d("iiiiiiiiiiiiiiiiiiiiii:---------" + iii);
                                if (!TextUtils.isEmpty(iii) && iii.length() < 4 && !iii.equals(lastStr)) {
                                    lastStr = iii;
                                    ip.append(iii);
                                }
                            }
                        }
                        proxyModel.setProxyIp(ip.toString());
                        Logger.t(TAG).d("IP :" + ip);
                        String port = childs.get(childs.size() - 1).text();
                        proxyModel.setProxyPort(port);
                        Logger.t(TAG).d("Port :" + port);
                        break;
                    case 1:
                        String anonymous = td.text();
                        proxyModel.setAnonymous(anonymous);
                        Logger.t(TAG).d(anonymous);
                        break;
                    case 2:
                        String type = td.text();
                        if ("https".equalsIgnoreCase(type)) {
                            proxyModel.setType(ProxyModel.TYPE_HTTPS);
                        } else if ("socks".equalsIgnoreCase(type)) {
                            proxyModel.setType(ProxyModel.TYPE_SOCKS);
                        } else {
                            proxyModel.setType(ProxyModel.TYPE_HTTP);
                        }
                        Logger.t(TAG).d(type);
                        break;
                    case 3:
                        String location = td.text();
                        proxyModel.setLocation(location);
                        Logger.t(TAG).d(location);
                        break;
                    case 5:
                        String responseTime = td.text();
                        proxyModel.setResponseTime(responseTime);
                        Logger.t(TAG).d(responseTime);
                        break;
                    case 6:
                        String validateTime = td.text();
                        proxyModel.setValidateTime(validateTime);
                        Logger.t(TAG).d(validateTime);
                        break;
                    case 7:
                        String liveTime = td.text();
                        proxyModel.setLiveTime(liveTime);
                        Logger.t(TAG).d("liveTime:" + liveTime);
                        break;
                    default:
                }
            }
            proxyModelList.add(proxyModel);
        }
        baseResult.setData(proxyModelList);
        //解析页码
        try {
            String totalPage = element.getElementsByClass("wp-pagenavi").first().select("a").last().text();
            baseResult.setTotalPage(Integer.valueOf(totalPage));
            Logger.t(TAG).d("total page:" + totalPage);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return baseResult;
    }
}
