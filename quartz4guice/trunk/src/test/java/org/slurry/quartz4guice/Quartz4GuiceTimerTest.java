/*
 *    Copyright 2009-2010 The slurry Team
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package org.slurry.quartz4guice;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slurry.quartz4guice.module.ScheduleModule;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.internal.Stopwatch;

/**
 * 
 * @version $Id$
 */
public class Quartz4GuiceTimerTest {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private InterfaceContainingTimedTask timedTask;

    @Before
    public void beforeTest() {
        Injector injector = Guice.createInjector(new ScheduleModule(), new GuiceModule());
        this.timedTask = injector.getInstance(InterfaceContainingTimedTask.class);
    }

    @Test
    public void minimalTest() throws InterruptedException {
        this.logger.info("Timer test starting");

        Stopwatch stopwatch = new Stopwatch();
        Thread.sleep(5000);
        Assert.assertEquals(2, this.timedTask.getInvocationsTimedTaskA());

        this.logger.info("Done checking task A {} ms ", stopwatch.reset());
    }

}
