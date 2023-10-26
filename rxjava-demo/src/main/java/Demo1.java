import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableEmitter;
import io.reactivex.rxjava3.core.ObservableOnSubscribe;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author sichaolong
 * @createdate 2023/10/26 13:46
 */

public class Demo1 {

    private static Logger  logger = LoggerFactory.getLogger(Demo1.class);

    public static void main(String[] args) {
        // 1、创建被观察者（数据源）
        Observable observable = testCreateObservable();

        // 2、创建观察者（数据处理器）
        Observer observer = testCreateObserver();

        // 3、连接，数据源主动连接数据处理器
        observable.subscribe(observer);

    }


    public static Observer testCreateObserver(){

        // 方式1
        Observer observer = new Observer() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {
                logger.info("观察者接收事件前，默认最先调用复写 onSubscribe()");

                logger.info("观察者 dispose 状态：{}",d.isDisposed());
                // d.dispose();
                // logger.info("观察者 dispose 状态：{}",d.isDisposed());



            }

            @Override
            public void onNext(@NonNull Object o) {
                logger.info("对 onNext 事件做出响应：{} ",o);
            }

            @Override
            public void onError(@NonNull Throwable e) {
                logger.info("对 onError 事件做出响应：{} ",e);
            }

            @Override
            public void onComplete() {
                logger.info("对 onComplete 事件做出响应 ");
            }
        };

        // 方式2
        // 说明：Subscriber类 = RxJava 内置的一个实现了 Observer 的抽象类，对 Observer 接口进行了扩展
        Subscriber subscriber = new Subscriber() {
            @Override
            public void onSubscribe(Subscription subscription) {
                logger.info("对 onSubscribe 事件做出响应：{} ",subscription);

            }

            @Override
            public void onNext(@NonNull Object o) {
                logger.info("对 onNext 事件做出响应：{} ",o);
            }

            @Override
            public void onError(@NonNull Throwable e) {
                logger.info("对 onError 事件做出响应：{} ",e);
            }

            @Override
            public void onComplete() {
                logger.info("对 onComplete 事件做出响应 ");
            }
        };

        return observer;
    }





    /**
     * 测试创建被观察者
     * @return
     */
    public static Observable testCreateObservable(){
        // 方式1
        Observable observable1 = Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Integer> emitter) throws Throwable {
                for (int i = 0; i < 1000; i++) {
                    Thread.sleep(1000);
                    emitter.onNext(i);
                }
                emitter.onComplete();
            }
        });

        // 方式2
        Observable<Integer> observable2 = Observable.just(1, 2, 3);

        // 方式3
        Observable<Integer> observable3 = Observable.fromArray(1,2,3,4);

        return observable1;

    }
}
