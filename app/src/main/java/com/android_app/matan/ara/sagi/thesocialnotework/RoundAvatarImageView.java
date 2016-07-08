package com.android_app.matan.ara.sagi.thesocialnotework;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Created by matanbaryosef on 08/07/2016.
 */
public class RoundAvatarImageView extends ImageView {

    public static float radius = 110.0f;

    public RoundAvatarImageView(Context context) {
        super(context);
    }

    public RoundAvatarImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RoundAvatarImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //float radius = 36.0f;
        Path clipPath = new Path();
        RectF rect = new RectF(0, 0, this.getWidth(), this.getHeight());
        clipPath.addRoundRect(rect, radius, radius, Path.Direction.CW);
        canvas.clipPath(clipPath);
        super.onDraw(canvas);
    }
}
