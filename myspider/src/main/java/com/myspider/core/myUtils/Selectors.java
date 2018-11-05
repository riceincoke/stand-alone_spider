package com.myspider.core.myUtils;

import com.myspider.core.crawlColletor.webcollector.model.Page;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Component;


public class Selectors {
    //id,class标签选择器
    public String detaliSelect(Page page, String[] selectList) {
        String text;
        Document document = page.doc();
        for (String x : selectList) {
            text = document.select(x).text().trim();
            if (!text.trim().equals("")) {
                return text;
            }
        }
        return "";
    }

    //id class 选择器
    public String IdClassSelect(Page page, String[] selectList) {
        String text;
        Document document = page.doc();
        for (String x : selectList) {
            text = document.select("." + x.trim()).text().trim();
            if (text.equals("")) {
                text = document.select("#" + x.trim()).text().trim();
                if (text.equals("")) {
                    text = document.select(x).text().trim();
                }
            }
            return text;
        }
        return "提取失败";
    }
}
