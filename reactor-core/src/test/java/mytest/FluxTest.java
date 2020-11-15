package mytest;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;

public class FluxTest {

	@Test
	public void TestFluxNever(){
		Flux<Integer> just = Flux.just(1);
		;
		just.map(integer -> integer.toString()).subscribe(string ->System.out.println(string));
		Flux.never().subscribe(o -> {
			System.out.println("aaaaaaaaa");
		});
	}


	@Test
	public void TestBackPress(){

	}


	@Test
	public void testGenerate2() {
		Flux.generate(
				() -> 1,    // 1
				(count, sink) -> {      // 2
					sink.next(count + " : " + new Date());
					try {
						TimeUnit.SECONDS.sleep(1);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					if (count >= 5) {
						sink.complete();
					}
					return count + 1;   // 3
				}).subscribe(System.out::println);

	}
}
