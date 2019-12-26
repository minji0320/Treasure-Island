package com.example.treasureisland;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.EmbossMaskFilter;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;

public class GameView extends SurfaceView implements SurfaceHolder.Callback {
    private GameThread mThread;
    SurfaceHolder mHolder;
    Context mContext;
    Board board;
    public boolean isGameOver;
    public int score;


    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        SurfaceHolder holder = getHolder();
        holder.addCallback(this);

        mHolder = holder;
        mContext = context;
        mThread = new GameThread(holder);

        board = new Board(this,8,10,15);
        board.set();

        this.isGameOver = false;
        this.score = 1000;
        setFocusable(true);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        mThread.setRunning(true);
        mThread.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        boolean retry = true;

        mThread.setRunning(false);
        while (retry) {
            try {
                mThread.join();
                retry = false;
            } catch (InterruptedException e) {

            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(event.getAction() == MotionEvent.ACTION_DOWN && !this.mThread.isWait) {
            synchronized (mHolder) {
                int tx = (int) event.getX();
                int ty = (int) event.getY();
                int hintX = (getWidth()-board.hint.getWidth())/2;
                int hintY = 1200;
                if (tx >= hintX && tx <= (hintX + board.hint.getWidth()) && ty >= hintY && ty <= (hintY + board.hint.getHeight())
                        && board.hintCount!=0) {    // hint 클릭 시
                    Toast.makeText(mContext,"보물과의 거리는 " + board.distance + "입니다.", Toast.LENGTH_SHORT).show();
                    board.hintCount--;
                }
                int tCol = (tx - 140) / 100;
                int tRow = (ty - 200) / 100;
                if ((tCol == board.meCol && tRow == board.meRow +1)             // 오른쪽 이동
                        || (tCol == board.meCol + 1 && tRow == board.meRow)     // 아래쪽 이동
                        || (tCol == board.meCol && tRow == board.meRow - 1)     // 왼쪽 이동
                        || (tCol == board.meCol - 1 && tRow == board.meRow)) {  // 위쪽 이동
                    board.reveal(tCol,tRow);
                }
            }
        }
        return true;
    }

    public void StopGame() {
        mThread.StopThread();
    }

    public void PauseGame() {
        mThread.PauseNResume(true);
    }

    public void ResumeGame() {
        mThread.PauseNResume(false);
    }

    public void RestartGame() {
        mThread.StopThread();
        mThread = null;
        mThread = new GameThread(mHolder);
        surfaceCreated(mHolder);

        board = new Board(this,8,5,10);
        board.set();
    }

    public class GameThread extends Thread {
        private boolean mRun = false;
        private boolean isWait = false;
        private SurfaceHolder mSurfaceHolder;

        public GameThread(SurfaceHolder surfaceHolder) {
            mSurfaceHolder = surfaceHolder;
        }

        public void run() {
            while (mRun) {
                Canvas c = null;
                try{
                    c = mSurfaceHolder.lockCanvas(null);
                    c.drawColor(Color.WHITE);
                    synchronized (mSurfaceHolder) {
                        draw(c,board);
                        if (isGameOver) {
                            Paint paint = new Paint();
                            paint.setColor(Color.WHITE);
                            paint.setAlpha(80);
                            if (board.getHeart() <= 0) {    // Game Over!!
                                c.drawColor(paint.getColor());
                                Bitmap over = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.over);
                                c.drawBitmap(over,(getWidth()-over.getWidth()+20)/2, 820,null);
                                isGameOver = !isGameOver;
                                mRun = false;
                                isWait = true;
                            } else {    // Game Clear!!
                                if(score > board.getRevealedCount())
                                    score = board.getRevealedCount();
                                c.drawColor(paint.getColor());
                                Bitmap clear = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.clear);
                                c.drawBitmap(clear,(getWidth()-clear.getWidth()+20)/2, 820,null);
                                isGameOver = !isGameOver;
                                mRun = false;
                                isWait = true;
                            }

                        }
                    }
                } finally {
                    if(c != null) {
                        mSurfaceHolder.unlockCanvasAndPost(c);
                    }
                }
                synchronized (this){
                    if(isWait) {
                        try {
                            sleep(500);
                            if (board.grid[board.meCol][board.meRow].isFood) {
                                board.grid[board.meCol][board.meRow].isFood = false;
                                this.isWait = false;
                            } else if (board.grid[board.meCol][board.meRow].isAnimal) {
                                board.grid[board.meCol][board.meRow].isAnimal = false;
                                this.isWait = false;
                            } else {
                                wait();
                            }
                        } catch (Exception e) {

                        }
                    }
                }
            }
        }

        public void draw(Canvas canvas, Board board) {
            // 타일 엠보싱
            Paint paint1 = new Paint();
            EmbossMaskFilter emboss = new EmbossMaskFilter(new float[] {1, 1, 1}, 0.5f, 1, 1);
            paint1.setMaskFilter(emboss);

            // 이동 횟수 표시
            Paint paint2 = new Paint();
            paint2.setTextSize(100);
            paint2.setTextAlign(Paint.Align.CENTER);
            Typeface tf = Typeface.createFromAsset(getResources().getAssets(),"cookie.ttf");
            paint2.setTypeface(tf);
            canvas.drawText("DAY " + board.getRevealedCount(), getWidth()/2, 140, paint2);

            for(int i = 0; i < 8; i++) {    // 보드 그리기
                for(int j = 0; j < 8; j++) {
                    canvas.drawBitmap(board.grid[i][j].image, board.grid[i][j].x, board.grid[i][j].y, paint1);
                }
            }

            if (board.grid[board.meCol][board.meRow].isFood) {
                canvas.drawBitmap(board.food, (getWidth()-500) / 2, 350, null);
                this.isWait = true;
            }
            if (board.grid[board.meCol][board.meRow].isAnimal) {
                canvas.drawBitmap(board.animal, (getWidth()-500) / 2, 350, null);
                this.isWait = true;
            }
            if (board.grid[board.meCol][board.meRow].isTreasure) {
                canvas.drawBitmap(board.treasure, (getWidth()-500) / 2, 350, null);
            }

            for(int i = 0; i < board.getHeart(); i++) {     // heart 그리기
                canvas.drawBitmap(board.heart, board.grid[i][7].x, board.grid[i][7].y + 120, null);
            }
            if (board.hintCount != 0)       // hint 그리기
                canvas.drawBitmap(board.hint, (getWidth()-board.hint.getWidth())/2, 1200, null);
            else
                canvas.drawBitmap(board.hint2, (getWidth()-board.hint.getWidth())/2, 1200, null);
        }

        public void StopThread() {
            mRun = false;
            synchronized (this) {
                this.notify();
            }
        }

        public void PauseNResume(boolean wait) {
            isWait = wait;
            synchronized (this) {
                this.notify();
            }
        }

        public void setRunning(boolean b) {
            mRun = b;
        }
    }
}
