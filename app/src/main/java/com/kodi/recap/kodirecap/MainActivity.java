package com.kodi.recap.kodirecap;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import org.json.JSONObject;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {

    private DatagramSocket socket;
    private InetAddress broadcastIP;
    private ArrayList<DatagramPacket> resultPackets = new ArrayList<>();
    private UDPReceiveTask udp_task;

    private int RECAPCODE = 0x1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        udp_task = new UDPReceiveTask(this);
        udp_task.execute(5646);
    }

    private class UDPReceiveTask extends AsyncTask<Integer, Integer, Long> {
        private String result = "";
        private Activity ac;

        UDPReceiveTask(Activity ac)
        {
            this.ac = ac;
        }

        protected Long doInBackground(Integer... ports) {
            int count = ports.length;
            long totalSize = 0;
            if(count > 1)
            {
                return null;
            }
            try
            {
                broadcastIP = InetAddress.getByName("0.0.0.0");

                byte[] recvBuf = new byte[15000];
                if (socket == null || socket.isClosed()) {
                    socket = new DatagramSocket(ports[0], broadcastIP);
                    socket.setBroadcast(true);
                }
                //socket.setSoTimeout(1000);
                //socket.setSoTimeout(10000);
                DatagramPacket packet = new DatagramPacket(recvBuf, recvBuf.length);
                Log.e("UDP", "Waiting for UDP broadcast");
                socket.receive(packet);

                String senderIP = packet.getAddress().getHostAddress();
                this.result = new String(packet.getData()).trim();
                socket.send(new DatagramPacket("OK".getBytes(),"OK".length(),packet.getAddress(),packet.getPort()));
                String returnMsg = "NO";

                Log.e("UDP", "Got UDB broadcast from " + senderIP + ", message: " + this.result);
                resultPackets.add(new DatagramPacket(returnMsg.getBytes(),returnMsg.length(),packet.getAddress(),packet.getPort()));
                //socket.close();
                return ((long) resultPackets.size()-1);
            }catch (Exception e){
                Log.e("UDP", "Exception, message: "+e.toString());
            }

            return null;
        }

        protected void onPostExecute(Long result) {
            if(this.result != "")
            {
                Intent i = new Intent(this.ac,CaptchaSolve.class);
                i.putExtra("server",this.result);
                i.putExtra("index",result);
                startActivityForResult(i,RECAPCODE);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == RECAPCODE) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                final DatagramPacket p = resultPackets.get((int) data.getLongExtra("index",0));
                resultPackets.remove(p);
                p.setData(data.getStringExtra("key").getBytes());
                p.setLength(data.getStringExtra("key").length());
                udp_task = new UDPReceiveTask(this);
                new Thread() {
                    @Override
                    public void run() {
                        try {
                            socket.send(p);
                            socket.close();
                            udp_task.execute(5646);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }.start();
                // The user picked a contact.
                // The Intent's data Uri identifies which contact was selected.

                // Do something with the contact here (bigger example below)
            }
        }
    }
}
