package io.netty.example.demo;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

/**
 * @author chenchen
 * @Description
 * @date 2022-10-07 22:01
 **/
public class EchoClient {
    public static void main(String[] args) {
        EventLoopGroup group = new NioEventLoopGroup(1);
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new EchoClientHandler());
                        }
                    });
            ChannelFuture channelFuture = bootstrap.connect(new InetSocketAddress(8083)).sync();
            Channel channel =  channelFuture.channel();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
            String sValue = "";
            ChannelFuture future = null;
            System.out.print("请输入：");
            while (true){
                sValue = bufferedReader.readLine();
                if (sValue.equals("exit")) break;
                future = channel.writeAndFlush(Unpooled.wrappedBuffer(sValue.getBytes(StandardCharsets.UTF_8)));
            }
            System.out.println("退出");
            if (future != null) {
                future.awaitUninterruptibly();
            }
        } catch (Exception e){
            System.out.println(e.getMessage());
        }finally {
            group.shutdownGracefully();
        }
    }
}
