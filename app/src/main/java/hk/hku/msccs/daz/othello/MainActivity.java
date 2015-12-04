/*
 * @author WANG Dazhi
 * @since 1st Nov, 2015
 * @version 1.0.0
 */
package hk.hku.msccs.daz.othello;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Stack;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = "MainActivity";
    public static final int EMPTY = -1;
    public static final int BLACK = 0;
    public static final int WHITE = 1;
    public static final int BLACK_HINT = 2;
    public static final int WHITE_HINT = 3;
    public static final int EMPTY_IMG = R.mipmap.transparent;
    public static final int BLACK_HINT_IMG = R.mipmap.black_chess_t;
    public static final int BLACK_IMG = R.mipmap.black_chess;
    public static final int WHITE_HINT_IMG = R.mipmap.white_chess_t;
    public static final int WHITE_IMG = R.mipmap.white_chess;
    public int mOthello[][];
    public int mImageViewIds[][];
    public int mCurrentPlayer;
    public int mOpponentPlayer;
    public Stack <int[][]> mOthelloStack;
    public boolean isBlackMovable;
    public boolean isWhiteMovable;
    public boolean isHintOn;
    private HashMap<Integer, Integer> mSoundMap;
    public String STR_WINNER = null;
    public int INT_WINNER_ICON;
    private ImageView mImageViews[][];
    private ImageView mCurrentTurnIV;
    private TextView mBlackNumTV;
    private TextView mWhiteNumTV;
    private SoundPool mSoundPool;
    private Chronometer mChronometer;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //noinspection ConstantConditions
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });



        mImageViewIds = new int[][]{{R.id.piece_00, R.id.piece_01, R.id.piece_02, R.id.piece_03,
                R.id.piece_04, R.id.piece_05, R.id.piece_06, R.id.piece_07},
                {R.id.piece_10, R.id.piece_11, R.id.piece_12, R.id.piece_13,
                        R.id.piece_14,R.id.piece_15, R.id.piece_16, R.id.piece_17},
                {R.id.piece_20, R.id.piece_21, R.id.piece_22, R.id.piece_23,
                        R.id.piece_24,R.id.piece_25, R.id.piece_26, R.id.piece_27},
                {R.id.piece_30, R.id.piece_31, R.id.piece_32, R.id.piece_33,
                        R.id.piece_34,R.id.piece_35, R.id.piece_36, R.id.piece_37},
                {R.id.piece_40, R.id.piece_41, R.id.piece_42, R.id.piece_43,
                        R.id.piece_44,R.id.piece_45, R.id.piece_46, R.id.piece_47},
                {R.id.piece_50, R.id.piece_51, R.id.piece_52, R.id.piece_53,
                        R.id.piece_54,R.id.piece_55, R.id.piece_56, R.id.piece_57},
                {R.id.piece_60, R.id.piece_61, R.id.piece_62, R.id.piece_63,
                        R.id.piece_64,R.id.piece_65, R.id.piece_66, R.id.piece_67},
                {R.id.piece_70, R.id.piece_71, R.id.piece_72, R.id.piece_73,
                        R.id.piece_74,R.id.piece_75, R.id.piece_76, R.id.piece_77}
        };

        initView();
        initOthello();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        isHintOn = false;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
            return true;
        }

        if (id == R.id.action_new) {
            new AlertDialog.Builder(MainActivity.this)
                    .setTitle("Othello")
                    .setMessage("Start a new game?")
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            initOthello();
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    }).show();
            return true;
        }
        if (id == R.id.action_retract) {
            retract();
            return true;
        }
        if (id == R.id.action_hint) {
            if (isHintOn) {
                item.setIcon(R.drawable.action_hint_off);
                isHintOn = false;
                scanOthello();
            } else {
                item.setIcon(R.drawable.action_hint_on);
                isHintOn = true;
                scanOthello();
            }
            return true;
        }
        /*if (id == R.id.action_settings) {
            return true;
        }*/
        return super.onOptionsItemSelected(item);
    }

    public void initView() {
        mChronometer = (Chronometer) findViewById(R.id.main_chronometer);
        mCurrentTurnIV = (ImageView) findViewById(R.id.content_turn_iv);
        mImageViews = new ImageView[8][8];
        for (int i = 0; i < 8; ++i) {
            for (int j = 0; j < 8; ++j) {
                mImageViews[i][j] = (ImageView) findViewById(mImageViewIds[i][j]);
            }
        }
        mBlackNumTV = (TextView) findViewById(R.id.black_num_tv);
        mWhiteNumTV = (TextView) findViewById(R.id.white_num_tv);

        mChronometer.setFormat("Time: %s");
    }

    public void initOthello() {
        mOthelloStack = new Stack<>();
        mOthello = new int[8][8];
        for (int i = 0; i < 8; ++i) {
            for (int j = 0; j < 8; ++j) {
                mOthello[i][j] = EMPTY;
            }
        }

        mOthello[3][4] = mOthello[4][3] = BLACK;
        mOthello[3][3] = mOthello[4][4] = WHITE;

        mCurrentPlayer = BLACK;
        mOpponentPlayer = WHITE;
        isBlackMovable = true;
        isWhiteMovable = true;

        mSoundMap = new HashMap<>();
        //noinspection deprecation
        mSoundPool = new SoundPool(10, AudioManager.STREAM_SYSTEM, 5);
        mSoundMap.put(1, mSoundPool.load(MainActivity.this, R.raw.piece, 0));

        mChronometer.setBase(SystemClock.elapsedRealtime());
        mChronometer.start();

        pushOthello();
        scanOthello();
    }

    public void retract () {
        //Log.e(TAG, "mOthelloStack size = " + mOthelloStack.size());
        if (!mOthelloStack.isEmpty()) {
            mOthelloStack.pop();
            if (!mOthelloStack.isEmpty()) {
                mOthello = mOthelloStack.peek();

                changeCurrentPlayer();
                scanOthello();
            } else {
                initOthello();
            }
        }
    }

    public int[] isUpAvailable (int x, int y) {
        int p;
        int q;
        if(x - 1 >= 0) {
            if (mOthello[x - 1][y] != EMPTY && mOthello[x - 1][y] == mOpponentPlayer) {
                for (int i = x - 1; i >= 0; --i) {
                    if (mOthello[i][y] == mCurrentPlayer) {
                        for (int j = x - 1; j > i; --j) {
                            if (mOthello[j][y] != mOpponentPlayer) {
                                return null;
                            }
                        }
                        p = i;
                        q = y;
/*                        Log.e(TAG,"getUp");
                        Log.e(TAG,"x = "+ x + " y = " + y);
                        Log.e(TAG, "p = " + p + " q = " + q);*/
                        return new int[] {p, q};
                    }
                }
            }
        }
        return null;
    }

    public int[] isDownAvailable (int x, int y) {
        int p;
        int q;
        if (x + 1 < 8) {
            if (mOthello[x + 1][y] != EMPTY && mOthello[x + 1][y] == mOpponentPlayer) {
                for (int i = x + 1; i < 8; ++i) {
                    if (mOthello[i][y] == mCurrentPlayer) {
                        for (int j = x + 1; j < i; ++j) {
                            if (mOthello[j][y] != mOpponentPlayer) {
                                return null;
                            }
                        }
                        p = i;
                        q = y;
/*                        Log.e(TAG,"getDown");
                        Log.e(TAG,"x = "+ x + " y = " + y);
                        Log.e(TAG, "p = " + p + " q = " + q);*/
                        return new int[] {p, q};
                    }
                }
            }
        }

        return null;
    }

    public int[] isLeftAvailable (int x, int y) {
        int p;
        int q;
        if (y - 1 >= 0) {
            if (mOthello[x][y - 1] != EMPTY && mOthello[x][y - 1] == mOpponentPlayer) {
                for (int i = y - 1; i >= 0; --i) {
                    if (mOthello[x][i] == mCurrentPlayer) {
                        for (int j = y - 1; j > i; --j) {
                            if (mOthello[x][j] != mOpponentPlayer) {
                                return null;
                            }
                        }
                        p = x;
                        q = i;
                       /* Log.e(TAG,"getLeft");
                        Log.e(TAG,"x = "+ x + " y = " + y);
                        Log.e(TAG, "p = " + p + " q = " + q);*/
                        return new int[] {p, q};
                    }
                }
            }
        }

        return null;
    }

    public int[] isRightAvailable (int x, int y) {
        int p;
        int q;
        if (y + 1 < 8) {
            if (mOthello[x][y + 1] != EMPTY && mOthello[x][y + 1] == mOpponentPlayer) {
                for (int i = y + 1; i < 8; ++i) {
                    if (mOthello[x][i] == mCurrentPlayer) {
                        for (int j = y + 1; j < i; ++j) {
                            if (mOthello[x][j] != mOpponentPlayer) {
                                return null;
                            }
                        }
                        p = x;
                        q = i;
                        /*Log.e(TAG,"getRight");
                        Log.e(TAG,"x = " + x + " y = " + y);
                        Log.e(TAG, "p = " + p + " q = " + q);*/
                        return new int[] {p, q};
                    }
                }
            }
        }

        return null;
    }

    public int[] isTopLeftAvailable (int x, int y) {
        int p;
        int q;
        if (x - 1 >= 0 && y - 1 >= 0) {
            if (mOthello[x - 1][y - 1] != EMPTY && mOthello[x - 1][y - 1] == mOpponentPlayer) {
                for (int i = x - 1, j = y - 1; i >= 0 && j >= 0; --i, --j) {
                    if (mOthello[i][j] == mCurrentPlayer) {
                        for (int k = x - 1, l = y - 1; k > i && l > j; --k, --l) {
                            if (mOthello[k][l] != mOpponentPlayer) {
                                return null;
                            }
                        }
                        p = i;
                        q = j;
                        /*Log.e(TAG,"getTopLeft");
                        Log.e(TAG,"x = "+ x + " y = " + y);
                        Log.e(TAG, "p = " + p + " q = " + q);*/
                        return new int[] {p, q};
                    }
                }
            }
        }

        return null;
    }

    public int[] isTopRightAvailable (int x, int y) {
        int p;
        int q;
        if (x - 1 >= 0 && y + 1 < 8) {
            if (mOthello[x - 1][y + 1] != EMPTY && mOthello[x - 1][y + 1] == mOpponentPlayer) {
                for (int i = x - 1, j = y + 1; i >= 0 && j < 8; --i, ++j) {
                    if (mOthello[i][j] == mCurrentPlayer) {
                        for (int k = x - 1, l = y + 1; k > i && l < j; --k, ++l) {
                            if (mOthello[k][l] != mOpponentPlayer) {
                                return null;
                            }
                        }
                        p = i;
                        q = j;
                       /* Log.e(TAG,"getTopRight");
                        Log.e(TAG,"x = "+ x + " y = " + y);
                        Log.e(TAG, "p = " + p + " q = " + q);*/
                        return new int[] {p, q};
                    }
                }
            }
        }

        return null;
    }

    public int[] isBottomLeftAvailable (int x, int y) {
        int p;
        int q;
        if (x + 1 < 8 && y - 1 >= 0) {
            if (mOthello[x + 1][y - 1] != EMPTY && mOthello[x + 1][y - 1] == mOpponentPlayer) {
                for (int i = x + 1, j = y - 1; i < 8 && j >= 0; ++i, --j) {
                    if (mOthello[i][j] == mCurrentPlayer) {
                        for (int k = x + 1, l = y - 1; k < i && l > j; ++k, --l) {
                            if (mOthello[k][l] != mOpponentPlayer) {
                                return null;
                            }
                        }
                        p = i;
                        q = j;
                        /*Log.e(TAG,"getBottomLeft");
                        Log.e(TAG,"x = "+ x + " y = " + y);
                        Log.e(TAG, "p = " + p + " q = " + q);*/
                        return new int[] {p, q};
                    }
                }
            }
        }

        return null;
    }

    public int[] isBottomRightAvailable (int x, int y) {
        int p;
        int q;
        if (x + 1 < 8 && y + 1 < 8) {
            if (mOthello[x + 1][y + 1] != EMPTY && mOthello[x + 1][y + 1] == mOpponentPlayer) {
                for (int i = x + 1, j = y + 1; i < 8 && j < 8; ++i, ++j) {
                    if (mOthello[i][j] == mCurrentPlayer) {
                        for (int k = x + 1, l = y + 1; k < i && l < j; ++k, ++l) {
                            if (mOthello[k][l] != mOpponentPlayer) {
                                return null;
                            }
                        }
                        p = i;
                        q = j;
                        /*Log.e(TAG,"getBottomRight");
                        Log.e(TAG,"x = "+ x + " y = " + y);
                        Log.e(TAG, "p = " + p + " q = " + q);*/
                        return new int[] {p, q};
                    }
                }
            }
        }

        return null;
    }

    public boolean checkAvailable (final int x, final int y) {
        final int mUpAvailable[] = isUpAvailable(x ,y);
        final int mDownAvailable[] = isDownAvailable(x, y);
        final int mLeftAvailable[] = isLeftAvailable(x, y);
        final int mRightAvailable[] = isRightAvailable(x, y);
        final int mTopLeftAvailable[] = isTopLeftAvailable(x, y);
        final int mTopRightAvailable[] = isTopRightAvailable(x, y);
        final int mBottomLeftAvailable[] = isBottomLeftAvailable(x, y);
        final int mBottomRightAvailable[] = isBottomRightAvailable(x, y);



        mImageViews[x][y].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSoundPool.play(mSoundMap.get(1), Float.valueOf("0.5"), Float.valueOf("0.5"), 0, 0, 1);

                if (mUpAvailable != null) {
                    flipUp(x, y, mUpAvailable[0], mUpAvailable[1]);
                }
                if (mDownAvailable != null) {
                    flipDown(x, y, mDownAvailable[0], mDownAvailable[1]);
                }
                if (mLeftAvailable != null) {
                    flipLeft(x, y, mLeftAvailable[0], mLeftAvailable[1]);
                }
                if (mRightAvailable != null) {
                    flipRight(x, y, mRightAvailable[0], mRightAvailable[1]);
                }
                if (mTopLeftAvailable != null) {
                    flipTopLeft(x, y, mTopLeftAvailable[0], mTopLeftAvailable[1]);
                }
                if (mTopRightAvailable != null) {
                    flipTopRight(x, y, mTopRightAvailable[0], mTopRightAvailable[1]);
                }
                if (mBottomLeftAvailable != null) {
                    flipBottomLeft(x, y, mBottomLeftAvailable[0], mBottomLeftAvailable[1]);
                }
                if (mBottomRightAvailable != null) {
                    flipBottomRight(x, y, mBottomRightAvailable[0], mBottomRightAvailable[1]);
                }

                if (mUpAvailable != null || mDownAvailable != null || mLeftAvailable != null
                        || mRightAvailable != null || mTopLeftAvailable != null || mTopRightAvailable != null
                        || mBottomLeftAvailable != null || mBottomRightAvailable != null) {
                    changeCurrentPlayer();
                    clearHint();

                    pushOthello();
                    scanOthello();
                    mImageViews[x][y].setClickable(false);
                }


            }
        });

        if (mUpAvailable == null && mDownAvailable == null && mLeftAvailable == null
                && mRightAvailable == null && mTopLeftAvailable == null && mTopRightAvailable == null
                && mBottomLeftAvailable == null && mBottomRightAvailable == null) {
            return  false;
        } else {
            mOthello[x][y] = mCurrentPlayer == BLACK ? BLACK_HINT : WHITE_HINT;
            return true;
        }

    }

    public void scanOthello () {
        if (!isHintOn) {
            clearHint();
        }
        int count = 0;
        for (int i = 0; i < 8; ++i) {
            for (int j = 0; j < 8; ++j) {
                if (mOthello[i][j] == EMPTY) {
                    if (checkAvailable(i, j)) {
                        ++ count;
                    }
                }
            }
        }
        //Log.e(TAG,count+"!!!!!!");

        if (count != 0) {
            if (mCurrentPlayer == BLACK) {
                isBlackMovable = true;
            } else {
                isWhiteMovable = true;
            }
        } else {
            if (mCurrentPlayer == BLACK) {
                isBlackMovable = false;
                changeCurrentPlayer();
            } else {
                isWhiteMovable = false;
                changeCurrentPlayer();

            }
        }

        updateOthello();


    }

    public void changeCurrentPlayer () {
        if (isFull()) {
            endOthello();
        } else {
            //clearHint();
            if (mCurrentPlayer == BLACK) {
                if (!isBlackMovable) {
                    Toast.makeText(MainActivity.this, "Black piece cannot move!", Toast.LENGTH_SHORT).show();
                    if (!isBlackMovable && !isWhiteMovable) {
                        endOthello();
                    } else {
                        mOpponentPlayer = BLACK;
                        mCurrentPlayer = WHITE;
                        mCurrentTurnIV.setImageResource(WHITE_IMG);
                        scanOthello();
                    }
                } else {
                    mOpponentPlayer = BLACK;
                    mCurrentPlayer = WHITE;
                    mCurrentTurnIV.setImageResource(WHITE_IMG);
                }

            } else {
                if (!isWhiteMovable) {
                    Toast.makeText(MainActivity.this, "White piece cannot move!", Toast.LENGTH_SHORT).show();
                    if (!isBlackMovable && !isWhiteMovable) {
                        endOthello();
                    } else {
                        mOpponentPlayer = WHITE;
                        mCurrentPlayer = BLACK;
                        mCurrentTurnIV.setImageResource(BLACK_IMG);
                        scanOthello();
                    }
                } else {
                    mOpponentPlayer = WHITE;
                    mCurrentPlayer = BLACK;
                    mCurrentTurnIV.setImageResource(BLACK_IMG);
                }
            }
        }
    }

    public void updateOthello() {
        if (!isHintOn) {
            clearHint();
        }
        for (int i = 0; i < 8; ++i) {
            for (int j = 0; j < 8; ++j) {
                //Log.e(TAG, mOthello[i][j]+"");
                switch (mOthello[i][j]) {

                    case BLACK :
                        mImageViews[i][j].setImageResource(BLACK_IMG);
                        break;
                    case BLACK_HINT:
                        mImageViews[i][j].setImageResource(BLACK_HINT_IMG);
                        break;
                    case WHITE :
                        mImageViews[i][j].setImageResource(WHITE_IMG);
                        break;
                    case WHITE_HINT :
                        mImageViews[i][j].setImageResource(WHITE_HINT_IMG);
                        break;
                    case EMPTY :
                        mImageViews[i][j].setImageResource(EMPTY_IMG);
                        break;


                }
            }
        }
        mBlackNumTV.setText(String.valueOf(getPieceCount(BLACK)));
        mWhiteNumTV.setText(String.valueOf(getPieceCount(WHITE)));
        Log.e(TAG, "------Updated!!!------");
    }

    public void flipUp (int x, int y, int p, int q) {
        Log.e(TAG, "flipUp");
        //Log.e(TAG,"x = " + x + " y = " + y + " p =  "+p+" q = "+q);
        for (int i = p; i <= x; ++i) {
            mOthello[i][y] = mOthello[p][q];//mCurrentPlayer;//mOthello[x][y];
        }
    }

    public void flipDown (int x, int y, int p, int q) {
        Log.e(TAG,"flipDown");
        for (int i = x; i <= p; ++i) {
            mOthello[i][y] = mOthello[p][q];//mCurrentPlayer;//mOthello[x][y];
        }

    }

    public void flipLeft (int x, int y, int p, int q) {
        Log.e(TAG,"flipLeft");
        for (int i = q; i <= y; ++i) {
            mOthello[x][i] = mOthello[p][q];//mCurrentPlayer;//mOthello[p][q];
        }
    }

    public void flipRight (int x, int y, int p, int q) {
        Log.e(TAG,"flipRight");
        for (int i = y; i <= q; ++i) {
            mOthello[x][i] = mOthello[p][q];//mCurrentPlayer;//mOthello[x][y];
        }
    }

    public void flipTopLeft (int x, int y, int p, int q) {
        Log.e(TAG,"flipTopLeft");
        for (int i = p, j = q; i <= x && j <= y; ++i, ++j) {
            mOthello[i][j] = mOthello[p][q];//mCurrentPlayer;//mOthello[x][y];
        }
    }

    public void flipTopRight (int x, int y, int p, int q) {
        Log.e(TAG,"flipTopRight");
        for (int i = p, j = q; i <= x && j >= y; ++i, --j) {
            mOthello[i][j] = mOthello[p][q];//mCurrentPlayer;//mOthello[x][y];
        }
    }

    public void flipBottomLeft (int x, int y, int p, int q) {
        Log.e(TAG,"flipBottomLeft");
        for (int i = x, j = y; i <= p && j >= q; ++i, --j) {
            mOthello[i][j] = mOthello[p][q];//mCurrentPlayer;//mOthello[x][y];
        }
    }

    public void flipBottomRight (int x, int y, int p, int q) {
        Log.e(TAG,"flipBottomRight");
        for (int i = x, j = y; i <= p && j <= q; ++i, ++j) {
            mOthello[i][j] = mOthello[p][q];//mCurrentPlayer;//mOthello[x][y];
        }
    }

    public void clearHint() {
        for (int i = 0; i < 8; ++i) {
            for (int j = 0; j < 8; ++j) {
                if (mOthello[i][j] == BLACK_HINT || mOthello[i][j] == WHITE_HINT) {
                    mOthello[i][j] = EMPTY;
                }

            }
        }

    }

    public void endOthello () {
        judgeOthello();
        new AlertDialog.Builder(MainActivity.this)
                .setTitle("Game Over")
                .setMessage(STR_WINNER)
                .setIcon(INT_WINNER_ICON)
                .setPositiveButton("New Game", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        initOthello();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).show();
    }

    public int getPieceCount(int type) {
        int count = 0;
        for (int i = 0; i < 8; ++i) {
            for (int j = 0; j < 8; ++j) {
                if (mOthello[i][j] == type) {
                    ++count;
                }
            }

        }
        return count;
    }

    public boolean isFull() {
        for (int i = 0; i < 8; ++i) {
            for (int j = 0; j < 8; ++j) {
                if (mOthello[i][j] == EMPTY || mOthello[i][j] == BLACK_HINT || mOthello[i][j] == WHITE_HINT) {
                    return false;
                }
            }
        }
        return true;


    }

    public void pushOthello() {
        int mOth[][] = new int[8][8];
        for (int i = 0; i < 8; ++i) {
            System.arraycopy(mOthello[i], 0, mOth[i], 0, 8);
        }
        mOthelloStack.push(mOth);
        Log.e(TAG, "=====End pushOthello====");
    }

    public String judgeOthello() {
        if (getPieceCount(BLACK) < getPieceCount(WHITE)) {
            STR_WINNER = "White piece won!";
            INT_WINNER_ICON = R.mipmap.white_chess;

        }
        if (getPieceCount(WHITE) < getPieceCount(BLACK)) {
            STR_WINNER = "Black piece won!";
            INT_WINNER_ICON = R.mipmap.black_chess;
        }
        if (getPieceCount(WHITE) == getPieceCount(BLACK)) {
            STR_WINNER = "Tie game!";
            INT_WINNER_ICON = 0;
        }
        return STR_WINNER;
    }

}
