package scl.demos.agent.async;

/**
 * @author sichaolong
 * @createdate 2024/6/21 14:22
 */

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Spliterators;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public interface AsyncGenerator<E> extends Iterable<E> {
    Data<E> next();

    static <E> AsyncGenerator<E> empty() {
        return Data::done;
    }

    static <E, U> AsyncGenerator<U> map(Iterator<E> iterator, Function<E, CompletableFuture<U>> mapFunction) {
        return () -> {
            return !iterator.hasNext() ? AsyncGenerator.Data.done() : AsyncGenerator.Data.of((CompletableFuture)mapFunction.apply(iterator.next()));
        };
    }

    static <E, U> AsyncGenerator<U> collect(Iterator<E> iterator, BiConsumer<E, Consumer<CompletableFuture<U>>> consumer) {
        List<CompletableFuture<U>> accumulator = new ArrayList();
        Consumer<CompletableFuture<U>> addElement = accumulator::add;

        while(iterator.hasNext()) {
            consumer.accept(iterator.next(), addElement);
        }

        Iterator<CompletableFuture<U>> it = accumulator.iterator();
        return () -> {
            return !it.hasNext() ? AsyncGenerator.Data.done() : AsyncGenerator.Data.of((CompletableFuture)it.next());
        };
    }

    static <E, U> AsyncGenerator<U> map(Collection<E> collection, Function<E, CompletableFuture<U>> mapFunction) {
        return collection != null && !collection.isEmpty() ? map(collection.iterator(), mapFunction) : empty();
    }

    static <E, U> AsyncGenerator<U> collect(Collection<E> collection, BiConsumer<E, Consumer<CompletableFuture<U>>> consumer) {
        return collection != null && !collection.isEmpty() ? collect(collection.iterator(), consumer) : empty();
    }

    default CompletableFuture<Void> toCompletableFuture() {
        Data<E> next = this.next();
        return next.done ? CompletableFuture.completedFuture((Void) null) : next.data.thenCompose((v) -> {
            return this.toCompletableFuture();
        });
    }

    default CompletableFuture<Void> forEachAsync(Consumer<E> consumer) {
        Data<E> next = this.next();
        return next.done ? CompletableFuture.completedFuture((Void) null) : next.data.thenApply((v) -> {
            consumer.accept(v);
            return null;
        }).thenCompose((v) -> {
            return this.forEachAsync(consumer);
        });
    }

    default <R extends List<E>> CompletableFuture<R> collectAsync(R result, Consumer<E> consumer) {
        Data<E> next = this.next();
        return next.done ? CompletableFuture.completedFuture((R) null) : next.data.thenApply((v) -> {
            consumer.accept(v);
            result.add(v);
            return null;
        }).thenCompose((v) -> {
            return this.collectAsync(result, consumer);
        });
    }

    default Stream<E> stream() {
        return StreamSupport.stream(Spliterators.spliteratorUnknownSize(this.iterator(), 16), false);
    }

    default Iterator<E> iterator() {
        return new Iterator<E>() {
            private final AtomicReference<Data<E>> currentFetchedData = new AtomicReference();

            {
                this.currentFetchedData.set(AsyncGenerator.this.next());
            }

            public boolean hasNext() {
                Data<E> value = (Data)this.currentFetchedData.get();
                return value != null && !value.done;
            }

            public E next() {
                Data<E> next = (Data)this.currentFetchedData.get();
                if (next != null && !next.done) {
                    next = (Data)this.currentFetchedData.getAndUpdate((v) -> {
                        return AsyncGenerator.this.next();
                    });
                    return next.data.join();
                } else {
                    throw new IllegalStateException("no more elements into iterator");
                }
            }
        };
    }

    public static class Data<E> {
        final CompletableFuture<E> data;
        final boolean done;

        public Data(CompletableFuture<E> data, boolean done) {
            this.data = data;
            this.done = done;
        }

        public static <E> Data<E> of(CompletableFuture<E> data) {
            return new Data(data, false);
        }

        public static <E> Data<E> done() {
            return new Data((CompletableFuture)null, true);
        }
    }
}
