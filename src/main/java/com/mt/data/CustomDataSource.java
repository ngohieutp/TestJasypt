package com.mt.data;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public class CustomDataSource extends HikariDataSource {

    public CustomDataSource(HikariConfig configuration) {
        super(configuration);
    }

}
