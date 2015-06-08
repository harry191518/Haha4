package com.rlai.haha4;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.*;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.util.DisplayMetrics;


public class MainActivity extends ActionBarActivity {
    private Chessing Game;
    public static int width;
    public static int length;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        DisplayMetrics monitorsize =new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(monitorsize);

        width = monitorsize.widthPixels;
        length = monitorsize.heightPixels;

        Game = new Chessing(this);
        setContentView(Game);//将view视图放到Activity中显示
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public class Chessing extends View {

        private float BoardSX = 40; //棋盤左上
        private float BoardSY = (length - width) / 2;
        private float spacing = (width - 80) / 14;
        private float BoardX[] = new float[15];
        private float BoardY[] = new float[15];
        private int Chess[][] = new int[15][15];
        private int point[][] = new int[15][15];

        private float mov_x;
        private float mov_y;
        private boolean who = true; //黑先
        private Paint paint;
        private Canvas canvas;
        private Bitmap bitmap;
        //private int blcolor;

        public Chessing(Context context) { //畫棋盤
            super(context);

            paint = new Paint(Paint.DITHER_FLAG);
            bitmap = Bitmap.createBitmap(480, 854, Bitmap.Config.ARGB_8888);
            canvas = new Canvas();
            canvas.setBitmap(bitmap);

            paint.setStyle(Style.FILL); //棋盤顏色
            paint.setColor(Color.rgb(233, 171, 100));
            canvas.drawRect(BoardSX, BoardSY, BoardSX + spacing * 14, BoardSY + spacing * 14, paint);

            paint.setStrokeWidth(3);
            paint.setColor(Color.BLACK);
            paint.setAntiAlias(true);

            for(int i = 0; i < 15; i++) { //畫格子
                canvas.drawLine(BoardSX, BoardSY + spacing * i, BoardSX + spacing * 14, BoardSY + spacing * i, paint);
                canvas.drawLine(BoardSX + spacing * i, BoardSY, BoardSX + spacing * i, BoardSY + spacing * 14, paint);
                BoardX[i] = BoardSX + spacing * i;
                BoardY[i] = BoardSY + spacing * i;
            }

            paint.setStyle(Style.FILL);
            SetPoint();
        }

        // set chessboard priority
        public void SetPoint() {
            for(int i = 0; i < 15; i++)
                for(int j = 0; j < 15; j++) {
                    int a = (i <= (14 - i)) ? i : (14 - i);
                    int b = (j <= (14 - j)) ? j : (14 - j);
                    point[i][j] = (a <= b) ? a + 1 : b + 1;
                }
        }

        @Override
        protected void onDraw(Canvas canvas) {
            canvas.drawBitmap(bitmap, 0, 0, null);
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                int check = 0;
                mov_x = event.getX();
                mov_y = event.getY();

                for(int i = 0; i < 15; i++)
                    for(int j = 0; j < 15; j++)
                        if (Math.abs(BoardX[i] - mov_x) < 10 && Math.abs(BoardY[j] - mov_y) < 10) {
                            if(Chess[i][j] != 0) break;
                            check = 1;
                            Chess[i][j] = (who) ? 1 : 2;
                            mov_x = BoardX[i];
                            mov_y = BoardY[j];
                            break;
                        }

                if(check == 1) {
                    if(who) paint.setColor(Color.BLACK);
                    else paint.setColor(Color.WHITE);
                    canvas.drawCircle(mov_x, mov_y, spacing / 2 - 1, paint);
                    invalidate();
                    who = !who;

                    if(StandardWin() == 1) {
                        paint.setColor(Color.BLACK);
                        canvas.drawCircle(20, 20, spacing / 2 - 1, paint);
                        invalidate();
                    }
                    else if(StandardWin() == 2) {
                        paint.setColor(Color.WHITE);
                        canvas.drawCircle(20, 20, spacing / 2 - 1, paint);
                        invalidate();
                    }

                    StandardAI();
                }
            }
            mov_x = event.getX();
            mov_y = event.getY();
            return true;
        }

