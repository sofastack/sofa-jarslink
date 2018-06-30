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
package com.alipay.sofa.jarslink.runtime.test;

import com.alipay.sofa.jarslink.runtime.command.DefaultCommandParser;
import com.alipay.sofa.jarslink.spi.command.Command;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author qilong.zql
 * @since 0.1.0
 */
public class CommandParserTest {

    @Test
    public void test() {
        DefaultCommandParser commandParser = new DefaultCommandParser();

        try {
            commandParser.parseCommand(" ");
        } catch (Exception ex) {
            Assert.assertTrue(ex.getMessage().contains("Command input must not be empty"));
        }

        Assert.assertNotNull(commandParser.parseCommand("install "));
        Assert.assertNotNull(commandParser.parseCommand("uninstall "));
        Assert.assertNotNull(commandParser.parseCommand("check"));
        Assert.assertNotNull(commandParser.parseCommand("switch"));
        Assert.assertNull(commandParser.parseCommand("unknown"));

        // install command
        Command command;
        command = commandParser.parseCommand("install -b file:\\xxx");
        Assert.assertTrue(command.validate());

        command = commandParser.parseCommand("install -biz file:\\xxx");
        Assert.assertTrue(command.validate());

        command = commandParser.parseCommand("install -biz -b file:\\xxx");
        Assert.assertFalse(command.validate());

        command = commandParser.parseCommand("install -biz file:\\xxx -b file:\\yyy");
        Assert.assertFalse(command.validate());

        command = commandParser.parseCommand("install -biz file:\\xxx file:\\yyy");
        Assert.assertFalse(command.validate());

        command = commandParser.parseCommand("install -x file:\\xxx");
        Assert.assertFalse(command.validate());

        command = commandParser.parseCommand("install file:\\xxx");
        Assert.assertFalse(command.validate());

        // uninstall command
        command = commandParser.parseCommand("uninstall -b file:\\xxx");
        Assert.assertFalse(command.validate());

        command = commandParser.parseCommand("uninstall -b file:\\xxx -v 1.0 -n xx");
        Assert.assertFalse(command.validate());

        command = commandParser.parseCommand("uninstall -v 1.0 -n xx");
        Assert.assertFalse(command.validate());

        command = commandParser.parseCommand("uninstall -biz -n xx");
        Assert.assertFalse(command.validate());

        command = commandParser.parseCommand("uninstall -b -v 1.0 -n xx");
        Assert.assertTrue(command.validate());

        // check command
        command = commandParser.parseCommand("check -b");
        Assert.assertTrue(command.validate());

        command = commandParser.parseCommand("check -biz");
        Assert.assertTrue(command.validate());

        command = commandParser.parseCommand("check -biz -name xx");
        Assert.assertTrue(command.validate());

        command = commandParser.parseCommand("check -biz -name xx -version yy");
        Assert.assertTrue(command.validate());

        command = commandParser.parseCommand("check -biz -version yy");
        Assert.assertFalse(command.validate());

        command = commandParser.parseCommand("check -biz -v");
        Assert.assertFalse(command.validate());

        command = commandParser.parseCommand("check -biz -x");
        Assert.assertFalse(command.validate());

        // switch command
        command = commandParser.parseCommand("switch -biz -n xx -v yy");
        Assert.assertTrue(command.validate());

        command = commandParser.parseCommand("switch -b -v xx -name yyy ");
        Assert.assertTrue(command.validate());

        command = commandParser.parseCommand("switch -n xx -v yy");
        Assert.assertFalse(command.validate());

        command = commandParser.parseCommand("switch -b -n xx");
        Assert.assertFalse(command.validate());

        command = commandParser.parseCommand("switch -b -v xx");
        Assert.assertFalse(command.validate());

        command = commandParser.parseCommand("switch -b -v xx -name yyy -z");
        Assert.assertFalse(command.validate());
    }
}