package com.eyedog.aftereffect.filters;

import android.content.Context;
import android.opengl.GLES30;
import com.eyedog.aftereffect.utils.OpenGLUtils;

/**
 * created by jw200 at 2019/3/12 21:16
 **/
public class StickerFilter extends GLImageFilter {

    protected int mInputTexture2Handle;//foreground texture
    protected int size;
    protected int center;
    protected int theta;
    protected int alpha;
    protected int blendMode;
    protected int mirrorMode;
    protected int aspectRatio;
    protected int s;
    protected int c;

    protected int mStickerTextureId = OpenGLUtils.GL_NOT_TEXTURE;
    protected Vec2 mSize = new Vec2(0.7f, 0.5f);
    protected Vec2 mCenter = new Vec2(0.5f, 0.5f);
    protected float mTheta = 0;
    protected float mAlpha = 1.0F;
    protected int mBlendMode = 1;
    protected int mMirrorMode = 0;
    protected float mAspectRatio = 1.0f;
    protected float mS = 0f;
    protected float mC = 1f;

    public StickerFilter(Context context) {
        this(context, VERTEX_SHADER, OpenGLUtils.getShaderFromAssets(context, "shader/base/fragment_sticker.glsl"));
    }

    public StickerFilter(Context context, String vertexShader, String fragmentShader) {
        super(context, vertexShader, fragmentShader);
    }

    @Override
    public void initProgramHandle() {
        super.initProgramHandle();
        if (mProgramHandle != OpenGLUtils.GL_NOT_INIT) {
            mInputTexture2Handle = GLES30.glGetUniformLocation(mProgramHandle,
                "inputImageTexture2");
            size = GLES30.glGetUniformLocation(mProgramHandle, "size");
            center = GLES30.glGetUniformLocation(mProgramHandle, "center");
            theta = GLES30.glGetUniformLocation(mProgramHandle, "theta");
            alpha = GLES30.glGetUniformLocation(mProgramHandle, "alpha");
            blendMode = GLES30.glGetUniformLocation(mProgramHandle, "blendMode");
            mirrorMode = GLES30.glGetUniformLocation(mProgramHandle, "mirrorMode");
            aspectRatio = GLES30.glGetUniformLocation(mProgramHandle, "aspectRatio");
            s = GLES30.glGetUniformLocation(mProgramHandle, "s");
            c = GLES30.glGetUniformLocation(mProgramHandle, "c");
            setFloat(alpha, mAlpha);
            setInteger(blendMode, mBlendMode);
            setInteger(mirrorMode, mMirrorMode);
            setFloat(aspectRatio, mAspectRatio);
        }
    }

    @Override
    public void onDrawFrameBegin() {
        super.onDrawFrameBegin();
        if (mStickerTextureId != OpenGLUtils.GL_NOT_TEXTURE) {
            OpenGLUtils.bindTexture(mInputTexture2Handle, mStickerTextureId, 1);
        }
        if (mSize != null) {
            GLES30.glUniform2f(size, mSize.wRatio, mSize.hRatio);
        }
        if (mCenter != null) {
            GLES30.glUniform2f(center, mCenter.wRatio, mCenter.hRatio);
        }
    }

    public void setStickerTextureId(int textureId) {
        mStickerTextureId = textureId;
    }

    public void setSize(Vec2 size) {
        if (size == null) {
            return;
        }
        if (size.wRatio >= 1) {
            size.wRatio = 1.0f;
        } else if (size.wRatio <= 0) {
            size.wRatio = 0;
        }
        if (size.hRatio >= 1) {
            size.hRatio = 1.0f;
        } else if (size.hRatio <= 0) {
            size.hRatio = 0;
        }
        this.mSize = size;
    }

    public void setTheta(float degree) {
        mTheta = degree;
        mS = (float) Math.sin(mTheta);
        mC = (float) Math.cos(mTheta);
        setFloat(theta, mTheta);
        setFloat(s, mS);
        setFloat(c, mC);
    }

    class Vec2 {
        public float wRatio;
        public float hRatio;

        public Vec2(float wRatio, float hRatio) {
            this.wRatio = wRatio;
            this.hRatio = hRatio;
        }
    }

    @Override
    public void release() {
        super.release();
        if (mStickerTextureId != OpenGLUtils.GL_NOT_TEXTURE) {
            GLES30.glDeleteTextures(1, new int[] { mStickerTextureId }, 0);
            mStickerTextureId = OpenGLUtils.GL_NOT_TEXTURE;
        }
    }
}