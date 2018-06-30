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
package com.alipay.sofa.jarslink.runtime.command;

import com.alipay.sofa.ark.common.thread.CommonThreadPool;
import com.alipay.sofa.ark.common.thread.ThreadPoolManager;
import com.alipay.sofa.jarslink.common.log.JSKLogger;
import com.alipay.sofa.jarslink.common.log.JSKLoggerFactory;
import com.alipay.sofa.jarslink.spi.command.Command;
import com.alipay.sofa.jarslink.spi.command.CommandHandler;
import com.alipay.sofa.jarslink.spi.command.CommandParser;
import com.alipay.sofa.jarslink.spi.command.CommandType;
import com.alipay.sofa.jarslink.spi.constant.Constants;

/**
 * @author qilong.zql
 * @since 0.1.0
 */
public class DefaultCommandHandler implements CommandHandler {

    private static final JSKLogger LOGGER        = JSKLoggerFactory.getDefaultLogger();
    private CommandParser          commandParser = new DefaultCommandParser();

    public DefaultCommandHandler() {
        CommonThreadPool commandPool = new CommonThreadPool().setAllowCoreThreadTimeOut(true)
            .setThreadPoolName(Constants.JARSLINK_COMMAND_THREAD_POOL_NAME);
        ThreadPoolManager.registerThreadPool(Constants.JARSLINK_COMMAND_THREAD_POOL_NAME,
            commandPool);
    }

    @Override
    public String handleCommand(String cmdLine) {
        try {
            Command command = commandParser.parseCommand(cmdLine);
            if (command == null || !command.validate()) {
                return String.format("命令无效: %s", cmdLine);
            }
            LOGGER.info(String.format("Start to process command:\'%s\'.", cmdLine));
            return command.process();
        } catch (Throwable throwable) {
            LOGGER.error(String.format("Process command:\'%s\' fail.", cmdLine), throwable);
            return String
                .format("Process command:\'%s\' fail. Please check jarslink log.", cmdLine);
        } finally {
            LOGGER.info(String.format("End to process command:\'%s\'.", cmdLine));
        }
    }

    @Override
    public CommandType getCommandType(String cmdMarker) {
        return CommandType.getCommandType(cmdMarker);
    }

    @Override
    public boolean validate(String cmdLine) {
        try {
            Command command = commandParser.parseCommand(cmdLine);
            return command.validate();
        } catch (Throwable throwable) {
            return false;
        }
    }
}