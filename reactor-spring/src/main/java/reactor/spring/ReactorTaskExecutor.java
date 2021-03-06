/*
 * Copyright (c) 2011-2013 GoPivotal, Inc. All Rights Reserved.
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

package reactor.spring;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import reactor.core.Reactor;
import reactor.fn.Consumer;
import reactor.fn.Event;
import reactor.fn.selector.Selector;
import reactor.fn.tuples.Tuple2;

import java.util.concurrent.Executor;

import static reactor.fn.Functions.$;

/**
 * A {@link TaskExecutor} implementation that uses a {@link Reactor} to dispatch and execute tasks.
 *
 * @author Jon Brisbin
 */
public class ReactorTaskExecutor implements TaskExecutor, Executor {

	private final Tuple2<Selector, Object> exec = $();
	private final Reactor reactor;

	@Autowired
	public ReactorTaskExecutor(Reactor reactor) {
		this.reactor = reactor;

		this.reactor.on(exec.getT1(), new Consumer<Event<Runnable>>() {
			@Override
			public void accept(Event<Runnable> ev) {
				ev.getData().run();
			}
		});
	}

	@Override
	public void execute(Runnable task) {
		reactor.notify(exec.getT2(), Event.wrap(task));
	}

}
