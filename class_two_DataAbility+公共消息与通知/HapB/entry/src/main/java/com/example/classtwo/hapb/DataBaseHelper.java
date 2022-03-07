package com.example.classtwo.hapb;

import ohos.aafwk.ability.DataAbilityHelper;
import ohos.aafwk.ability.DataAbilityRemoteException;
import ohos.app.Context;
import ohos.data.dataability.DataAbilityPredicates;
import ohos.data.rdb.ValuesBucket;
import ohos.data.resultset.ResultSet;
import ohos.hiviewdfx.HiLog;
import ohos.hiviewdfx.HiLogLabel;
import ohos.utils.net.Uri;

public class DataBaseHelper {
    private static final HiLogLabel LABEL_LOG = new HiLogLabel(3, 0xD001100, "Demo");
    static final String DB_COLUMN_NAME = "name";
    static final String DB_COLUMN_ID = "id";
    static final String DataAbility_URL = "dataability:///com.example.classTwo.HapA.DataAbility";
    static Uri table_uri = Uri.parse(DataAbility_URL + "/book");

    public DataAbilityHelper dataAbilityHelper;

    public DataBaseHelper(Context c) {
        dataAbilityHelper = DataAbilityHelper.creator(c);
    }

    public void updateDataBase(){
        String[] columns = new String[] {DB_COLUMN_NAME, DB_COLUMN_ID};
        DataAbilityPredicates predicates = new DataAbilityPredicates();
        try {
            ResultSet resultSet = dataAbilityHelper.query(table_uri, columns,
                    predicates);
            if (!resultSet.goToLastRow()) {
                HiLog.info(LABEL_LOG, "%{public}s", "query:No result found");
                return;
            }
            int bookNameIndex = resultSet.getColumnIndexForName(DB_COLUMN_NAME);
            int bookIndex = resultSet.getColumnIndexForName(DB_COLUMN_ID);
            for (int i = 0; i<10; i++){
                DataAbilityPredicates updatePredicates = new DataAbilityPredicates();
                updatePredicates.equalTo(DB_COLUMN_ID, resultSet.getInt(bookIndex));
                String name = resultSet.getString(bookNameIndex) + "-TEST";
                int id = resultSet.getInt(bookIndex) + 20;

                ValuesBucket valuesBucket = new ValuesBucket();
                valuesBucket.putString(DB_COLUMN_NAME, name);
                valuesBucket.putInteger(DB_COLUMN_ID, id);
                dataAbilityHelper.update(table_uri, valuesBucket,updatePredicates);

                resultSet.goToPreviousRow();
            }


        } catch (DataAbilityRemoteException | IllegalStateException exception) {
            HiLog.error(LABEL_LOG, "%{public}s", "query: dataRemote exception|illegalStateException");
        }
    }
}
