package se.eoo.bot.handler;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import se.eoo.bot.Bot;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.ReferenceCountUtil;

/**
 * Dispatch handler.
 * 
 * {@inheritDoc}
 */
public class DispatchHandler extends ChannelHandlerAdapter {
	private final static Logger logger = LogManager.getLogger();
	
	/**
	 * Once the channel becomes active, send password and user details.
	 * 
	 * {@inheritDoc}
	 */
	public void channelActive(ChannelHandlerContext context) throws Exception {
		context.write("PASS oauth:" + Bot.BOT_AUTH + "\r\n");
		context.write("NICK " + Bot.BOT_NICK + "\r\n");
		context.write("USER " + Bot.BOT_NICK + " 8 * :" + Bot.BOT_NAME + "\r\n");
		context.flush();
	}
	
 	/**
	 * When a message arrives on the channel, process it.
	 * 
	 * {@inheritDoc}
	 */
	@Override
    public void channelRead(ChannelHandlerContext context, Object object) {
		String lines = (String) object;

		for(String line : lines.split("\\r?\\n")) {
			// Server messages
			Matcher connect = Pattern.compile(":(.*) 376 (.*):(.*)").matcher(line);
			Matcher unknown = Pattern.compile(":(.*) 421 (.*):(.*)").matcher(line);
			Matcher ping = Pattern.compile("PING (.*)").matcher(line);
			
			// Channel messages
			Matcher mode = Pattern.compile("").matcher(line); 
			Matcher join = Pattern.compile(":(.*)!.*@(.*) JOIN (#.*)").matcher(line);
			Matcher part = Pattern.compile(":(.*)!.*@(.*) PART (#.*)").matcher(line);
			Matcher privmsg = Pattern.compile(":(.*)!.*@(.*) PRIVMSG (#?[^ ]*) :(.*)").matcher(line);
			
			//:tmi.twitch.tv 376 nick :Message
			if(connect.find()) {
				String host = connect.group(1);
				String channel = connect.group(2);
				String message = connect.group(3);
				context.write("JOIN " + Bot.BOT_CHANNEL + "\r\n");
				context.flush();
			}
			
			//PING :tmi.twitch.tv
			if(ping.find()) {
				String host = ping.group(1);
				context.write("PONG :" + host);
				context.flush();
			}
			
			//:nick!name@127.0.0.1 PART #channel
			if(part.find()) {
				String nick = part.group(1);
				String ip = part.group(2);
				String channel = part.group(3);
				System.out.println(nick + " left " + channel);
			}
			
			//:nick!name@127.0.0.1 JOIN #channel
			if(join.find()) {
				String nick = join.group(1);
				String ip = join.group(2);
				String channel = join.group(3);
				System.out.println(nick + " joined " + channel);
			}
			
			//:nick!name@127.0.0.1 PRIVMSG #channel :Message
			/*
			 * Handle:
			 * .mod x/unmod x
			 * .r9kbeta/r9kbetaoff
			 * .commercial/commercial x
			 * .mods
			 */
			if(privmsg.find()) {
				String nick = privmsg.group(1);
				String ip = privmsg.group(2);
				String channel = privmsg.group(3);
				String message = privmsg.group(4);
				System.out.println(nick +"@" + channel + ": " + message);
				
				Matcher marryme = Pattern.compile("!marryme").matcher(message);
				if(marryme.find()) {
					context.write(Marriage.marryMe(channel, nick));
					context.flush();
				}
				
				Matcher ban = Pattern.compile(".ban (.*)").matcher(message);
				Matcher unban = Pattern.compile(".unban (.*)").matcher(message);
				Matcher timeout = Pattern.compile(".timeout (.*)").matcher(message);
				Matcher slow = Pattern.compile(".slow (.*)").matcher(message);
				Matcher slowoff = Pattern.compile(".slowoff").matcher(message);
				Matcher subscribers = Pattern.compile(".subscribers").matcher(message);
				Matcher subscribersoff = Pattern.compile(".subscribersoff").matcher(message);
				Matcher clear = Pattern.compile(".clear").matcher(message);
			}
		}
		
		ReferenceCountUtil.release(object);
    }
	
	/**
	 * Catch any exceptions that occur.
	 * 
	 * {@inheritDoc}
	 */
    @Override
    public void exceptionCaught(ChannelHandlerContext context, Throwable cause) {
        logger.error(cause);
        context.close();
    }
}