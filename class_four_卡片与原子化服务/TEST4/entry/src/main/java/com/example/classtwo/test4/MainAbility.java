package com.example.classtwo.test4;

import com.example.classtwo.test4.slice.MainAbilitySlice;
import ohos.aafwk.ability.Ability;
import ohos.aafwk.ability.AbilitySlice;
import ohos.aafwk.ability.ProviderFormInfo;
import ohos.aafwk.content.Intent;
import ohos.agp.components.ComponentProvider;
import ohos.data.DatabaseHelper;
import ohos.data.orm.OrmContext;
import ohos.hiviewdfx.HiLogLabel;

public class MainAbility extends Ability {

    @Override
    protected ProviderFormInfo onCreateForm(Intent intent) {
        return new ProviderFormInfo(ResourceTable.Layout_form_image_with_info_date_card_2_2
                , this);
    }

    @Override
    public void onStart(Intent intent) {
        super.onStart(intent);
        super.setMainRoute(MainAbilitySlice.class.getName());
    }

}
