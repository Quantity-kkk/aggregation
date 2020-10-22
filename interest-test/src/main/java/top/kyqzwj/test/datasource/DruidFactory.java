package top.kyqzwj.test.datasource;

import com.alibaba.druid.pool.DruidDataSource;
import com.jfinal.kit.StrKit;
import top.zwjkyq.commons.util.StringUtil;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Description:
 * Copyright: © 2019 CSNT. All rights reserved.
 * Company:CSNT
 *
 * @author kyq
 * @version 1.0
 * @Date 2020/10/21 10:26
 */
public class DruidFactory {

    public static final DataSource getInstance(){
        return DruidDataSourceHolder.ds;
    }


    private static class DruidDataSourceHolder{
        private static final DruidDataSource ds = new DruidDataSource();

        //连接池的名称
        private static String name = "kyq-test";

        private static String url = "jdbc:mysql://localhost:3306/wx_jx_miniprogram?useUnicode=true&characterEncoding=UTF-8&allowPublicKeyRetrieval=TRUE&autoReconnect=true&useSSL=false&serverTimezone=PRC";
        private static String username = "root";
        private static String password = "root";
        private static String driverClass = "com.mysql.jdbc.Driver";

        private static String publicKey;

        // 初始连接池大小、最小空闲连接数、最大活跃连接数
        private static int initialSize = 1;
        private static int minIdle = 10;
        private static int maxActive = 32;

        // 配置获取连接等待超时的时间
        private static long maxWait = DruidDataSource.DEFAULT_MAX_WAIT;

        // 配置间隔多久才进行一次检测，检测需要关闭的空闲连接，单位是毫秒
        private static long timeBetweenEvictionRunsMillis = DruidDataSource.DEFAULT_TIME_BETWEEN_EVICTION_RUNS_MILLIS;
        // 配置连接在池中最小生存的时间
        private static long minEvictableIdleTimeMillis = DruidDataSource.DEFAULT_MIN_EVICTABLE_IDLE_TIME_MILLIS;
        // 配置发生错误时多久重连
        private static long timeBetweenConnectErrorMillis = DruidDataSource.DEFAULT_TIME_BETWEEN_CONNECT_ERROR_MILLIS;
        /**
         * hsqldb - "select 1 from INFORMATION_SCHEMA.SYSTEM_USERS"
         * Oracle - "select 1 from dual"
         * DB2 - "select 1 from sysibm.sysdummy1"
         * mysql - "select 1"
         */
        private static String validationQuery = "select 1";
        private static String  connectionInitSql = null;
        private static String connectionProperties = null;
        private static boolean testWhileIdle = true;
        private static boolean testOnBorrow = false;
        private static boolean testOnReturn = false;

        // 是否打开连接泄露自动检测
        private static boolean removeAbandoned = false;
        // 连接长时间没有使用，被认为发生泄露时长
        private static long removeAbandonedTimeoutMillis = 300 * 1000;
        // 发生泄露时是否需要输出 log，建议在开启连接泄露检测时开启，方便排错
        private static boolean logAbandoned = false;
        // 只要maxPoolPreparedStatementPerConnectionSize>0,poolPreparedStatements就会被自动设定为true，使用oracle时可以设定此值。
        private static int maxPoolPreparedStatementPerConnectionSize = -1;

        // 配置监控统计拦截的filters
        private static String filters;	// 监控统计："stat"    防SQL注入："wall"     组合使用： "stat,wall"

        /**
         * 创建数据库连接池
         * */
        static {
            //设置连接池的名称
            ds.setName(name);
            ds.setUrl(url);
            ds.setUsername(username);
            ds.setPassword(password);
            ds.setDriverClassName(driverClass);
            ds.setInitialSize(initialSize);
            ds.setMinIdle(minIdle);

            ds.setMaxActive(maxActive);
            ds.setMaxWait(maxWait);
            ds.setTimeBetweenConnectErrorMillis(timeBetweenConnectErrorMillis);
            ds.setTimeBetweenEvictionRunsMillis(timeBetweenEvictionRunsMillis);
            ds.setMinEvictableIdleTimeMillis(minEvictableIdleTimeMillis);

            ds.setValidationQuery(validationQuery);
            if(StringUtil.isNotEmpty(connectionInitSql)){
                List<String> connectionInitSqls = new ArrayList<String>();
                connectionInitSqls.add(connectionInitSql);
                ds.setConnectionInitSqls(connectionInitSqls);
            }
            ds.setTestWhileIdle(testWhileIdle);
            ds.setTestOnBorrow(testOnBorrow);
            ds.setTestOnReturn(testOnReturn);

            ds.setRemoveAbandoned(removeAbandoned);
            ds.setRemoveAbandonedTimeoutMillis(removeAbandonedTimeoutMillis);
            ds.setLogAbandoned(logAbandoned);

            //只要maxPoolPreparedStatementPerConnectionSize>0,poolPreparedStatements就会被自动设定为true，参照druid的源码
            ds.setMaxPoolPreparedStatementPerConnectionSize(maxPoolPreparedStatementPerConnectionSize);

            boolean hasSetConnectionProperties = false;
            if (StringUtil.isNotEmpty(filters)){
                try {
                    ds.setFilters(filters);
                    //支持加解密数据库
                    if(filters.contains("config")){
                        //判断是否设定了公钥
                        if(StringUtil.isEmpty(publicKey)){
                            throw new RuntimeException("Druid连接池的filter设定了config时，必须设定publicKey");
                        }
                        String decryptStr = "config.decrypt=true;config.decrypt.key="+publicKey;
                        String cp = connectionProperties;
                        if(StrKit.isBlank(cp)){
                            cp = decryptStr;
                        }else{
                            cp = cp + ";" + decryptStr;
                        }
                        ds.setConnectionProperties(cp);
                        hasSetConnectionProperties = true;
                    }
                } catch (SQLException e) {throw new RuntimeException(e);}
            }
            //确保setConnectionProperties被调用过一次
            if(!hasSetConnectionProperties && StringUtil.isNotEmpty(connectionProperties)){
                ds.setConnectionProperties(connectionProperties);
            }
        }
    }

}
