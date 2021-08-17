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
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.ainirobot.coreservice.client.Definition;
import com.ainirobot.coreservice.client.RobotApi;
import com.ainirobot.coreservice.client.listener.CommandListener;
import com.ainirobot.robotos.LogTools;
import com.ainirobot.robotos.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class LocationFragment extends BaseFragment {

    private double mCurrentX;
    private double mCurrentY;
    private double mCurrentTheta;

    private Button mIs_location;
    private Button mGet_location;
    private Button mSet_location;
    private Button mIs_in_location;
    private Button mRemove_location;
    private Button mSet_reception_point;
    private Spinner tIs_in_location;
    private Spinner tRemove_location;
    private EditText tNewName_set_location;

    private Context context;


    @Override
    public View onCreateView(Context context) {
        View root = mInflater.inflate(R.layout.fragment_location_layout, null, false);
        this.context = context;
        initViews(root);
        return root;
    }

    private void initViews(View root) {
        mIs_location = (Button) root.findViewById(R.id.is_location);
        mGet_location = (Button) root.findViewById(R.id.get_location);
        mSet_location = (Button) root.findViewById(R.id.set_location_start);
        mIs_in_location = (Button) root.findViewById(R.id.is_in_location);
        mRemove_location = (Button) root.findViewById(R.id.remove_location);
        mSet_reception_point = (Button) root.findViewById(R.id.set_location);
        tIs_in_location = (Spinner) root.findViewById(R.id.list_is_in_location);
        tRemove_location = (Spinner) root.findViewById(R.id.list_remove_location);
        tNewName_set_location = (EditText) root.findViewById(R.id.newName_set_location);

        getAllPosition();

        mIs_in_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isRobotInlocation();
            }
        });

        mSet_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setPostEstimate();
            }
        });

        mIs_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isRobotEstimate();
            }
        });

        mSet_reception_point.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setLocation();
            }
        });

        mGet_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getLocation();
            }
        });

        mRemove_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeLocation();
            }
        });


    }

    private void getAllPosition() {
        RobotApi.getInstance().getPlaceList(0, new CommandListener() {
            @Override
            public void onResult(int result, String message) {
                try {
                    final JSONArray jsonArray = new JSONArray(message);
                    final int length = jsonArray.length();
                    final String[] position = new String[length];
                    for (int i = 0; i < length; i++) {
                        JSONObject json = jsonArray.getJSONObject(i);
                        position[i] = json.getString("name");
                    }
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, position);
                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            tIs_in_location.setAdapter(adapter);
                            tRemove_location.setAdapter(adapter);
                        }
                    });

//                    tIs_in_location.setOnItemSelectedListener(this);


                } catch (JSONException | NullPointerException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * ตรวจสอบว่าหุ่นยนต์อยู่ที่ตำแหน่งหรือไม่
     */
    private void isRobotInlocation() {
        try {
            String namePosition = String.valueOf(tIs_in_location.getSelectedItem());
            LogTools.info("setLocation result: " + namePosition);
            JSONObject params = new JSONObject();
            params.put(Definition.JSON_NAVI_TARGET_PLACE_NAME, namePosition);
            params.put(Definition.JSON_NAVI_COORDINATE_DEVIATION, 2.0);

            RobotApi.getInstance().isRobotInlocations(0,
                    params.toString(), new CommandListener() {
                        @Override
                        public void onResult(int result, String message) {
                            try {
                                JSONObject json = new JSONObject(message);
                                json.getBoolean(Definition.JSON_NAVI_IS_IN_LOCATION);
                                LogTools.info("isRobotInlocation result: " + result + " message: " + message);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * กำหนดจุดพิกัดเริ่มต้นของหุ่นยนต์
     */
    private void setPostEstimate() {
        if (mCurrentX == 0 || mCurrentY == 0) {
            LogTools.info("พิกัดว่าง ขอพิกัดปัจจุบันก่อน");
            return;
        }
        try {
            JSONObject params = new JSONObject();
            params.put(Definition.JSON_NAVI_POSITION_X, mCurrentX);
            params.put(Definition.JSON_NAVI_POSITION_Y, mCurrentY);
            params.put(Definition.JSON_NAVI_POSITION_THETA, mCurrentTheta);

            RobotApi.getInstance().setPoseEstimate(0, params.toString(), new CommandListener() {
                @Override
                public void onResult(int result, String message) {
                    LogTools.info("setPostEstimate result: " + result + " message: " + message);
                    if ("succeed".equals(message)) {
                    }
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * ตรวจสอบว่าปัจจุบันอยู่ตำแหน่งเริ่มต้นหรือไม่
     */
    private void isRobotEstimate() {
        RobotApi.getInstance().isRobotEstimate(0, new CommandListener() {
            @Override
            public void onResult(int result, String message) {
                LogTools.info("isRobotEstimate result: " + result + " message: " + message);
                if (!"true".equals(message)) {
                } else {
                }
            }
        });
    }

    /**
     * ตั้งชื่อตำแหน่งปัจจุบัน
     */
    private void setLocation() {
        final String newNameLocation = tNewName_set_location.getText().toString();
        LogTools.info("setLocation result: " + newNameLocation);
        if (newNameLocation == null || newNameLocation.isEmpty() || newNameLocation.trim().isEmpty()){
            LogTools.info("not have name " + newNameLocation);

        } else {
            LogTools.info("setLocation result:" + newNameLocation);

            RobotApi.getInstance().setLocation(0, newNameLocation, new CommandListener() {
                @Override
                public void onResult(int result, String message) {
                    LogTools.info("setLocation result: " + result + " message: " + message);
//                if ("succeed".equals(message)) {
                    if ("0".equals(message)) {

                        Toast.makeText(context, "ตั้งค่าตำแหน่ง : " + newNameLocation + " สำเร็จ",Toast.LENGTH_SHORT).show();
                        getAllPosition();
                    } else {
                        Toast.makeText(context, "ตั้งค่าตำแหน่ง : " + newNameLocation + " ไม่สำเร็จ" + "ERROR : " + message,Toast.LENGTH_SHORT).show();

                    }
                }
            });
        }

    }

    /**
     * รับจุดพิกัดปัจจุบัน
     */
    private void getLocation() {
        RobotApi.getInstance().getPosition(0, new CommandListener() {
            @Override
            public void onResult(int result, String message) {
                LogTools.info("getLocation result: " + result + " message: " + message);
                try {
                    JSONObject json = new JSONObject(message);
                    mCurrentX = json.getDouble(Definition.JSON_NAVI_POSITION_X);
                    mCurrentY = json.getDouble(Definition.JSON_NAVI_POSITION_Y);
                    mCurrentTheta = json.getDouble(Definition.JSON_NAVI_POSITION_THETA);
                } catch (JSONException | NullPointerException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * ลบสถานที่
     */
    private void removeLocation() {
        final String namePosition = String.valueOf(tRemove_location.getSelectedItem());
        LogTools.info("setLocation result: " + namePosition);
        RobotApi.getInstance().removeLocation(0, namePosition, new CommandListener() {
            @Override
            public void onResult(int result, String message) {
                LogTools.info("removeLocation result: " + result + " message: " + message);
                if ("succeed".equals(message)) {
                    Toast.makeText(context, "ลบตำแหน่ง : " + namePosition + " สำเร็จ",Toast.LENGTH_SHORT).show();
                    getAllPosition();

                } else {
                    Toast.makeText(context, "ลบตำแหน่ง : " + namePosition + " ไม่สำเร็จ" + "ERROR : " + message,Toast.LENGTH_SHORT).show();

                }
            }
        });
    }

    public static Fragment newInstance() {
        return new LocationFragment();
    }
}
