package top.kyqzwj.test.datasource;

import com.alibaba.druid.pool.DruidDataSource;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Description: 用于学习DruidDataSource数据库连接池源码的测试类
 * 用于追踪源码：getConnection，recycleConnection，
 * Copyright: © 2019 CSNT. All rights reserved.
 * Company:CSNT
 *
 * @author kyq
 * @version 1.0
 * @Date 2020/10/21 10:08
 */
public class TestDruidDataSource {
    private DruidDataSource ds = (DruidDataSource) DruidFactory.getInstance();

    public static void main(String[] args){
        TestDruidDataSource t = new TestDruidDataSource();
        List query = t.query();

        System.out.println(query.size());

        t.close();
    }

    public void close(){
        ds.close();
    }
    public List query(){
        List result = new ArrayList();

        try(PreparedStatement pst = ds.getConnection().prepareStatement("select * from kz_file");
            ResultSet rs = pst.executeQuery()) {

            int colAmount = rs.getMetaData().getColumnCount();
            if (colAmount > 1) {
                while (rs.next()) {
                    Object[] temp = new Object[colAmount];
                    for (int i=0; i<colAmount; i++) {
                        temp[i] = rs.getObject(i + 1);
                    }
                    result.add(temp);
                }
            }
            else if(colAmount == 1) {
                while (rs.next()) {
                    result.add(rs.getObject(1));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return result;
    }

}
