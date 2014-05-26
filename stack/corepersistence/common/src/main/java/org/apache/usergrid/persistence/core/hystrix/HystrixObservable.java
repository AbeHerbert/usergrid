/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.usergrid.persistence.core.hystrix;


import com.netflix.config.ConfigurationManager;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandProperties;
import com.netflix.hystrix.HystrixObservableCommand;
import com.netflix.hystrix.HystrixThreadPoolProperties.Setter;

import rx.Observable;


/**
 * A utility class that creates graph observables wrapped in Hystrix for 
 * timeouts and circuit breakers.
 */
public class HystrixObservable {

//    static {
//        // TODO: can't we put these in the our normal properties file?
//        ConfigurationManager.getConfigInstance()
//            .setProperty("hystrix.command.default.execution.isolation.strategy","THREAD");
//        ConfigurationManager.getConfigInstance()
//            .setProperty("hystrix.threadpool.default.coreSize", 1032);
//    }

    /**
     * Command group used for realtime user commands
     */
    private static final HystrixCommandGroupKey USER_GROUP = 
            HystrixCommandGroupKey.Factory.asKey( "user" );

    /**
     * Command group for asynchronous operations
     */
    private static final HystrixCommandGroupKey ASYNC_GROUP = 
            HystrixCommandGroupKey.Factory.asKey( "async" );


    /**
     * Wrap the observable in the timeout for user facing operation.  
     * This is for user reads and deletes.
     */
    public static <T> Observable<T> user( final Observable<T> observable ) {

//        HystrixCommandProperties.Setter hcpSetter = HystrixCommandProperties.Setter()
//            .withExecutionIsolationStrategy(HystrixCommandProperties.ExecutionIsolationStrategy.THREAD);
//
//        final HystrixObservableCommand.Setter setter = HystrixObservableCommand.Setter
//            .withGroupKey(USER_GROUP)
//            .andCommandPropertiesDefaults(hcpSetter);

        return new HystrixObservableCommand<T>( USER_GROUP ) {

            @Override
            protected Observable<T> run() {
                return observable;
            }
        }.observe();
    }


    /**
     * Wrap the observable in the timeout for asynchronous operations.  
     * This is for compaction and cleanup processing.
     */
    public static <T> Observable<T> async( final Observable<T> observable ) {
        return new HystrixObservableCommand<T>( ASYNC_GROUP ) {

            @Override
            protected Observable<T> run() {
                return observable;
            }
        }.observe();
    }
}