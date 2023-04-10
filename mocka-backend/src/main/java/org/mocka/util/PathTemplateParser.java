package org.mocka.util;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.web.util.UriComponentsBuilder;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PathTemplateParser {

    private static final PathTemplateParser INSTANCE = new PathTemplateParser();

    private static final Pattern PATH_TEMPLATE_PATH_VAR_PATTERN = Pattern.compile("\\{([^/]+?)\\}");


    public static boolean isValid(String pathTemplate) {
        try {
            INSTANCE.internalValidate(pathTemplate);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static ParseResult parse(String pathTemplate) {
        return INSTANCE.internalParse(pathTemplate);
    }


    private void internalValidate(String pathTemplate) {
        if (!pathTemplate.startsWith("/")) {
            throw new IllegalArgumentException("Path template must starts with \"/\"");
        }
        try {
            UriComponentsBuilder.fromPath(pathTemplate).buildAndExpand(new UriVariablesStubMap()).toUri();
        } catch (Exception e) {
            throw new IllegalArgumentException("Cannot parse path template", e);
        }
    }

    private ParseResult internalParse(String pathTemplate) {
        internalValidate(pathTemplate);

        int cursor = 0;
        var keys = new ArrayList<String>();
        var pathRegexBuilder = new StringBuilder();
        pathRegexBuilder.append("^");
        for (var pathVarMatch : getPathVarMatches(pathTemplate)) {
            if (cursor != pathVarMatch.start) {
                // add and encode for regex using preceding non-var path segment
                pathRegexBuilder.append(encode(pathTemplate.substring(cursor, pathVarMatch.start)));
            }
            // add regex group for path var searching
            pathRegexBuilder.append("([^/]+)");
            keys.add(pathVarMatch.key);
            cursor = pathVarMatch.end;
        }
        if (cursor != pathTemplate.length()) {
            // add and encode for regex using last (or the only one) non-var path segment
            pathRegexBuilder.append(encode(cursor != 0 ? pathTemplate.substring(cursor) : pathTemplate));
        }
        pathRegexBuilder.append("$");

        return new ParseResult(
            pathTemplate,
            pathRegexBuilder.toString(),
            Collections.unmodifiableList(keys)
        );
    }


    private String encode(String segment) {
        return Pattern.quote(UriComponentsBuilder.fromPath(segment).toUriString());
    }

    private List<PathVarMatch> getPathVarMatches(String sourcePathTemplate) {
        var result = new ArrayList<PathVarMatch>();
        var pathVarMatcher = PATH_TEMPLATE_PATH_VAR_PATTERN.matcher(sourcePathTemplate);
        while (pathVarMatcher.find()) {
            var key = pathVarMatcher.group(1);
            var matchStart = pathVarMatcher.start();
            var matchEnd = pathVarMatcher.end();
            result.add(new PathVarMatch(key, matchStart, matchEnd));
        }
        return Collections.unmodifiableList(result);
    }


    @Getter
    @RequiredArgsConstructor
    public static class ParseResult {

        private final String sourcePathTemplate;
        private final String resultPathRegex;
        private final List<String> keys;
    }

    @RequiredArgsConstructor
    private static class PathVarMatch {

        private final String key;
        private final int start;
        private final int end;
    }

    private static class UriVariablesStubMap extends AbstractMap<String, String> {

        private static final String STUB_VALUE = "x";

        @Override public boolean containsKey(Object key)        { return true; }
        @Override public String get(Object key)                 { return STUB_VALUE; }
        @Override public Set<Entry<String, String>> entrySet()  { throw new UnsupportedOperationException(); }
    }
}
