package com.example.classtwo.hapb;

import ohos.aafwk.ability.Ability;
import ohos.aafwk.content.Intent;
import ohos.aafwk.content.IntentParams;
import ohos.bundle.ElementName;
import ohos.event.commonevent.*;
import ohos.event.intentagent.IntentAgent;
import ohos.event.intentagent.IntentAgentConstant;
import ohos.event.intentagent.IntentAgentHelper;
import ohos.event.intentagent.IntentAgentInfo;
import ohos.event.notification.NotificationHelper;
import ohos.event.notification.NotificationRequest;
import ohos.event.notification.NotificationSlot;
import ohos.rpc.IRemoteObject;
import ohos.hiviewdfx.HiLog;
import ohos.hiviewdfx.HiLogLabel;
import ohos.rpc.RemoteException;

import java.util.ArrayList;
import java.util.List;

public class ServiceAbility extends Ability {
    private static final HiLogLabel LABEL_LOG = new HiLogLabel(3, 0xD001100, "Demo");

    DataBaseHelper mDataBaseHelper;

    public class EventSubcriber extends CommonEventSubscriber {

        public EventSubcriber(CommonEventSubscribeInfo subscribeInfo) {
            super(subscribeInfo);
        }

        @Override
        public void onReceiveEvent(CommonEventData commonEventData) {
            String action = commonEventData.getIntent().getAction();
            publishNotification();
            HiLog.info(LABEL_LOG,"get the event-action: " + action);
        }
    }

    @Override
    public void onStart(Intent intent) {
        HiLog.error(LABEL_LOG, "ServiceAbility::onStart");
        super.onStart(intent);
        mDataBaseHelper = new DataBaseHelper(this);
        mDataBaseHelper.updateDataBase();
        addNotificationSlot();
        subscribeEvent();
    }

    private void subscribeEvent() {
        MatchingSkills matchingSkills = new MatchingSkills();
        matchingSkills.addEvent("cvte.hm.demo.finishDataInit");
        CommonEventSubscribeInfo info = new CommonEventSubscribeInfo(matchingSkills);
        EventSubcriber subcriber = new EventSubcriber(info);
        try{
            CommonEventManager.subscribeCommonEvent(subcriber);
        }catch (RemoteException e){

        }
    }

    private void addNotificationSlot(){
        NotificationSlot notificationSlot = new NotificationSlot("slot_1",
                "slot_1",NotificationSlot.LEVEL_HIGH);
        notificationSlot.setEnableVibration(true);
        notificationSlot.canEnableLight();
        try {
            NotificationHelper.addNotificationSlot(notificationSlot);
        } catch (RemoteException ex) {
            HiLog.error(LABEL_LOG, "%{public}s", "defineNotificationSlot remoteException.");
        }
    }

    private void publishNotification() {
        NotificationRequest request = new NotificationRequest()
                .setSlotId("slot_1");
        NotificationRequest.NotificationNormalContent normal_content =
                new NotificationRequest.NotificationNormalContent()
                        .setTitle("Hap B EventSubcriber")
                        .setText("From this to start HapA");

        NotificationRequest.NotificationContent content1 =
                new NotificationRequest.NotificationContent(normal_content);

        IntentAgent intentAgent = createIntentAgent();
        request.setIntentAgent(intentAgent);

        request.setContent(content1);
        try {
            NotificationHelper.publishNotification(request);
        }catch (RemoteException ex) {

        }
    }

    private IntentAgent createIntentAgent() {
        Intent intent = new Intent();
        intent.setElement(new ElementName("", "com.example.classTwo.HapA",
                "com.example.classTwo.HapA.MainAbility"));

        List<Intent> intents = new ArrayList<>();
        intents.add(intent);
        IntentAgentInfo agentInfo = new IntentAgentInfo(1000, IntentAgentConstant.OperationType.START_ABILITY,
                IntentAgentConstant.Flags.UPDATE_PRESENT_FLAG, intents, new IntentParams());
        return IntentAgentHelper.getIntentAgent(this, agentInfo);
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
        return null;
    }

    @Override
    public void onDisconnect(Intent intent) {
    }
}