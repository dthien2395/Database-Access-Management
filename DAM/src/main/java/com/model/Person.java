package com.model;

import com.field.DataType;
import com.field.DatabaseField;
import com.table.DatabaseTable;

/**
 * Created by DangThiHien on 05/07/2017.
 */
@DatabaseTable(tableName = "person")
public class Person {

    @DatabaseField(generatedId = true)
    private Integer id;

    @DatabaseField(columnName = "name", dataType = DataType.STRING, canBeNull = false)
    private String name;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
