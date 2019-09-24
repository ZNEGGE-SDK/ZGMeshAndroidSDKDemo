package com.example.meshdemo.LwlEventBus;

import androidx.annotation.NonNull;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;

/***
 * Created by lwl on 2018/12/10.
 */

//混淆配置
//注解不可以被混淆
//-keepattributes *Annotation*
//-keepclassmembers class ** {
// @com.zengge.wifi.LwlEventBus.Subscribe <methods>;
// }
//
//枚举也不可以被混淆
//-keep enum com.zengge.wifi.LwlEventBus.ThreadMode { *; }

public class EventBus {

    private final Subject<Object> bus;
    private final Map<String, List<Disposable>> disposableArray;
    private final Map<Class<?>, Object> stickyEvents;

    public EventBus() {
        stickyEvents = new ConcurrentHashMap<>();
        disposableArray = new HashMap<>();
        bus = PublishSubject.create().toSerialized();
    }

    private static volatile EventBus mInstance;

    public static EventBus getDefault() {
        if (mInstance == null) {
            synchronized (EventBus.class) {
                if (mInstance == null) {
                    mInstance = new EventBus();
                }
            }
        }
        return mInstance;
    }

    public void register(@NonNull final Object observable) {
        boolean isRegisterSuccess = false;
        final String subscriptionKey = observable.getClass().getName();
        Method[] methods = observable.getClass().getMethods();
        for (Method method : methods) {
            if (!method.isAnnotationPresent(Subscribe.class)) continue;
            Subscribe sub = method.getAnnotation(Subscribe.class);
            final Method subscriptionMethod = method;
            Class<?> key = method.getParameterTypes()[0];
            Disposable disposable = bus.mergeWith(Observable.create(emitter -> {
                Object o = stickyEvents.get(key);
                if (o != null) {
                    emitter.onNext(o);
                }
            })).ofType(key).observeOn(ThreadMode.getScheduler(sub.thread())).subscribe(o -> {
                try {
                    subscriptionMethod.setAccessible(true);
                    subscriptionMethod.invoke(observable, o);
                } catch (Exception e) {
                    throw new RuntimeException(subscriptionKey + " isn't allowed to register!Cause :" + e.toString());
                }
            });
            List<Disposable> disposables;
            if (disposableArray.containsKey(subscriptionKey)) {
                disposables = disposableArray.get(subscriptionKey);
            } else {
                disposables = new ArrayList<>();
            }
            disposables.add(disposable);
            disposableArray.put(subscriptionKey, disposables);
            isRegisterSuccess = true;
        }
        if (!isRegisterSuccess)
            throw new RuntimeException(subscriptionKey + " has no any subscribed events!");
    }

    public void unregister(Object observable) {
        String subscriptionKey = observable.getClass().getName();
        if (!disposableArray.containsKey(subscriptionKey)) return;
        List<Disposable> disposables = disposableArray.get(subscriptionKey);
        if (disposables != null) {
            for (Disposable disposable : disposables) {
                if (!disposable.isDisposed()) disposable.dispose();
            }
        }
        disposableArray.remove(subscriptionKey);
    }

    public void post(Object event) {
        bus.onNext(event);
    }

    public void postSticky(Object event) {
        synchronized (stickyEvents) {
            stickyEvents.put(event.getClass(), event);
        }
        post(event);
    }

    public void removeAllStickyEvents() {
        synchronized (stickyEvents) {
            stickyEvents.clear();
        }
    }
}