package com.cpic.barsms.bpm.common.utils;

import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class StringSplitter {

    private StringSplitter() {}

    public static String splitByPipe(String source, int index) {
        if (!StringUtils.hasText(source)) {
            return null;
        }
        String[] parts = source.split("\\|");
        if (index < parts.length) {
            return parts[index].trim();
        }
        return null;
    }

    public static List<String> splitByComma(String source) {
        if (!StringUtils.hasText(source)) {
            return new ArrayList<>();
        }
        return Arrays.stream(source.split(","))
                .map(String::trim)
                .filter(StringUtils::hasText)
                .collect(Collectors.toList());
    }
}