        public int StandardWin() {
            int win;

            for(int i = 0; i < 15; i++)
                for(int j = 0; j < 15; j++) {
                    win = Chess[i][j];
                    if ((i < 2 || i >= 13) && (j < 2 || j >= 13) || win == 0) ;
                    else if ((i < 2 || i >= 13) && j >= 2 && j < 13) {
                        if (Chess[i][j-1] == win && Chess[i][j-2] == win &&
                                Chess[1][j+1] == win && Chess[i][j+2] == win)
                            return win;
                    } else if ((j < 2 || j >= 13) && i >= 2 && i < 13) {
                        if (Chess[i-1][j] == win && Chess[i-2][j] == win &&
                                Chess[i+1][j] == win && Chess[i+2][j] == win)
                            return win;
                    }
                    else {
                        if (Chess[i-1][j] == win && Chess[i-2][j] == win &&
                                Chess[i+1][j] == win && Chess[i+2][j] == win)
                            return win;
                        if (Chess[i][j-1] == win && Chess[i][j-2] == win &&
                                Chess[i][j+1] == win && Chess[i][j+2] == win)
                            return win;
                        if (Chess[i-1][j-1] == win && Chess[i-2][j-2] == win &&
                                Chess[i+1][j+1] == win && Chess[i+2][j+2] == win)
                            return win;
                        if (Chess[i-1][j+1] == win && Chess[i-2][j+2] == win &&
                                Chess[i+1][j-1] == win && Chess[i+2][j-2] == win)
                            return win;
                    }
                }

            return 0;
        }

        public void StandardAI() {
            int x = 7, y = 7;
            int p, max = 0;

            for(int i = 0; i < 15; i++)
                for(int j = 0; j < 15; j++) {
                    if(Chess[i][j] != 0) continue;
                    p = point[i][j];
                    p += two(i, j, who);
                    p += three(i, j, who);
                    p += four(i, j, who);
                    p += five(i, j, who);
                    p += defendfour(i, j, who);
                    p += defendfive(i, j, who);
                    if(p > max){
                        x = i;
                        y = j;
                        max = p;
                    }
                }

            Chess[x][y] = (who) ? 1 : 2;
            if(who) paint.setColor(Color.BLACK);
            else paint.setColor(Color.WHITE);
            canvas.drawCircle(BoardX[x], BoardY[y], spacing / 2 - 1, paint);
            invalidate();
            who = !who;

            if(StandardWin() == 1) {
                paint.setColor(Color.BLACK);
                canvas.drawCircle(20, 20, spacing / 2 - 1, paint);
                invalidate();
            }
            else if(StandardWin() == 2) {
                paint.setColor(Color.WHITE);
                canvas.drawCircle(20, 20, spacing / 2 - 1, paint);
                invalidate();
            }
        }

        public int two(int x, int y, boolean who) {
            int a = (who) ? 1 : 2;
            int b = (who) ? 2 : 1;
            int p = 0;

            for(int i = -1; i <= 1; i++)
                for(int j = -1; j <= 1; j++) {
                    if(x + 3 * i < 0 || y + 3 * j < 0 || x + 3 * i > 14 || y + 3 * j > 14
                       || x - 2 * i < 0 || y - 2 * j < 0 || x - 2 * i > 14 || y - 2 * j > 14) ;
                    else {
                        if(Chess[x + i][y + j] == a && Chess[x + 2 * i][y + 2 * j] == 0
                           && Chess[x + 3 * i][y + 3 * j] == 0) {
                            if(Chess[x - i][y - j] != b && Chess[x - 2 * i][y - 2 * j] != b)
                                p += 3;
                            if(Chess[x - i][y - j] != b && Chess[x - 2 * i][y - 2 * j] == b)
                                p += 2;
                        }
                        if(Chess[x + i][y + j] == 0 && Chess[x + 2 * i][y + 2 * j] == a
                           && Chess[x + 3 * i][y + 3 * j] == 0) {
                            if(Chess[x - i][y - j] != b && Chess[x - 2 * i][y - 2 * j] != b)
                                p += 2;
                            if(Chess[x - i][y - j] != b && Chess[x - 2 * i][y - 2 * j] == b)
                                p += 1;
                        }
                    }
                }

            return p;
        }

