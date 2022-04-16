package com.mt.data;


import com.mt.data.enums.Direction;

public class Sortable {
    private String field;
    private Direction direction;
    private String tableName;

    public Sortable(String sort) {
        if (sort != null) {
            String[] arr = sort.split(",");
            this.field = arr[0].trim();
            if (arr.length == 1) {
                this.direction = Direction.ASC;
            } else {
                String directionStr = arr[1].toUpperCase().trim();
                this.direction = directionStr.equals("DESC") ? Direction.DESC : Direction.ASC;
            }
        }
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public Direction getDirection() {
        return direction;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    public boolean canSort() {
        return this.direction != null && this.field != null;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

}
