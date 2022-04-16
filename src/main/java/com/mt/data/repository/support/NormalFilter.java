package com.mt.data.repository.support;

import com.mt.data.Constants;
import com.mt.data.QueryHelper;
import com.mt.data.enums.Operator;
import com.mt.data.enums.OperatorLogical;
import com.mt.data.repository.anotation.FindByReference;
import com.mt.data.repository.anotation.IgnoreConditionFilter;
import com.mt.data.repository.anotation.JoinTypeDetail;
import com.mt.data.repository.anotation.TableJoinType;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.criteria.*;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NormalFilter<T extends Serializable> implements CustomizeSpecification<T> {
    private final static Logger LOGGER = LoggerFactory.getLogger(NormalFilter.class);
    private Object requestObj;
    private Long userId;
    private Map<String, Join> mapPathTempl = new HashMap<>();
    private final Map<String, JoinType> mapTableJoinType = new HashMap<>();

    public NormalFilter(Object requestObj) {
        super();
        this.requestObj = requestObj;
    }

    @Override
    public List<Predicate> toPredicates(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder builder) throws IllegalAccessException {
        List<Predicate> predicates = new ArrayList<>();

        if (requestObj == null) return predicates;

        // Initial map join type for table
        initMapTableJoinType();

        Field[] fields = FieldUtils.getAllFields(requestObj.getClass());

        Map<String, Join> mapPath = new HashMap<>();

        for (Field field : fields) {
            field.setAccessible(true);
            if (field.getAnnotation(IgnoreConditionFilter.class) == null) {
                Object value = field.get(requestObj);
                if (value != null) {
                    String fieldName = field.getName();
                    String tableNames = "";
                    Operator operator = Operator.DEFAULT;
                    OperatorLogical operatorLogical = OperatorLogical.AND;

                    // Check search by field FK
                    FindByReference findByReference = field.getAnnotation(FindByReference.class);
                    if (findByReference != null) {
                        if (StringUtils.isNotBlank(findByReference.altField())) {
                            fieldName = findByReference.altField();
                        }
                        tableNames = findByReference.tableName();
                        operator = findByReference.operator();
                        operatorLogical = findByReference.logicalOperator();
                    }

                    if (operator == Operator.DEFAULT) {
                        operator = Operator.EQ;
                        if (field.getType().isAssignableFrom(String.class)) {
                            operator = Operator.LIKE_CONTAIN_LOWERCASE;
                        }
                    }

                    if (operatorLogical == OperatorLogical.OR) {
                        buildOrFilterBuilder(root, query, builder, value, predicates, mapPath);
                    } else {
                        appendCondition(root, query, builder, fieldName, operator, value, tableNames, predicates, mapPath);
                    }
                }
            }
        }
        this.mapPathTempl = mapPath;
        return predicates;
    }

    public List<Predicate> appendCondition(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder builder, String field, Operator op, Object value, String tableNames, List<Predicate> predicates, Map<String, Join> mapPath) {
        Expression expression;
        if (StringUtils.isBlank(tableNames)) {
            expression = root.get(field);
        } else {
            String[] paths = tableNames.split("\\.");
            Join joinPath = null;
            for (String p : paths) {
                if (mapPath.get(p) == null) {
                    JoinType joinType = mapTableJoinType.get(p) == null ? JoinType.INNER : mapTableJoinType.get(p);
                    joinPath = joinPath == null ? root.join(p, joinType) : joinPath.join(p, joinType);
                    mapPath.put(p, joinPath);
                } else {
                    joinPath = mapPath.get(p);
                }
            }
            expression = joinPath.get(field);
        }
        switch (op) {
            case EQ:
                predicates.add(builder.equal(expression, value));
                break;
            case NE:
                predicates.add(builder.notEqual(expression, value));
                break;
            case NOTNULL:
                predicates.add(builder.isNotNull(expression));
                break;
            case NULL:
                predicates.add(builder.isNull(expression));
                break;
            case LIKE:
                predicates.add(builder.like(expression, String.valueOf(value)));
                break;
            case LT:
                predicates.add(builder.lessThan(expression, (Comparable) value));
                break;
            case GT:
                predicates.add(builder.greaterThan(expression, (Comparable) value));
                break;
            case LTE:
                predicates.add(builder.lessThanOrEqualTo(expression, (Comparable) value));
                break;
            case GTE:
                predicates.add(builder.greaterThanOrEqualTo(expression, (Comparable) value));
                break;
            case LIKE_CONTAIN_LOWERCASE:
                predicates.add(builder.like(builder.lower(expression), Constants.STRING_PERCENT + (value != null ? value.toString().toLowerCase() : "") + Constants.STRING_PERCENT));
                break;
            case LIKE_IGNORE_LOWERCASE:
                predicates.add(builder.like(builder.lower(expression), String.valueOf(value).toLowerCase()));
                break;
            case LIKE_START_WITH:
                predicates.add(builder.like(builder.lower(expression), QueryHelper.formatLikeStartWith(String.valueOf(value).toLowerCase())));
                break;
            case IN:
                predicates.add(builder.in(expression).value(value));
                break;
            case NIN:
                predicates.add(builder.in(expression).value(value).not());
                break;
            default:
                break;
        }
        return predicates;
    }

    private void buildOrFilterBuilder(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder builder, Object request, List<Predicate> predicates, Map<String, Join> mapPath) throws IllegalArgumentException, IllegalAccessException {
        Field[] fields = FieldUtils.getAllFields(request.getClass());
        List<Predicate> orPredicates = new ArrayList<>();

        for (Field field : fields) {
            field.setAccessible(true);
            if (field.getAnnotation(IgnoreConditionFilter.class) == null) {
                Object value = field.get(request);
                if (value != null) {
                    String fieldName = field.getName();
                    String tableNames = "";
                    Operator operator = Operator.DEFAULT;
                    // Check search by field FK
                    FindByReference findByReference = field.getAnnotation(FindByReference.class);
                    if (findByReference != null) {
                        if (StringUtils.isNotBlank(findByReference.altField())) {
                            fieldName = findByReference.altField();
                        }
                        tableNames = findByReference.tableName();
                        operator = findByReference.operator();
                    }

                    if (operator == Operator.DEFAULT) {
                        operator = Operator.EQ;
                        if (field.getType().isAssignableFrom(String.class)) {
                            operator = Operator.LIKE_CONTAIN_LOWERCASE;
                        }
                    }
                    appendCondition(root, query, builder, fieldName, operator, value, tableNames, orPredicates, mapPath);
                }
            }
        }

        predicates.add(builder.or(orPredicates.toArray(new Predicate[]{})));
    }

    private void initMapTableJoinType() {
        TableJoinType tableJoinType = requestObj.getClass().getAnnotation(TableJoinType.class);
        if (tableJoinType != null && tableJoinType.joinTypeDetails().length > 0) {
            for (JoinTypeDetail typeDetail : tableJoinType.joinTypeDetails()) {
                mapTableJoinType.put(typeDetail.table(), typeDetail.joinType());
            }
        }
    }
    // private void buildOrToBuilder

    public Object getRequestObj() {
        return requestObj;
    }

    public void setRequestObj(Object requestObj) {
        this.requestObj = requestObj;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Map<String, Join> getMapPathTempl() {
        return mapPathTempl;
    }

}
