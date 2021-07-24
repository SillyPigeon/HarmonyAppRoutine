package com.example.hapb.slice;

import com.example.hapb.ResourceTable;
import ohos.aafwk.ability.AbilitySlice;
import ohos.aafwk.ability.IAbilityConnection;
import ohos.aafwk.content.Intent;
import ohos.aafwk.content.Operation;
import ohos.bundle.ElementName;
import ohos.distributedschedule.interwork.DeviceInfo;
import ohos.distributedschedule.interwork.DeviceManager;
import ohos.hiviewdfx.HiLog;
import ohos.hiviewdfx.HiLogLabel;
import ohos.rpc.IRemoteObject;
import ohos.rpc.MessageOption;
import ohos.rpc.MessageParcel;
import ohos.rpc.RemoteException;

import java.util.ArrayList;
import java.util.List;

public class MainAbilitySlice extends AbilitySlice {
    private static final HiLogLabel LABEL_LOG = new HiLogLabel(3, 0xD001100, "Demo");
    @Override
    public void onStart(Intent intent) {
        super.onStart(intent);
        super.setUIContent(ResourceTable.Layout_ability_main);
        findComponentById(ResourceTable.Id_connect_by_self).setClickedListener(
                component -> connectLocal()
        );
        String[] permissions = {
                "ohos.permission.READ_USER_STORAGE",
                "ohos.permission.WRITE_USER_STORAGE",
                "ohos.permission.DISTRIBUTED_DATASYNC"
        };
        List applyPermissions = new ArrayList<>();
        for (String element : permissions) {
            if (verifySelfPermission(element) != 0) {
                if (canRequestPermission(element)) {
                    applyPermissions.add(element);
                } else {
                }
            } else {
            }
        }
        requestPermissionsFromUser((String[]) applyPermissions.toArray(new String[0]), 0);

    }

    private IAbilityConnection connection = new IAbilityConnection() {
        @Override
        public void onAbilityConnectDone(ElementName elementName, IRemoteObject iRemoteObject, int i) {
            MessageParcel data = MessageParcel.obtain();
            MessageParcel reply = MessageParcel.obtain();
            data.writeString("getProcess");
            try {
                iRemoteObject.sendRequest(1, data, reply, new MessageOption(0));
            } catch (RemoteException e) {
                e.printStackTrace();
            }

            String processMessage = reply.readString();

            List deviceInfoList = DeviceManager.getDeviceList(DeviceInfo.FLAG_GET_ONLINE_DEVICE);

            for(int j = 0; j<deviceInfoList.size(); j++){
                HiLog.debug(LABEL_LOG,deviceInfoList.get(j));
            }

            HiLog.debug(LABEL_LOG,processMessage);

        }

        @Override
        public void onAbilityDisconnectDone(ElementName elementName, int i) {

        }
    };

    void connectLocal (){
        Intent intent = new Intent();
        Operation operation = new Intent.OperationBuilder()
                .withDeviceId("")
                .withBundleName("com.example.hapb")
                .withAbilityName("com.example.hapb.ServiceAbility")
                .withAction("action.connectBySelf")
                .build();
        intent.setOperation(operation);
        connectAbility(intent,connection);
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
