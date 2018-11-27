package com.finding.spiderCore.crawldb;


import com.finding.spiderCore.crawldb.Idbutil.DataBase;
import com.finding.spiderCore.crawldb.Idbutil.GeneratorFilter;
import com.finding.spiderCore.entities.CrawlDatum;
import com.finding.spiderCore.spiderConfig.configUtil.ConfigurationUtils;
import com.finding.spiderCore.spiderConfig.DefaultConfigImp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 任务生成器
 */
public abstract class AbstractGenerator<T> extends DefaultConfigImp {

    public static final Logger LOG = LoggerFactory.getLogger(AbstractGenerator.class.getSimpleName());

    private DataBase<T> dataBase;
    private GeneratorFilter filter = null;
    private int topN = 0;

    private int totalGenerate;
    private int maxExecuteCount;

    public AbstractGenerator() {
        this.totalGenerate = 0;
//      this.maxExecuteCount = getConfig().getOrDefault(Configuration.KEY_MAX_EXECUTE_COUNT, Integer.MAX_VALUE);
    }
    public void initGenerator(Integer topN,Integer maxExecuteCount){
        this.setTopN(topN);
        this.maxExecuteCount = maxExecuteCount;
    }
    /**
     * return null if there is no CrawlDatum to generate
     * @return
     */
    public CrawlDatum next(){
        if(getTopN() > 0 && totalGenerate >= getTopN()){
            return null;
        }
        CrawlDatum datum;
        while (true) {
            try {
                datum = nextWithoutFilter();
                if (datum == null) {
                    return datum;
                }
                if(filter == null || (datum = filter.filter(datum))!=null){
                    if (datum.getExecuteCount() > maxExecuteCount) {
                        continue;
                    }
                    totalGenerate += 1;
                    return datum;
                }
            } catch (Exception e) {
                LOG.info("Exception when generating", e);
                return null;
            }
        }
    }

    public abstract CrawlDatum nextWithoutFilter() throws Exception;

    public abstract void close() throws Exception;

    public GeneratorFilter getFilter() {
        return filter;
    }

    public void setFilter(GeneratorFilter filter) {
        ConfigurationUtils.setTo(this, filter);
        this.filter = filter;
    }


    @Override
    public String toString() {
        return "AbstractGenerator{" +
                "dataBase=" + dataBase +
                ", filter=" + filter +
                ", topN=" + topN +
                ", totalGenerate=" + totalGenerate +
                ", maxExecuteCount=" + maxExecuteCount +
                '}';
    }

    public DataBase<T> getDataBase() {
        return dataBase;
    }

    public void setDataBase(DataBase<T> dataBase) {
        this.dataBase = dataBase;
    }

    public int getTopN() {
        return topN;
    }

    public void setTopN(int topN) {
        this.topN = topN;
    }

    public int getTotalGenerate() {
        return totalGenerate;
    }

    public void setTotalGenerate(int totalGenerate) {
        this.totalGenerate = totalGenerate;
    }

    public int getMaxExecuteCount() {
        return maxExecuteCount;
    }

    public void setMaxExecuteCount(int maxExecuteCount) {
        this.maxExecuteCount = maxExecuteCount;
    }
}
