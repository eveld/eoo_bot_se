package se.eoo.bot.util;

import java.util.Collection;

/**
 * Utility class that makes working with Strings easier.
 */
public class StringUtils {
	/**
	 * Util method that transforms a collection into a comma separated String.
	 * 
	 * @param collection The collection to transform
	 * 
	 * @return The comma seperated String
	 */
	public static <T> String buildCommaSeparatedString(Collection<T> collection) {
	    if (collection == null || collection.isEmpty()) return "";
	    
	    StringBuilder result = new StringBuilder();
	    for (T val : collection) {
	        result.append(val);
	        result.append(", ");
	    }
	    return result.substring(0, result.length() - 1);
	}
}