package org.jbehave.core.embedder.executors;

import org.jbehave.core.embedder.EmbedderControls;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ExecutorService;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;

class ExecutorsBehaviour {

    @Test
    void shouldCreateExecutors() {
        assertThat(new FixedThreadExecutors().create(new EmbedderControls()), instanceOf(ExecutorService.class));
        assertThat(new DirectExecutorService().create(new EmbedderControls()), instanceOf(ExecutorService.class));
    }
  
}
