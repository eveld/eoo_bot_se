package se.eoo.bot.plugin;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

/**
 * First iteration of a "Plugin" that handles Channel marriage proposals.
 */
public class Marriage {
	private final static HashMap<String, Set<String>> marriages = new HashMap<String, Set<String>>();
	
	/**
	 * Handle marriage proposals and keep track of the candidates.
	 * 
	 * @param channel The channel to handle the proposals for
	 * @param nick The nick of the person that just proposed
	 * 
	 * @return The response to the marriage proposal
	 */
	public static String marryMe(String channel, String nick) {
		if(!marriages.containsKey(channel)) {
			marriages.put(channel, new LinkedHashSet<String>());
		}
		
		String response;
		Set<String> candidates = marriages.get(channel); 
		if(candidates.isEmpty()) {
			response = String.format("PRIVMSG %s :Congratulations %s, you are first in line!\r\n", channel, nick);
		}
		else {
			String verb = (candidates.size() > 1) ? "are" : "is";
			response = String.format("PRIVMSG %s :Get in line %s! %s %s already in line.\r\n", channel, nick, StringUtils.join(candidates, ", "), verb);
		}
		candidates.add(nick);
		return response;
	}
}
