package com.simple.sms.model;

import android.provider.BaseColumns;

public final class RuleTable {
    // To prevent someone from accidentally instantiating the contract class,
    // make the constructor private.
    private RuleTable() {}

    /* Inner class that defines the table contents */
    public static class RuleEntry implements BaseColumns {
        public static final String TABLE_NAME = "rule";
        public static final String COLUMN_NAME_FILED = "filed";
        public static final String COLUMN_NAME_CHECK = "tcheck";
        public static final String COLUMN_NAME_VALUE = "value";
        public static final String COLUMN_NAME_SENDER_ID= "sender_id";
        public static final String COLUMN_NAME_TIME = "time";
        public static final String COLUMN_NAME_IS_CHOOSE = "is_chose";
    }
}
