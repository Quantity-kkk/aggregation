package top.kyqzwj.demo.netty.frame;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;

/**
 * Description:
 * Copyright: © 2019 CSNT. All rights reserved.
 * Company:CSNT
 *
 * @author kyq
 * @version 1.0
 * @Date 2020/10/16 14:13
 */
public class FrameHandler extends ChannelInboundHandlerAdapter {
    private Logger logger = LoggerFactory.getLogger(FrameHandler.class);

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if(msg instanceof String){
            logger.info(Thread.currentThread()+"最终获取到的消息：" + msg);
        }else {
            ByteBuf byteBuf = (ByteBuf) msg;
            byte[] content = new byte[byteBuf.readableBytes()];
            byteBuf.readBytes(content);
            String msgContent = new String(content);
            logger.info(Thread.currentThread()+"最终获取到的消息：" + msgContent);
            byteBuf.release();

            //对数据进行回信
            String req = msgContent.replaceAll("请服务器接收消息，消息编号：","");
            req = req.substring(0,req.indexOf("，"));

            byte[] bytes = (String.format("已收到编号为%s的消息：",req)).getBytes(StandardCharsets.UTF_8);
            ByteBuf buffer = ctx.alloc().buffer();
            buffer.writeInt(1024);
            buffer.writeInt(bytes.length);
            buffer.writeBytes(bytes);

            ctx.channel().writeAndFlush(buffer);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error("{}", cause.getMessage(), cause);
        ctx.close();
    }
}
