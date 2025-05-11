package com.Vistoraproject.VistoraProject;

import java.util.*;

public class modelgenerator {
    public String generateModelAsString(String table, MetaDataService service) {
        List<Map<String, Object>> columns = service.getColumnMetadata(table);
        List<Map<String, Object>> foreignKeys = service.getForeignKeys(table);
        String className = capitalize(table);

        Set<String> imports = new HashSet<>();
        StringBuilder sb = new StringBuilder();

        sb.append("package com.Vistoraproject.VistoraProject.generated;\n\n");

        // Imports (e.g. java.sql.Date)
        for (Map<String, Object> column : columns) {
            String sqlType = ((String) column.get("Type")).toLowerCase();
            if (sqlType.contains("date")) {
                imports.add("import java.sql.Date;");
            }
        }

        // Add imports for foreign key references
        for (Map<String, Object> fk : foreignKeys) {
            String refTable = (String) fk.get("References Table");
            if (refTable != null) {
                imports.add("import com.Vistoraproject.VistoraProject.generated." + capitalize(refTable) + ";");
            }
        }

        for (String imp : imports) {
            sb.append(imp).append("\n");
        }
        if (!imports.isEmpty()) sb.append("\n");

        sb.append("public class ").append(className).append(" {\n\n");

        // Fields
        for (Map<String, Object> column : columns) {
            String name = (String) column.get("Column Name");
            String sqlType = (String) column.get("Type");

            Optional<Map<String, Object>> fk = foreignKeys.stream()
                    .filter(f -> f.get("FK Column").equals(name)).findFirst();

            if (fk.isPresent()) {
                String referencedClass = capitalize((String) fk.get().get("References Table"));
                sb.append("    private ").append(referencedClass).append(" ").append(name).append(";").append("\n");
            } else {
                sb.append("    private ").append(mapToJavaType(sqlType)).append(" ").append(name).append(";").append("\n");
            }
        }

        sb.append("\n");

        // Getters and Setters
        for (Map<String, Object> column : columns) {
            String name = (String) column.get("Column Name");
            String sqlType = (String) column.get("Type");
            String methodSuffix = capitalize(name);

            Optional<Map<String, Object>> fk = foreignKeys.stream()
                    .filter(f -> f.get("FK Column").equals(name)).findFirst();

            String type = fk.isPresent()
                    ? capitalize((String) fk.get().get("References Table"))
                    : mapToJavaType(sqlType);

            sb.append("    public ").append(type).append(" get").append(methodSuffix).append("() {\n");
            sb.append("        return ").append(name).append(";\n");
            sb.append("    }\n\n");

            sb.append("    public void set").append(methodSuffix).append("(").append(type).append(" ").append(name).append(") {\n");
            sb.append("        this.").append(name).append(" = ").append(name).append(";\n");
            sb.append("    }\n\n");
        }

        sb.append("}\n");

        return sb.toString();
    }

    private String mapToJavaType(String sqlType) {
        sqlType = sqlType.toLowerCase();
        if (sqlType.contains("int")) return "int";
        if (sqlType.contains("char") || sqlType.contains("text") || sqlType.contains("varchar")) return "String";
        if (sqlType.contains("date")) return "Date";
        if (sqlType.contains("float") || sqlType.contains("double") || sqlType.contains("decimal")) return "double";
        if (sqlType.contains("bool") || sqlType.contains("bit")) return "boolean";
        return "String"; // default fallback
    }

    private String capitalize(String str) {
        if (str == null || str.isEmpty()) return str;
        return Character.toUpperCase(str.charAt(0)) + str.substring(1);
    }
}
