package com.landmark.media.controller.utils.MP3ID3v2;

import com.landmark.media.controller.utils.LogUtils;

/**
 * Use byte Util class
 */
public class ByteUtil {

    /**
     * Positive index
     */
    public static int indexOf(byte[] tag, byte[] src) {
        return indexOf(tag, src, 1);
    }

    /**
     * Gets the position of index
     * Index starts at 1
     */
    public static int indexOf(byte[] tag, byte[] src, int index) {
        return indexOf(tag, src, 1, src.length);
    }

    /**
     * Gets the position of index
     * Index starts at 1
     */
    public static int indexOf(byte[] tag, byte[] src, int index, int len) {
        if (len > src.length) {
            try {
                throw new Exception("大于总个数");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        int tagLen = tag.length;
        byte[] tmp = new byte[tagLen];
        for (int j = 0; j < len - tagLen + 1; j++) {
            System.arraycopy(src, j, tmp, 0, tagLen);
            for (int i = 0; i < tagLen; i++) {
                if (tmp[i] != tag[i])
                    break;
                if (i == tagLen - 1) {
                    return j;
                }
            }

        }
        return -1;
    }

    /**
     * Reverse index
     */
    public static int lastIndexOf(byte[] tag, byte[] src) {

        return lastIndexOf(tag, src, 1);
    }

    /**
     * Get the position of index in reverse order
     * index starts at 1
     */
    public static int lastIndexOf(byte[] tag, byte[] src, int index) {
        return lastIndexOf(tag, src, src.length);
    }

    /**
     * Get the position of index in reverse order
     * index starts at 1
     */
    public static int lastIndexOf(byte[] tag, byte[] src, int index, int len) {
        if (len > src.length) {
            try {
                throw new Exception("大于总个数");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        int tagLen = tag.length;
        byte[] tmp = new byte[tagLen];
        for (int j = len - tagLen; j >= 0; j--) {
            for (int i = 0; i < tagLen; i++) {
                tmp[i] = src[j + i];

            }
            for (int i = 0; i < tagLen; i++) {
                if (tmp[i] != tag[i])
                    break;
                if (i == tagLen - 1) {
                    return j;
                }
            }
        }
        return -1;
    }

    /**
     * The number of statistical
     */
    public static int size(byte[] tag, byte[] src) {
        int size = 0;
        int tagLen = tag.length;
        int srcLen = src.length;
        byte[] tmp = new byte[tagLen];
        for (int j = 0; j < srcLen - tagLen + 1; j++) {
            for (int i = 0; i < tagLen; i++) {
                tmp[i] = src[j + i];
            }
            for (int i = 0; i < tagLen; i++) {
                if (tmp[i] != tag[i])
                    break;
                if (i == tagLen - 1) {
                    size++;
                }
            }
        }
        return size;
    }

    /**
     * Get cute byte[]
     */
    public static byte[] cutBytes(int start, int end, byte[] src) {
        if (end <= start || start < 0 || end > src.length) {
            try {
                LogUtils.debug("cutBytes error!");
                return null;
            } catch (Exception e) {
            }
        }
        byte[] tmp = new byte[end - start];
        if (end - start >= 0) System.arraycopy(src, start, tmp, 0, end - start);
        return tmp;
    }
}