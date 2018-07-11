package layout;


import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.android.lab07.R;

import java.util.Random;

public class MyFragment extends Fragment {
    private static final String ARG_PARAM1 = "LayoutID";
    private @LayoutRes int layout;
    private FloatingActionButton mRestart;
    private TableLayout mTable;
    private TextView win, lose, topRecord, youRecord;
    private View mView;
    private int cnt_open;
    private long recordTime, startTime, topTime;

    private interface Run {
        public abstract void method(@IdRes int id);
    }


    public MyFragment() {
        // Required empty public constructor
    }

    public static MyFragment newInstance(@LayoutRes int param1) {
        MyFragment fragment = new MyFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            layout = getArguments().getInt(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(layout, container, false);


        if (layout == R.layout.fragment_game) {
            mRestart = (FloatingActionButton) mView.findViewById(R.id.restart);
            mTable = (TableLayout) mView.findViewById(R.id.minefields);
            win = (TextView) mView.findViewById(R.id.winer);
            lose = (TextView) mView.findViewById(R.id.looser);
            topRecord = (TextView) mView.findViewById(R.id.text_topRecord);
            youRecord = (TextView) mView.findViewById(R.id.text_youRecord);
            mRestart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    restart(v);
                }
            });
            topTime = 12 * 60 * 60;
            restart(mRestart);
        }

        return mView;
    }

    private class Show implements Run {
        @Override
        public void method(@IdRes int id) {
            TextView tv;
            tv = (TextView) mView.findViewById(id);
            open(tv);
        }
    }

    private class PlusOne implements Run {
        @Override
        public void method(@IdRes int id) {
            TextView tv;
            int num;
            tv = (TextView) mView.findViewById(id);
            if (!tv.getText().equals("B")) {
                num = Integer.parseInt(tv.getText().toString());
                num += 1;
                tv.setText("" + num);
            }
        }
    }

    public void restart(View v) {
        TextView item;
        TableRow row;
        Random random = new Random();
        PlusOne plus = new PlusOne();
        int[] boom = {random.nextInt(25), random.nextInt(25)};

        while (boom[0] == boom[1]) {
            boom[0] = random.nextInt(25);
        }

        for (int i = 0; i < 5; ++i) {
            row = (TableRow) mTable.getChildAt(i);
            for (int j = 0; j < 5; ++j) {
                item = (TextView) row.getChildAt(j);
                item.setText(R.string.zero);
                item.setTextColor(ContextCompat.getColor(getActivity(), R.color.colorAccent));
                //item.setTextColor(Color.parseColor("#ffffff"));
                item.setBackgroundResource(R.drawable.before);
                item.setElevation(10);
                item.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        open(v);
                    }
                });
            }
        }
        for (int i = 0; i < 5; ++i) {
            row = (TableRow) mTable.getChildAt(i);
            for (int j = 0; j < 5; ++j) {
                item = (TextView) row.getChildAt(j);
                if ((i == boom[0] % 5) && (j == boom[0] / 5) || (i == boom[1] % 5) && (j == boom[1] / 5)) {
                    item.setText(R.string.bomb);
                    surround(j, i, plus);
                }
            }
        }
        win.setVisibility(View.INVISIBLE);
        lose.setVisibility(View.INVISIBLE);
        topRecord.setVisibility(View.INVISIBLE);
        youRecord.setVisibility(View.INVISIBLE);
        cnt_open = 0;
        startTime = System.currentTimeMillis();
        mRestart.hide();
    }

    private void surround(int x, int y, Run run) {
        int[] index = {-1, -1, -1, 0, 1, 1, 1, 0};

        for (int k = 0; k < 8; ++k) {
            if ((x + index[k] >= 0) && (x + index[k] < 5) && (y + index[(k + 6) % 8] >= 0) && (y + index[(k + 6) % 8] < 5)) {
                run.method(getResources().getIdentifier("textView" + ((x + index[k]) + (y + index[(k + 6) % 8]) * 5), "id", getActivity().getPackageName()));
            }
        }
    }

    private void open (View vobj) {
        TextView flip  = (TextView) vobj;
        Show mShow = new Show();
        if (flip.getElevation() != 0) {
            flip.setElevation(0);

            if (flip.getText().equals("0")) {
                flip.setBackgroundResource(R.color.colorOpen);
                int index = Integer.parseInt(flip.getTag().toString());
                surround(index % 5, index / 5, mShow);
                flip.setTextColor(ContextCompat.getColor(getActivity(), R.color.colorOpen));
            }
            else {
                flip.setBackgroundResource(R.color.colorOpen);
                flip.setTextColor(Color.parseColor("#ffffff"));
                if (flip.getText().equals("B")) {
                    TextView item;
                    TableRow row;
                    for (int i = 0; i < 5; ++i) {
                        row = (TableRow) mTable.getChildAt(i);
                        for (int j = 0; j < 5; ++j) {
                            item = (TextView) row.getChildAt(j);
                            item.setTextColor(Color.parseColor("#ffffff"));
                            item.setBackgroundResource(R.color.colorOpen);
                            if (item.getText().equals("B")) {
                                item.setBackgroundResource(R.color.colorBomb);
                            }
                        }
                    }
                    flip.setBackgroundResource(R.color.colorBomb);
                    lose.setVisibility(View.VISIBLE);
                    mRestart.show();
                    return;
                }
            }

            if (++cnt_open == 23) {
                mRestart.show();
                recordTime = (System.currentTimeMillis() - startTime) / 1000;
                if (recordTime < topTime) {
                    topTime = recordTime;
                }
                topRecord.setText("Top: " + "" + topTime + "Sec ");
                youRecord.setText("You: " + "" + recordTime + "Sec");
                win.setVisibility(View.VISIBLE);
                topRecord.setVisibility(View.VISIBLE);
                youRecord.setVisibility(View.VISIBLE);
                TextView item;
                TableRow row;
                for (int i = 0; i < 5; ++i) {
                    row = (TableRow) mTable.getChildAt(i);
                    for (int j = 0; j < 5; ++j) {
                        item = (TextView) row.getChildAt(j);
                        item.setTextColor(Color.parseColor("#ffffff"));
                        item.setBackgroundResource(R.color.colorOpen);
                        if (item.getText().equals("B")) {
                            item.setBackgroundResource(R.color.colorWin);
                        }
                    }
                }
            }
        }
    }

}
