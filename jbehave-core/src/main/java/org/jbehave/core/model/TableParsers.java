package org.jbehave.core.model;

import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.jbehave.core.configuration.Keywords;
import org.jbehave.core.model.ExamplesTable.TableProperties;
import org.jbehave.core.model.ExamplesTable.TablePropertiesQueue;
import org.jbehave.core.model.ExamplesTable.TableRows;
import org.jbehave.core.steps.ParameterConverters;

public class TableParsers {

    private static final String ROW_SEPARATOR_PATTERN = "\r?\n";

    public TableParsers() {
    }

    public TablePropertiesQueue parseProperties(String tableAsString, Keywords keywords,
            ParameterConverters parameterConverters) {
        return parseProperties(tableAsString, keywords.examplesTableHeaderSeparator(), keywords.examplesTableValueSeparator(),
                keywords.examplesTableIgnorableSeparator(), parameterConverters);
    }

    public TablePropertiesQueue parseProperties(String tableAsString, String headerSeparator, String valueSeparator,
            String ignorableSeparator, ParameterConverters parameterConverters) {
        Deque<TableProperties> properties = new LinkedList<>();
        String tableWithoutProperties = tableAsString.trim();
        Matcher matcher = ExamplesTable.INLINED_PROPERTIES_PATTERN.matcher(tableWithoutProperties);
        while (matcher.matches()) {
            String propertiesAsString = matcher.group(1);
            propertiesAsString = StringUtils.replace(propertiesAsString, "\\{", "{");
            propertiesAsString = StringUtils.replace(propertiesAsString, "\\}", "}");
            properties.add(new TableProperties(propertiesAsString, headerSeparator,
                    valueSeparator, ignorableSeparator, parameterConverters));
            tableWithoutProperties = matcher.group(2).trim();
            matcher = ExamplesTable.INLINED_PROPERTIES_PATTERN.matcher(tableWithoutProperties);
        }
        if (properties.isEmpty()) {
            properties.add(new TableProperties("", headerSeparator, valueSeparator, ignorableSeparator, parameterConverters));
        }
        return new TablePropertiesQueue(tableWithoutProperties, properties);
    }

    public TableRows parseRows(String tableAsString, TableProperties properties) {
        List<String> headers = new ArrayList<>();
        List<Map<String, String>> data = new ArrayList<>();

        String[] rows = tableAsString.split(ROW_SEPARATOR_PATTERN);
        for (String row : rows) {
            String trimmedRow = row.trim();
            if (trimmedRow.startsWith(properties.getIgnorableSeparator()) || trimmedRow.isEmpty()) {
                // skip ignorable or empty lines
                continue;
            } else if (headers.isEmpty()) {
                headers.addAll(parseRow(trimmedRow, true, properties));
            } else {
                List<String> columns = parseRow(trimmedRow, false, properties);
                Map<String, String> map = new LinkedHashMap<>();
                for (int column = 0; column < columns.size(); column++) {
                    if (column < headers.size()) {
                        map.put(headers.get(column), columns.get(column));
                    }
                }
                data.add(map);
            }
        }

        return new TableRows(headers, data);
    }

    public List<String> parseRow(String rowAsString, boolean header, TableProperties properties) {
        String separator = header ? properties.getHeaderSeparator() : properties.getValueSeparator();
        return parseRow(rowAsString.trim(), separator, properties.getCommentSeparator(), properties.isTrim());
    }

    private List<String> parseRow(String rowAsString, String separator, String commentSeparator, boolean trimValues) {
        return Stream.of(StringUtils.split(rowAsString, separator))
                     .map(cell -> StringUtils.substringBefore(cell, commentSeparator))
                     .map(cell -> trimValues ? cell.trim() : cell)
                     .collect(Collectors.toList());
    }
}
