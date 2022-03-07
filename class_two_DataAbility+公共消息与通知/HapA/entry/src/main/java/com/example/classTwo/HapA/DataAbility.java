package com.example.classTwo.HapA;

import ohos.aafwk.ability.Ability;
import ohos.aafwk.ability.DataAbilityHelper;
import ohos.aafwk.content.Intent;
import ohos.data.DatabaseHelper;
import ohos.data.dataability.DataAbilityUtils;
import ohos.data.rdb.*;
import ohos.data.resultset.ResultSet;
import ohos.data.dataability.DataAbilityPredicates;
import ohos.hiviewdfx.HiLog;
import ohos.hiviewdfx.HiLogLabel;
import ohos.utils.net.Uri;
import ohos.utils.PacMap;

import java.io.FileDescriptor;

public class DataAbility extends Ability {
    private static final HiLogLabel LABEL_LOG = new HiLogLabel(3, 0xD001100, "Demo");

    public static final String DB_TAB_NAME = "book";
    public static final String DB_COLUMN_NAME = "name";
    public static final String DB_COLUMN_ID = "id";

    private RdbStore rdbStore;

    //存储配置:只读、同步...
    private StoreConfig config = StoreConfig.newDefaultConfig("dataability.db");

    //关联数据库开启时的回调
    private RdbOpenCallback rdbOpenCallback = new RdbOpenCallback() {
        @Override
        public void onCreate(RdbStore rdbStore) {
            rdbStore.executeSql("create table if not exists " + DB_TAB_NAME + " (userId integer primary key autoincrement, "
                    + DB_COLUMN_NAME + " text not null, " + DB_COLUMN_ID + " integer)");
            HiLog.info(LABEL_LOG, "%{public}s", "create a  new database");
        }

        @Override
        public void onUpgrade(RdbStore rdbStore, int i, int i1) {
            HiLog.info(LABEL_LOG, "%{public}s", "DataBase upgrade");
        }
    };

    @Override
    public void onStart(Intent intent) {
        super.onStart(intent);
        DatabaseHelper helper = new DatabaseHelper(this);
        rdbStore = helper.getRdbStore(config,1,rdbOpenCallback);
        HiLog.info(LABEL_LOG, "DataAbility onStart");
    }

    @Override
    public ResultSet query(Uri uri, String[] columns, DataAbilityPredicates predicates) {
        RdbPredicates rdbPredicates = DataAbilityUtils.createRdbPredicates(predicates,DB_TAB_NAME);
        return rdbStore.query(rdbPredicates,columns);
    }

    @Override
    public int insert(Uri uri, ValuesBucket value) {
        int index = (int) rdbStore.insert(DB_TAB_NAME,value);
        DataAbilityHelper.creator(this).notifyChange(uri);
        return index;
    }

    @Override
    public int delete(Uri uri, DataAbilityPredicates predicates) {
        RdbPredicates rdbPredicates = DataAbilityUtils.createRdbPredicates(predicates,DB_TAB_NAME);
        int index = rdbStore.delete(rdbPredicates);
        DataAbilityHelper.creator(this).notifyChange(uri);
        return index;
    }

    @Override
    public int update(Uri uri, ValuesBucket value, DataAbilityPredicates predicates) {
        RdbPredicates rdbPredicates = DataAbilityUtils.createRdbPredicates(predicates,DB_TAB_NAME);
        int index = rdbStore.update(value,rdbPredicates);
        DataAbilityHelper.creator(this).notifyChange(uri);
        return index;
    }

    @Override
    public FileDescriptor openFile(Uri uri, String mode) {
        return null;
    }

    @Override
    public String[] getFileTypes(Uri uri, String mimeTypeFilter) {
        return new String[0];
    }

    @Override
    public PacMap call(String method, String arg, PacMap extras) {
        return null;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }
}