package top.kyqzwj.demo.netty.frame;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;


/**
 * Description: 拆包/粘包处理 测试
 * Copyright: © 2019 CSNT. All rights reserved.
 * Company:CSNT
 *
 * @author kyq
 * @version 1.0
 * @Date 2020/10/16 9:14
 */
public class FrameServer {
    private Logger logger = LoggerFactory.getLogger(FrameServer.class);

    private int port;

    public FrameServer(int port){
        this.port = port;
    }

    public void run(){
        NioEventLoopGroup acceptGroup = new NioEventLoopGroup();
        NioEventLoopGroup readGroup = new NioEventLoopGroup();

        try{
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap
                    .group(acceptGroup, readGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline()
                                    .addLast(new ChunkedWriteHandler())
//                                    .addLast(new FrameEncoder())
                                    .addLast(new IdleStateHandler(0, 0, 60, TimeUnit.SECONDS))
                                    //粘包拆包解码器
                                    .addLast(new FrameDecoder())
                                    //内容处理器
                                    .addLast( new DefaultEventExecutorGroup(5), new FrameHandler());
                        }
                    });

            serverBootstrap.bind(port).sync().channel().closeFuture().sync();
        } catch (InterruptedException e) {
            logger.error("{}", e.getMessage(), e);
        } finally {
            acceptGroup.shutdownGracefully();
            readGroup.shutdownGracefully();
        }
    }

    public static void main(String[] args){
        FrameServer server = new FrameServer(8080);
        server.run();
    }
}
