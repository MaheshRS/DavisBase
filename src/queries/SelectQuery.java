package queries;

import model.*;
import common.CatalogDB;
import common.Constants;
import common.Utils;
import datatypes.DT_Text;
import datatypes.DT;
import javafx.util.Pair;
import storage.StorageManager;
import internal.DataRecord;
import internal.InternalCondition;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Mahesh on 15/4/17.
 */

public class SelectQuery implements IQuery {
    public String databaseName;
    public String tableName;
    public ArrayList<String> columns;
    public boolean isSelectAll;
    public ArrayList<Condition> conditions = new ArrayList<>();

    public SelectQuery(String databaseName, String tableName, ArrayList<String> columns, ArrayList<Condition> conditions, boolean isSelectAll) {
        this.databaseName = databaseName;
        this.tableName = tableName;
        this.columns = columns;
        this.conditions = conditions;
        this.isSelectAll = isSelectAll;
    }

    @Override
    public Result ExecuteQuery() {
        try {
            ResultSet resultSet = ResultSet.CreateResultSet();

            ArrayList<Record> records = GetData();
            resultSet.setColumns(this.columns);
            for (Record record : records) {
                resultSet.addRecord(record);
            }

            return resultSet;
        }
        catch (Exception e) {
            Utils.printMessage(e.getMessage());
        }
        return null;
    }

    @Override
    public boolean ValidateQuery() {
        try {
            StorageManager manager = new StorageManager();

            // Check if the table exists.
            if (!manager.checkTableExists(this.databaseName, tableName)) {
                Utils.printMissingTableError(this.databaseName, tableName);
                return false;
            }

            Pair<HashMap<String, Integer>, HashMap<Integer, String>> maps = mapOrdinalIdToColumnName(this.tableName);
            HashMap<String, Integer> columnToIdMap = maps.getKey();
            HashMap<String, Integer> columnDataTypeMapping = manager.fetchAllTableColumnDataTypes(this.databaseName, tableName);

            // Validate column data type.
            if (conditions != null) {
                // Retrieve the columns of the tables.
                List<String> retrievedColumns = manager.fetchAllTableColumns(this.databaseName, tableName);

                // Check for data types.
                for (Condition condition : conditions) {
                    if (!Utils.checkConditionValueDataTypeValidity(columnDataTypeMapping, retrievedColumns, condition)) {
                        return false;
                    }
                }
            }

            if (this.columns != null) {
                for (String column : this.columns) {
                    if (!columnToIdMap.containsKey(column)) {
                        Utils.printError(String.format("Unknown column '%s' in table '%s'", column, this.tableName));
                        return false;
                    }
                }
            }

            if (conditions != null) {
                for (Condition condition : conditions) {
                    if (!columnToIdMap.containsKey(condition.column)) {
                        Utils.printError((String.format("Unknown column '%s' in table '%s'", condition.column, this.tableName)));
                        return false;
                    }
                }
            }
        } catch (Exception e) {
            Utils.printMessage(e.getMessage());
            return false;
        }
        return true;
    }

    private ArrayList<Record> GetData() throws Exception {
        ArrayList<Record> records = new ArrayList<>();
        Pair<HashMap<String, Integer>, HashMap<Integer, String>> maps = mapOrdinalIdToColumnName(this.tableName);
        HashMap<String, Integer> columnToIdMap = maps.getKey();
        ArrayList<Byte> columnsList = new ArrayList<>();
        List<DataRecord> internalRecords;
        StorageManager manager = new StorageManager();

        List<InternalCondition> conditions = new ArrayList<>();
        InternalCondition internalCondition = null;

        if(this.conditions != null){
            for(Condition condition : this.conditions) {
                internalCondition = new InternalCondition();
                if (columnToIdMap.containsKey(condition.column)) {
                    internalCondition.setIndex(columnToIdMap.get(condition.column).byteValue());
                }

                DT dataType = DT.CreateDT(condition.value);
                internalCondition.setValue(dataType);

                Short operatorShort = Utils.ConvertFromOperator(condition.operator);
                internalCondition.setConditionType(operatorShort);
                conditions.add(internalCondition);
            }
        }

        if(this.columns == null) {
            internalRecords = manager.findRecord(this.databaseName,
                    this.tableName, conditions, false);

            HashMap<Integer, String> idToColumnMap = maps.getValue();
            this.columns = new ArrayList<>();
            for (int i=0; i<columnToIdMap.size();i++) {
                if(idToColumnMap.containsKey(i)){
                    columnsList.add((byte)i);
                    this.columns.add(idToColumnMap.get(i));
                }
            }
        }
        else {
            for (String column : this.columns) {
                if (columnToIdMap.containsKey(column)) {
                    columnsList.add(columnToIdMap.get(column).byteValue());
                }
            }

            internalRecords = manager.findRecord(this.databaseName,
                    this.tableName, conditions, columnsList, false);
        }

        Byte[] columnIds = new Byte[columnsList.size()];
        int k = 0;
        for(Byte column : columnsList){
            columnIds[k] = column;
            k++;
        }

        HashMap<Integer, String> idToColumnMap = maps.getValue();
        for(DataRecord internalRecord : internalRecords){
            Object[] dataTypes = new DT[internalRecord.getColumnValueList().size()];
            k=0;
            for(Object columnValue : internalRecord.getColumnValueList()){
                dataTypes[k] = columnValue;
                k++;
            }
            Record record = Record.CreateRecord();
            for(int i=0;i<columnIds.length;i++) {
                Literal literal;
                if(idToColumnMap.containsKey((int)columnIds[i])) {
                    literal = Literal.CreateLiteral((DT)dataTypes[i], Utils.resolveClass(dataTypes[i]));
                    record.put(idToColumnMap.get((int)columnIds[i]), literal);
                }
            }
            records.add(record);
        }

        return records;
    }


    private Pair<HashMap<String, Integer>, HashMap<Integer, String>> mapOrdinalIdToColumnName(String tableName) throws Exception {
        HashMap<Integer, String> idToColumnMap = new HashMap<>();
        HashMap<String, Integer> columnToIdMap = new HashMap<>();
        List<InternalCondition> conditions = new ArrayList<>();
        conditions.add(InternalCondition.CreateCondition(CatalogDB.COLUMNS_TABLE_SCHEMA_TABLE_NAME, InternalCondition.EQUALS, new DT_Text(tableName)));

        StorageManager manager = new StorageManager();
        List<DataRecord> records = manager.findRecord(Constants.DEFAULT_CATALOG_DATABASENAME, Constants.SYSTEM_COLUMNS_TABLENAME, conditions, false);

        for (int i = 0; i < records.size(); i++) {
            DataRecord record = records.get(i);
            Object object = record.getColumnValueList().get(CatalogDB.COLUMNS_TABLE_SCHEMA_COLUMN_NAME);
            idToColumnMap.put(i, ((DT) object).getStringValue());
            columnToIdMap.put(((DT) object).getStringValue(), i);
        }

        return new Pair<>(columnToIdMap, idToColumnMap);
    }
}
