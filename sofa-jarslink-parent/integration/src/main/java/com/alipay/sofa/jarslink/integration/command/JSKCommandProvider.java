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
package com.alipay.sofa.jarslink.integration.command;

import com.alipay.sofa.ark.spi.service.session.CommandProvider;
import com.alipay.sofa.jarslink.runtime.command.DefaultCommandHandler;
import com.alipay.sofa.jarslink.spi.command.CommandHandler;
import com.alipay.sofa.jarslink.spi.command.CommandType;

/**
 * Explain command {@link CommandProvider}
 *
 * @author qilong.zql
 * @since 0.1.0
 */
public class JSKCommandProvider implements CommandProvider {

    private static String  cmdGroupInfo  = "----Jarslink 操作命令";
    private static String  leadingSpaces = "    ";

    private CommandHandler cmdHandler    = new DefaultCommandHandler();

    @Override
    public String getHelp() {
        StringBuilder sb = new StringBuilder();
        sb.append(cmdGroupInfo).append("\n");
        sb.append(leadingSpaces)
            .append(String.format("%-20s", CommandType.INSTALL.getCommandMarker()))
            .append(CommandType.INSTALL.getCommandHelp()).append("\n");
        sb.append(leadingSpaces)
            .append(String.format("%-20s", CommandType.UNINSTALL.getCommandMarker()))
            .append(CommandType.UNINSTALL.getCommandHelp()).append("\n");
        sb.append(leadingSpaces)
            .append(String.format("%-20s", CommandType.SWITCH.getCommandMarker()))
            .append(CommandType.SWITCH.getCommandHelp()).append("\n");
        sb.append(leadingSpaces)
            .append(String.format("%-20s", CommandType.CHECK.getCommandMarker()))
            .append(CommandType.CHECK.getCommandHelp()).append("\n");
        sb.append("\n");
        return sb.toString();
    }

    @Override
    public String getHelp(String cmdMarker) {
        CommandType cmdType = cmdHandler.getCommandType(cmdMarker);
        StringBuilder sb = new StringBuilder();
        if (cmdType != CommandType.UNKNOWN) {
            sb.append(cmdGroupInfo).append("\n");
            sb.append(leadingSpaces).append(String.format("%-10s", cmdType.getCommandMarker()))
                .append(cmdType.getCommandHelp()).append("\n");
        } else {
            sb.append(String.format("没有找到命令: %s", cmdMarker)).append("\n");
        }
        sb.append("\n");
        return sb.toString();
    }

    @Override
    public boolean validate(String cmd) {
        return cmdHandler.validate(cmd);
    }

    @Override
    public String handleCommand(String command) {
        return cmdHandler.handleCommand(command);
    }
}