        public int three(int x, int y, boolean who) {
            int a = (who) ? 1 : 2;
            int b = (who) ? 2 : 1;
            int p = 0;

            for(int i = -1; i <= 1; i++)
                for(int j = -1; j <= 1; j++) {
                    if(x + 4 * i < 0 || y + 4 * j < 0 || x + 4 * i > 14 || y + 4 * j > 14
                       || x - 2 * i < 0 || y - 2 * j < 0 || x - 2 * i > 14 || y - 2 * j > 14) ;
                    else {
                        if(Chess[x + i][y + j] == a && Chess[x + 2 * i][y + 2 * j] == a) {
                            if(Chess[x + 3 * i][y + 3 * j] == 0 && Chess[x + 4 * i][y + 4 * j] == 0
                               && Chess[x - i][y - j] == 0 && Chess[x - 2 * i][y - 2 * j] == 0)
                                p += 8;
                            if(Chess[x + 3 * i][y + 3 * j] == 0 && Chess[x + 4 * i][y + 4 * j] == 0
                               && Chess[x - i][y - j] == 0 && Chess[x - 2 * i][y - 2 * j] == b)
                                p += 4;
                            if(Chess[x + 3 * i][y + 3 * j] == 0 && Chess[x + 4 * i][y + 4 * j] == b
                               && Chess[x - i][y - j] == 0 && Chess[x - 2 * i][y - 2 * j] == 0)
                                p += 4;
                        }
                        if(Chess[x + i][y + j] == 0 && Chess[x + 2 * i][y + 2 * j] == a
                           && Chess[x + 3 * i][y + 3 * j] == a) {
                            if(Chess[x + 4 * i][y + 4 * j] == 0 && Chess[x - i][y - j] == 0)
                                p += 5;
                        }
                        if(Chess[x + i][y + j] == a && Chess[x + 2 * i][y + 2 * j] == 0
                           && Chess[x + 3 * i][y + 3 * j] == a) {
                            if(Chess[x + 4 * i][y + 4 * j] == 0 && Chess[x - i][y - j] == 0)
                                p += 5;
                        }
                    }
                }

            return p;
        }

        public int four(int x, int y, boolean who) {
            int a = (who) ? 1 : 2;
            int p = 0;

            for(int i = -1; i <= 1; i++)
                for(int j = -1; j <= 1; j++) {
                    if(x + 4 * i < 0 || y + 4 * j < 0 || x + 4 * i > 14 || y + 4 * j > 14
                            || x - 2 * i < 0 || y - 2 * j < 0 || x - 2 * i > 14 || y - 2 * j > 14) ;
                    else {
                        if(Chess[x + i][y + j] == a && Chess[x + 2 * i][y + 2 * j] == a
                           && Chess[x + 3 * i][y + 3 * j] == a) {
                            if(Chess[x + 4 * i][y + 4 * j] == 0 || Chess[x - i][y - j] == 0)
                                p += 25;
                        }
                        if(Chess[x + i][y + j] == a && Chess[x + 2 * i][y + 2 * j] == a
                           && Chess[x - i][y - j] == a) {
                            if(Chess[x + 3 * i][y + 3 * j] == 0 || Chess[x - 2 * i][y - 2 * j] == 0)
                                p += 25;
                        }
                        if(Chess[x + i][y + j] == 0 && Chess[x + 2 * i][y + 2 * j] == a
                           && Chess[x + 3 * i][y + 3 * j] == a && Chess[x + 4 * i][y + 4 * j] == a) {
                            p += 20;
                        }
                        if(Chess[x + i][y + j] == 0 && Chess[x + 2 * i][y + 2 * j] == a
                           && Chess[x + 3 * i][y + 3 * j] == a && Chess[x - i][y - j] == a) {
                            p += 20;
                        }
                        if(Chess[x + i][y + j] == a && Chess[x + 2 * i][y + 2 * j] == a
                           && Chess[x - i][y - j] == 0 && Chess[x - 2 * i][y - 2 * j] == a) {
                            p += 20;
                        }
                    }
                }

            return p;
        }

