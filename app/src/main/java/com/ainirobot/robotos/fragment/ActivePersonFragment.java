package com.ainirobot.robotos.fragment;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.fragment.app.Fragment;

import com.ainirobot.coreservice.client.Definition;
import com.ainirobot.coreservice.client.RobotApi;
import com.ainirobot.coreservice.client.listener.ActionListener;
import com.ainirobot.coreservice.client.listener.Person;
import com.ainirobot.coreservice.client.person.PersonApi;
import com.ainirobot.coreservice.client.person.PersonListener;
import com.ainirobot.robotos.LogTools;
import com.ainirobot.robotos.R;

import java.util.List;

import static com.ainirobot.base.upload.CrashlyticsCore.TAG;

public class ActivePersonFragment extends BaseFragment {
    int id; //face id
    int angle; //face angle
    double distance; //distance
    int headSpeed; //robot head speed
    long latency; //data latency
    int facewidth; //facewidth
    int faceheight; //faceheight
    double faceAngleX; //X-axis angle of face
    double faceAngleY; //Y-axis angle of face
    double angleInView; //The angle of the person relative to the robot's head
    int faceX; //X-axis coordinate of face
    int faceY; //Y-axis coordinate of face
    int bodyX; //X-axis coordinate of human body
    int bodyY; //Y-axis coordinate of human body
    String remoteFaceId; //Remote registration id
    int age; //human age
    String gender; //human gender
    int glasses;//if human wearing glasses

    private Button mPersonChangeMonitoring;
    private Button mGetAllPersonnelInformation;
    private Button mGetListPeopleDetectedBody;
    private Button mGetListPeopleDetectedFaces;
    private Button mGetListPeopleDetectedCompleteFaces;
    private Button mGetPersonFollowingFocus;
    private Button mStartFollowFocus;
    private Button mStopFollowFocus;

    @Override
    public View onCreateView(Context context) {
        View root = mInflater.inflate(R.layout.active_person, null, false);
        initViews(root);
        return root;
    }

    private void initViews(View root) {
        mPersonChangeMonitoring = (Button) root.findViewById(R.id.Person_Change_Monitoring);
        mGetAllPersonnelInformation = (Button) root.findViewById(R.id.Get_All_Personnel_Information);
        mGetListPeopleDetectedBody = (Button) root.findViewById(R.id.Get_List_People_Detected_Body);
        mGetListPeopleDetectedFaces = (Button) root.findViewById(R.id.Get_List_People_Detected_Faces);
        mGetListPeopleDetectedCompleteFaces = (Button) root.findViewById(R.id.Get_List_People_Detected_Complete_Faces);
        mGetPersonFollowingFocus = (Button) root.findViewById(R.id.Get_Person_Following_Focus);
        mStartFollowFocus = (Button) root.findViewById(R.id.start_follow_focus);
        mStopFollowFocus = (Button) root.findViewById(R.id.stop_follow_focus);

        mPersonChangeMonitoring.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        mGetAllPersonnelInformation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<Person> personList = PersonApi.getInstance().getAllPersons();
                for (int i = 0; i < personList.size(); i++) {
//                    for (int j = 0; j < personList.get(i))
                    LogTools.info(personList.get(i).toString());
//                    System.out.println(personList.get(i));
                }
            }
        });

        mGetListPeopleDetectedBody.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<Person> personList = PersonApi.getInstance().getAllBodyList();
                for (int i = 0; i < personList.size(); i++) {
                    LogTools.info(personList.get(i).toString());
//                    System.out.println(personList.get(i));
                }
            }
        });

        mGetListPeopleDetectedFaces.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<Person> personList = PersonApi.getInstance().getAllFaceList();
                for (int i = 0; i < personList.size(); i++) {
                    LogTools.info(personList.get(i).toString());
//                    System.out.println(personList.get(i));
                }
            }
        });

        mGetListPeopleDetectedCompleteFaces.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<Person> personList = PersonApi.getInstance().getCompleteFaceList();
                for (int i = 0; i < personList.size(); i++) {
                    LogTools.info(personList.get(i).toString());
//                    System.out.println(personList.get(i));
                }
            }
        });

        mGetPersonFollowingFocus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Person person = PersonApi.getInstance().getFocusPerson();
                LogTools.info(person.toString());
            }
        });

        mStartFollowFocus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                focusFollow();
            }
        });

        mStopFollowFocus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RobotApi.getInstance().stopFocusFollow(0);
            }
        });

        PersonListener listener = new PersonListener() {
            @Override
            public void personChanged() {
                super.personChanged();
                //when person changed, use "getAllPersons()" to get all people info in the robot's field of view
            }
        };

    }

    public void focusFollow () {
        RobotApi.getInstance().startFocusFollow(0, 0, 2, 2, new ActionListener() {
            @Override
            public void onStatusUpdate(int status, String data) {
                switch (status) {
                    case Definition.STATUS_TRACK_TARGET_SUCCEED:
                        //Follow the target successfully
                        break;
                    case Definition.STATUS_GUEST_LOST:
                        //lost target
                        break;
                    case Definition.STATUS_GUEST_FARAWAY:
                        // target distance is greater than the set maximum distance
                        break;
                    case Definition.STATUS_GUEST_APPEAR:
                        // target re-enter the set maximum distance
                        break;
                }
            }
            @Override
            public void onError(int errorCode, String errorString) {
                switch (errorCode) {
                    case Definition.ERROR_SET_TRACK_FAILED:
                    case Definition.ERROR_TARGET_NOT_FOUND:
                        //target not found
                        break;
                    case Definition.ACTION_RESPONSE_ALREADY_RUN:
                        //robot is doing a following task, please stop first before re-executing
                        break;
                    case Definition.ACTION_RESPONSE_REQUEST_RES_ERROR:
                        //There are already api calls that need to control the chassis (for example: lead, navigation), please stop first, then continue to call
                        break;
                }
            }
            @Override
            public void onResult(int status, String responseString) {
                Log.d(TAG, "startTrackPerson onResult status: " + status);
                LogTools.info("startTrackPerson onResult status: " + status);
                switch (status) {
                    case Definition.ACTION_RESPONSE_STOP_SUCCESS:
                        //during the focus following process, use stopFocusFollow to stop successfully
                        break;
                }
            }
        });
    }

    public static Fragment newInstance() {
        return new ActivePersonFragment();
    }

}
