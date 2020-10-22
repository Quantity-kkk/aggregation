package top.kyqzwj.test.http.ioblock;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.multipart.Attribute;
import io.netty.handler.codec.http.multipart.DefaultHttpDataFactory;
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder;
import io.netty.handler.codec.http.multipart.InterfaceHttpData;
import io.netty.handler.codec.json.JsonObjectDecoder;
import io.netty.util.AsciiString;
import io.netty.util.CharsetUtil;
import jdk.nashorn.internal.objects.Global;
import jdk.nashorn.internal.parser.JSONParser;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Description:
 * Copyright: © 2019 CSNT. All rights reserved.
 * Company:CSNT
 *
 * @author kyq
 * @version 1.0
 * @Date 2020/9/29 15:40
 */
public class HttpHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    private AsciiString contentType = HttpHeaderValues.TEXT_PLAIN;


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest msg) throws Exception {
        System.out.println("class:" + msg.getClass().getName());

        Map<String, Object> paramMap = getRequestParam(msg);
        DefaultFullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,
                HttpResponseStatus.OK,
                Unpooled.wrappedBuffer("{\"name\":\"Jack\"}".getBytes()));

        HttpHeaders heads = response.headers();
        heads.add(HttpHeaderNames.CONTENT_TYPE, contentType + "; charset=UTF-8");
        heads.add(HttpHeaderNames.CONTENT_LENGTH, response.content().readableBytes());
        heads.add(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);

        //TODO 模拟服务器处理耗时，直接休眠60s，看客户端是如何操作的
        Thread.sleep(60000L);
        ctx.write(response);
    }

    private Map<String, Object> getRequestParam(FullHttpRequest msg) throws IOException {
        Map<String, Object> ret = new HashMap<>(16);

        HttpMethod method = msg.method();
        if(HttpMethod.GET == method){
            QueryStringDecoder decoder = new QueryStringDecoder(msg.uri());
            decoder.parameters().entrySet().forEach(stringListEntry -> ret.put(stringListEntry.getKey(), stringListEntry.getValue().get(0)));
        }else if(HttpMethod.POST == method){
            String contentType = msg.headers().get("Content-Type");
            contentType = contentType.split(";")[0];

            //根据请求内容的类型进行处理，如果是一般的formdata类型，直接使用body解析，如果是json，直接用json反解。
            if(contentType.contains("application/json")){
                ByteBuf jsonBuf = msg.content();
                String jsonStr = jsonBuf.toString(CharsetUtil.UTF_8);
                Gson gson = new Gson();
                Map map = gson.fromJson(jsonStr, Map.class);
                ret.putAll(map);
            }else {
                HttpPostRequestDecoder decoder = new HttpPostRequestDecoder(new DefaultHttpDataFactory(false), msg);
                List<InterfaceHttpData> paramList = decoder.getBodyHttpDatas();
                for(InterfaceHttpData param : paramList){
                    Attribute data = (Attribute) param;
                    ret.put(data.getName(), data.getValue());
                }
            }
        }else {
            throw new RuntimeException("暂不支持的请求方式！");
        }

        return ret;
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        System.out.println("channelReadComplete");
        super.channelReadComplete(ctx);
        ctx.flush(); // 4
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println("exceptionCaught");
        if(null != cause){
            cause.printStackTrace();
        }
        if(null != ctx){
            ctx.close();
        }
    }
}
