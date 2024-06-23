/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to you under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.jmeter.treebuilder

import org.apache.jmeter.reporters.Summariser
import org.apache.jmeter.testelement.TestElement
import org.apache.jmeter.testelement.TestPlan
import org.apache.jmeter.threads.ThreadGroup
import org.apache.jmeter.treebuilder.dsl.testTree
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

class TreeBuilderTest {
    @Test
    fun `extending DSL`() {
        fun TreeBuilder.threadGroup(
            name: String,
            numThreads: Int = 1,
            rampUp: Duration = 3.seconds,
            body: Action<ThreadGroup>
        ) {
            ThreadGroup::class {
                this.name = name
                this.numThreads = numThreads
                this.rampUp = rampUp.inWholeSeconds.toInt()
                +Summariser::class
                body(this)
            }
        }

        testTree {
            TestPlan::class {
                threadGroup(name = "Test Thread", numThreads = 5, rampUp = 1.seconds) {
                    // other elements
                }
            }
        }
    }

    @Test
    fun configureEach() {
        val tree = testTree {
            configureEach<TestElement> {
                name = "sample name"
            }
            +TestPlan::class
        }

        Assertions.assertEquals(
            "sample name",
            (tree.list().firstOrNull() as? TestPlan)?.props?.get { name },
            "Name should be generated by configureEach action"
        )
    }

    @Test
    fun autonumber() {
        val autonumber = object : Action<TestElement> {
            private var seq = 1

            override fun TestElement.execute() {
                name = "$name $seq"
                seq += 1
            }
        }

        val tree = testTree {
            configureEach<TestElement>(autonumber)
            TestPlan::class {
                name = "Named plan"
            }
            TestPlan::class {
                name = "Another plan"
            }
            // Unnamed plan
            +TestPlan::class
        }

        Assertions.assertEquals(
            "Named plan 1, Another plan 2,  3",
            tree.list()
                .asSequence()
                .filterIsInstance<TestPlan>()
                .joinToString { it.name.toString() },
            "Name should be generated by configureEach action"
        )
    }

    @Test
    fun `hierarchical names`() {
        val tree = testTree {
            configureEach<TestElement> {
                parent?.let {
                    name = "${it.name} / $name"
                }
            }
            TestPlan::class {
                name = "Test Plan"

                ThreadGroup::class {
                    name = "Thread Group"

                    +Summariser::class
                }
            }
        }

        Assertions.assertEquals(
            "[Test Plan, Test Plan / Thread Group, Test Plan / ]",
            listOf(
                tree.list().first().let { (it as TestElement).name },
                tree.values.first().list().first().let { (it as TestElement).name },
                tree.values.first().values.first().list().first().let { (it as TestElement).name },
            ).toString(),
            "Names should build up hierarchically"
        )
    }
}