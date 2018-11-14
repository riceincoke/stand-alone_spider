package com.finding.myspider.spiderTools;

import com.finding.myspider.DbUtils.DataStoreTool;
import com.finding.myspider.DbUtils.IStore.Store;
import com.finding.myspider.entity.ContentRules;
import com.finding.myspider.entity.MyNew;
import com.finding.myspider.entity.SiteConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author 一杯咖啡
 * @Title：${enclosing_method}
 * @Description: [页面正文提取工具 集合]
 */
@Component
public class ParesUtil {

    @Autowired private Selectors selectors;
    @Autowired private RulesSplitUtil rulesSplitUtil;
    @Autowired private ParesCounter paresCounter;
    @Autowired private TimeFilter timeFilter;
    /**
     * DataToDB 数据持久化工具
     * contentRules 页面正文提取规则 原始字符串
     * contRules 解析后的正文提取规则
     */
    private Store store;
    private ContentRules contentRules;
    private String[] contRules;

    public ParesUtil(){}
    /**
     * 初始化 自定义页面解析器
     * @param siteConfig 网站匹配规则
     */
    public void initParesUitl(SiteConfig siteConfig, DataStoreTool store){
        this.contentRules = siteConfig.getContentRules();
        this.contRules = rulesSplitUtil.splitRule(siteConfig.getContentPares());
        this.store = store;
    }
    public Selectors getSelectors() {
        return selectors;
    }

    public RulesSplitUtil getRulesSplitUtil() {
        return rulesSplitUtil;
    }

    public ParesCounter getParesCounter() {
        return paresCounter;
    }

    public TimeFilter getTimeFilter() {
        return timeFilter;
    }

    public Store<MyNew> getDataStoreTool() {
        return store;
    }

    public ContentRules getContentRules() {
        return contentRules;
    }

    public String[] getContRules() {
        return contRules;
    }

    @Override
    public String toString() {
        return "ParesUitl{" +
                "\n selectors=" + selectors.getClass().getName() +
                "\n rulesSplitUtil=" + rulesSplitUtil.getClass().getName() +
                "\n paresCounter=" + paresCounter.getClass().getName() +
                "\n timeFilter=" + timeFilter.getClass().getName() +
                "\n dataStoreTool=" + store.getClass().getName() +
                '}';
    }
}
