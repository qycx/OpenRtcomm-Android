package com.qycx.base;

import static com.qycx.base.Consts.AUDIO_UPLOAD;
import static com.qycx.base.Consts.VIDEO_UPLOAD;
import static com.qycx.base.qyWmComm.Consts.CONST_Status_Window_Function_Open;

import android.graphics.Paint;

import com.qycx.base.statuscenter.messagebean.NetConnectEvent;
import com.qycx.base.statuscenter.messagebean.NetConnectStatus;
import com.qycx.base.util.Log;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

public class commonSock_android {

    public static class qyFuncs_Droid {

        public static boolean isSocketInvalid_android(commonSock.QY_SOCK pSock) {
            if (pSock == null) return false;
            if (pSock.socketChannel == null) return true;
            return false;
        }

        public static guiShare.PF_bQySockOpen bQySockOpen_android = (commonSock.QY_SOCK pSock) ->
        {
            if (pSock == null) return false;
            return pSock.socketChannel != null;
        };


        public static guiShare.PF_qyDisconnect qyDisconnect_android = (commonSock.QY_SOCK pSock) ->
        {
            //Log.d("qyDisconnect_android");
            //
            if (!isSocketInvalid_android(pSock)) {
                //
                if  (  pSock.socket1!=null) {
                    try {
                        // 取消标签
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
                            android.net.TrafficStats.untagSocket(pSock.socket1);
                        }
                        //
                        pSock.socket1.close();
                        //
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    pSock.socket1=null;
                }
                //
                SocketChannel socketChannel = pSock.socketChannel;
                try {
                    socketChannel.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                pSock.socketChannel = null;
            }
            if (pSock.selector_r != null) {
                try {
                    pSock.selector_r.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                pSock.selector_r = null;
            }
            if (pSock.selector_w != null) {
                try {
                    pSock.selector_w.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                pSock.selector_w = null;
            }

            if (pSock.bb_buf_r != null) {
                pSock.bb_buf_r = null;
            }

            if (pSock.bb_buf_w != null) {
                pSock.bb_buf_w = null;
            }
        };

        public static guiShare.PF_qyConnect qyConnect_android = (String ipaddr, int port, commonSock.SOCK_PARAM pSockParam, commonSock.QY_SOCK pSock) -> {

            //
            //Log.d("qyConnect_android");

            //
            int iErr = -1;

            if (!isSocketInvalid_android(pSock)) return -1;

            try {
                pSock.socketChannel = SocketChannel.open();
                if (pSock.socketChannel == null) {
                    qyDisconnect_android.call(pSock);
                    return iErr;
                }

                SocketChannel socketChannel = pSock.socketChannel;
                // Connect to the server
                socketChannel.socket().connect(new InetSocketAddress(ipaddr, port),pSockParam.to.iConnect);

                //
                pSock.socket1=socketChannel.socket();
                // 进行 TrafficStats 标签
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
                    android.net.TrafficStats.tagSocket(pSock.socket1);
                }

                //
                pSock.selector_r = Selector.open();
                pSock.selector_w = Selector.open();
                // Configure the SocketChannel
                socketChannel.configureBlocking(false);
                socketChannel.register(pSock.selector_r, SelectionKey.OP_READ);
                socketChannel.register(pSock.selector_w, SelectionKey.OP_WRITE);
                while (!socketChannel.finishConnect())
                {
                    Log.d(".");
                }
                iErr = 0;
            } catch (IOException e) {
              //  e.printStackTrace();
                //Log.e("connection failed!!!!!!!!!!");
                //
                qyDisconnect_android.call(pSock);
                return iErr;
            }
            return iErr;
        };

      /*  public static guiShare.PF_qySend qySend_android = (commonSock.QY_SOCK pSock, byte[] buf, int offset,  int len, int timeoutVal) ->
        {
            int nWrite = 0;

            SocketChannel socketChannel = pSock.socketChannel;
            Selector selector = pSock.selector_w;
            if(selector == null) return -1;
            try {
                selector.select();
                Set<SelectionKey> keys = selector.selectedKeys();
                Iterator<SelectionKey> it = keys.iterator();
                while (it.hasNext()) {
                    SelectionKey key = it.next();
                    it.remove();

                    if (key.isWritable()) {

                        //
                        //buf.position(offset);
                        //buf.limit(offset  +  len);
                        ByteBuffer buf1=ByteBuffer.wrap(buf,offset,len);
                        nWrite = socketChannel.write(buf1);

                        //
                        if  (  false  ) {
                            if (nWrite > 200 && nWrite < 1000) {
                                Log.d(AUDIO_UPLOAD, "<stage6> qySend_android nWrite: " + nWrite + " len: " + len);
                            } else if (nWrite >= 1000) {
                                Log.d(VIDEO_UPLOAD, "<stage6> qySend_android nWrite: " + nWrite + " len: " + len);
                            } else {
                                Log.d("qySend_android: " + nWrite + " len " + len);
                            }
                        }

                        //
                        key.interestOps(SelectionKey.OP_WRITE);
                    }
                }

            } catch (Exception e) {
                //
                Log.e( "anc","qyWrite_android exception " + e);
                Log.d("anc", "<stage6> qySend_android nWrite: exception ");
                EventBus.getDefault().post(new NetConnectEvent(NetConnectStatus.DISCONNECTED));
                //
                qyDisconnect_android.call(pSock);
            }
            return nWrite;
        };

       */

        //
        public static guiShare.PF_qySend qySend_android =
                (commonSock.QY_SOCK pSock,
                 byte[] buf,
                 int offset,
                 int len,
                 int timeoutVal) -> {

                    if (pSock == null || buf == null || len <= 0) {
                        return -1;
                    }

                    SocketChannel socketChannel = pSock.socketChannel;
                    Selector selector = pSock.selector_w;

                    if (socketChannel == null || selector == null) {
                        return -1;
                    }

                    int totalWrite = 0;

                    try {

                        ByteBuffer buffer = ByteBuffer.wrap(buf, offset, len);

                        while (buffer.hasRemaining()) {

                            // 等待可写
                            int ready = selector.select(timeoutVal);

                            if (ready == 0) {
                                // 超时
                                break;
                            }

                            Iterator<SelectionKey> it = selector.selectedKeys().iterator();

                            while (it.hasNext()) {

                                SelectionKey key = it.next();
                                it.remove();

                                if (!key.isValid()) {
                                    continue;
                                }

                                if (!key.isWritable()) {
                                    continue;
                                }

                                int n = socketChannel.write(buffer);

                                if (n < 0) {
                                    // Socket 已关闭
                                    return -1;
                                }

                                if (n == 0) {
                                    // 发送缓冲区已满，下次继续
                                    break;
                                }

                                totalWrite += n;

                                // 全部发送完成
                                if (!buffer.hasRemaining()) {
                                    break;
                                }
                            }

                            if (!buffer.hasRemaining()) {
                                break;
                            }
                        }

                        return totalWrite;

                    } catch (Exception e) {

                        //Log.e("anc", "qySend_android exception", e);
                        Log.e("anc,qySend_android exception");

                        //
                        try {
                            if (socketChannel != null) {
                                socketChannel.close();
                            }
                        } catch (Exception ignored) {
                        }

                        try {
                            if (selector != null) {
                                selector.close();
                            }
                        } catch (Exception ignored) {
                        }

                        EventBus.getDefault().post(
                                new NetConnectEvent(NetConnectStatus.DISCONNECTED));

                        return -1;
                    }
                };


        //
        public static int qyRecv_android(commonSock.QY_SOCK pSock, ByteBuffer buffer, int offset, int len,  int timeoutVal) {

            if (pSock == null) {
                Log.e("qy", "qyRecv_android failed, pSock == null");
                return -1;
            }

            if (buffer == null) {
                Log.e("qy", "qyRecv_android failed, buffer == null");
                return -1;
            }

            if (offset < 0 || len < 0) {
                Log.e("qy", "qyRecv_android failed, invalid offset/len");
                return -1;
            }

            if (offset > buffer.capacity() - len) {
                Log.e("qy", "qyRecv_android failed, buffer overflow");
                return -1;
            }

            SocketChannel channel = pSock.socketChannel;
            Selector selector = pSock.selector_r;

            if (channel == null) {
                Log.e("qy", "qyRecv_android failed, channel == null");
                return -1;
            }

            if (selector == null) {
                Log.e("qy", "qyRecv_android failed, selector == null");
                return -1;
            }

            try {

                int ready = selector.select(timeoutVal);

                if (ready <= 0) {
                    //
                    Log.d("at","qyRecv_android: select timeout");
                    // timeout
                    return 0;
                }

                Iterator<SelectionKey> it = selector.selectedKeys().iterator();

                while (it.hasNext()) {

                    SelectionKey key = it.next();
                    it.remove();

                    if (!key.isValid()) {
                        continue;
                    }

                    if (!key.isReadable()) {
                        continue;
                    }

                    int oldPos = buffer.position();
                    int oldLimit = buffer.limit();

                    try {

                        buffer.position(offset);
                        buffer.limit(offset + len);

                        int nRead = channel.read(buffer);

                        if (nRead < 0) {
                            Log.e("qy", "Socket closed.");
                            return -1;
                        }

                        return nRead;

                    } finally {

                        // 保留新的 position（read() 已自动推进）
                        int newPos = buffer.position();

                        buffer.limit(oldLimit);
                        buffer.position(newPos);
                    }
                }

                return 0;

            } catch (Exception e) {

                Log.e("qy", "qyRecv_android exception" );
                return -1;
            }

            //return  -1;
        }


        //
        // Use select() to determine when
        // data is waiting to be read
        //  返回值：>  0  为有数据
        //			== 0  为超时
        //			<  0  为失败
        public static guiShare.PF_qyWaitForRead qyWaitForRead_android = (commonSock.QY_SOCK pSock, int timeoutInMs) ->
        {
            int iErr = -1;

            try {
                Selector selector = pSock.selector_r;
                if(selector == null) return -1;

                int nReady = selector.select(timeoutInMs);
                if(nReady != 1)
                {
                    iErr = 0;
                }else{
                    Set<SelectionKey> keys = selector.selectedKeys();
                    Iterator<SelectionKey> it = keys.iterator();

                    while (it.hasNext()) {
                        SelectionKey key = it.next();
                        it.remove();

                        if (key.isValid() && key.isReadable()) {
                            key.interestOps(SelectionKey.OP_READ);
                        }
                    }
                    iErr = 1;
                }
            } catch (Exception e) {
                Log.e( "qyWaitForRead_android exception " + e);
                EventBus.getDefault().post(new NetConnectEvent(NetConnectStatus.DISCONNECTED));
                qyDisconnect_android.call(pSock);
                e.printStackTrace();
                return iErr;
            }


            return iErr;
        };


        public static guiShare.PF_qyWaitForWrite qyWaitForWrite_android = (commonSock.QY_SOCK pSock, int timeoutInMs) ->
        {
            int iErr = -1;

            //
            if  (  pSock.bDbg)  {
                //
                Log.d("at","snd: l309: qyWaitForWrite_android: enters.");
            }

            //
            try {
                Selector selector = pSock.selector_w;
                if(selector == null) return -1;
                //
                int nReady = selector.select( timeoutInMs);
                if (nReady != 1)
                {
                    iErr  =  0;
                }else{
                    Set<SelectionKey> keys = selector.selectedKeys();
                    Iterator<SelectionKey> it = keys.iterator();

                    while (it.hasNext()) {
                        SelectionKey key = it.next();
                        it.remove();

                        if (key.isValid() && key.isWritable()) {
                            key.interestOps(SelectionKey.OP_WRITE);
                        }
                    }

                    //
                    iErr = 1;
                }
            } catch (Exception e) {
                Log.e( "qyWaitForWrite_android exception " + e);
                EventBus.getDefault().post(new NetConnectEvent(NetConnectStatus.DISCONNECTED));
                qyDisconnect_android.call(pSock);
                Log.e( e.getMessage());
                return iErr;
            }

            //
            if  (  pSock.bDbg)  {
                Log.d("at","snd: l344: qyWaitForWrite_android: leaves. iErr "  +  iErr);
            }

            //
            return iErr;
        };
    }
}
