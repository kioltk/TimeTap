package me.jesuscodes.timetap;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;


/**
 * A placeholder fragment containing a simple view.
 */
public class GameFragment extends Fragment {

    private static final String TAG = "GameFragment";
    private GameStatus gameStatus;
    private int score = 0;
    private View.OnTouchListener gameTapListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    if (currentTick == 0) {
                        score++;
                        scoreView.setText("Score: " + score);
                        if(score%4>0)
                            currentTickerRange++;
                        curretTickPeriod = (long) (curretTickPeriod / 1.07);
                        Toast.makeText(getActivity(), "GOTYA", Toast.LENGTH_SHORT).show();

                    }
                    Log.d(TAG, "Tap time: " + System.currentTimeMillis() + " gameTicker " + currentTick);
                    break;
            }
            return false;
        }
    };
    private View rootView;
    private TextView timerView;
    private TextView adviceView;
    private TextView debugView;
    private long currentTick;
    private long curretTickPeriod = 300;
    private long currentTickerRange = 5;
    private TextView scoreView;

    public GameFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        rootView = view;
        timerView = (TextView) view.findViewById(R.id.timer);
        adviceView = (TextView) view.findViewById(R.id.advice);
        debugView = (TextView) view.findViewById(R.id.debug);
        scoreView = (TextView) view.findViewById(R.id.score);
        if (!BuildConfig.DEBUG) {
            debugView.setVisibility(View.GONE);
        }
        gameStatus = GameStatus.Prepare;
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                view.setOnClickListener(null);
                timerView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        timerView.setText("READY?");
                        timerView.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                timerView.setText("GO");
                                timerView.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        startGame();
                                    }
                                }, 500);
                            }
                        }, 500);
                    }
                }, 500);
            }
        });
    }

    private void startGame() {
        rootView.setOnClickListener(null);
        rootView.setOnTouchListener(gameTapListener);
        new Thread(new Runnable() {
            @Override
            public void run() {
                Thread.currentThread().setPriority(Thread.MIN_PRIORITY);
                int currentSecondTicks = 0;
                long lastThreadSecondTick = System.currentTimeMillis();
                long lastGameTickTime = System.currentTimeMillis();
                while (true) {
                    if (BuildConfig.DEBUG) {
                        currentSecondTicks++;
                        if (lastThreadSecondTick < System.currentTimeMillis() - 999) {
                            lastThreadSecondTick = System.currentTimeMillis();
                            final int secondTicks = currentSecondTicks;
                            currentSecondTicks = 0;
                            debugView.post(new Runnable() {
                                @Override
                                public void run() {
                                    debugView.setText(secondTicks + " cycles per second");
                                }
                            });
                        }
                    }
                    if (lastGameTickTime < System.currentTimeMillis() - curretTickPeriod) {
                        lastGameTickTime = System.currentTimeMillis();
                        currentTick++;
                        if (currentTick > currentTickerRange) {
                            currentTick = 0;
                        }
                        timerView.post(new Runnable() {
                            @Override
                            public void run() {
                                timerView.setText(formatTimer());
                            }
                        });
                    }

                }
            }
        }).start();
    }

    private String formatTimer() {
        if (currentTick>9) {
            return ""+currentTick;
        }
        return "0"+currentTick;
    }
}
