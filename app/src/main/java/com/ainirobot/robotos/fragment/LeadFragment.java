/*
 *  Copyright (C) 2017 OrionStar Technology Project
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.ainirobot.robotos.fragment;

import android.content.Context;
import android.os.RemoteException;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.fragment.app.Fragment;

import com.ainirobot.coreservice.client.Definition;
import com.ainirobot.coreservice.client.RobotApi;
import com.ainirobot.coreservice.client.actionbean.LeadingParams;
import com.ainirobot.coreservice.client.listener.ActionListener;
import com.ainirobot.robotos.LogTools;
import com.ainirobot.robotos.R;

public class LeadFragment extends BaseFragment {

    private Button mStop_lead_btn;
    private Button mStart_lead_btn;
    private EditText mLead_point;

    @Override
    public View onCreateView(Context context) {
        View root = mInflater.inflate(R.layout.fragment_lead_layout,null,false);
        bindViews(root);
        return root;
    }

    private void bindViews(View root) {
        mStop_lead_btn = (Button) root.findViewById(R.id.stop_lead_btn);
        mStart_lead_btn = (Button) root.findViewById(R.id.start_lead_btn);
        mLead_point = (EditText)root.findViewById(R.id.et_lead_point);

        mStart_lead_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startLead();
            }
        });

        mStop_lead_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopLead();
            }
        });
    }

    private String getLeadPoint(){
        String leadPoint = mLead_point.getText().toString();
        if(TextUtils.isEmpty(leadPoint)){
            leadPoint = mLead_point.getHint().toString();
        }
        return leadPoint;
    }
    /**
     * 开始引领
     */
    private void startLead() {
        LeadingParams params = new LeadingParams();
        params.setPersonId(0);
        params.setDestinationName(getLeadPoint());
        params.setLostTimer(10 * 1000);
        params.setDetectDelay(5 * 1000);
        params.setMaxDistance(3);

        int reqId = 0;
        LogTools.info("startLead:" + getLeadPoint());
        RobotApi.getInstance().startLead(reqId, params, new ActionListener() {

            @Override
            public void onResult(int status, String responseString) throws RemoteException {
                switch (status) {
                    case Definition.RESULT_OK:
                        LogTools.info("onResult status :" + status + "(นำพาไปสู่จุดหมายได้สำเร็จ)" + " result:" + responseString);
                        break;

                    case Definition.ACTION_RESPONSE_STOP_SUCCESS:
                        LogTools.info("onResult status :" + status + "(ประสบความสำเร็จในการนำไปสู่เป้าหมายและนำไปสู่การดำเนินการ lead ，เรียกใช้ stopLead ，หยุดนำสำเร็จ )" + " result:" + responseString);
                        break;

                    default:
                        break;
                }
            }

            @Override
            public void onError(int errorCode, String errorString) throws RemoteException {
                switch (errorCode) {
                    case Definition.ERROR_NOT_ESTIMATE:
                        LogTools.info("onError errorCode :" + errorCode + "(ไม่ได้อยู่ในขณะนี้ )" + " result:" + errorString);
                        break;

                    case Definition.ERROR_SET_TRACK_FAILED:
                        break;
                    case Definition.ERROR_TARGET_NOT_FOUND:
                        LogTools.info("onError errorCode :" + errorCode + "(ไม่พบเป้าหมายหลัก )" + " result:" + errorString);
                        break;

                    case Definition.ERROR_IN_DESTINATION:
                        LogTools.info("onError errorCode :" + errorCode + "(ปัจจุบันเป็นผู้นำปลายทาง )" + " result:" + errorString);
                        break;

                    case Definition.ERROR_DESTINATION_CAN_NOT_ARRAIVE:
                        LogTools.info("onError errorCode :" + errorCode + "(หมดเวลาการหลีกเลี่ยงอุปสรรค ，ค่าเริ่มต้นคือระยะไปข้างหน้า 20 วินาทีของหุ่นยนต์น้อยกว่า 0.1 เมตร)" + " result:" + errorString);
                        break;

                    case Definition.ERROR_DESTINATION_NOT_EXIST:
                        LogTools.info("onError errorCode :" + errorCode + "(ปลายทางไม่มีอยู่จริง )" + " result:" + errorString);
                        break;

                    case Definition.ERROR_HEAD:
                        LogTools.info("onError errorCode :" + errorCode + "(ล้มเหลวในการใช้ไม้กันสั่นศีรษะขณะเป็นผู้นำ )" + " result:" + errorString);
                        break;

                    case Definition.ACTION_RESPONSE_ALREADY_RUN:
                        LogTools.info("onError errorCode :" + errorCode + "(เป็นผู้นำอยู่แล้วในความคืบหน้า，คราวที่แล้วโปรดหยุดนำ，เพื่อดำเนินการใหม่)" + " result:" + errorString);
                        break;

                    case Definition.ACTION_RESPONSE_REQUEST_RES_ERROR:
                        LogTools.info("onError errorCode :" + errorCode + "(มีการเรียกอินเทอร์เฟซที่ต้องควบคุมแชสซีอยู่แล้ว，ขอหยุดก่อน，เพื่อโทรต่อ )" + " result:" + errorString);
                        break;

                    default:
                        break;
                }
            }

            @Override
            public void onStatusUpdate(int status, String data) throws RemoteException {
                switch (status) {
                    case Definition.STATUS_NAVI_OUT_MAP:
                        LogTools.info("onStatusUpdate status :" + status + "(ไม่สามารถไปถึงเป้าหมายได้ ，ปลายทางอยู่นอกแผนที่ ，เป็นไปได้ว่าแผนที่ไม่ตรงกับจุดที่ตั้ง ，กรุณารีเซ็ตตำแหน่ง )" + " result:" + data);
                        break;

                    case Definition.STATUS_NAVI_AVOID:
                        LogTools.info("onStatusUpdate status :" + status + "(เส้นทางนำปัจจุบันถูกกีดขวางโดยสิ่งกีดขวาง )" + " result:" + data);
                        break;

                    case Definition.STATUS_NAVI_AVOID_END:
                        LogTools.info("onStatusUpdate status :" + status + "(ขจัดอุปสรรค )" + " result:" + data);
                        break;

                    case Definition.STATUS_GUEST_FARAWAY:
                        LogTools.info("onStatusUpdate status :" + status + "(นำเป้าหมายไปให้ไกลจากหุ่นยนต์เกินไป ，มาตรฐานการตัดสินถูกกำหนดโดยพารามิเตอร์ maxDistance )" + " result:" + data);
                        break;

                    case Definition.STATUS_DEST_NEAR:
                        LogTools.info("onStatusUpdate status :" + status + "(นำเป้าหมายเข้าสู่ช่วง maxDistance ของหุ่นยนต์ )" + " result:" + data);
                        break;

                    case Definition.STATUS_LEAD_NORMAL:
                        LogTools.info("onStatusUpdate status :" + status + "(เริ่มการนำทางอย่างเป็นทางการ )" + " result:" + data);
                        break;

                    default:
                        break;
                }
            }
        });
    }

    /**
     * 结束引领
     * isResetHW： 引领时会切换摄像头到后置摄像头，isResetHW是用于设置停止引领时是否恢复摄像头状态，
     * true：恢复摄像头为前置，false : 保持停止时的状态
     */
    private void stopLead() {
        RobotApi.getInstance().stopLead(0, true);
    }

    public static Fragment newInstance() {
        return new LeadFragment();
    }

}
