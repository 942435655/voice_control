package com.example.administrator.tab;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.speech.SpeechRecognizer;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.ui.RecognizerDialog;
import com.iflytek.cloud.ui.RecognizerDialogListener;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.UUID;

/**
 * Created by Administrator on 2016/7/24.
 * 作用是：
 */
public class VoiceControl extends Fragment {
    public ArrayList<ChatBean> mchatList=new ArrayList<ChatBean>();
    public  ListView lvList;
    public  ChatAdapter chatAdapter;
    //***************************************
    private BluetoothAdapter bluetoothAdapter;
    private  final UUID UID8051=UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private BufferedOutputStream stream ;
    private BluetoothSocket socket;
    private int ENABLE=2;
    private byte [] array;


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.voice_control, container, false);
        lvList= (ListView) view.findViewById(R.id.lv_list);
        chatAdapter = new ChatAdapter();
        lvList.setAdapter(chatAdapter);
        view.findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listenUi();
            }
        });

        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if(!bluetoothAdapter.isEnabled()){
            Intent intent=new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(intent, ENABLE);


        }

        BluetoothDevice device = bluetoothAdapter.getRemoteDevice("98:D3:31:20:68:37");
                   try {
                socket = device.createRfcommSocketToServiceRecord(UID8051);
                socket.connect();
                       stream=new BufferedOutputStream(socket.getOutputStream());
                       array= BigInteger.valueOf(512*2+255).toByteArray();
                       stream.write(array);
                       stream.flush();
                   } catch (IOException e) {
                       e.printStackTrace();
                       Toast.makeText(getActivity(),"socket获取失败",Toast.LENGTH_LONG).show();
                   }
    }

    public void listenUi(){
        RecognizerDialog iatDialog = new RecognizerDialog(getActivity(),mInitListener);
    //2.设置听写参数， 同上节

        iatDialog.setParameter(SpeechConstant.DOMAIN, "iat");
        iatDialog.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
        iatDialog.setParameter(SpeechConstant.ACCENT, "mandarin ");
    //3.设置回调接口
        iatDialog.setListener(recognizerDialogListener);
    //4.开始听写
        iatDialog.show();

    }
    public InitListener mInitListener=new InitListener() {
        @Override
        public void onInit(int i) {

        }
    };
    StringBuffer sb=new StringBuffer();
    public  RecognizerDialogListener recognizerDialogListener=new RecognizerDialogListener() {
       public void onResult(RecognizerResult results, boolean isLast) {
         Log.d("Result:", results.getResultString());
        System.out.println(results.getResultString());

           String text = parseData(results.getResultString());
           sb.append(text);
           if(!isLast){

           }
           else{
              String  finalText=  sb.toString();
               sb=new StringBuffer();
               System.out.println("----------->" + finalText);

               mchatList.add(new ChatBean(finalText,true));
               String answer="没听清，请主人再说一遍";


               if(finalText.contains("开启电灯")){
                    answer="主人,电灯已经打开";
                    array=BigInteger.valueOf(512+127).toByteArray();

                    try {

                        stream.write(array);

                        stream.flush();
                        read(answer);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }else if(finalText.contains("关闭电灯")){
                    answer="主人,电灯已经关闭";
                    array=BigInteger.valueOf(512+255).toByteArray();

                    try {

                        stream.write(array);

                        stream.flush();
                        read(answer);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
               else if(finalText.contains("开启风扇")){
                   answer="主人,风扇已经开启";
                   array=BigInteger.valueOf(512*3+127).toByteArray();

                   try {


                           stream.write(array);


                       stream.flush();
                       read(answer);
                   } catch (IOException e) {
                       e.printStackTrace();
                   }
               }
               else if(finalText.contains("关闭风扇")){
                   answer="主人,风扇已经关闭";
                   array=BigInteger.valueOf(512*3+255).toByteArray();

                   try {

                       stream.write(array);

                       stream.flush();
                       read(answer);
                   } catch (IOException e) {
                       e.printStackTrace();
                   }
               }  else if(finalText.contains("开启电动机")){
                   answer="主人,电动机已经开启";
                   array=BigInteger.valueOf(512*2+127).toByteArray();

                   try {
                       for(int i=0;i<100;i++){
                           stream.write(array);
                       }


                       stream.flush();
                       read(answer);
                   } catch (IOException e) {
                       e.printStackTrace();
                   }
               }else if(finalText.contains("关闭电动机")){
                   answer="主人,电动机已经关闭";
                   array=BigInteger.valueOf(512*2+255).toByteArray();

                   try {

                       stream.write(array);

                       stream.flush();
                       read(answer);
                   } catch (IOException e) {
                       e.printStackTrace();
                   }
               }

               mchatList.add(new ChatBean(answer,false));


               chatAdapter.notifyDataSetChanged();
               lvList.setSelection(mchatList.size()-1);


           }



    }

        private String parseData(String resultString) {
            Gson gson =new Gson();
            StringBuffer sb=new StringBuffer();
            VoiceBean bean = gson.fromJson(resultString, VoiceBean.class);
            ArrayList<VoiceBean.WSBean> ws = bean.ws;
            for (VoiceBean.WSBean wsBean:ws){
                String text = wsBean.cw.get(0).w;
                sb.append(text);
            }
            return sb.toString();

        }

        @Override
        public void onError(SpeechError speechError) {

        }
    };
    public void read(String  string){
        SpeechSynthesizer mTts=SpeechSynthesizer.createSynthesizer(getActivity(),null);
        mTts.setParameter(SpeechConstant.VOICE_NAME, "xiaoyan");
        mTts.setParameter(SpeechConstant.SPEED, "50");
        mTts.setParameter(SpeechConstant.VOLUME, "80");
        mTts.setParameter(SpeechConstant.ENGINE_TYPE,SpeechConstant.TYPE_CLOUD );
        mTts.startSpeaking(string,null);
    }


    class ChatAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return mchatList.size();
        }

        @Override
        public ChatBean getItem(int position) {
            return mchatList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
           ViewHolder holder;
            if(convertView==null){
                holder=new ViewHolder();
                convertView=View.inflate(getActivity(),R.layout.list_item,null);
                holder.textView1= (TextView) convertView.findViewById(R.id.textView1);
                holder.textView2= (TextView) convertView.findViewById(R.id.textView2);
                convertView.setTag(holder);
            }
            else{
                holder= (ViewHolder) convertView.getTag();
            }
           ChatBean item= getItem(position);
            if(item.isAsker){
                holder.textView1.setVisibility(View.VISIBLE);
                holder.textView2.setVisibility(View.GONE);
                holder.textView1.setText(item.text);

            }
            else{
                holder.textView1.setVisibility(View.GONE);
                holder.textView2.setVisibility(View.VISIBLE);
                holder.textView2.setText(item.text);
            }

            return convertView;
        }
    }
    static class  ViewHolder{
        public TextView textView1;
        public TextView textView2;

    }

}
