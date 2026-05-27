package com.qycx.smxj;

import static com.intel.realsense.librealsense.StreamFormat.BGR8;
import static com.intel.realsense.librealsense.StreamFormat.RGB8;
import static com.qycx.base.qyMcMain_global.g.g_pQyMc;

import android.graphics.Rect;
import android.opengl.GLES10;

import com.intel.realsense.librealsense.Extension;
import com.intel.realsense.librealsense.GLFrame;
import com.intel.realsense.librealsense.StreamType;
import com.intel.realsense.librealsense.VideoFrame;
import com.intel.realsense.librealsense.VideoStreamProfile;
import com.qycx.base.ctxQmc;
import com.qycx.base.ctxQyMc;
import com.qycx.base.guiShare;
import com.qycx.base.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;

import javax.microedition.khronos.opengles.GL10;

public class myGLVideoFrame extends GLFrame {
    private IntBuffer mGlTexture;

    public myGLVideoFrame(){
        mGlTexture = IntBuffer.allocate(1);
        GLES10.glGenTextures(1, mGlTexture);

        //
        int  texId =  getTexture();
        Log.d("qy","");
    }

    public int getTexture() { return mGlTexture.array()[0]; }

    private Rect adjustRatio(Rect in){
        if (mFrame == null || !(mFrame.is(Extension.VIDEO_FRAME)))
            return null;

        float ratio = 0;
        try(VideoFrame vf = mFrame.as(Extension.VIDEO_FRAME)) {
            ratio = (float)vf.getWidth() / (float)vf.getHeight();
        }
        float newHeight = in.height();
        float newWidth = in.height() * ratio;
        if(newWidth > in.width()){
            ratio = in.width() / newWidth;
            newWidth *= ratio;
            newHeight *= ratio;
        }

        float newLeft = in.left + (in.width() - newWidth) / 2f;
        float newTop = in.top + (in.height() - newHeight) / 2f;
        float newRight = newLeft + newWidth;
        float newBottom = newTop + newHeight;
        return new Rect((int)newLeft, (int)newTop, (int)newRight, (int)newBottom);
    }

    @Override
    public synchronized void draw(Rect rect)
    {
        if (mFrame == null || !(mFrame.is(Extension.VIDEO_FRAME)))
            return;

        try(VideoFrame vf = mFrame.as(Extension.VIDEO_FRAME)) {
            int size = vf.getStride() * vf.getHeight();
            if(mBuffer == null || mBuffer.array().length != size){
                mBuffer = ByteBuffer.allocate(size);
                mBuffer.order(ByteOrder.LITTLE_ENDIAN);
            }
            mFrame.getData(mBuffer.array());
            mBuffer.rewind();

            //
            try(VideoStreamProfile profile = vf.getProfile()) {
                int num = mFrame.getNumber();
                StreamType type = profile.getType();
                if  (  profile.getFormat()  ==  RGB8)  {

                    //
                    String tag  =  ""+type;
                    //saveTextureAsBmp(null, mBuffer, vf.getWidth(), vf.getHeight(), tag);

                    if  (  type==StreamType.COLOR)
                    {
                        //mFrame.
                        //
                        //Log.d("qy","");
                        //
                        //
                        ctxQyMc.CCtxQyMc pQyMc  =  g_pQyMc;
                        ctxQmc.CCtxQmc pProcInfo  =  pQyMc.get_pProcInfo();
                        //
                        pProcInfo.smxj.save_buf(mBuffer);
                    }
                }
            }



                //
            upload(vf, mBuffer, mGlTexture.get(0));
            Rect r = adjustRatio(rect);
            draw(r, mGlTexture.get(0));
        }
    }

    @Override
    public synchronized void close() {
        if(mFrame != null)
            mFrame.close();
        GLES10.glDeleteTextures(1, mGlTexture);
    }

    public static void upload(VideoFrame vf, ByteBuffer buffer, int texture)
    {
        GLES10.glBindTexture(GLES10.GL_TEXTURE_2D, texture);
        try(VideoStreamProfile profile = vf.getProfile()){
            switch (profile.getFormat())
            {
                case RGB8:
                case BGR8:
                    GLES10.glTexImage2D(GLES10.GL_TEXTURE_2D, 0, GLES10.GL_RGB, vf.getWidth(), vf.getHeight(), 0, GLES10.GL_RGB, GLES10.GL_UNSIGNED_BYTE, buffer);
                    break;
                case RGBA8:
                    GLES10.glTexImage2D(GLES10.GL_TEXTURE_2D, 0, GLES10.GL_RGBA, vf.getWidth(), vf.getHeight(), 0, GLES10.GL_RGBA, GLES10.GL_UNSIGNED_BYTE, buffer);
                    break;
                case Y8:
                    GLES10.glTexImage2D(GLES10.GL_TEXTURE_2D, 0, GLES10.GL_LUMINANCE, vf.getWidth(), vf.getHeight(), 0, GLES10.GL_LUMINANCE, GLES10.GL_UNSIGNED_BYTE, buffer);
                    break;
                default:
                    throw new RuntimeException("The requested format is not supported by the viewer");
            }

            //
            if  (  profile.getFormat()  ==  RGB8)  {
                //
                //Log.d("qy","");

            }
        }


        GLES10.glTexParameterx(GLES10.GL_TEXTURE_2D, GLES10.GL_TEXTURE_MAG_FILTER, GLES10.GL_LINEAR);
        GLES10.glTexParameterx(GLES10.GL_TEXTURE_2D, GLES10.GL_TEXTURE_MIN_FILTER, GLES10.GL_LINEAR);
        GLES10.glTexParameterx(GLES10.GL_TEXTURE_2D, GLES10.GL_TEXTURE_WRAP_S, 0x2900);
        GLES10.glTexParameterx(GLES10.GL_TEXTURE_2D, GLES10.GL_TEXTURE_WRAP_T, 0x2900);
        GLES10.glPixelStorei(0x0CF2, 0);
        GLES10.glBindTexture(GLES10.GL_TEXTURE_2D, 0);
    }


