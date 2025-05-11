package com.Vistoraproject.VistoraProject;

import org.springframework.stereotype.Service;

import java.sql.*;
import java.util.*;

@Service
public class MetaDataService {

    private Connection connection;

    public void initConnection(Configfile config) {
        if (connection == null) {
            try {
                connection = DriverManager.getConnection(config.getUrl(), config.getUsername(), config.getPassword());
                System.out.println("✅ Connection established successfully.");
            } catch (SQLException e) {
                System.out.println("❌ Failed to connect to database.");
                e.printStackTrace();
            }
        }
    }

    public List<String> getAllTables() {
        List<String> tables = new ArrayList<>();
        try {
            if (connection == null) return tables;

            DatabaseMetaData metaData = connection.getMetaData();
            ResultSet rs = metaData.getTables(null, null, "%", new String[]{"TABLE"});

            while (rs.next()) {
                tables.add(rs.getString("TABLE_NAME"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tables;
    }

    public List<Map<String, Object>> getColumnMetadata(String tableName) {
        List<Map<String, Object>> columnsList = new ArrayList<>();
        try {
            if (connection == null) return columnsList;

            DatabaseMetaData metaData = connection.getMetaData();
            ResultSet columns = metaData.getColumns(null, null, tableName, null);

            while (columns.next()) {
                Map<String, Object> column = new LinkedHashMap<>();
                column.put("Column Name", columns.getString("COLUMN_NAME"));
                column.put("Type", columns.getString("TYPE_NAME"));
                column.put("Size", columns.getInt("COLUMN_SIZE"));
                column.put("Nullable", columns.getInt("NULLABLE") == DatabaseMetaData.columnNullable);
                columnsList.add(column);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return columnsList;
    }

    public List<Map<String, Object>> getPrimaryKeys(String tableName) {
        List<Map<String, Object>> pkList = new ArrayList<>();
        try {
            if (connection == null) return pkList;

            DatabaseMetaData metaData = connection.getMetaData();
            ResultSet pk = metaData.getPrimaryKeys(null, null, tableName);

            while (pk.next()) {
                Map<String, Object> entry = new LinkedHashMap<>();
                entry.put("Primary Key Column", pk.getString("COLUMN_NAME"));
                entry.put("Key Name", pk.getString("PK_NAME"));
                entry.put("Sequence", pk.getShort("KEY_SEQ"));
                pkList.add(entry);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return pkList;
    }

    public List<Map<String, Object>> getForeignKeys(String tableName) {
        List<Map<String, Object>> fkList = new ArrayList<>();
        try {
            if (connection == null) return fkList;

            DatabaseMetaData metaData = connection.getMetaData();
            ResultSet fk = metaData.getImportedKeys(null, null, tableName);

            while (fk.next()) {
                Map<String, Object> entry = new LinkedHashMap<>();
                entry.put("FK Column", fk.getString("FKCOLUMN_NAME"));
                entry.put("References Table", fk.getString("PKTABLE_NAME"));
                entry.put("References Column", fk.getString("PKCOLUMN_NAME"));
                entry.put("Foreign Key Name", fk.getString("FK_NAME"));
                fkList.add(entry);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return fkList;
    }

    public List<Map<String, Object>> getIndexes(String tableName) {
        List<Map<String, Object>> indexList = new ArrayList<>();
        try {
            if (connection == null) return indexList;

            DatabaseMetaData metaData = connection.getMetaData();
            ResultSet indexes = metaData.getIndexInfo(null, null, tableName, false, false);

            while (indexes.next()) {
                Map<String, Object> index = new LinkedHashMap<>();
                index.put("Index Name", indexes.getString("INDEX_NAME"));
                index.put("Column Name", indexes.getString("COLUMN_NAME"));
                index.put("Is Unique", !indexes.getBoolean("NON_UNIQUE"));
                indexList.add(index);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return indexList;
    }
}
