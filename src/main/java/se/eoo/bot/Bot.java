package se.eoo.bot;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

import org.apache.commons.daemon.Daemon;
import org.apache.commons.daemon.DaemonContext;
import org.apache.commons.daemon.DaemonInitException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import se.eoo.bot.handler.DispatchHandler;

public class Bot implements Daemon {
	private final static Logger logger = LogManager.getLogger();
	
	public final static String BOT_AUTH = "eemvmtzs9yuvjc12m7qflwgsh4wg8nh";
	public final static String BOT_NICK = "eoo_bot_se";
	public final static String BOT_NAME = "eoo_bot_se";
	public final static String BOT_CHANNEL = "#swebliss";
	
	private final static String IRC_HOST = "irc.twitch.tv";
	private final static int IRC_PORT = 6667;
	
	private Thread main;
	private final static EventLoopGroup workerGroup = new NioEventLoopGroup();
	
	public static void main(String[] args) {
		try {	
			Bootstrap client = new Bootstrap();
			client.group(workerGroup);
			client.channel(NioSocketChannel.class);
			client.option(ChannelOption.SO_KEEPALIVE, true);
			client.handler(new ChannelInitializer<SocketChannel>() {
				@Override
				public void initChannel(SocketChannel channel) throws Exception {
					channel.pipeline().addLast(new StringEncoder());
					channel.pipeline().addLast(new StringDecoder());
					channel.pipeline().addLast(new DispatchHandler());
				}
			});
				
			// Start the client.
			ChannelFuture future = client.connect(IRC_HOST, IRC_PORT).sync();
			
			// Wait until the connection is closed.
			future.channel().closeFuture().sync();
		} catch (InterruptedException cause) {
			logger.error(cause);
		}
	}
	
	/**
	 * Destroy the daemon and leave no traces.
	 * 
	 * {@inheritDoc}
	 */
	public void destroy() {
		main = null;
	}
	
	/**
	 * Initialize the daemon.
	 * 
	 * {@inheritDoc}
	 */
	public void init(DaemonContext context) throws DaemonInitException, Exception {
		main = new Thread() {
			/**
			 * Start the daemon thread.
			 * 
			 * {@inheritDoc}
			 */
			public synchronized void start() {
				super.start();
			}
			 
			/**
			 * Do the actual work in the thread.
			 * 
			 * {@inheritDoc}
			 */
			public synchronized void run() {
				try {	
					Bootstrap client = new Bootstrap();
					client.group(workerGroup);
					client.channel(NioSocketChannel.class);
					client.option(ChannelOption.SO_KEEPALIVE, true);
					client.handler(new ChannelInitializer<SocketChannel>() {
						@Override
						public void initChannel(SocketChannel channel) throws Exception {
							channel.pipeline().addLast(new StringEncoder());
							channel.pipeline().addLast(new StringDecoder());
							channel.pipeline().addLast(new DispatchHandler());
						}
					});
						
					// Start the client.
					ChannelFuture future = client.connect(IRC_HOST, IRC_PORT).sync();
					
					// Wait until the connection is closed.
					future.channel().closeFuture().sync();
				} catch (InterruptedException cause) {
					logger.error(cause);
				}
			}
		};
	}
	
	/**
	 * Start the daemon.
	 * 
	 * {@inheritDoc}
	 */
	public void start() throws Exception {
		workerGroup.shutdownGracefully();
		main.start();
	}
	
	/**
	 * Stop the daemon and clean up.
	 * 
	 * {@inheritDoc}
	 */
	public void stop() throws Exception {
		main.join(1000);
	}
}