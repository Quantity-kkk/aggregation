package top.kyqzwj.demo.netty.frame;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.util.Random;

/**
 * Description:
 * Copyright: © 2019 CSNT. All rights reserved.
 * Company:CSNT
 *
 * @author kyq
 * @version 1.0
 * @Date 2020/10/16 14:23
 */
public class FrameClient {
    private Logger logger = LoggerFactory.getLogger(FrameClient.class);

    public static void main(String[] args) throws InterruptedException {
        FrameClient client = new FrameClient();
        client.run();
    }

    public void run() throws InterruptedException {
        NioEventLoopGroup nioEventLoopGroup = new NioEventLoopGroup();

        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(nioEventLoopGroup)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception {
                        Random random = new Random(1024);
                        ch.pipeline()
                                .addLast(new FrameDecoder())
                                .addLast(new ChannelInboundHandlerAdapter(){
                                    //向服务端随机的发送1000条消息，看服务端输出的信息是否完整
                                    @Override
                                    public void channelActive(ChannelHandlerContext ctx) throws Exception {
                                        for (int i = 0; i < 1000; i++) {
                                            int randMsgLen = random.nextInt(100);
                                            String msg = String.format("请服务器接收消息，消息编号：%s，随机长度编码：%s。", i, randMsgLen);
                                            msg = FrameClient.padRight(msg, randMsgLen, '0');

                                            //TODO 此处发送消息可以交由encoder来处理，封装专门的对象来进行数据传输
                                            byte[] bytes = msg.getBytes(StandardCharsets.UTF_8);
                                            ByteBuf buffer = ctx.alloc().buffer();
                                            buffer.writeInt(1024);
                                            buffer.writeInt(bytes.length);
                                            buffer.writeBytes(bytes);
                                            ctx.channel().writeAndFlush(buffer);

                                            System.out.println("完成消息发送："+msg);
                                        }
                                    }

                                    @Override
                                    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                        //接收服务端返回的消息
                                        if(msg instanceof String){
                                            logger.info(Thread.currentThread()+"收到服务端回信：" + msg);
                                        }else {
                                            ByteBuf byteBuf = (ByteBuf) msg;
                                            byte[] content = new byte[byteBuf.readableBytes()];
                                            byteBuf.readBytes(content);
                                            logger.info(Thread.currentThread()+"收费服务端回信：" + new String(content));
                                            byteBuf.release();
                                        }
                                    }
                                });
                    }
                });

        ChannelFuture f =  bootstrap.connect("localhost", 8080);
    }


    /**
     * 右填充字符
     *
     * @param src
     * @param len
     * @param ch
     * @return
     */
    public static String padRight(String src, int len, char ch) {
        int diff = len - src.length();
        if (diff <= 0) {
            return src;
        }
        char[] charr = new char[len];
        System.arraycopy(src.toCharArray(), 0, charr, 0, src.length());
        for (int i = src.length(); i < len; i++) {
            charr[i] = ch;
        }
        return new String(charr);
    }
}
