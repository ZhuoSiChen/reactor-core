package mytest;

import java.time.Duration;
import java.util.function.Consumer;

import ch.qos.logback.core.util.ExecutorServiceUtil;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

public class OneSourceAndMutilSubsciber {

	/**
	 * out put
	 * main
	 * bbbbbbbbbb-7
	 * 1
	 * bbbbbbbbbb-7
	 * 2
	 * aaaaaaaaaaa-8
	 * 1bbbbbbbbbb-7
	 * aaaaaaaaaaa-8
	 * 1bbbbbbbbbb-7aaaaaaaaaaa-8
	 * aaaaaaaaaaa-8
	 * 2bbbbbbbbbb-7
	 * aaaaaaaaaaa-8
	 * 2bbbbbbbbbb-7aaaaaaaaaaa-8
	 * CCCCCCCCC-6
	 * 1bbbbbbbbbb-7aaaaaaaaaaa-8aaaaaaaaaaa-8
	 * CCCCCCCCC-6
	 * 1bbbbbbbbbb-7aaaaaaaaaaa-8aaaaaaaaaaa-8CCCCCCCCC-6
	 * 1bbbbbbbbbb-7aaaaaaaaaaa-8aaaaaaaaaaa-8CCCCCCCCC-6CCCCCCCCC-6CCCCCCCCC-6
	 * 1bbbbbbbbbb-7aaaaaaaaaaa-8aaaaaaaaaaa-8CCCCCCCCC-6CCCCCCCCC-6
	 * CCCCCCCCC-6
	 * 2bbbbbbbbbb-7aaaaaaaaaaa-8aaaaaaaaaaa-8
	 * CCCCCCCCC-6
	 * 2bbbbbbbbbb-7aaaaaaaaaaa-8aaaaaaaaaaa-8CCCCCCCCC-6
	 * 2bbbbbbbbbb-7aaaaaaaaaaa-8aaaaaaaaaaa-8CCCCCCCCC-6CCCCCCCCC-6CCCCCCCCC-6
	 * 2bbbbbbbbbb-7aaaaaaaaaaa-8aaaaaaaaaaa-8CCCCCCCCC-6CCCCCCCCC-6
	 * main
	 * 1
	 * 1
	 *
	 * @throws InterruptedException
	 */
	@Test
	public void testOneSourceAndMutilSSubscriber() throws InterruptedException {
		System.out.println(Thread.currentThread().getName());
		Flux.range(1,2)
			.map(l -> {
			System.out.println(Thread.currentThread().getName());
			System.out.println(l);
			return l + Thread.currentThread().getName();
		})
			.publishOn(Schedulers.newElastic("aaaaaaaaaaa"))
			.map(i -> {
				System.out.println(Thread.currentThread().getName());
				System.out.println(i);
			return i+Thread.currentThread().getName();
		})
			.subscribeOn(Schedulers.newElastic("bbbbbbbbbb"))
				.map(i->{
					System.out.println(Thread.currentThread().getName());
					System.out.println(i);
					return i+Thread.currentThread().getName();
				})
				.publishOn(Schedulers.newElastic("CCCCCCCCC"))
				.map(i->{
					System.out.println(Thread.currentThread().getName());
					System.out.println(i);
					return i+Thread.currentThread().getName();
				})
				.subscribeOn(Schedulers.newElastic("dDDDDD"))
				.map(i->{
					System.out.println(Thread.currentThread().getName());
					System.out.println(i);
					return i+Thread.currentThread().getName();
				})
			.subscribe(i->{
				System.out.println(i+Thread.currentThread().getName());
				System.out.println(i);
			});
		Thread.sleep(10000);
		System.out.println(Thread.currentThread().getName());

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
