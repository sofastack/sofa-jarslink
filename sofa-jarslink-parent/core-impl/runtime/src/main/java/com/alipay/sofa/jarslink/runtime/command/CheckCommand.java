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

import com.alipay.sofa.ark.spi.model.Biz;
import com.alipay.sofa.jarslink.spi.command.CommandOption;
import com.alipay.sofa.jarslink.spi.command.CommandType;

import java.util.*;

/**
 * @author qilong.zql
 * @since 0.1.0
 */
public class CheckCommand extends AbstractCommand {

    public CheckCommand(CommandOption[] commandOptions) {
        super(commandOptions);
    }

    @Override
    public CommandType supportCommandType() {
        return CommandType.CHECK;
    }

    @Override
    public boolean validate() {
        boolean valid = super.validate();
        if (nameCommandOption == null && versionCommandOption != null) {
            return false;
        }
        valid = valid && (typeCommandOption.getArgs().length == 0)
                && (nameCommandOption == null || nameCommandOption.getArgs().length == 1)
                && (versionCommandOption == null || versionCommandOption.getArgs().length == 1);
        return valid;
    }

    @Override
    public String process() throws Throwable {
        Map<String, List<Biz>> bizSet = new HashMap<>();
        if (versionCommandOption != null) {
            String bizName = nameCommandOption.getArgs()[0];
            String bizVersion = versionCommandOption.getArgs()[0];
            if (bizManagerService.getBiz(bizName, bizVersion) != null) {
                bizSet.put(bizName,
                    Collections.singletonList(bizManagerService.getBiz(bizName, bizVersion)));
            }
        } else if (nameCommandOption != null) {
            String bizName = nameCommandOption.getArgs()[0];
            bizSet.put(bizName, bizManagerService.getBiz(bizName));
        } else {
            Set<String> bizNames = bizManagerService.getAllBizNames();
            for (String bizName : bizNames) {
                bizSet.put(bizName, bizManagerService.getBiz(bizName));
            }
        }
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("Biz count=%d", bizManagerService.getBizInOrder().size())).append(
            "\n");
        for (String bizName : bizSet.keySet()) {
            for (Biz biz : bizSet.get(bizName)) {
                sb.append(
                    String.format("bizName=\'%s\', bizVersion=\'%s\', bizState=\'%s\'",
                        biz.getBizName(), biz.getBizVersion(), biz.getBizState())).append("\n");
            }
        }
        LOGGER.info(String.format("Check result is:%s", sb.toString()));
        return sb.toString();
    }
}