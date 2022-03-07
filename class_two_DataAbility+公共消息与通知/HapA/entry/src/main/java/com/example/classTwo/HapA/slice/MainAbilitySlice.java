package com.example.classTwo.HapA.slice;

import com.example.classTwo.HapA.ResourceTable;
import ohos.aafwk.ability.AbilitySlice;
import ohos.aafwk.ability.DataAbilityHelper;
import ohos.aafwk.ability.DataAbilityRemoteException;
import ohos.aafwk.ability.IAbilityConnection;
import ohos.aafwk.content.Intent;
import ohos.aafwk.content.Operation;
import ohos.agp.components.Text;
import ohos.app.dispatcher.task.TaskPriority;
import ohos.bundle.ElementName;
import ohos.data.dataability.DataAbilityPredicates;
import ohos.data.rdb.ValuesBucket;
import ohos.data.resultset.ResultSet;
import ohos.event.commonevent.CommonEventData;
import ohos.event.commonevent.CommonEventManager;
import ohos.event.commonevent.CommonEventPublishInfo;
import ohos.eventhandler.EventHandler;
import ohos.eventhandler.EventRunner;
import ohos.eventhandler.InnerEvent;
import ohos.hiviewdfx.HiLog;
import ohos.hiviewdfx.HiLogLabel;
import ohos.rpc.IRemoteObject;
import ohos.rpc.RemoteException;
import ohos.utils.IntentConstants;
import ohos.utils.net.Uri;

public class MainAbilitySlice extends AbilitySlice {

    private static final HiLogLabel LABEL_LOG = new HiLogLabel(3, 0xD001100, "Demo");
    static final int    EVENT_MESSAGE_INIT_FINISH = 9001;
    static final int    EVENT_MESSAGE_REFRESH_UI = 9002;
    static final String DB_COLUMN_NAME = "name";
    static final String DB_COLUMN_ID = "id";
    static final String DataAbility_URL = "dataability:///com.example.classTwo.HapA.DataAbility";
    static Uri table_uri = Uri.parse(DataAbility_URL + "/book");

    private DataAbilityHelper dataAbilityHelper;

    private Text logText;


    EventRunner runner = EventRunner.create(true);
    MyEventHandler myHandler = new MyEventHandler(runner);

    @Override
    public void onStart(Intent intent) {
        super.onStart(intent);
        super.setUIContent(ResourceTable.Layout_ability_main);
        logText = (Text) findComponentById(ResourceTable.Id_log_text);
        dataAbilityHelper = DataAbilityHelper.creator(this);
        if (queryLastName() == null){
            getGlobalTaskDispatcher(TaskPriority.DEFAULT).syncDispatch(mInitThread);
        }else {
            myHandler.removeAllEvent();
            myHandler.processEvent(InnerEvent.get(EVENT_MESSAGE_REFRESH_UI));
        }
    }

    private void insertData(String name,int id){
        ValuesBucket valuesBucket = new ValuesBucket();
        valuesBucket.putString(DB_COLUMN_NAME, name);
        valuesBucket.putInteger(DB_COLUMN_ID, id);
        try {
            dataAbilityHelper.insert(table_uri, valuesBucket);
        }catch (DataAbilityRemoteException e){
            HiLog.error(LABEL_LOG, "INSERT ERROR");
        }
    }

    private String queryLastName(){
        String[] columns = new String[] {DB_COLUMN_NAME, DB_COLUMN_ID};
        DataAbilityPredicates predicates = new DataAbilityPredicates();
        String name = null;
        try {
            ResultSet resultSet  = dataAbilityHelper.query(table_uri, columns,
                    predicates);
            if (!resultSet.goToLastRow()) {
                HiLog.info(LABEL_LOG, "%{public}s", "query:No result found");
                return null;
            }
            int bookNameIndex = resultSet.getColumnIndexForName(DB_COLUMN_NAME);
            name = resultSet.getString(bookNameIndex);
            HiLog.info(LABEL_LOG, "queryLastName is " + name);
        } catch (DataAbilityRemoteException | IllegalStateException exception) {
            HiLog.error(LABEL_LOG, "%{public}s", "query: dataRemote exception|illegalStateException");
        }
        return name;
    }

    private void displayData() {
        String[] columns = new String[] {DB_COLUMN_NAME, DB_COLUMN_ID};
        DataAbilityPredicates predicates = new DataAbilityPredicates();
        try {
            ResultSet resultSet = dataAbilityHelper.query(table_uri, columns,
                    predicates);
            if (!resultSet.goToFirstRow()) {
                HiLog.info(LABEL_LOG, "%{public}s", "query:No result found");
                return;
            }
            logText.setText("");
            int bookNameIndex = resultSet.getColumnIndexForName(DB_COLUMN_NAME);
            int bookIndex = resultSet.getColumnIndexForName(DB_COLUMN_ID);
            do {
                String name = resultSet.getString(bookNameIndex);
                int id = resultSet.getInt(bookIndex);
                logText.append("Book: " + name + "-" + id + System.lineSeparator());
            } while (resultSet.goToNextRow());
        } catch (DataAbilityRemoteException | IllegalStateException exception) {
            HiLog.error(LABEL_LOG, "%{public}s", "query: dataRemote exception|illegalStateException");
        }
    }

    public void publishEvent(){
        Intent intent = new Intent();
        Operation operation = new Intent.OperationBuilder()
                .withAction("cvte.hm.demo.finishDataInit")
                .build();
        intent.setOperation(operation);
        CommonEventData eventData = new CommonEventData(intent);
        CommonEventPublishInfo publishInfo = new CommonEventPublishInfo();
        publishInfo.setSticky(true);
        try{
            CommonEventManager.publishCommonEvent(eventData, publishInfo);
        }catch (RemoteException e){
            HiLog.error(LABEL_LOG, "publishCommonEvent error");
        }
    }

    private IAbilityConnection connection = new IAbilityConnection() {
        @Override
        public void onAbilityConnectDone(ElementName elementName, IRemoteObject iRemoteObject, int i) {
        }

        @Override
        public void onAbilityDisconnectDone(ElementName elementName, int i) {
        }
    };

    void connectHapB(){
        Intent intent = new Intent();
        Operation operation = new Intent.OperationBuilder()
                .withDeviceId("")
                .withBundleName("com.example.classtwo.hapb")
                .withAbilityName("com.example.classtwo.hapb.ServiceAbility")
                .build();
        intent.setOperation(operation);
        connectAbility(intent,connection);
    }

    private class MyEventHandler extends EventHandler {

        public MyEventHandler(EventRunner runner) throws IllegalArgumentException {
            super(runner);
        }

        @Override
        protected void processEvent(InnerEvent event) {
            super.processEvent(event);
            if (event == null) {
                return;
            }
            int eventId = event.eventId;
            switch (eventId) {
                case EVENT_MESSAGE_INIT_FINISH:
                    displayData();
                    publishEvent();
                    connectHapB();
                    break;
                case EVENT_MESSAGE_REFRESH_UI:
                    displayData();
                    break;
                default:
                    break;
            }
        }
    }

    Runnable mInitThread = () -> {
        for (int i = 1; i <= 100 ; i++){
            insertData("Book_"+i, i);
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            HiLog.info(LABEL_LOG, "add book" + i);
        }
        myHandler.removeAllEvent();
        myHandler.processEvent(InnerEvent.get(EVENT_MESSAGE_INIT_FINISH));
        HiLog.info(LABEL_LOG, "post EVENT_MESSAGE_FINISH_INSERT");
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
