package top.zwjkyq.commons.util;

/**
 * Description:
 * Copyright: © 2019 CSNT. All rights reserved.
 * Company:CSNT
 *
 * @author kyq
 * @version 1.0
 * @Date 2020/10/21 11:01
 */
public class CloseableUtil {

    public static final void close(AutoCloseable ... closeables){
        if(closeables !=null && closeables.length>0){
            for (AutoCloseable closeable: closeables){
                if(closeable!=null){
                    try {
                        closeable.close();
                    }catch (Exception e){
                        throw new RuntimeException(e);
                    }
                }
            }
        }
    }
}
