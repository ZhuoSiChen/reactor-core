/*
 * Copyright (c) 2011-2016 Pivotal Software Inc, All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package reactor;

import java.util.Collections;
import java.util.Queue;
import java.util.concurrent.LinkedTransferQueue;
import java.util.logging.Level;

import org.junit.Assert;
import org.junit.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Hooks;
import reactor.core.publisher.Mono;
import reactor.core.publisher.OperatorAdapter;
import reactor.core.publisher.Operators;

/**
 * @author Stephane Maldini
 */
public class HooksTest {

	void simpleFlux(){
		Flux.just(1)
		    .map(d -> d + 1)
		    .doOnNext(d -> {throw new RuntimeException("test");})
		    .collectList()
		    .otherwiseReturn(Collections.singletonList(2))
		    .block();
	}

	static final class TestException extends RuntimeException {

		public TestException(String message) {
			super(message);
		}
	}

	@Test
	public void errorHooks() throws Exception {

		Hooks.onOperatorError((e, s) -> new TestException(s.toString()));
		Hooks.onNextDropped(d -> {
			throw new TestException(d.toString());
		});
		Hooks.onErrorDropped(e -> {
			throw new TestException("errorDrop");
		});

		Throwable w = Operators.onOperatorError(null, new Exception(), "hello");

		Assert.assertTrue(w instanceof TestException);
		Assert.assertTrue(w.getMessage()
		                   .equals("hello"));

		try {
			Operators.onNextDropped("hello");
			Assert.fail();
		}
		catch (Throwable t) {
			t.printStackTrace();
			Assert.assertTrue(t instanceof TestException);
			Assert.assertTrue(t.getMessage()
			                   .equals("hello"));
		}

		try {
			Operators.onErrorDropped(new Exception());
			Assert.fail();
		}
		catch (Throwable t) {
			Assert.assertTrue(t instanceof TestException);
			Assert.assertTrue(t.getMessage()
			                   .equals("errorDrop"));
		}

		Hooks.resetOnOperatorError();
		Hooks.resetOnNextDropped();
		Hooks.resetOnErrorDropped();
	}

	@Test
	public void verboseExtension() {
		Queue<String> q = new LinkedTransferQueue<>();

		Hooks.onOperator(hooks -> hooks.operatorStacktrace()
		                               .doOnEach(d -> q.offer(hooks.publisher() + ": " + d),
				                               t -> q.offer(hooks.publisher() + "! " +
						                               (t.getSuppressed().length != 0)),
				                               null,
				                               null));

		simpleFlux();

		Assert.assertArrayEquals(q.toArray(),
				new String[]{"Just: 1", "{ operator : \"MapFuseable\" }: 2",
						"{ operator : \"PeekFuseable\" }! false",
						"{ operator : \"CollectList\" }! true", "Just: [2]",
						"{ operator : \"Otherwise\" }: [2]"});

		q.clear();

		Hooks.onOperator(hooks -> hooks.log("", Level.INFO)
		                               .doOnEach(d -> q.offer(hooks.publisher() + ": " + d),
				                               t -> q.offer(hooks.publisher() + "! " +
						                               (t.getSuppressed().length != 0)),
				                               null,
				                               null));

		simpleFlux();

		Assert.assertArrayEquals(q.toArray(),
				new String[]{"Just: 1", "{ operator : \"MapFuseable\" }: 2",
						"{ operator : \"PeekFuseable\" }! false",
						"{ operator : \"CollectList\" }! false", "Just: [2]",
						"{ operator : \"Otherwise\" }: [2]"});

		q.clear();

		Hooks.onOperator(hooks -> hooks.log("", Level.INFO));

		simpleFlux();

		Assert.assertArrayEquals(q.toArray(), new String[0]);

		q.clear();

		Hooks.resetOnOperator();

		simpleFlux();
	}


	@Test
	public void testTrace() throws Exception {
		Hooks.onOperator(Hooks.HookOptions::operatorStacktrace);
		try {
			Mono.fromCallable(() -> {
				throw new RuntimeException();
			})
			    .map(d -> d)
			    .block();
		}
		catch(Exception e){
			e.printStackTrace();
			Assert.assertTrue(e.getSuppressed()[0].getMessage().contains("MonoCallable"));
			return;
		}
		finally {
			Hooks.resetOnOperator();
		}
		throw new IllegalStateException();
	}


	@Test
	public void testTrace2() throws Exception {
		Hooks.onOperator(hooks -> hooks.ifOperatorName("map")
		                               .operatorStacktrace());
		try {
			Mono.just(1)
			    .map(d -> {
				    throw new RuntimeException();
			    })
			    .filter(d -> true)
			    .doOnNext(d -> System.currentTimeMillis())
			    .map(d -> d)
			    .block();
		}
		catch(Exception e){
			e.printStackTrace();
			Assert.assertTrue(e.getSuppressed()[0].getMessage().contains
					("HooksTest.java:"));
			Assert.assertTrue(e.getSuppressed()[0].getMessage().contains("|_\tMono.map" +
					"(HooksTest.java:"));
			return;
		}
		finally {
			Hooks.resetOnOperator();
		}
		throw new IllegalStateException();
	}

}
