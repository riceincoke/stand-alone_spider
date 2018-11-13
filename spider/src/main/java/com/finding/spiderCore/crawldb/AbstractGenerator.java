/*
 * Copyright (C) 2014 hu
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
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

    protected DataBase<T> dataBase;
    protected GeneratorFilter filter = null;
    protected int topN = 0;

    protected int totalGenerate;
    protected int maxExecuteCount;

    public AbstractGenerator() {
        this.setTopN(configuration.getTopN());
        this.totalGenerate = 0;
//      this.maxExecuteCount = getConfig().getOrDefault(Configuration.KEY_MAX_EXECUTE_COUNT, Integer.MAX_VALUE);
        this.maxExecuteCount = getConfig().getMaxExecuteCount();
    }

    /**
     * return null if there is no CrawlDatum to generate
     * @return
     */
    public CrawlDatum next(){
        if(topN > 0 && totalGenerate >= topN){
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


    public int getTopN() {
        return topN;
    }

    public void setTopN(int topN) {
        this.topN = topN;
    }


    public int getMaxExecuteCount() {
        return maxExecuteCount;
    }

    public void setMaxExecuteCount(int maxExecuteCount) {
        this.maxExecuteCount = maxExecuteCount;
    }

    public int getTotalGenerate(){
        return totalGenerate;
    }

    public abstract void close() throws Exception;

    public GeneratorFilter getFilter() {
        return filter;
    }

    public void setFilter(GeneratorFilter filter) {
        ConfigurationUtils.setTo(this, filter);
        this.filter = filter;
    }

    public DataBase getDataBase() {
        return dataBase;
    }

    @Override
    public String toString() {
        return "AbstractGenerator{" +
                "\n generatorFilter=" +(filter == null? "null":filter.getClass().getName()) +
                "\n topN=" + topN +
                "\n totalGenerate=" + totalGenerate +
                "\n maxExecuteCount=" + maxExecuteCount +
                '}';
    }
}
