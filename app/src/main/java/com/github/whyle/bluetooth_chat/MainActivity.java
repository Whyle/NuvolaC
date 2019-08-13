package com.github.whyle.bluetooth_chat;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_ENABLE_BT = 10001;
    public static String me = "Y";

    private BluetoothAdapter mBluetoothAdapter;
    private StringBuffer mOutStringBuffer;
    private BluetoothChatService mChatService;
    private String mConnectedDeviceName;
    private TextView main_msg;
    private Button main_send;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.main_chat);
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerView.setHasFixedSize(true);
        // use a linear layout manager
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);


        //
        ArrayList<Message> msgs = new ArrayList<>();
        Message msg1 = new Message();
        msg1.setName("Y");
        msg1.setMsg("How are you?");
        msg1.setTime("00:00");
        msgs.add(msg1);
        Message msg2 = new Message();
        msg2.setName("H");
        msg2.setMsg("I'm good!!!I'm good!!!I'm good!!!I'm good!!!I'm good!!!I'm good!!!I'm good!!!I'm good!!!I'm good!!!I'm good!!!I'm good!!!");
        msg2.setTime("00:00");
        msgs.add(msg2);
        Message msg3 = new Message();
        msg3.setName("L");
        msg3.setMsg("Me tooooooo!\nMe tooooooo!\nMe tooooooo!\nMe tooooooo!\nMe tooooooo!\nMe tooooooo!");
        msg3.setTime("00:03");
        msgs.add(msg3);
        Message msg4 = new Message();
        msg4.setName("L");
        msg4.setMsg("Me tooooooo!\nMe tooooooo!\nMe tooooooo!\nMe tooooooo!\nMe tooooooo!\nMe tooooooo!");
        msg4.setTime("00:03");
        msgs.add(msg4);
        Message msg5 = new Message();
        msg5.setName("Y");
        msg5.setMsg("ok is very nice!!!");
        msg5.setTime("00:05");
        msgs.add(msg5);
        Message msg6 = new Message();
        msg6.setName("Y");
        msg6.setMsg("我竟然能把模板作出来了，虽然现在没有任何功能，但是有这个页面，我就心满意足了，恩恩 nicce  i love java     because it can give you infinite ideas  特色他 test  test    test    test    test    test    特色他");
        msg6.setTime("00:05");
        msgs.add(msg6);

        // specify an adapter (see also next example)
        ChatAdapter mAdapter = new ChatAdapter(msgs);
        recyclerView.setAdapter(mAdapter);
        recyclerView.smoothScrollToPosition(msgs.size()-1);


    }

    private void init()
    {
        // Get local Bluetooth adapter
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        // If the adapter is null, then Bluetooth is not supported
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "此设备不支持蓝牙！", Toast.LENGTH_LONG).show();
            this.finish();
        }
        main_msg = findViewById(R.id.main_text);
        main_send = findViewById(R.id.main_send);
        main_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage(main_msg.getText().toString());
                main_msg.setText("");
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        // If BT is not on, request that it be enabled.
        // setupChat() will then be called during onActivityResult
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
            // Otherwise, setup the chat session
        } else if (mChatService == null) {
            setupChat();
        }
    }

    private void setupChat()
    {
        // Initialize the BluetoothChatService to perform bluetooth connections
        mChatService = new BluetoothChatService(this, mHandler);

        // Initialize the buffer for outgoing messages
        mOutStringBuffer = new StringBuffer("");
    }


    /**
     * 设在此设备可见度 300 seconds (5 minutes).
     */
    private void ensureDiscoverable() {
        if (mBluetoothAdapter.getScanMode() !=
                BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
            Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
            startActivity(discoverableIntent);
        }
    }

    /**
     * Establish connection with other device
     *
     * @param data   An {@link Intent} with {@link DeviceListActivity#EXTRA_DEVICE_ADDRESS} extra.
     * @param secure Socket Security type - Secure (true) , Insecure (false)
     */
    private void connectDevice(Intent data, boolean secure) {
        // Get the device MAC address
        String address = data.getExtras()
                .getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
        // Get the BluetoothDevice object
        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
        // Attempt to connect to the device
        mChatService.connect(device, secure);
    }


    /**
     * 发送消息
     *
     * @param message 要发送的字串.
     */
    private void sendMessage(String message) {
        // Check that we're actually connected before trying anything
        if (mChatService.getState() != BluetoothChatService.STATE_CONNECTED) {
            Toast.makeText(this, "蓝牙没有连接！", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check that there's actually something to send
        if (message.length() > 0) {
            // Get the message bytes and tell the BluetoothChatService to write
            byte[] send = message.getBytes();
            mChatService.write(send);

            // Reset out string buffer to zero and clear the edit text field
            //mOutStringBuffer.setLength(0);
            //mOutEditText.setText(mOutStringBuffer);
        }
    }


    /**
     * The Handler that gets information back from the BluetoothChatService
     */
    private final Handler mHandler = new Handler() {

        public void handleMessage(android.os.Message msg) {

            switch (msg.what) {
                case Constants.MESSAGE_STATE_CHANGE:
                    switch (msg.arg1) {
                        case BluetoothChatService.STATE_CONNECTED:
                            //mConversationArrayAdapter.clear();
                            break;
                        case BluetoothChatService.STATE_CONNECTING:
                           // setStatus(R.string.title_connecting);
                            break;
                        case BluetoothChatService.STATE_LISTEN:
                        case BluetoothChatService.STATE_NONE:
                            //setStatus(R.string.title_not_connected);
                            break;
                    }
                    break;
                case Constants.MESSAGE_WRITE:
                    byte[] writeBuf = (byte[]) msg.obj;
                    // construct a string from the buffer
                    String writeMessage = new String(writeBuf);
                    //mConversationArrayAdapter.add("Me:  " + writeMessage);
                    break;
                case Constants.MESSAGE_READ:
                    byte[] readBuf = (byte[]) msg.obj;
                    // construct a string from the valid bytes in the buffer
                    String readMessage = new String(readBuf, 0, msg.arg1);
                    //mConversationArrayAdapter.add(mConnectedDeviceName + ":  " + readMessage);
                    break;
                case Constants.MESSAGE_DEVICE_NAME:
                    // save the connected device's name
                    mConnectedDeviceName = msg.getData().getString(Constants.DEVICE_NAME);
                   Toast.makeText(MainActivity.this, "Connected to "
                                + mConnectedDeviceName, Toast.LENGTH_SHORT).show();

                    break;
                case Constants.MESSAGE_TOAST:

                        Toast.makeText(MainActivity.this, msg.getData().getString(Constants.TOAST),
                                Toast.LENGTH_SHORT).show();

                    break;
            }
        }
    };

}
