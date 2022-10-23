package ir.sadad.co.checkversionapi.utils;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.Option;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Component
public class MessageFiller {

    private static final String REGEX = "(#\\{)(.*?)(\\})";

    private final Pattern pattern = Pattern.compile(REGEX);

    public String fill(final String message, final String json) {
        final StringBuilder buffer = new StringBuilder(message);
        Configuration conf = Configuration.defaultConfiguration();
        conf.addOptions(Option.DEFAULT_PATH_LEAF_TO_NULL);
        conf.addOptions(Option.SUPPRESS_EXCEPTIONS);
        final String jsonPathStarter = "$.";

        Matcher matcher = pattern.matcher(buffer);
        LinkedHashMap<String, Replacement> linkedHashMap = new LinkedHashMap<>();

        while (matcher.find()) {
            String parameter = matcher.group(2);
            final Object value = JsonPath
                    .parse(json,
                            Configuration.defaultConfiguration()
                                    .addOptions(Option.SUPPRESS_EXCEPTIONS))
                    .read(jsonPathStarter.concat(parameter));
            if (value != null) {
                Replacement replacement = new Replacement(matcher.start(), matcher.end(),
                        String.valueOf(value));
                linkedHashMap.put(parameter, replacement);
            }
        }

        return initializeMessage(linkedHashMap, buffer);
    }

    /**
     * this method recommend
     *
     * @param message
     * @param map
     * @return
     */
    public String fill(final String message, final Map<String, Object> map) {
        final StringBuilder buffer = new StringBuilder(message);

        Matcher matcher = pattern.matcher(buffer);
        LinkedHashMap<String, Replacement> linkedHashMap = new LinkedHashMap<>();

        while (matcher.find()) {
            String parameter = matcher.group(2);
            final Object value = map.get(parameter);
            if (value != null) {
                Replacement replacement = new Replacement(matcher.start(), matcher.end(),
                        String.valueOf(value));
                linkedHashMap.put(parameter, replacement);
            }
        }

        return initializeMessage(linkedHashMap, buffer);
    }

    private String initializeMessage(LinkedHashMap<String, Replacement> linkedHashMap,
                                     final StringBuilder buffer) {
        Integer pointer = 0;
        final StringBuilder messageBuilder = new StringBuilder();

        for (Map.Entry<String, Replacement> entry : linkedHashMap.entrySet()) {
            final String unmodifiedMessage = buffer.substring(pointer,
                    entry.getValue().getStartIndex());
            messageBuilder.append(unmodifiedMessage);
            messageBuilder.append(entry.getValue().getReplacementValue());
            pointer = entry.getValue().getEndIndex();
        }
        messageBuilder.append(buffer.substring(pointer));
        return messageBuilder.toString();
    }

    @Getter
    private final class Replacement {

        private final int startIndex;

        private final int endIndex;

        private final String replacementValue;

        private Replacement(final int startIndex, final int endIndex,
                            final String replacementValue) {
            this.startIndex = startIndex;
            this.endIndex = endIndex;
            this.replacementValue = replacementValue;
        }

    }

}