        public int five(int x, int y, boolean who) {
            int a = (who) ? 1 : 2;
            int p = 0;

            for(int i = -1; i <= 1; i++)
                for(int j = -1; j <= 1; j++) {
                    if(x + 4 * i < 0 || y + 4 * j < 0 || x + 4 * i > 14 || y + 4 * j > 14
                            || x - 2 * i < 0 || y - 2 * j < 0 || x - 2 * i > 14 || y - 2 * j > 14) ;
                    else {
                        if(Chess[x + i][y + j] == a && Chess[x + 2 * i][y + 2 * j] == a
                           && Chess[x + 3 * i][y + 3 * j] == a && Chess[x + 4 * i][y + 4 * j] == a) {
                            p += 150;
                        }
                        if(Chess[x + i][y + j] == a && Chess[x + 2 * i][y + 2 * j] == a
                           && Chess[x + 3 * i][y + 3 * j] == a && Chess[x - i][y - j] == a) {
                            p += 150;
                        }
                        if(Chess[x + i][y + j] == a && Chess[x + 2 * i][y + 2 * j] == a
                           && Chess[x - i][y - j] == a && Chess[x - 2 * i][y - 2 * j] == a) {
                            p += 150;
                        }
                    }
                }

            return p;
        }

        public int defendfour(int x, int y, boolean who) {
            int a = (who) ? 2 : 1;
            int b = (who) ? 1 : 2;
            int p = 0;

            for(int i = -1; i <= 1; i++)
                for(int j = -1; j <= 1; j++) {
                    if(x + 4 * i < 0 || y + 4 * j < 0 || x + 4 * i > 14 || y + 4 * j > 14
                            || x - 2 * i < 0 || y - 2 * j < 0 || x - 2 * i > 14 || y - 2 * j > 14) ;
                    else {
                        if(Chess[x + i][y + j] == a && Chess[x + 2 * i][y + 2 * j] == a
                           && Chess[x + 3 * i][y + 3 * j] == a && Chess[x + 4 * i][y + 4 * j] == 0) {
                            if(Chess[x - i][y - j] == b)
                                p += 5;
                            else
                                p += 22;
                        }
                        if(Chess[x + i][y + j] == a && Chess[x + 2 * i][y + 2 * j] == a
                           && Chess[x - i][y - j] == a) {
                            if(Chess[x + 3 * i][y + 3 * j] == 0 || Chess[x - 2 * i][y - 2 * j] == 0)
                                p += 18;
                        }
                        if(Chess[x + i][y + j] == 0 && Chess[x + 2 * i][y + 2 * j] == a
                                && Chess[x + 3 * i][y + 3 * j] == a && Chess[x + 4 * i][y + 4 * j] == a) {
                            p += 5;
                        }
                        if(Chess[x + i][y + j] == 0 && Chess[x + 2 * i][y + 2 * j] == a
                                && Chess[x + 3 * i][y + 3 * j] == a && Chess[x - i][y - j] == a) {
                            p += 15;
                        }
                        if(Chess[x + i][y + j] == a && Chess[x + 2 * i][y + 2 * j] == a
                                && Chess[x - i][y - j] == 0 && Chess[x - 2 * i][y - 2 * j] == a) {
                            p += 15;
                        }
                    }
                }

            return p;
        }

        public int defendfive(int x, int y, boolean who) {
            int a = (who) ? 2 : 1;
            int p = 0;

            for(int i = -1; i <= 1; i++)
                for(int j = -1; j <= 1; j++) {
                    if(x + 4 * i < 0 || y + 4 * j < 0 || x + 4 * i > 14 || y + 4 * j > 14
                            || x - 2 * i < 0 || y - 2 * j < 0 || x - 2 * i > 14 || y - 2 * j > 14) ;
                    else {
                        if(Chess[x + i][y + j] == a && Chess[x + 2 * i][y + 2 * j] == a
                           && Chess[x + 3 * i][y + 3 * j] == a && Chess[x + 4 * i][y + 4 * j] == a) {
                            p += 120;
                        }
                        if(Chess[x + i][y + j] == a && Chess[x + 2 * i][y + 2 * j] == a
                           && Chess[x + 3 * i][y + 3 * j] == a && Chess[x - i][y - j] == a) {
                            p += 120;
                        }
                        if(Chess[x + i][y + j] == a && Chess[x + 2 * i][y + 2 * j] == a
                           && Chess[x - i][y - j] == a && Chess[x - 2 * i][y - 2 * j] == a) {
                            p += 120;
                        }
                    }
                }

            return p;
        }
    }
}