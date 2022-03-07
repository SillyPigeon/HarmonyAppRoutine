package com.example.hapb;

import ohos.aafwk.ability.Ability;
import ohos.aafwk.content.Intent;
import ohos.rpc.*;
import ohos.hiviewdfx.HiLog;
import ohos.hiviewdfx.HiLogLabel;

public class ServiceAbility extends Ability {
    private static final HiLogLabel LABEL_LOG = new HiLogLabel(3, 0xD001100, "Demo");
    private static final int GET_PROCESS = 1;

    @Override
    public void onStart(Intent intent) {
        HiLog.error(LABEL_LOG, "ServiceAbility::onStart");
        super.onStart(intent);
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
    public IRemoteObject onConnect(Intent intent) {
        if (intent.getAction().equals("action.connectBySelf")){
            return new LocalOb("LocalOb");
        }else {
            return myApi;
        }
    }

    IRemoteObject myApi = new MyIdlInterfaceStub("myApi") {
        @Override
        public String myApi_GetProcessInfo(int command) throws RemoteException {
            String processInfo;
            if (command == GET_PROCESS){
                processInfo = getProcessName() + " PID is: " + getProcessInfo().getPid();
            }else {
                processInfo = "ERROR";
            }

            return processInfo;
        }
    };

    class LocalOb extends RemoteObject{

        public LocalOb(String descriptor) {
            super(descriptor);
        }

        @Override
        public boolean onRemoteRequest(int code, MessageParcel data, MessageParcel reply, MessageOption option) throws RemoteException {
            String command = data.readString();
            if ((command.equals("getProcess")) && (reply != null)){
                reply.writeString("This Process name is: " + getProcessName()
                        + "; process ID is:" + getProcessInfo().getPid());
            }
            return super.onRemoteRequest(code, data, reply, option);
        }
    }

    @Override
    public void onDisconnect(Intent intent) {
    }
}