/**********************************************
 * FileName: Test
 * Author: gaochengjia@zlingsmart.com.cn
 * Date: 8/13/21 4:33 PM
 * Description: 
 * History:
 *------------------------------------------------------
 * Version    date      author          description
 * V0.XX     8/13/21    gaochengjia     1)...
 ***********************************************/
package com.landmark.scannernative;

/**
 * @ClassName: Test
 * @Description: java类作用描述
 * @Author: gaby
 * @Date: 8/13/21 4:33 PM
 */
class Test {
    static {
        System.loadLibrary("scanner_jni");
        native_init();
    }
    Test(){
        native_setup();
    }

    public native void test();
    public native static void native_init();
    public native void native_setup();
}
