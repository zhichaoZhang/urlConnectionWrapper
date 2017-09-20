package in.joye.urlconnection;

import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Process;
import android.support.annotation.NonNull;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

/**
 * 平台差异
 * <p>
 * Created by joye on 2017/9/17.
 */

public abstract class Platform {
    static final String THREAD_PREFIX = "UrlConnectionWrapper-";
    static final String IDLE_THREAD_NAME = THREAD_PREFIX + "idle";

    private static final Platform PLATFORM = findPlatform();

    public static Platform get() {
        return PLATFORM;
    }

    private static Platform findPlatform() {
        try {
            Class.forName("android.os.Build");
            if (Build.VERSION.SDK_INT != 0) {
                return new Android();
            }
        } catch (ClassNotFoundException e) {
        }
        return new Base();
    }

    abstract Executor defaultHttpExecutor();


    abstract Executor defaultCallbackExecutor();

    private static class Android extends Platform {

        @Override
        Executor defaultHttpExecutor() {
            return Executors.newCachedThreadPool(new ThreadFactory() {
                @Override
                public Thread newThread(@NonNull final Runnable r) {
                    return new Thread(new Runnable() {
                        @Override
                        public void run() {
                            Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
                            r.run();
                        }
                    }, IDLE_THREAD_NAME);
                }
            });
        }

        @Override
        Executor defaultCallbackExecutor() {
            return new Executor() {
                final Handler handler = new Handler(Looper.getMainLooper());

                @Override
                public void execute(@NonNull Runnable command) {
                    handler.post(command);
                }
            };
        }
    }

    private static class Base extends Platform {

        @Override
        Executor defaultHttpExecutor() {
            return Executors.newCachedThreadPool(new ThreadFactory() {
                @Override
                public Thread newThread(@NonNull final Runnable r) {
                    return new Thread(new Runnable() {
                        @Override
                        public void run() {
                            Thread.currentThread().setPriority(Thread.MIN_PRIORITY);
                            r.run();
                        }
                    }, IDLE_THREAD_NAME);
                }
            });
        }

        @Override
        Executor defaultCallbackExecutor() {
            return new Executor() {
                @Override
                public void execute(@NonNull Runnable command) {
                    command.run();
                }
            };
        }
    }
}
