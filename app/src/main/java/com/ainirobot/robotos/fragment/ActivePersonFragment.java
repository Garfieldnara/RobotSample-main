package com.ainirobot.robotos.fragment;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Looper;
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

import java.io.File;
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

    int milliseconds = 2000;
    MediaPlayer hello;


    private Button mPersonChangeMonitoring;
    private Button mGetAllPersonnelInformation;
    private Button mGetListPeopleDetectedBody;
    private Button mGetListPeopleDetectedFaces;
    private Button mGetListPeopleDetectedCompleteFaces;
    private Button mGetPersonFollowingFocus;
    private Button mStartFollowFocus;
    private Button mStopFollowFocus;

    final Handler handler = new Handler(Looper.getMainLooper());
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            // Do the task...
            LogTools.info("test interval");
            findFocusFollow();
            handler.postDelayed(this, milliseconds);// Optional, to repeat the task.
        }
    };



    @Override
    public View onCreateView(Context context) {
        View root = mInflater.inflate(R.layout.active_person, null, false);
        initViews(root);
        hello = MediaPlayer.create(context, R.raw.mssiam);
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
                PersonListener listener = new PersonListener() {
                    @Override
                    public void personChanged() {
                        super.personChanged();
                        //when person changed, use "getAllPersons()" to get all people info in the robot's field of view
//                        List<Person> personList = PersonApi.getInstance().getAllPersons();
                    }
                };

//                PersonApi.getInstance().registerPersonListener(listener);
            }

        });

        mGetAllPersonnelInformation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<Person> personList = PersonApi.getInstance().getAllPersons();
                LogTools.info("mGetAllPersonnelInformation");
                System.out.println(personList);

                if (personList != null) {

                    for (int i = 0; i < personList.size(); i++) {
//                    for (int j = 0; j < personList.get(i))
                        LogTools.info("mGetAllPersonnelInformation :" + i + personList.get(i).toString());
                        LogTools.info("mGetAllPersonnelInformation :" + i + personList.get(i).getId());

                        //                    System.out.println(personList.get(i));
                    }
                }

            }
        });

        mGetListPeopleDetectedBody.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<Person> personList = PersonApi.getInstance().getAllBodyList();
                if (personList != null) {

                    for (int i = 0; i < personList.size(); i++) {
                        LogTools.info("mGetListPeopleDetectedBody :" + i + personList.get(i).toString());
//                    System.out.println(personList.get(i));
                    }
                }
            }
        });

        mGetListPeopleDetectedFaces.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<Person> personList = PersonApi.getInstance().getAllFaceList(1);
                if (personList != null) {
                    for (int i = 0; i < personList.size(); i++) {
                        LogTools.info("mGetListPeopleDetectedFaces :" + i + personList.get(i).toString());
//                    System.out.println(personList.get(i));
                    }

                }
            }
        });

        mGetListPeopleDetectedCompleteFaces.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<Person> personList = PersonApi.getInstance().getCompleteFaceList();
                if (personList != null) {

                    for (int i = 0; i < personList.size(); i++) {
                        LogTools.info("mGetListPeopleDetectedCompleteFaces :" + i + personList.get(i).toString());
//                    System.out.println(personList.get(i));
                    }
                }
            }
        });

        mGetPersonFollowingFocus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Person person = PersonApi.getInstance().getFocusPerson();
                if (person != null) {
                    LogTools.info("mGetPersonFollowingFocus :" + person.toString());

                }
            }
        });

        mStartFollowFocus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findFocusFollow();
            }
        });

        mStopFollowFocus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RobotApi.getInstance().stopFocusFollow(0);
                handler.removeCallbacks(runnable);
            }
        });

        PersonListener listener = new PersonListener() {
            @Override
            public void personChanged() {
                super.personChanged();
                //when person changed, use "getAllPersons()" to get all people info in the robot's field of view
                findFocusFollow();
                System.out.println("personChanged");
                LogTools.info("personChanged");


            }
        };

//         set time interval
        handler.postDelayed(runnable, milliseconds);

        Runtime.getRuntime().addShutdownHook(new Thread()
        {
            @Override
            public void run()
            {
                handler.removeCallbacks(runnable);
                //do your stuff
            }
        });


    }


    public void findFocusFollow() {
        List<Person> personList = PersonApi.getInstance().getAllPersons();
        if (personList != null) {
            for (int i = 0; i < personList.size(); i++) {
                if (personList.get(i).getId() >= 0) {
                    List<Person> completeFaceList = PersonApi.getInstance().getCompleteFaceList();
                    focusFollow(completeFaceList.get(0).getId());
                    LogTools.info("mStartFollowFocus :" + completeFaceList.get(0).getId());

                }
            }
        }
    }

    public void focusFollow(int id) {
        RobotApi.getInstance().startFocusFollow(0, id, 2, 2, new ActionListener() {
            int count;

            @Override
            public void onStatusUpdate(int status, String data) {
                System.out.println("onStatusUpdate" + status);

                switch (status) {
                    case Definition.STATUS_TRACK_TARGET_SUCCEED:
                        //Follow the target successfully
                        System.out.println("successfully");
                        count = 0;
                        // Stop a repeating task like this.
                        handler.removeCallbacks(runnable);
//                        hello= MediaPlayer.create(this, R.raw.mssiam);
                        hello.start();

                        break;
                    case Definition.STATUS_GUEST_LOST:
                        //lost target
                        System.out.println("lost target");
                        RobotApi.getInstance().stopFocusFollow(0);
                        handler.postDelayed(runnable, milliseconds);
                        break;
                    case Definition.STATUS_GUEST_FARAWAY:
                        // target distance is greater than the set maximum distance
                        System.out.println("target distance is greater than the set maximum distance");
                        count++;
                        if (count >= 10) {
                            RobotApi.getInstance().stopFocusFollow(0);
                            handler.postDelayed(runnable, milliseconds);

                        }
                        break;
                    case Definition.STATUS_GUEST_APPEAR:
                        // target re-enter the set maximum distance
                        System.out.println("target re-enter the set maximum distance");

                        break;
                }
            }

            @Override
            public void onError(int errorCode, String errorString) {
                System.out.println("onError" + errorCode);
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
