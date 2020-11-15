package mytest;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;

import java.util.function.Consumer;

public class OneSourceAndMutilSubsciber {

    @Test
    public void testOneSourceAndMutilSSubscriber() {
        Flux<Integer> just = Flux.just(1);
        Consumer<Integer> integerConsumer = i -> System.out.println(i);
        just.subscribe(integerConsumer);
        just.subscribe(integerConsumer);
    }


    @Test
    public void indexBugTest() {
        System.out.println("aaaa");
//        int numOfItems = 20;
//
//        //this line causes an java.lang.ArrayIndexOutOfBoundsException unless there is a break point in ZipAction
//        // .createSubscriber()
//        TopicProcessor<String> ring = TopicProcessor.<String>builder().name("test").bufferSize(1024).build();
//
////        EmitterProcessor<String> ring = EmitterProcessor.create();
//
//
//        Flux<String> stream2 = ring
//                .zipWith(Mono.fromCallable(System::currentTimeMillis).repeat(), (t1, t2) ->
//                        String.format("%s : %s", t1, t2));
//
//        Mono<List<String>> p = stream2
//                .doOnNext(System.out::println)
//                .buffer(numOfItems)
//                .next();
//
//        for (int curr = 0; curr < numOfItems; curr++) {
//            if (curr % 5 == 0 && curr % 3 == 0) ring.onNext("FizBuz"+curr);
//            else if (curr % 3 == 0) ring.onNext("Fiz"+curr);
//            else if (curr % 5 == 0) ring.onNext("Buz"+curr);
//            else ring.onNext(String.valueOf(curr));
//        }

//        Assert.assertTrue("Has not returned list", p.block(Duration.ofSeconds(5)) !=null);

    }


}
