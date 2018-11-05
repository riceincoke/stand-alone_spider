package com.myspider.core.redis;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * <p>项目名称: ${序列化} </p>
 * <p>文件名称: ${SerializeUtil} </p>
 * <p>描述: [对象序列化和反序列化对象] </p>
 * <p>创建时间: ${date} </p>
 * @author <a href="mail to: *******@******.com" rel="nofollow">作者</a>
 * @version v1.0
 * @update [序号][日期YYYY-MM-DD] [更改人姓名][变更描述]
 **/
@Component
public class SerializeUtil {

    /**
     * @Title：${序列化}
     * @Description: [序列化对象为字符串]
     * @author <a href="mail to: 113985238@qq.com" rel="nofollow">whitenoise</a>
     * @CreateDate: ${date} ${time}</p>
     * @update: [序号][日期YYYY-MM-DD] [更改人姓名][变更描述]
     */
    public String serializeToString(Object obj) throws Exception{
        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        ObjectOutputStream objOut = new ObjectOutputStream(byteOut);
        objOut.writeObject(obj);
        //此处只能是ISO-8859-1,但是不会影响中文使用
        String str = byteOut.toString("ISO-8859-1");
        return str;
    }
    //反序列化
    /**
     * @Title：${反序列化}
     * @Description: [反序列化字符串为对象]
     * @author <a href="mail to: *******@******.com" rel="nofollow">作者</a>
     * @CreateDate: ${date} ${time}</p>
     * @update: [序号][日期YYYY-MM-DD] [更改人姓名][变更描述]
     */
    public Object deserializeToObject(String str) throws Exception{
        ByteArrayInputStream byteIn = new ByteArrayInputStream(str.getBytes("ISO-8859-1"));
        ObjectInputStream objIn = new ObjectInputStream(byteIn);
        Object obj =objIn.readObject();
        return obj;
    }
}
