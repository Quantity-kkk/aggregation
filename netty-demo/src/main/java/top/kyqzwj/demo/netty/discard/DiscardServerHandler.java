package top.kyqzwj.demo.netty.discard;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;

/**
 * Description:
 * Copyright: © 2019 CSNT. All rights reserved.
 * Company:CSNT
 *
 * @author kyq
 * @version 1.0
 * @Date 2020/9/29 10:56
 */
public class DiscardServerHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg){
        // Discard the received da|ta silently.
        /*
        try {
            // Do something with msg
            // Do nothing with msg
            ByteBuf in = (ByteBuf) msg;
            while (in.isReadable()){
                System.out.println((char)in.readByte());
                System.out.flush();
            }

        } finally {
            ReferenceCountUtil.release(msg);
        }*/
        ctx.write(msg);
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause){
        cause.printStackTrace();
        ctx.close();
    }
}
