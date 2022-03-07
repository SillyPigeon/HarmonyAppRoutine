package com.example.classtwo.testtablet;

import com.example.classtwo.testtablet.slice.MainAbilitySlice;
import ohos.aafwk.ability.Ability;
import ohos.aafwk.content.Intent;
import ohos.bundle.IBundleManager;
import ohos.security.SystemPermission;

public class MainAbility extends Ability {
    @Override
    public void onStart(Intent intent) {
        super.onStart(intent);
        super.setMainRoute(MainAbilitySlice.class.getName());
        requestPermission();
    }

    private void requestPermission() {
        if (verifySelfPermission(SystemPermission.DISTRIBUTED_DATASYNC) != IBundleManager.PERMISSION_GRANTED) {
            requestPermissionsFromUser(new String[] {SystemPermission.DISTRIBUTED_DATASYNC}, 0);
        }
    }

    @Override
    public void onRequestPermissionsFromUserResult(int requestCode, String[] permissions, int[] grantResults) {
        if (permissions == null || permissions.length == 0 || grantResults == null || grantResults.length == 0) {
            return;
        }
        if (requestCode == 0) {
            if (grantResults[0] == IBundleManager.PERMISSION_DENIED) {
                terminateAbility();
            }
        }
    }

}
