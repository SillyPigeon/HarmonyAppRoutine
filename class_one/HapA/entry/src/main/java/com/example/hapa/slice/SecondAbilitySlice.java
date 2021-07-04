package com.example.hapa.slice;

import com.example.hapa.ResourceTable;
import com.example.hapb.IMyIdlInterface;
import com.example.hapb.MyIdlInterfaceStub;
import ohos.aafwk.ability.AbilitySlice;
import ohos.aafwk.ability.IAbilityConnection;
import ohos.aafwk.content.Intent;
import ohos.aafwk.content.Operation;
import ohos.bundle.ElementName;
import ohos.hiviewdfx.HiLog;
import ohos.hiviewdfx.HiLogLabel;
import ohos.rpc.IRemoteObject;
import ohos.rpc.RemoteException;

public class SecondAbilitySlice extends AbilitySlice {

    private static final HiLogLabel LABEL_LOG = new HiLogLabel(3, 0xD001100, "Demo");
    private static final int GET_PROCESS = 1;

    @Override
    public void onStart(Intent intent) {
        super.onStart(intent);
        super.setUIContent(ResourceTable.Layout_ability_second);

        findComponentById(ResourceTable.Id_connect_hapA_service).setClickedListener(
                component -> connectRemote()
        );
    }

    IMyIdlInterface myApi;

    private IAbilityConnection connection = new IAbilityConnection() {
        @Override
        public void onAbilityConnectDone(ElementName elementName, IRemoteObject iRemoteObject, int i) {
            myApi = MyIdlInterfaceStub.asInterface(iRemoteObject);
            String ProcessInfoHapA = "";
            try {
                ProcessInfoHapA = myApi.myApi_GetProcessInfo(GET_PROCESS);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            HiLog.debug(LABEL_LOG,"Current Process is: " + getProcessName()
                    + "; PID is: " + getProcessInfo().getPid());
            HiLog.debug(LABEL_LOG,"Get myApi is in: " + ProcessInfoHapA);
        }

        @Override
        public void onAbilityDisconnectDone(ElementName elementName, int i) {
            myApi = null;
        }
    };

    void connectRemote(){
        Intent intent = new Intent();
        Operation operation = new Intent.OperationBuilder()
                .withDeviceId("")
                .withBundleName("com.example.hapb")
                .withAbilityName("com.example.hapb.ServiceAbility")
                .withAction("")
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
