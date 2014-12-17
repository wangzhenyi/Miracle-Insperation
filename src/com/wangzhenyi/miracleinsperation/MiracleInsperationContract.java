package com.wangzhenyi.miracleinsperation;

import android.provider.BaseColumns;

/* Contract class for the Miracle Insperation app */
public final class MiracleInsperationContract {
	public MiracleInsperationContract() {
	}

	/* Inner class that defines the table contents */
	public static abstract class InsperatioinEntry implements BaseColumns {
		public static final String TABLE_NAME = "inspections";
		public static final String COLUMN_NAME_ID = "id";
		public static final String COLUMN_NAME_DATE = "date";
		public static final String COLUMN_NAME_CONTENT = "content";
		public static final String COLUMN_NAME_DELETED = "deleted";
	}

}
