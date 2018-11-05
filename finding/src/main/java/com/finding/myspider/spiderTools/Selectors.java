package com.finding.myspider.spiderTools;

import com.finding.spiderCore.entities.Page;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Component;

/**
 * 网页标签选择器
 */
@Component
public class Selectors {

    /**
     * desc: 详细标签选择器
     * @Return:
     **/
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

    /**
     * desc: ID CLASS 选择器
     * @Return:
     **/
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
