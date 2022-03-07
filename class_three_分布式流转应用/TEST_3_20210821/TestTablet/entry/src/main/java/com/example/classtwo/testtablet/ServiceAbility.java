package com.example.classtwo.testtablet;

import ohos.aafwk.ability.Ability;
import ohos.aafwk.content.Intent;
import ohos.agp.window.dialog.ToastDialog;
import ohos.app.Context;
import ohos.app.dispatcher.TaskDispatcher;
import ohos.app.dispatcher.task.TaskPriority;
import ohos.eventhandler.EventHandler;
import ohos.eventhandler.EventRunner;
import ohos.eventhandler.InnerEvent;
import ohos.rpc.IRemoteObject;
import ohos.hiviewdfx.HiLog;
import ohos.hiviewdfx.HiLogLabel;
import ohos.rpc.RemoteException;

public class ServiceAbility extends Ability {
    private static final HiLogLabel LABEL_LOG = new HiLogLabel(3, 0xD001100, "Demo");

    @Override
    public void onStart(Intent intent) {
        HiLog.error(LABEL_LOG, "ServiceAbility::onStart");
        super.onStart(intent);
    }

    @Override
    public IRemoteObject onConnect(Intent intent) {
        HiLog.info(LABEL_LOG, "onConnect");
        return myApi;
    }

    IRemoteObject myApi = new MyIdlInterfaceStub("myApi") {
        @Override
        public void sayHello(String device_name) throws RemoteException {
            TaskDispatcher uiTaskDispatcher = getUITaskDispatcher();
            uiTaskDispatcher.asyncDispatch(()->showTips(getContext(),
                    "Hello " + device_name));

        }
    };

    private void showTips(Context context, String msg) {
        new ToastDialog(context).setText(msg).setDuration(5000).show();
    }

    @Override
    public void onBackground() {
        super.onBackground();
        HiLog.info(LABEL_LOG, "ServiceAbility::onBackground");
    }

    @Override
    public void onStop() {
        super.onStop();
        HiLog.info(LABEL_LOG, "ServiceAbility::onStop");
    }

    @Override
    public void onCommand(Intent intent, boolean restart, int startId) {
    }

    @Override
    public void onDisconnect(Intent intent) {
    }
}