package top.kyqzwj.demo.netty.frame;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Description:
 * Copyright: © 2019 CSNT. All rights reserved.
 * Company:CSNT
 *
 * @author kyq
 * @version 1.0
 * @Date 2020/10/16 9:41
 */
public class FrameDecoder extends ByteToMessageDecoder {
    private static final int MAGIC_NUMBER = 1024;
    private static final int HEADER_LENGTH = 8;

    private Logger logger = LoggerFactory.getLogger(FrameServer.class);
    private ByteBuf tmpMsg = Unpooled.buffer();

    private int dataLength = 0;
    private int curLength = 0;
    private  boolean isReadHead = true;

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        //TODO 此处可改造解码方法，将结果包装为消息对象返回。
        System.out.println(Thread.currentThread()+"收到了一次数据包，长度是：" + in.readableBytes());

        //1.如果有上一次的数据余留下来，先进行数据粘合
        ByteBuf message;
        int tmpMsgSize = tmpMsg.readableBytes();

        if (tmpMsgSize > 0){
            message = Unpooled.buffer();
            message.writeBytes(tmpMsg);
            message.writeBytes(in);
            System.out.println("进行了数据包合并...");
        }else {
            message = in;
        }


        //2.进行数据头的解析，读取消息，消费消息，直到此次的消息消费完：
        while(isReadHead && decodeHead(ctx, message, out)){
            //3.读取指定长度的数据包
            int readLen = message.readableBytes();

            //长度足够，进行信息消费
            if(readLen + curLength >= dataLength){
                readLen = dataLength - curLength;

                byte[] outData = new byte[dataLength];
                message.readBytes(outData, curLength, readLen);
                curLength += readLen;

                //4.如果读取到了足够的数据，将读取的数据提交出去
                if(curLength == dataLength){
                    out.add(Unpooled.copiedBuffer(outData));
                    //准备进行下一次读
                    reset();
                }
            }else {
                //长度不够，将多余的数据放到暂存区里面去，同时在decodeHead方法中读取的magicNumber和dataLength也还回到byteBuffer中，留待下一次读取
                message.resetReaderIndex();
                reset();
                break;
            }
        }

        //如果有多余的信息，存储到tmpMsg里面去
        int remainSize = message.readableBytes();
        if(remainSize != 0){
            System.out.println("多余的数据长度：" + remainSize);
            tmpMsg.clear();
            tmpMsg.writeBytes(message.readBytes(remainSize));
        }
    }

    private boolean decodeHead(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
        //包头需要包含一个4位的magicNumber+4位的长度标识字段，所以最小也是8.
        if(in.readableBytes() < HEADER_LENGTH){
            return false;
        }

        //标记读取指针，如果消息不完整，便于还原读取点。
        in.markReaderIndex();

        int magicNumber = in.readInt();
        this.dataLength = in.readInt();

        if(magicNumber != MAGIC_NUMBER || this.dataLength <= 0){
            String errorMsg = String.format("netty数据格式异常， 消息体解析失败 magicNumber: %d, length: %d", magicNumber, this.dataLength);
            logger.error(errorMsg);

            //由于判定了消息长度，魔数不匹配，则表示收到了无效消息，对buf进行清空
            in.clear();

            reset();
            return false;
        }

        this.isReadHead = false;
        this.curLength = 0;

        return true;
    }

    private void reset() {
        this.dataLength = 0;
        this.isReadHead = true;
        this.curLength = 0;
        this.tmpMsg.clear();
    }
}
