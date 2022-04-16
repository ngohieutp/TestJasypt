package com.mt.data;

import com.mt.data.repository.anotation.FindByReference;
import com.mt.data.repository.anotation.IgnoreConditionFilter;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;

import java.lang.reflect.Field;
import java.math.BigInteger;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class QueryHelper {

    /**
     * Append filter or operation condition for query
     *
     * @param fromAndWhere
     * @param queryParams
     * @param field
     * @param op
     * @param value
     */
    public static void appendOrCondition(StringBuilder fromAndWhere, Map<String, Object> queryParams, String field,
                                         String op, Object value) {
        fromAndWhere.append(String.format(Constants.QUERY_APPEND_OR_CONDITION, field, op, field));
        queryParams.put(field, value);
    }

    /**
     * Append filter condition for query
     *
     * @param fromAndWhere
     * @param queryParams
     * @param field
     * @param op
     * @param value
     */
    public static void appendCondition(StringBuilder fromAndWhere, Map<String, Object> queryParams, String field,
                                       String op, Object value, String prefixFieldName) {
        if (StringUtils.isBlank(prefixFieldName)) {
            appendCondition(fromAndWhere, queryParams, field, op, value);
        } else {
            fromAndWhere.append(String.format(Constants.QUERY_APPEND_CONDITION,
                    String.format(Constants.QUERY_FIELD_BY_FK, prefixFieldName, field), op, field));
        }
        queryParams.put(field, value);
    }

    /**
     * Append filter condition for query
     *
     * @param fromAndWhere
     * @param queryParams
     * @param field
     * @param op
     * @param value
     */
    public static void appendCondition(StringBuilder fromAndWhere, Map<String, Object> queryParams, String field,
                                       String op, Object value) {
        fromAndWhere.append(String.format(Constants.QUERY_APPEND_CONDITION, field, op, field));
        queryParams.put(field, value);
    }

    /**
     * Append filter condition for query
     *
     * @param fromAndWhere
     * @param queryParams
     * @param field
     * @param op
     * @param value
     */
    public static void appendCondition(StringBuilder fromAndWhere, Map<String, Object> queryParams, String field,
                                       String op, Object value, boolean isIgnoreCase) {
        if (!isIgnoreCase) {
            appendCondition(fromAndWhere, queryParams, field, op, value);
        } else {
            fromAndWhere.append(String.format(Constants.QUERY_APPEND_CONDITION_IGNORE_CASE, field, op, field));
            queryParams.put(field, value);
        }
    }

    /**
     * The method processes append filter by DTO
     *
     * @param dto
     * @param fromAndWhere
     * @param queryParams
     * @throws Exception
     */
    public static void appendFilterByDTO(Object dto, StringBuilder fromAndWhere, Map<String, Object> queryParams)
            throws Exception {
        Field[] fields = FieldUtils.getAllFields(dto.getClass());
        for (Field field : fields) {
            field.setAccessible(true);
            if (field.getAnnotation(IgnoreConditionFilter.class) == null) {
                Object value = field.get(dto);
                if (value != null) {
                    String fieldName = field.getName();
                    String prefixFieldName = Constants.STRING_EMPTY;
                    // Check search by FK
                    FindByReference findByReference = field.getAnnotation(FindByReference.class);
                    if (findByReference != null) {
                        prefixFieldName = findByReference.tableName();
                    }
                    if (field.getType().isAssignableFrom(Long.class) || field.getType().isAssignableFrom(Integer.class)
                            || field.getType().isAssignableFrom(Double.class)
                            || field.getType().isAssignableFrom(Short.class)
                            || field.getType().isAssignableFrom(BigInteger.class)
                            || Enum.class.isAssignableFrom(field.getType())) {
                        QueryHelper.appendCondition(fromAndWhere, queryParams, fieldName, Constants.QUERY_OPERATION_EQ,
                                value, prefixFieldName);
                    } else if (field.getType().isAssignableFrom(String.class)) {
                        QueryHelper.appendCondition(fromAndWhere, queryParams, "lower(" + fieldName + ")",
                                Constants.QUERY_OPERATION_LIKE, String.format(Constants.QUERY_VALUE_LIKE_CONTAIN,
                                        Constants.STRING_PERCENT, value, Constants.STRING_PERCENT),
                                prefixFieldName);
                    }
                }
            }
        }
    }

    public static void appendFilterByFilter(Object dto, StringBuilder fromAndWhere, Map<String, Object> queryParams,
                                            String tableIdentify) throws Exception {
        Field[] fields = FieldUtils.getAllFields(dto.getClass());
        for (Field field : fields) {
            field.setAccessible(true);
            if (field.getAnnotation(IgnoreConditionFilter.class) == null) {
                Object value = field.get(dto);
                if (value != null) {
                    String fieldName = field.getName();
                    // String prefixFieldName = Constants.STRING_EMPTY;
                    // Check search by FK
                    FindByReference findByReference = field.getAnnotation(FindByReference.class);
                    if (findByReference != null) {
                        // prefixFieldName = findByReference.tableName();
                        appendFilterByFilter(value, fromAndWhere, queryParams, fieldName);
                    }
                    if (field.getType().isAssignableFrom(Long.class) || field.getType().isAssignableFrom(Integer.class)
                            || field.getType().isAssignableFrom(Double.class)
                            || field.getType().isAssignableFrom(Short.class)
                            || field.getType().isAssignableFrom(BigInteger.class)
                            || Enum.class.isAssignableFrom(field.getType())) {
                        QueryHelper.appendCondition(fromAndWhere, queryParams, fieldName, Constants.QUERY_OPERATION_EQ,
                                value, tableIdentify);
                    } else if (field.getType().isAssignableFrom(String.class)) {
                        QueryHelper.appendCondition(fromAndWhere, queryParams, "lower(" + fieldName + ")",
                                Constants.QUERY_OPERATION_LIKE, String.format(Constants.QUERY_VALUE_LIKE_CONTAIN,
                                        Constants.STRING_PERCENT, value, Constants.STRING_PERCENT),
                                tableIdentify);
                    }
                }
            }
        }
    }

    /**
     * The function append order by query
     *
     * @param orderByQuery
     * @param sortable
     */
    public static void appendOrderBySql(Class<?> clazz, StringBuilder orderByQuery, Sortable sortable) {
        if (sortable != null && StringUtils.isNotBlank(sortable.getField())
                && QueryHelper.hasField(clazz, sortable.getField())) {
            orderByQuery.append(String.format(Constants.QUERY_ORDER, sortable.getField(), sortable.getDirection()));
        }
    }

    public static boolean hasField(Class<?> type, String fieldName) {
        Set<String> fields = getAllFields(type);
        return fields.contains(fieldName);
    }

    private static Set<String> getAllFields(final Class<?> type) {
        Set<String> fields = new HashSet<String>();
        for (Field field : type.getDeclaredFields()) {
            fields.add(field.getName());
        }

        if (type.getSuperclass() != null) {
            fields.addAll(getAllFields(type.getSuperclass()));
        }
        return fields;
    }

    public static String formatLike(String value) {
        return String.format("%%%s%%", value != null ? value : "");
    }

    public static String formatLikeBlank(String value) {
        return StringUtils.isNotBlank(value) ? String.format("%%%s%%", value) : null;
    }

    public static String formatLikeStartWith(String value) {
        return String.format("%s%%", value != null ? value : "");
    }

    public static String formatLikeStartWithBlank(String value) {
        return StringUtils.isNotBlank(value) ? String.format("%s%%", value) : null;
    }

}
