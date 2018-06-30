/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alipay.sofa.jarslink.integration.test;

import com.alipay.sofa.jarslink.integration.command.JSKCommandProvider;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author qilong.zql
 * @since 0.1.0
 */
public class CommandProviderTest {

    @Test
    public void test() {
        JSKCommandProvider commandProvider = new JSKCommandProvider();
        commandProvider.getHelp();
        commandProvider.getHelp("install");
        commandProvider.getHelp("uninstall");
        commandProvider.getHelp("check");
        commandProvider.getHelp("switch");
        commandProvider.getHelp("log");

        Assert.assertTrue(commandProvider.validate("install  -biz   file://xxx"));
        Assert.assertTrue(commandProvider.validate("install  -b   file://xxx"));
        Assert.assertFalse(commandProvider.validate("install  -b   file://xxx file://xxx"));
        Assert.assertFalse(commandProvider.validate("install  -x   file://xxx"));
        Assert.assertFalse(commandProvider.validate("install  "));
        Assert.assertFalse(commandProvider.validate("install  file://xxx"));
        Assert.assertFalse(commandProvider.validate("x -b   file://xxx"));

        Assert.assertTrue(commandProvider.validate("uninstall  -biz  -n xx -v yy"));
        Assert.assertTrue(commandProvider.validate("uninstall  -biz  -name xx -version yy"));
        Assert.assertFalse(commandProvider.validate("uninstall  -biz  -name yy"));
        Assert.assertFalse(commandProvider.validate("uninstall  -biz  xx yy"));

        Assert.assertTrue(commandProvider.validate("check -biz"));
        Assert.assertTrue(commandProvider.validate("check -biz -n xx"));
        Assert.assertTrue(commandProvider.validate("check -biz -n xx -v yy"));
        Assert.assertFalse(commandProvider.validate("check -biz -n xx yy"));
        Assert.assertFalse(commandProvider.validate("check -biz -v xx"));
        Assert.assertFalse(commandProvider.validate("check -biz -n"));
        Assert.assertFalse(commandProvider.validate("check -biz  xx yy"));

    }

}