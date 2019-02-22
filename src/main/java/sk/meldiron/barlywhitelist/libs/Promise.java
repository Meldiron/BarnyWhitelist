package sk.meldiron.barlywhitelist.libs;

public class Promise {
    public interface FinalPromiseCallback {
        void cb(Object[] data);
    }

    public interface AfterPromiseCallback {
            void close(Object obj);
    }

    public interface PromiseCallback {
        void cb(AfterPromiseCallback promise);
    }

    private AfterPromiseCallback finalCb;
    public Promise(PromiseCallback cb) {
        cb.cb((obj) -> {
            if(finalCb != null) {
                finalCb.close(obj);
            }
        });
    }

    public void setFinalCb(AfterPromiseCallback cb) {
        this.finalCb = cb;
    }

    public static void all(Promise[] promises, FinalPromiseCallback cb) {
        Integer total = promises.length;

        final Integer[] current = {0};

        Object[] objs = new Object[promises.length];

        for(Promise p : promises) {
            p.setFinalCb((obj) -> {
                objs[current[0]] = obj;

                current[0]++;


                if(current[0] >= total) {
                    cb.cb(objs);
                }
            });
        }
    }
}
