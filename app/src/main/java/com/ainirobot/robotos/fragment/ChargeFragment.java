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
import android.view.View;
import android.widget.Button;

import androidx.fragment.app.Fragment;

import com.ainirobot.coreservice.client.Definition;
import com.ainirobot.coreservice.client.RobotApi;
import com.ainirobot.coreservice.client.listener.ActionListener;
import com.ainirobot.robotos.LogTools;
import com.ainirobot.robotos.R;

public class ChargeFragment extends BaseFragment {

    private Button mStop_auto_charge;
    private Button mStart_auto_charge;
    private Button mStop_charge_leave;

    @Override
    public View onCreateView(Context context) {
        View root = mInflater.inflate(R.layout.fragment_charge_layout, null, false);
        initViews(root);
        return root;
    }

    private void initViews(View root) {
        mStop_auto_charge = (Button) root.findViewById(R.id.stop_auto_charge);
        mStart_auto_charge = (Button) root.findViewById(R.id.start_auto_charge);
        mStop_charge_leave = (Button) root.findViewById(R.id.stop_charge_leave);

        mStart_auto_charge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startCharge();
            }
        });

        mStop_auto_charge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopAutoChargeAction();
            }
        });

        mStop_charge_leave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopChargingByApp();
            }
        });
    }

    /**
     * 开始自动回充
     */
    private void startCharge() {
        LogTools.info("เริ่มการชาร์จแบตอัตโนมัติ ");
        RobotApi.getInstance().startNaviToAutoChargeAction(0, 3 * Definition.MINUTE, mActionListener);
    }

    /**
     * 结束自动回充
     */
    private void stopAutoChargeAction() {
        LogTools.info("สิ้นสุดการชาร์จแบตอัตโนมัติ ");
        RobotApi.getInstance().stopAutoChargeAction(0, true);
    }

    /**
     * 停止充电并脱离充电桩
     */
    private void stopChargingByApp() {
        LogTools.info("หยุดชาร์จแล้ว");
        RobotApi.getInstance().stopChargingByApp();
    }

    private ActionListener mActionListener = new ActionListener() {

        @Override
        public void onResult(int status, String responseString) throws RemoteException {
            switch (status) {
                case Definition.RESULT_OK:
                    LogTools.info("onResult result: " + status +"(ชาร์จเรียบร้อยแล้ว )"+ " message: "+  responseString);
                    break;

                case Definition.RESULT_FAILURE:
                    LogTools.info("onResult result: " + status +"(การชาร์จล้มเหลว )"+ " message: "+  responseString);
                    break;
                default:
                    break;
            }
        }

        @Override
        public void onStatusUpdate(int status, String data) throws RemoteException {
            switch (status) {
                case Definition.STATUS_NAVI_GLOBAL_PATH_FAILED:
                    LogTools.info("onStatusUpdate result: " + status +"(การวางแผนเส้นทางทั่วโลกล้มเหลว)"+ " message: "+  data);
                    break;

                case Definition.STATUS_NAVI_OUT_MAP:
                    LogTools.info("onStatusUpdate result: " + status +"(ไม่สามารถเข้าถึงจุดเป้าหมาย，ปลายทางอยู่นอกแผนที่，เป็นไปได้ว่าแผนที่ไม่ตรงกับจุดที่ตั้ง，กรุณารีเซ็ตตำแหน่ง )"+ " message: "+  data);
                    break;

                case Definition.STATUS_NAVI_AVOID:
                    LogTools.info("onStatusUpdate result: " + status +"(เส้นทางไปยังจุดชาร์จถูกอุปสรรคขัดขวาง )"+ " message: "+  data);
                    break;

                case Definition.STATUS_NAVI_AVOID_END:
                    LogTools.info("onStatusUpdate result: " + status +"(ขจัดอุปสรรค )"+ " message: "+  data);
                    break;
                default:
                    break;
            }
        }

        @Override
        public void onError(int errorCode, String errorString, String extraData) throws RemoteException {
            super.onError(errorCode, errorString, extraData);
            LogTools.info("onError result: " + errorCode +"(การชาร์จและการชาร์จล้มเหลว)"+ " message: "+  errorString);
        }
    };

    public static Fragment newInstance() {
        return new ChargeFragment();
    }
}
