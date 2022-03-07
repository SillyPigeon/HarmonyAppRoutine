package com.example.classtwo.hapb.slice;

import com.example.classtwo.hapb.DataBaseHelper;
import com.example.classtwo.hapb.ResourceTable;
import ohos.aafwk.ability.AbilitySlice;
import ohos.aafwk.content.Intent;
import ohos.hiviewdfx.HiLogLabel;

public class MainAbilitySlice extends AbilitySlice {
    private static final HiLogLabel LABEL_LOG = new HiLogLabel(3, 0xD001100, "Demo");

    DataBaseHelper mDataBaseHelper;

    @Override
    public void onStart(Intent intent) {
        super.onStart(intent);
        super.setUIContent(ResourceTable.Layout_ability_main);
        mDataBaseHelper = new DataBaseHelper(this);
        findComponentById(ResourceTable.Id_update_database).setClickedListener(
                component -> mDataBaseHelper.updateDataBase());
    }

    @Override
    public void onActive() {
        super.onActive();
    }

    @Override
    public void onForeground(Intent intent) {
        super.onForeground(intent);
    }
}
