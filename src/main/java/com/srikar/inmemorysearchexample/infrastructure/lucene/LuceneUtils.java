package com.srikar.inmemorysearchexample.infrastructure.lucene;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.util.regex.Pattern;

import static java.util.Optional.ofNullable;
import static java.util.regex.Pattern.compile;
import static lombok.AccessLevel.PRIVATE;
import static org.apache.commons.lang3.RegExUtils.removeAll;
import static org.apache.commons.lang3.StringUtils.deleteWhitespace;

@NoArgsConstructor(access = PRIVATE)
public class LuceneUtils {

    private static final Pattern NON_ALPHANUMERIC_PATTERN = compile("([^a-z0-9\\s])");

    public static String sanitise(String unsanitised) {
        return
                ofNullable(unsanitised)
                .map(StringUtils::lowerCase)
                .map(subject -> removeAll(subject, NON_ALPHANUMERIC_PATTERN))
                .orElse(null);
    }

    public static String sanitiseAndRemoveWhitespace(String unsanitised) {
        return deleteWhitespace(sanitise(unsanitised));
    }
}
