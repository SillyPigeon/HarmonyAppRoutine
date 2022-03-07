package com.example.classTwo.HapA;

import com.example.classTwo.HapA.slice.MainAbilitySlice;
import ohos.aafwk.ability.Ability;
import ohos.aafwk.ability.DataAbilityHelper;
import ohos.aafwk.content.Intent;
import ohos.hiviewdfx.HiLogLabel;
import ohos.utils.net.Uri;

public class MainAbility extends Ability {

    @Override
    public void onStart(Intent intent) {
        super.onStart(intent);
        super.setMainRoute(MainAbilitySlice.class.getName());
    }
}
