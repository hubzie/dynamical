package com.example.dynamical.data

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

class Migrations {
    companion object {
        val MIGRATION_2_3 = object : Migration(2,3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("alter table route_table add column date integer not null default 0")
            }
        }

        val MIGRATION_3_4 = object : Migration(2,3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("alter table route_table add column shared integer not null default 0")
            }
        }
    }
}