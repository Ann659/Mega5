package com.example.mega;

import android.os.CountDownTimer;

public abstract class CountDownTimerUtils extends CountDownTimer {
    /**
     * @param millisInFuture    The number of millis in the future from the call
     *                          to {@link #start()} until the countdown is done and {@link #onFinish()}
     *                          is called.
     * @param countDownInterval The interval along the way to receive
     *                          {@link #onTick(long)} callbacks.
     */
    public CountDownTimerUtils(long millisInFuture, long countDownInterval) {
        super(millisInFuture, countDownInterval);
    }

    public abstract void onTick(long millisUntilFinished);

    public abstract void onFinish();

    public interface TimerCallback {
        void onTick(long millisUntilFinished);
        void onFinish();
    }

    public static CountDownTimer createTimer(long millisInFuture, long countDownInterval, TimerCallback callback) {
        return new CountDownTimerUtils(millisInFuture, countDownInterval) {
            @Override
            public void onTick(long millisUntilFinished) {
                if (callback != null) {
                    callback.onTick(millisUntilFinished);
                }
            }

            @Override
            public void onFinish() {
                if (callback != null) {
                    callback.onFinish();
                }
            }
        };
    }
}
