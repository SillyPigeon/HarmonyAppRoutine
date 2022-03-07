package com.example.classtwo.testphone.slice;

import com.example.classtwo.testphone.ResourceTable;
import com.example.classtwo.testtablet.IMyIdlInterface;
import com.example.classtwo.testtablet.MyIdlInterfaceStub;
import ohos.aafwk.ability.AbilitySlice;
import ohos.aafwk.ability.IAbilityConnection;
import ohos.aafwk.ability.IAbilityContinuation;
import ohos.aafwk.content.Intent;
import ohos.aafwk.content.IntentParams;
import ohos.aafwk.content.Operation;
import ohos.agp.window.dialog.ToastDialog;
import ohos.bundle.ElementName;
import ohos.distributedschedule.interwork.DeviceInfo;
import ohos.distributedschedule.interwork.DeviceManager;
import ohos.hiviewdfx.HiLog;
import ohos.hiviewdfx.HiLogLabel;
import ohos.rpc.IRemoteObject;
import ohos.rpc.RemoteException;

import java.util.List;

public class MainAbilitySlice extends AbilitySlice {
    private static final HiLogLabel LABEL_LOG = new HiLogLabel(3, 0xD001100, "Demo");
    private static IMyIdlInterface myApi;

    @Override
    public void onStart(Intent intent) {
        super.onStart(intent);
        super.setUIContent(ResourceTable.Layout_ability_main);
        findComponentById(ResourceTable.Id_continue_ability).setClickedListener(
                component -> continueAbility()
        );
        findComponentById(ResourceTable.Id_start_tv_app).setClickedListener(
                component -> startAbility("com.example.classtwo.testtablet.MainAbility")
        );
        findComponentById(ResourceTable.Id_bind_tv_service).setClickedListener(
                component -> connectRemote()
        );
        findComponentById(ResourceTable.Id_disconnect_tv_service).setClickedListener(
                component -> {
                    disconnectAbility(connection);
                    new ToastDialog(getContext()).setText("DisconnectDone").setDuration(3000).show();
                }
        );
    }

    private String getRemoteDeviceId() {
        List<DeviceInfo> infoList = DeviceManager.getDeviceList(DeviceInfo.FLAG_GET_ONLINE_DEVICE);
        if ((infoList == null) || (infoList.size() == 0)) {
            return "";
        }
        HiLog.info(LABEL_LOG, "get ONLINE device:");
        for (DeviceInfo info : infoList){
            HiLog.info(LABEL_LOG, "device id: " +  info.getDeviceId() + " name: " + info.getDeviceName());
        }
        return infoList.get(0).getDeviceId();
    }

    private String getRemoteDeviceName() {
        List<DeviceInfo> infoList = DeviceManager.getDeviceList(DeviceInfo.FLAG_GET_ONLINE_DEVICE);
        if ((infoList == null) || (infoList.size() == 0)) {
            return "";
        }
        HiLog.info(LABEL_LOG, "get ONLINE device:");
        for (DeviceInfo info : infoList){
            HiLog.info(LABEL_LOG, "device id: " +  info.getDeviceId() + " name: " + info.getDeviceName());
        }
        return infoList.get(0).getDeviceName();
    }

    private void startAbility(String name){
        Intent intent = new Intent();
        Operation operation = new Intent.OperationBuilder()
                .withDeviceId(getRemoteDeviceId())
                .withBundleName("com.example.classtwo.testtablet")
                .withAbilityName(name)
                .withFlags(Intent.FLAG_ABILITYSLICE_MULTI_DEVICE)
                .build();
        intent.setOperation(operation);
        startAbility(intent);
    }

    void connectRemote(){
        Intent intent = new Intent();
        Operation operation = new Intent.OperationBuilder()
                .withDeviceId(getRemoteDeviceId())
                .withBundleName("com.example.classtwo.testtablet")
                .withAbilityName("com.example.classtwo.testtablet.ServiceAbility")
                .withFlags(Intent.FLAG_ABILITYSLICE_MULTI_DEVICE)
                .withAction("")
                .build();
        intent.setOperation(operation);
        connectAbility(intent,connection);
    }


    private IAbilityConnection connection = new IAbilityConnection() {
        @Override
        public void onAbilityConnectDone(ElementName elementName, IRemoteObject iRemoteObject, int i) {
            myApi = MyIdlInterfaceStub.asInterface(iRemoteObject);
            try {
                myApi.sayHello(getRemoteDeviceName());
            } catch (RemoteException e) {
                HiLog.error(LABEL_LOG, "error");
                e.printStackTrace();
            }
            HiLog.info(LABEL_LOG, "onAbilityConnectDone");
        }

        @Override
        public void onAbilityDisconnectDone(ElementName elementName, int i) {
            HiLog.info(LABEL_LOG, "onAbilityDisconnectDone");
            myApi = null;
        }
    };

    @Override
    public void onActive() {
        super.onActive();
    }

    @Override
    public void onForeground(Intent intent) {
        super.onForeground(intent);
    }
}