    public static void saveTextureAsBmp( ByteBuffer pixelBuffer, int width, int height, String tag) {
        // 读取像素数据
        /*
        ByteBuffer pixelBuffer = ByteBuffer.allocateDirect(width * height * 3);
        pixelBuffer.order(ByteOrder.nativeOrder());
        gl.glReadPixels(0, 0, width, height, GL10.GL_RGB, GL10.GL_UNSIGNED_BYTE, pixelBuffer);

         */

        // 构建 BMP 文件头
        byte[] bmpHeader = new byte[54];
        // BMP 文件头
        bmpHeader[0] = 'B';
        bmpHeader[1] = 'M';
        int fileSize = 54 + width * height * 3;
        bmpHeader[2] = (byte) (fileSize & 0xFF);
        bmpHeader[3] = (byte) ((fileSize >> 8) & 0xFF);
        bmpHeader[4] = (byte) ((fileSize >> 16) & 0xFF);
        bmpHeader[5] = (byte) ((fileSize >> 24) & 0xFF);
        bmpHeader[10] = 54; // 数据偏移量

        // BMP 信息头
        bmpHeader[14] = 40; // 信息头大小
        bmpHeader[18] = (byte) (width & 0xFF);
        bmpHeader[19] = (byte) ((width >> 8) & 0xFF);
        bmpHeader[20] = (byte) ((width >> 16) & 0xFF);
        bmpHeader[21] = (byte) ((width >> 24) & 0xFF);
        bmpHeader[22] = (byte) (height & 0xFF);
        bmpHeader[23] = (byte) ((height >> 8) & 0xFF);
        bmpHeader[24] = (byte) ((height >> 16) & 0xFF);
        bmpHeader[25] = (byte) ((height >> 24) & 0xFF);
        bmpHeader[26] = 1; // 平面数
        bmpHeader[28] = 24; // 位深度

        // 写入 BMP 文件
        try {
            //

            //
            String fn  =  "/sdcard/texture."+tag+".bmp";
            File file = new File(fn);
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(bmpHeader);

            // 反转像素数据（BMP 格式是从下到上存储）
            for (int y = height - 1; y >= 0; y--) {
                for (int x = 0; x < width; x++) {
                    int index = (y * width + x) * 3;
                    if  (  false ) {
                        fos.write(pixelBuffer.get(index));
                        fos.write(pixelBuffer.get(index + 1));
                        fos.write(pixelBuffer.get(index + 2));
                    }
                    else  {
                        fos.write(pixelBuffer.get(index + 2));
                        fos.write(pixelBuffer.get(index + 1));
                        fos.write(pixelBuffer.get(index));
                    }
                }
                //
                Log.d("qysmxj","dumpBmp: y "  +  y);
            }
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void draw(Rect r, int texture)
    {
        setViewport(r);

        GLES10.glBindTexture(GLES10.GL_TEXTURE_2D, texture);
        GLES10.glEnable(GLES10.GL_TEXTURE_2D);

        float[] verArray = {
                0,          0,
                0,          r.height(),
                r.width(),  r.height(),
                r.width(),   0
        };
        ByteBuffer ver = ByteBuffer.allocateDirect(verArray.length * 4);
        ver.order(ByteOrder.nativeOrder());

        float[] texArray = {
                0,0,
                0,1,
                1,1,
                1,0
        };
        ByteBuffer tex = ByteBuffer.allocateDirect(texArray.length * 4);
        tex.order(ByteOrder.nativeOrder());

        ver.asFloatBuffer().put(verArray);
        tex.asFloatBuffer().put(texArray);

        GLES10.glEnableClientState(GLES10.GL_VERTEX_ARRAY);
        GLES10.glEnableClientState(GLES10.GL_TEXTURE_COORD_ARRAY);

        GLES10.glVertexPointer(2, GLES10.GL_FLOAT, 0, ver);
        GLES10.glTexCoordPointer(2, GLES10.GL_FLOAT, 0, tex);
        GLES10.glDrawArrays(GLES10.GL_TRIANGLE_FAN,0,4);

        GLES10.glDisableClientState(GLES10.GL_VERTEX_ARRAY);
        GLES10.glDisableClientState(GLES10.GL_TEXTURE_COORD_ARRAY);

        GLES10.glDisable(GLES10.GL_TEXTURE_2D);
        GLES10.glBindTexture(GLES10.GL_TEXTURE_2D, 0);
    }
}
