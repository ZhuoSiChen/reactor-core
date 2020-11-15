package mytest;

import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.util.concurrent.atomic.AtomicLongFieldUpdater;


public abstract class MyFlux<T> implements Publisher<T> {

    public static <T> MyFlux<T> just(T... array) {
        return new MyFluxArray<>(array);
    }

    public static void main(String[] args) {
        MyFlux.just(1,2,3,4,5,6).subscribe(new MySubsciber());
    }
}



class MySubsciber<T> implements Subscriber<T> {

    public void onSubscribe(Subscription s){
        //全都拿出来的订阅者
        s.request(Long.MAX_VALUE);
    }

    @Override
    public void onNext(T o) {
        System.out.println(o);
    }

    @Override
    public void onError(Throwable t) {
        System.out.println(t);
    }

    @Override
    public void onComplete() {
        System.out.println("onComplete");
    }
}

class MyFluxArray<T> extends MyFlux<T>{
    T[] arr;
    public MyFluxArray(T[] arr) {
        this.arr = arr;
    }

    @Override
    public void subscribe(Subscriber<? super T> s) {
        s.onSubscribe(new MyArraySubscription(s,arr));
    }

   static class MyArraySubscription<T> implements Subscription{
        final Subscriber<? super T> actual;

        final T[] array;

        int index;

        volatile boolean cancelled;

        volatile long requested;

        @SuppressWarnings("rawtypes")
        static final AtomicLongFieldUpdater<MyArraySubscription> REQUESTED =
                AtomicLongFieldUpdater.newUpdater(MyArraySubscription.class, "requested");

       MyArraySubscription(Subscriber<? super T> actual, T[] array) {
            this.actual = actual;
            this.array = array;
        }

       @Override
       public void request(long n) {
           for (int i = 0; i < this.array.length; i++) {
                this.actual.onNext(this.array[i]);
           }
       }

       @Override
       public void cancel() {

       }
   }
}


