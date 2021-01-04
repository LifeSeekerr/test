import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class SyncRunner {
    private static final Executor executor = Executors.newCachedThreadPool(new ThreadFactory() {
        AtomicInteger num = new AtomicInteger(0);

        @Override
        public Thread newThread(Runnable r) {
            return new Thread(r, "AsyncCmd-" + num.incrementAndGet());
        }
    });
    protected static Logger logger = LoggerFactory.getLogger(SyncRunner.class);

//    public SyncRunner(Callable<String> task) {
//        this.task = task;
//        AbstractExecutor executor = AbstractExecutor.curThreadExecutor();
//        if (executor != null) {
//            executor.setSyncRunner(this);
//        }
//    }

    private static class Worker extends Thread {
        private final Process process;
        private Integer exit;
        private Worker(Process process) {
            this.process = process;
        }
        @Override
        public void run() {
            try {
                exit = process.waitFor();
            } catch (InterruptedException ignore) {
                return;
            }
        }
    }

    public static void main(String[] args) throws InterruptedException, IOException {
        Runtime r = Runtime.getRuntime();
        Process pro = null;
//        String[] cmd = {"ffmpeg", "-i", "/Users/luoweiheng/Documents/nts/videos/biemianqiang.mp4", "-c:v", "libx264", "-f", "mp4",
//                "/Users/luoweiheng/Documents/nts/test.mp4"};
        String[] cmd = {"ps", "-aux"};
        pro = r.exec(cmd);
        Worker worker = new Worker(pro);
        worker.start();
//        worker.join(30000);
        System.out.println(getPidOfProcess(pro));
        while (true) {
            boolean isAlive = pro.isAlive();
            System.out.println(isAlive);
            if (!isAlive) {
                System.out.println(pro.exitValue());
                return;
            }
            Thread.sleep(1000);
        }

    }

    public static synchronized long getPidOfProcess(Process p) {
        long pid = -1;

        try {
            if (p.getClass().getName().equals("java.lang.UNIXProcess")) {
                Field f = p.getClass().getDeclaredField("pid");
                f.setAccessible(true);
                pid = f.getLong(p);
                f.setAccessible(false);
            }
        } catch (Exception e) {
            pid = -1;
        }
        return pid;
    }
}
