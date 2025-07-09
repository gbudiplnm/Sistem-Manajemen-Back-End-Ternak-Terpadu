package com.ternak.sapi.helper;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.*;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.filter.CompareFilter.CompareOp;
import org.apache.hadoop.hbase.filter.Filter;
import org.apache.hadoop.hbase.filter.FilterList;
import org.apache.hadoop.hbase.filter.PageFilter;
import org.apache.hadoop.hbase.filter.SingleColumnValueFilter;
import org.apache.hadoop.hbase.filter.SkipFilter;
import org.apache.hadoop.hbase.util.Bytes;
import com.fasterxml.jackson.core.type.TypeReference;

import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HBaseCustomClient {

    private HBaseAdmin admin;
    private Connection connection = null;

    public HBaseCustomClient(Configuration conf) throws IOException {
        connection = ConnectionFactory.createConnection(conf);
        admin = (HBaseAdmin) connection.getAdmin();
    }

    public List<String> getColumnsWithPrefix(String tableName, String rowKey, String family, String prefix)
            throws IOException {
        List<String> matchedColumns = new ArrayList<>();

        Table table = connection.getTable(TableName.valueOf(tableName));
        Get get = new Get(Bytes.toBytes(rowKey));
        get.addFamily(Bytes.toBytes(family));
        Result result = table.get(get);

        for (Cell cell : result.rawCells()) {
            String qualifier = Bytes.toString(CellUtil.cloneQualifier(cell));
            if (qualifier.startsWith(prefix)) {
                matchedColumns.add(qualifier);
            }
        }

        table.close();
        return matchedColumns;
    }

    public void deleteColumns(String tableName, String rowKey, String family, List<String> qualifiers)
            throws IOException {
        Table table = connection.getTable(TableName.valueOf(tableName));
        Delete delete = new Delete(Bytes.toBytes(rowKey));

        for (String qualifier : qualifiers) {
            delete.addColumn(Bytes.toBytes(family), Bytes.toBytes(qualifier));
        }

        table.delete(delete);
        table.close();
    }

    public void createTable(TableName tableName, String[] CFs) {

        try {
            if (admin.tableExists(tableName)) {

                System.out.println(tableName + "Already Exists");

            } else {

                HTableDescriptor tableDescriptor = new HTableDescriptor(TableName.valueOf(tableName.toString()));

                for (String CFName : CFs) {
                    tableDescriptor.addFamily(new HColumnDescriptor(CFName));
                }

                admin.createTable(tableDescriptor);

            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    public void deleteTable(TableName tableName) {

        try {
            if (admin.tableExists(tableName)) {

                admin.disableTable(tableName);
                admin.deleteTable(tableName);
            } else {
                System.out.println(tableName + " Doesn't exist");
            }

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    public void insertRecord(TableName tableName, String rowKey, String family, String qualifier, String value) {

        try {
            Table table = connection.getTable(tableName);
            Put p = new Put(Bytes.toBytes(rowKey));
            p.addColumn(Bytes.toBytes(family), Bytes.toBytes(qualifier), Bytes.toBytes(value));
            table.put(p);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void insertListRecord(TableName tableName, String rowKey, String family, String qualifier,
            List<String> values) {
        try {
            Table table = connection.getTable(tableName);
            Put p = new Put(Bytes.toBytes(rowKey));
            for (String value : values) {
                p.addColumn(Bytes.toBytes(family), Bytes.toBytes(qualifier), Bytes.toBytes(value));
            }
            table.put(p);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void deleteRecord(String tableName, String rowKey) {

        try {
            Table table = connection.getTable(TableName.valueOf(tableName));
            Delete d = new Delete(Bytes.toBytes(rowKey));
            table.delete(d);

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    public Table getTable(String tableName) throws IOException {
        return connection.getTable(TableName.valueOf(tableName));
    }

    public <T> List<T> showListTable(String tablename, Map<String, String> columnMapping, Class<T> modelClass,
            int sizeLimit) {
        ResultScanner rsObj = null;

        try {
            Table table = connection.getTable(TableName.valueOf(tablename));

            Scan s = new Scan();
            s.setCaching(100);
            if (sizeLimit > 0) {
                s.setLimit(sizeLimit);
            }

            TableDescriptor tableDescriptor = connection.getAdmin().getDescriptor(TableName.valueOf(tablename));
            ColumnFamilyDescriptor[] columnFamilies = tableDescriptor.getColumnFamilies();
            for (ColumnFamilyDescriptor columnFamily : columnFamilies) {
                byte[] family = columnFamily.getName();
                s.addFamily(family);
            }

            rsObj = table.getScanner(s);

            // Create a list to store the objects
            List<T> objects = new ArrayList<T>();

            for (Result result : rsObj) {
                T object = modelClass.newInstance();
                for (Cell cell : result.listCells()) {
                    String familyName = Bytes.toString(CellUtil.cloneFamily(cell));
                    String columnName = Bytes.toString(CellUtil.cloneQualifier(cell));

                    String variableName = columnName;
                    int underscoreIndex = columnName.indexOf("_");
                    if (underscoreIndex != -1) {
                        int numberStartIndex = underscoreIndex + 1;
                        int numberEndIndex = numberStartIndex;
                        while (numberEndIndex < columnName.length()
                                && Character.isDigit(columnName.charAt(numberEndIndex))) {
                            numberEndIndex++;
                        }
                        variableName = columnName.substring(0, underscoreIndex);
                    }
                    variableName = columnMapping.get(variableName);

                    String value = Bytes.toString(CellUtil.cloneValue(cell));
                    if (columnMapping.containsKey(familyName)) {
                        String subFieldName = columnName.substring(columnName.indexOf(".") + 1);
                        Field familyField = object.getClass().getDeclaredField(familyName);
                        familyField.setAccessible(true);
                        Object familyObject = familyField.get(object);
                        if (familyObject == null) {
                            if (familyField.getType() == List.class) {
                                familyObject = new ArrayList<>();
                                familyField.set(object, familyObject);
                            } else {
                                familyObject = familyField.getType().newInstance();
                                familyField.set(object, familyObject);
                            }
                        }

                        if (familyObject instanceof List) {
                            List targetList = (List) familyObject;
                            ObjectMapper mapper = new ObjectMapper();

                            try {
                                JsonNode jsonNode = mapper.readTree((String) value);

                                if (jsonNode.isArray()) {
                                    // Try to detect target generic type
                                    Field listField = object.getClass().getDeclaredField(familyName);
                                    Class<?> listItemType = Object.class;

                                    if (listField.getGenericType() instanceof ParameterizedType) {
                                        ParameterizedType listType = (ParameterizedType) listField.getGenericType();
                                        listItemType = (Class<?>) listType.getActualTypeArguments()[0];
                                    }

                                    for (JsonNode itemNode : jsonNode) {
                                        if (itemNode.isObject()) {
                                            Object item = mapper.treeToValue(itemNode, listItemType);
                                            targetList.add(item);
                                        } else if (itemNode.isValueNode()) {
                                            targetList.add(mapper.treeToValue(itemNode, String.class));
                                        }
                                    }

                                } else if (jsonNode.isObject()) {
                                    // Handle single map-like object
                                    Map<String, Object> singleMap = mapper.convertValue(jsonNode,
                                            new TypeReference<Map<String, Object>>() {
                                            });
                                    targetList.add(singleMap);
                                } else {
                                    // Fallback: plain string
                                    targetList.add(value);
                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                                // fallback
                                targetList.add(value);
                            }

                        } else {
                            Field subField = familyObject.getClass().getDeclaredField(subFieldName);
                            subField.setAccessible(true);
                            setField(subField, familyObject, value);
                        }
                    } else {
                        if (variableName != null) {
                            Field field = object.getClass().getDeclaredField(variableName);
                            field.setAccessible(true);
                            if (field.getType().isEnum()) {
                                Object[] enumConstants = field.getType().getEnumConstants();
                                String enumName = value;
                                for (Object constant : enumConstants) {
                                    if (((Enum) constant).name().equals(enumName)) {
                                        field.set(object, constant);
                                        break;
                                    }
                                }
                            } else if (field.getType().isArray()) {
                                Class<?> componentType = field.getType().getComponentType();
                                ObjectMapper objectMapper = new ObjectMapper();

                                try {
                                    // Step 1: Read value into JsonNode to inspect
                                    JsonNode rootNode = objectMapper.readTree(value);

                                    // Step 2: Deserialize into a list of proper componentType
                                    List<Object> list = new ArrayList<>();

                                    if (rootNode.isArray()) {
                                        for (JsonNode itemNode : rootNode) {
                                            if (componentType == String.class || componentType.isPrimitive()
                                                    || isWrapperType(componentType)) {
                                                // Simple value types
                                                Object simpleValue = objectMapper.treeToValue(itemNode, componentType);
                                                list.add(simpleValue);
                                            } else {
                                                // Complex object types
                                                Object obj = objectMapper.treeToValue(itemNode, componentType);
                                                list.add(obj);
                                            }
                                        }
                                    }

                                    // Step 3: Convert list to typed array
                                    Object newArray = Array.newInstance(componentType, list.size());
                                    for (int i = 0; i < list.size(); i++) {
                                        Array.set(newArray, i, list.get(i));
                                    }

                                    // Step 4: Set into object
                                    field.setAccessible(true);
                                    field.set(object, newArray);

                                } catch (Exception e) {
                                    e.printStackTrace(); // Handle parse/instantiation error
                                }
                            } else {
                                setField(field, object, value);
                            }
                        }
                    }
                }
                objects.add(object);
            }

            rsObj.close();
            return objects;
        } catch (IOException e) {
            if (rsObj != null) {
                rsObj.close();
            }
            e.printStackTrace();
        } catch (InstantiationException | IllegalAccessException | NoSuchFieldException e) {
            throw new RuntimeException(e);
        }

        return null;
    }

    private static boolean isWrapperType(Class<?> clazz) {
        return clazz == Boolean.class || clazz == Integer.class || clazz == Long.class ||
                clazz == Double.class || clazz == Float.class || clazz == Short.class ||
                clazz == Byte.class || clazz == Character.class;
    }

    public <T> T showDataTable(String tablename, Map<String, String> columnMapping, String uuid, Class<T> modelClass) {
        Result result = null;

        try {
            Table table = connection.getTable(TableName.valueOf(tablename));
            Get get = new Get(Bytes.toBytes(uuid));
            TableDescriptor tableDescriptor = connection.getAdmin().getDescriptor(TableName.valueOf(tablename));
            ColumnFamilyDescriptor[] columnFamilies = tableDescriptor.getColumnFamilies();
            for (ColumnFamilyDescriptor columnFamily : columnFamilies) {
                byte[] family = columnFamily.getName();
                get.addFamily(family);
            }

            result = table.get(get);

            // Create a list to store the objects
            T object = modelClass.newInstance();

            // Do something with the result, e.g. print it to the console
            for (Cell cell : result.rawCells()) {
                // Get the column name
                String familyName = Bytes.toString(CellUtil.cloneFamily(cell));
                String columnName = Bytes.toString(CellUtil.cloneQualifier(cell));

                // Get the variable name from the columnMapping
                String variableName = columnMapping.get(columnName);

                String value = Bytes.toString(CellUtil.cloneValue(cell));
                // Get the value of the cell as a string
                // Check if the variableName contains "department"
                if (columnMapping.containsKey(familyName)) {
                    // Get the subfield name
                    String subFieldName = columnName.substring(columnName.indexOf(".") + 1);
                    // Get the department object from the main object
                    Field familyField = object.getClass().getDeclaredField(familyName);
                    familyField.setAccessible(true);
                    Object familyObject = familyField.get(object);
                    if (familyObject == null) {
                        if (familyField.getType() == List.class) {
                            familyObject = new ArrayList<>();
                            familyField.set(object, familyObject);
                        } else {
                            familyObject = familyField.getType().newInstance();
                            familyField.set(object, familyObject);
                        }
                    }
                    // Set the value to the subfield
                    if (familyObject instanceof List) {
                        Object currentObject = familyObject;
                        ObjectMapper mapper = new ObjectMapper();

                        JsonNode jsonNode = null;
                        try {
                            jsonNode = mapper.readTree((String) value);
                        } catch (Exception e) {
                            // Tidak berformat JSON, lakukan konversi biasa
                        }

                        if (jsonNode != null
                                && jsonNode.getNodeType() == JsonNodeFactory.instance.objectNode().getNodeType()) {
                            // Value berformat JSON, lakukan konversi ke Map
                            Map<String, Object> dataList = mapper.readValue((String) value,
                                    new TypeReference<Map<String, Object>>() {
                                    });
                            ((List) currentObject).add(dataList);
                        } else {
                            // Value tidak berformat JSON, lakukan konversi biasa
                            ((List) currentObject).add(value);
                        }
                    } else {
                        Field subField = familyObject.getClass().getDeclaredField(subFieldName);
                        subField.setAccessible(true);
                        setField(subField, familyObject, value);
                    }
                } else {
                    if (variableName != null) {
                        // Set the value to the variable
                        Field field = object.getClass().getDeclaredField(variableName);
                        field.setAccessible(true);
                        setField(field, object, value);
                    }
                }
            }

            // Close the scanner and table objects
            table.close();
            return object;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InstantiationException | IllegalAccessException | NoSuchFieldException e) {
            throw new RuntimeException(e);
        }

        return null;
    }

    private boolean isSimpleType(Class<?> clazz) {
        return clazz.isPrimitive()
                || clazz == String.class
                || clazz == Boolean.class
                || clazz == Integer.class
                || clazz == Long.class
                || clazz == Double.class
                || clazz == Float.class
                || clazz == Short.class
                || clazz == Byte.class
                || clazz == Character.class;
    }

    /**
     * Searches for a row containing a specific value in a given column,
     * then maps the first found row to an object.
     *
     * @param tableName             The name of the HBase table.
     * @param columnMapping         A map to translate HBase column names to Java
     *                              field names.
     * @param searchColumnFamily    The column family to search within.
     * @param searchColumnQualifier The column qualifier (column name) to search
     *                              within.
     * @param valueToSearchInColumn The value to look for in the specified column.
     * @param modelClass            The class of the object to map the data to.
     * @param <T>                   The type of the modelClass.
     * @return An object of type T populated with data from the first matching row,
     *         or null if no match is found.
     */
    public <T> T getDataByColumn(String tableName, Map<String, String> columnMapping,
            String searchColumnFamily, String searchColumnQualifier,
            String valueToSearchInColumn, Class<T> modelClass) {
        Table table = null;
        ResultScanner scanner = null;

        try {
            table = connection.getTable(TableName.valueOf(tableName));
            Scan scan = new Scan();

            // Filter: find row with specific column value
            SingleColumnValueFilter filter = new SingleColumnValueFilter(
                    Bytes.toBytes(searchColumnFamily),
                    Bytes.toBytes(searchColumnQualifier),
                    CompareOperator.EQUAL,
                    Bytes.toBytes(valueToSearchInColumn));
            filter.setFilterIfMissing(true);
            scan.setFilter(filter);

            // Scan all column families
            TableDescriptor descriptor = connection.getAdmin().getDescriptor(TableName.valueOf(tableName));
            for (ColumnFamilyDescriptor family : descriptor.getColumnFamilies()) {
                scan.addFamily(family.getName());
            }

            scanner = table.getScanner(scan);
            Result result = scanner.next();

            if (result == null || result.isEmpty())
                return null;

            T object = modelClass.getDeclaredConstructor().newInstance();
            ObjectMapper objectMapper = new ObjectMapper();

            for (Cell cell : result.listCells()) {
                String familyName = Bytes.toString(CellUtil.cloneFamily(cell));
                String qualifier = Bytes.toString(CellUtil.cloneQualifier(cell));
                String cellValue = Bytes.toString(CellUtil.cloneValue(cell));

                String baseField = qualifier.contains("_") ? qualifier.substring(0, qualifier.indexOf("_")) : qualifier;
                String targetField = columnMapping.getOrDefault(baseField,
                        columnMapping.getOrDefault(qualifier, qualifier));
                String familyMapped = columnMapping.get(familyName);

                if (familyMapped != null) {
                    Field familyField = modelClass.getDeclaredField(familyMapped);
                    familyField.setAccessible(true);
                    Object familyObject = familyField.get(object);

                    if (familyObject == null) {
                        if (List.class.isAssignableFrom(familyField.getType())) {
                            familyObject = new ArrayList<>();
                        } else if (!familyField.getType().isPrimitive() && familyField.getType() != String.class) {
                            familyObject = familyField.getType().getDeclaredConstructor().newInstance();
                        }
                        familyField.set(object, familyObject);
                    }

                    if (familyObject instanceof List) {
                        List<Object> list = (List<Object>) familyObject;

                        JsonNode node = null;
                        try {
                            node = objectMapper.readTree(cellValue);
                        } catch (Exception e) {
                            list.add(cellValue);
                            continue;
                        }

                        if (node.isArray()) {
                            Class<?> listType = Object.class;
                            if (familyField.getGenericType() instanceof ParameterizedType) {
                                listType = (Class<?>) ((ParameterizedType) familyField.getGenericType())
                                        .getActualTypeArguments()[0];
                            }
                            for (JsonNode item : node) {
                                Object parsed = objectMapper.treeToValue(item, listType);
                                list.add(parsed);
                            }
                        } else if (node.isObject()) {
                            list.add(objectMapper.convertValue(node, new TypeReference<Map<String, Object>>() {
                            }));
                        } else {
                            list.add(cellValue);
                        }

                    } else {
                        // Assume it's a nested object
                        Field subField;
                        try {
                            subField = familyObject.getClass().getDeclaredField(targetField);
                        } catch (NoSuchFieldException e) {
                            System.err.printf("⚠️ Sub-field '%s' not found in '%s'. Skipping.%n", targetField,
                                    familyObject.getClass().getSimpleName());
                            continue;
                        }

                        subField.setAccessible(true);
                        setField(subField, familyObject, cellValue);
                    }
                } else {
                    Field field;
                    try {
                        field = modelClass.getDeclaredField(targetField);
                    } catch (NoSuchFieldException e) {
                        // Skip fields not defined in the Java model
                        System.err.printf("⚠️ Field '%s' not found in model class '%s'. Skipping it.%n", targetField,
                                modelClass.getSimpleName());
                        continue;
                    }
                    field.setAccessible(true);

                    if (field.getType().isEnum()) {
                        Object[] constants = field.getType().getEnumConstants();
                        for (Object constant : constants) {
                            if (((Enum<?>) constant).name().equals(cellValue)) {
                                field.set(object, constant);
                                break;
                            }
                        }
                    } else if (field.getType().isArray()) {
                        Class<?> compType = field.getType().getComponentType();
                        JsonNode node = objectMapper.readTree(cellValue);
                        if (node.isArray()) {
                            Object array = Array.newInstance(compType, node.size());
                            for (int i = 0; i < node.size(); i++) {
                                Object value = objectMapper.treeToValue(node.get(i), compType);
                                Array.set(array, i, value);
                            }
                            field.set(object, array);
                        }
                    } else {
                        setField(field, object, cellValue);
                    }
                }
            }

            return object;

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to get row data", e);
        } finally {
            try {
                if (scanner != null)
                    scanner.close();
                if (table != null)
                    table.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private Object convertStringToObject(String value, Class<?> targetType) {
        if (targetType == String.class) {
            return value;
        } else if (targetType == Integer.class || targetType == int.class) {
            return Integer.parseInt(value);
        } else if (targetType == Long.class || targetType == long.class) {
            return Long.parseLong(value);
        } else if (targetType == Double.class || targetType == double.class) {
            return Double.parseDouble(value);
        } else if (targetType == Boolean.class || targetType == boolean.class) {
            return Boolean.parseBoolean(value);
        }
        throw new IllegalArgumentException("Unsupported type: " + targetType);
    }

    public <T> List<T> getDataListByColumn(String tableName, Map<String, String> columnMapping, String familyName,
            String columnName, String columnValue, Class<T> modelClass, int sizeLimit) {
        ResultScanner rsObj = null;

        try {
            Table table = connection.getTable(TableName.valueOf(tableName));

            Scan s = new Scan();
            s.setCaching(100);
            s.setLimit(sizeLimit);
            TableDescriptor tableDescriptor = connection.getAdmin().getDescriptor(TableName.valueOf(tableName));
            ColumnFamilyDescriptor[] columnFamilies = tableDescriptor.getColumnFamilies();
            for (ColumnFamilyDescriptor columnFamily : columnFamilies) {
                byte[] family = columnFamily.getName();
                s.addFamily(family);
            }

            Filter filter = new SingleColumnValueFilter(Bytes.toBytes(familyName), Bytes.toBytes(columnName),
                    CompareOperator.EQUAL, Bytes.toBytes(columnValue));
            s.setFilter(filter);

            rsObj = table.getScanner(s);

            // Create a list to store the objects
            List<T> objects = new ArrayList<T>();

            for (Result result : rsObj) {
                // Do something with the result, e.g. print it to the console
                T object = modelClass.newInstance();
                for (Cell cell : result.listCells()) {
                    // Get the column name
                    String familyName2 = Bytes.toString(CellUtil.cloneFamily(cell));
                    String columnName2 = Bytes.toString(CellUtil.cloneQualifier(cell));

                    // Get the variable name from the columnMapping
                    String variableName = columnMapping.get(columnName2);

                    String value = Bytes.toString(CellUtil.cloneValue(cell));
                    // Get the value of the cell as a string
                    // Check if the variableName contains "department"
                    if (columnMapping.containsKey(familyName2)) {
                        // Get the subfield name
                        String subFieldName = columnName2.substring(columnName2.indexOf(".") + 1);
                        // Get the department object from the main object
                        Field familyField = object.getClass().getDeclaredField(familyName2);
                        familyField.setAccessible(true);
                        Object familyObject = familyField.get(object);
                        if (familyObject == null) {
                            if (familyField.getType() == List.class) {
                                familyObject = new ArrayList<>();
                                familyField.set(object, familyObject);
                            } else {
                                familyObject = familyField.getType().newInstance();
                                familyField.set(object, familyObject);
                            }
                        }
                        // Set the value to the subfield
                        if (familyObject instanceof List) {
                            Object currentObject = familyObject;
                            ObjectMapper mapper = new ObjectMapper();

                            JsonNode jsonNode = null;
                            try {
                                jsonNode = mapper.readTree((String) value);
                            } catch (Exception e) {
                                // Tidak berformat JSON, lakukan konversi biasa
                            }

                            if (jsonNode != null
                                    && jsonNode.getNodeType() == JsonNodeFactory.instance.objectNode().getNodeType()) {
                                // Value berformat JSON, lakukan konversi ke Map
                                Map<String, Object> dataList = mapper.readValue((String) value,
                                        new TypeReference<Map<String, Object>>() {
                                        });
                                ((List) currentObject).add(dataList);
                            } else {
                                // Value tidak berformat JSON, lakukan konversi biasa
                                ((List) currentObject).add(value);
                            }
                        } else {
                            Field subField = familyObject.getClass().getDeclaredField(subFieldName);
                            subField.setAccessible(true);
                            setField(subField, familyObject, value);
                        }
                    } else {
                        if (variableName != null) {
                            // Set the value to the variable
                            Field field = object.getClass().getDeclaredField(variableName);
                            field.setAccessible(true);
                            setField(field, object, value);
                        }
                    }
                }
                objects.add(object);
            }

            // Close the scanner and table objects
            rsObj.close();
            return objects;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            rsObj.close();
            e.printStackTrace();
        } catch (InstantiationException | IllegalAccessException | NoSuchFieldException e) {
            throw new RuntimeException(e);
        }

        return null;
    }

    public <T> List<T> getDataListByColumnWithPagination(String tableName,
            Map<String, String> columnMapping,
            String familyName,
            String columnName,
            String columnValue,
            Class<T> modelClass,
            int limit,
            int offset) {

        ResultScanner rsObj = null;

        try {
            Table table = connection.getTable(TableName.valueOf(tableName));

            Scan s = new Scan();
            s.setCaching(limit);
            s.setLimit(limit);

            TableDescriptor tableDescriptor = connection.getAdmin().getDescriptor(TableName.valueOf(tableName));
            ColumnFamilyDescriptor[] columnFamilies = tableDescriptor.getColumnFamilies();
            for (ColumnFamilyDescriptor columnFamily : columnFamilies) {
                byte[] family = columnFamily.getName();
                s.addFamily(family);
            }

            // Tambahkan filter untuk pagination
            FilterList filterList = new FilterList(FilterList.Operator.MUST_PASS_ALL);

            // Filter untuk kolom yang dicari
            Filter valueFilter = new SingleColumnValueFilter(
                    Bytes.toBytes(familyName),
                    Bytes.toBytes(columnName),
                    CompareOperator.EQUAL,
                    Bytes.toBytes(columnValue));

            // Filter untuk skip sejumlah row sesuai offset
            Filter pageFilter = new PageFilter(limit);
            Filter skipFilter = new SkipFilter(new PageFilter(offset));

            filterList.addFilter(valueFilter);
            filterList.addFilter(skipFilter);
            filterList.addFilter(pageFilter);

            s.setFilter(filterList);

            rsObj = table.getScanner(s);

            // Create a list to store the objects
            List<T> objects = new ArrayList<T>();

            for (Result result : rsObj) {
                T object = modelClass.newInstance();
                for (Cell cell : result.listCells()) {
                    String familyName2 = Bytes.toString(CellUtil.cloneFamily(cell));
                    String columnName2 = Bytes.toString(CellUtil.cloneQualifier(cell));
                    String variableName = columnMapping.get(columnName2);
                    String value = Bytes.toString(CellUtil.cloneValue(cell));

                    if (columnMapping.containsKey(familyName2)) {
                        String subFieldName = columnName2.substring(columnName2.indexOf(".") + 1);
                        Field familyField = object.getClass().getDeclaredField(familyName2);
                        familyField.setAccessible(true);
                        Object familyObject = familyField.get(object);

                        if (familyObject == null) {
                            if (familyField.getType() == List.class) {
                                familyObject = new ArrayList<>();
                                familyField.set(object, familyObject);
                            } else {
                                familyObject = familyField.getType().newInstance();
                                familyField.set(object, familyObject);
                            }
                        }

                        if (familyObject instanceof List) {
                            Object currentObject = familyObject;
                            ObjectMapper mapper = new ObjectMapper();

                            JsonNode jsonNode = null;
                            try {
                                jsonNode = mapper.readTree(value);
                            } catch (Exception e) {
                                // Bukan format JSON
                            }

                            if (jsonNode != null
                                    && jsonNode.getNodeType() == JsonNodeFactory.instance.objectNode().getNodeType()) {
                                Map<String, Object> dataList = mapper.readValue(value,
                                        new TypeReference<Map<String, Object>>() {
                                        });
                                ((List) currentObject).add(dataList);
                            } else {
                                ((List) currentObject).add(value);
                            }
                        } else {
                            Field subField = familyObject.getClass().getDeclaredField(subFieldName);
                            subField.setAccessible(true);
                            setField(subField, familyObject, value);
                        }
                    } else {
                        if (variableName != null) {
                            Field field = object.getClass().getDeclaredField(variableName);
                            field.setAccessible(true);
                            setField(field, object, value);
                        }
                    }
                }
                objects.add(object);
            }

            rsObj.close();
            return objects;

        } catch (IOException e) {
            if (rsObj != null) {
                rsObj.close();
            }
            e.printStackTrace();
        } catch (InstantiationException | IllegalAccessException | NoSuchFieldException e) {
            throw new RuntimeException(e);
        }

        return null;
    }

    // Helper method untuk set field

    private void setField(Field field, Object object, String value) throws IllegalAccessException {
        // Ubah hak akses field agar dapat diakses
        field.setAccessible(true);

        // Dapatkan tipe data dari field
        Class<?> fieldType = field.getType();

        // Parsing nilai string menjadi nilai sesuai tipe data
        if (fieldType == Integer.class) {
            Integer intValue = Integer.parseInt(value);
            field.set(object, intValue);
        } else if (fieldType == long.class) {
            long longValue = Long.parseLong(value);
            field.setLong(object, longValue);
        } else if (fieldType == float.class) {
            float floatValue = Float.parseFloat(value);
            field.setFloat(object, floatValue);
        } else if (fieldType == Float.class) {
            Float floatValue = Float.parseFloat(value);
            field.set(object, floatValue);
        } else if (fieldType == double.class) {
            double doubleValue = Double.parseDouble(value);
            field.setDouble(object, doubleValue);
        } else if (fieldType == Double.class) {
            Double doubleValue = Double.valueOf(value);
            field.setDouble(object, doubleValue);
        } else if (fieldType == boolean.class) {
            boolean booleanValue = Boolean.parseBoolean(value);
            field.setBoolean(object, booleanValue);
        } else if (fieldType == Boolean.class) {
            Boolean booleanValue;
            if (value.equalsIgnoreCase("true")) {
                booleanValue = Boolean.TRUE;
            } else {
                booleanValue = Boolean.FALSE;
            }
            field.set(object, booleanValue);
        } else if (fieldType == String.class) {
            field.set(object, value);
        } else if (fieldType == Instant.class) {
            Instant instantValue = Instant.parse(value);
            field.set(object, instantValue);
        } else if (fieldType.isEnum()) {
            Object enumValue = Enum.valueOf((Class<Enum>) fieldType, value);
            field.set(object, enumValue);
        } else {
            // Tipe data yang tidak dikenal, lewati saja
            System.out.println("Tipe data " + fieldType + " tidak dikenali.");
        }
    }

    public List<String> getExistingByColumn(String tableName, Map<String, String> columnMapping, String family,
            String columnName, List<String> values) {
        List<String> existingValues = new ArrayList<>();
        try {
            Table table = connection.getTable(TableName.valueOf(tableName));

            // Membuat scan untuk mencari kolom tertentu
            Scan scan = new Scan();
            scan.addFamily(Bytes.toBytes(family));
            scan.addColumn(Bytes.toBytes(family), Bytes.toBytes(columnName));

            // Melakukan scan pada tabel
            ResultScanner scanner = table.getScanner(scan);

            // Loop untuk memeriksa hasil scan
            for (Result result : scanner) {
                for (Cell cell : result.rawCells()) {
                    // Ambil nilai dari cell dan cek apakah sudah ada dalam list values
                    String cellValue = Bytes.toString(CellUtil.cloneValue(cell));
                    if (values.contains(cellValue)) {
                        existingValues.add(cellValue);
                    }
                }
            }

            // Menutup scanner dan tabel
            scanner.close();
            table.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return existingValues;
    }

    public String getRecord(TableName tableName, String rowKey, String columnFamily, String qualifier)
            throws IOException {
        Table table = null;
        try {
            table = connection.getTable(tableName);
            Get get = new Get(Bytes.toBytes(rowKey));
            get.addColumn(Bytes.toBytes(columnFamily), Bytes.toBytes(qualifier));
            Result result = table.get(get);
            byte[] value = result.getValue(Bytes.toBytes(columnFamily), Bytes.toBytes(qualifier));
            return value != null ? Bytes.toString(value) : null;
        } finally {
            if (table != null) {
                table.close();
            }
        }
    }
}