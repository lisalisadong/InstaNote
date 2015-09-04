package pennapps.campicdemo;

import android.app.ActionBar;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class PaintActivity extends AppCompatActivity {

    DrawingView dv;
    private Paint mPaint;
    //private DrawingManager mDrawingManager = null;

    public class DrawingView extends View {
        public int width;
        public  int height;
        private Bitmap mBitmap;
        private Canvas mCanvas;
        private Path    mPath;
        private Paint   mBitmapPaint;
        Context context;
        private Paint circlePaint;
        private Path circlePath;
        private Paint darkPaint;
        private Path darkPath;

        public DrawingView(Context c) {
            super(c);
            context=c;
            mPath = new Path();
            mBitmapPaint = new Paint(Paint.DITHER_FLAG);
            circlePaint = new Paint();
            circlePath = new Path();
            circlePaint.setAntiAlias(true);
            circlePaint.setColor(Color.BLUE);
            circlePaint.setStyle(Paint.Style.STROKE);
            circlePaint.setStrokeJoin(Paint.Join.MITER);
            circlePaint.setStrokeWidth(4f);

            darkPaint = new Paint();
            darkPath = new Path();
            darkPaint.setColor(Color.BLACK);
            darkPaint.setAlpha(200);
            darkPaint.setStrokeWidth(60);
        }

        @Override
        protected void onSizeChanged(int w, int h, int oldw, int oldh) {
            super.onSizeChanged(w, h, oldw, oldh);

            mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
            mCanvas = new Canvas(mBitmap);

        }
        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);

            canvas.drawBitmap(mBitmap, 0, 0, mBitmapPaint);

            canvas.drawPath( mPath,  mPaint);

            // dark transparent
            //canvas.drawPath(darkPath, darkPaint);

            canvas.drawPath(circlePath, circlePaint);


        }

        private float mX, mY;
        private static final float TOUCH_TOLERANCE = 4;
        private RelativeLayout blackTransparent;

        private void touch_start(float x, float y) {
            //mBitmapPaint.setColor(0xFFFFFFFF);
            //mBitmapPaint.setAlpha(200);
            //this.setBackgroundColor(0xFFFFFFFF);
/*            blackTransparent = new RelativeLayout(context);
            RelativeLayout.LayoutParams rlp = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.MATCH_PARENT);
            blackTransparent.setBackgroundColor(0x00000000);
            blackTransparent.setAlpha((float) 0.8);
            addContentView(blackTransparent, rlp);*/
            //Point size = new Point();
            //Display display = getWindowManager().getDefaultDisplay();
            //display.getRealSize(size);
            //darkPath.addRect(0, 0, size.x, size.y, Path.Direction.CW);
            Point size = new Point();
            Display display = getWindowManager().getDefaultDisplay();
            display.getRealSize(size);
            RectF rect = new RectF(0, 0, size.x, size.y);
            mCanvas.drawRect(rect, darkPaint);

            mPath.reset();
            mPath.moveTo(x, y);
            mX = x;
            mY = y;
        }
        private void touch_move(float x, float y) {
            float dx = Math.abs(x - mX);
            float dy = Math.abs(y - mY);
            if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
                mPath.quadTo(mX, mY, (x + mX)/2, (y + mY)/2);
                //darkPath.quadTo(mX, mY, (x + mX)/2, (y + mY)/2);
                mX = x;
                mY = y;

                circlePath.reset();
                circlePath.addCircle(mX, mY, 30, Path.Direction.CW);
                Log.i("touch location", "x:" + mX + ", y: " + mY);
            }
        }
        private void touch_up() {
            mPath.lineTo(mX, mY);
            circlePath.reset();
            //darkPath.reset();
            // commit the path to our offscreen
            mCanvas.drawPath(mPath, mPaint);
            // kill this so we don't double draw
            mPath.reset();

            //mBitmapPaint.setAlpha(0);
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            float x = event.getX();
            float y = event.getY();

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    touch_start(x, y);
                    invalidate();
                    break;
                case MotionEvent.ACTION_MOVE:
                    touch_move(x, y);
                    invalidate();
                    break;
                case MotionEvent.ACTION_UP:
                    touch_up();
                    invalidate();
                    break;
            }
            return true;
        }
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dv = new DrawingView(this);
        // setContentView(R.layout.activity_paint);
        setContentView(dv);
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        //mPaint.setColor(Color.GREEN);
        mPaint.setAlpha(0);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(60);
        mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));

        // load img
        Bundle extras = getIntent().getExtras();
        if (extras == null) return;
        String path = (String) extras.get("IMAGE");
        Drawable backgroundImg = Drawable.createFromPath(path);
        // Resize img
        if (backgroundImg != null) {
            Bitmap bitmap = ((BitmapDrawable) backgroundImg).getBitmap();
            int width = bitmap.getWidth();
            int height = bitmap.getHeight();
            Point size = new Point();
            Display display = getWindowManager().getDefaultDisplay();
            display.getRealSize(size);
            int newWidth = size.x;
            int newHeight = size.y;
            float scaleWidth = ((float) newWidth) / width;
            float scaleHeight = ((float) newHeight) / height;
            Matrix matrix = new Matrix();
            matrix.postScale(scaleWidth, scaleHeight);
            Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
            backgroundImg = new BitmapDrawable(resizedBitmap);
        }
        // Create a relative layout to display image in background.
//        RelativeLayout backgroundImgLayout = new RelativeLayout(this);
//        RelativeLayout.LayoutParams rlp = new RelativeLayout.LayoutParams(
//                RelativeLayout.LayoutParams.MATCH_PARENT,
//                RelativeLayout.LayoutParams.MATCH_PARENT);
//        addContentView(backgroundImgLayout, rlp);
//        backgroundImgLayout.setBackground(backgroundImg);
        dv.setBackgroundDrawable(backgroundImg);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_paint, menu);
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
}
