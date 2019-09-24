package com.example.meshdemo.LwlEventBus;

import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/***
 * Created by lwl on 2018/12/11.
 */

public enum ThreadMode {
    //主线程
    MAIN_THREAD,
    //新的线程
    NEW_THREAD,
    //读写线程
    IO,
    //计算工作默认线程
    COMPUTATION,
    //在当前线程中按照队列方式执行
    TRAMPOLINE;

    public static Scheduler getScheduler(ThreadMode threadMode) {
        switch (threadMode) {
            case MAIN_THREAD:
                return AndroidSchedulers.mainThread();
            case NEW_THREAD:
                return Schedulers.newThread();
            case IO:
                return Schedulers.io();
            case COMPUTATION:
                return Schedulers.computation();
            case TRAMPOLINE:
                return Schedulers.trampoline();
            default:
                return AndroidSchedulers.mainThread();
        }
    }
}
