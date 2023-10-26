import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.observers.DefaultObserver;
import io.reactivex.rxjava3.subjects.AsyncSubject;
import io.reactivex.rxjava3.subjects.BehaviorSubject;
import io.reactivex.rxjava3.subjects.PublishSubject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author sichaolong
 * @createdate 2023/10/26 14:42
 */
public class Demo2 {

    private static Logger logger = LoggerFactory.getLogger(Demo2.class);

    public static void main(String[] args) {

        // testAsyncSubject();
        // testPublishSubject();
        testBehaviorSubject();

    }

    /**
     * 测试被观察者是AsyncSubject
     * 特点：观察者只能收到最后一次被观察者发送的数据
     */
    public static void testAsyncSubject() {
        // 被观察者,只会发送距离onComplete()最近的那个数据
        AsyncSubject<Object> asyncObservable = AsyncSubject.create();
        asyncObservable.onNext(1);

        // 即充当观察者，有充当DefaultObserver的被观察者
        AsyncSubject<Object> observer = AsyncSubject.create();

        // 连接第一对
        asyncObservable.subscribe(observer);

        // 连接第二对
        observer.subscribe(new DefaultObserver<Object>() {
            @Override
            public void onNext(@NonNull Object o) {
                logger.info("对 onNext 事件做出响应：{} ", o);
            }

            @Override
            public void onError(@NonNull Throwable e) {
            }

            @Override
            public void onComplete() {
            }
        });

        asyncObservable.onNext(2);
        asyncObservable.onNext("最后一个需要发送的数据，后面需要 onComplete()");
        asyncObservable.onComplete();

    }

    /**
     * 测试被观察者是PublishSubject
     * 特点：观察者只能收到连接之后发送的数据
     */
    public static void testPublishSubject() {
        // 被观察者,只会发送距离onComplete()最近的那个数据
        PublishSubject<Object> publishObservable = PublishSubject.create();
        publishObservable.onNext(1);

        // 即充当观察者，有充当DefaultObserver的被观察者
        PublishSubject<Object> observer = PublishSubject.create();

        // 连接第一对
        publishObservable.subscribe(observer);

        // 连接第二对
        observer.subscribe(new DefaultObserver<Object>() {
            @Override
            public void onNext(@NonNull Object o) {
                logger.info("对 onNext 事件做出响应：{} ", o);
            }

            @Override
            public void onError(@NonNull Throwable e) {
            }

            @Override
            public void onComplete() {
            }
        });

        publishObservable.onNext(2);
        publishObservable.onNext(3);
        publishObservable.onComplete();

    }


    /**
     * 测试被观察者是BehaviorSubject
     * 特点：相比PublishSubject对连接和发送数据的顺序没有要求，都能收到消息

     */
    public static void testBehaviorSubject() {
        // 被观察者,只会发送距离onComplete()最近的那个数据
        BehaviorSubject<Object> behaviorObservable = BehaviorSubject.createDefault(0);
        behaviorObservable.onNext(1);


        // 第一个观察者
        behaviorObservable.subscribe(new DefaultObserver<Object>() {
            @Override
            public void onNext(@NonNull Object o) {
                logger.info("观察者1 对 onNext 事件做出响应：{} ", o);
            }

            @Override
            public void onError(@NonNull Throwable e) {
            }

            @Override
            public void onComplete() {
            }
        });

        behaviorObservable.onNext(2);

        // 第二个观察者
        behaviorObservable.subscribe(new DefaultObserver<Object>() {
            @Override
            public void onNext(@NonNull Object o) {
                logger.info("观察者2 对 onNext 事件做出响应：{} ", o);
            }

            @Override
            public void onError(@NonNull Throwable e) {
            }

            @Override
            public void onComplete() {
            }
        });

        behaviorObservable.onNext(3);
        behaviorObservable.onComplete();

    }


}
