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
import com.alipay.sofa.ark.spi.model.BizState;
import com.alipay.sofa.jarslink.spi.command.CommandOption;
import com.alipay.sofa.jarslink.spi.command.CommandType;

/**
 * @author qilong.zql
 * @since 0.1.0
 */
public class SwitchCommand extends AbstractCommand {

    public SwitchCommand(CommandOption[] commandOptions) {
        super(commandOptions);
    }

    @Override
    public CommandType supportCommandType() {
        return CommandType.SWITCH;
    }

    @Override
    public boolean validate() {
        boolean invalid = super.validate();
        invalid = invalid && commandOptions.length == 3 && nameCommandOption != null
                  && versionCommandOption != null && typeCommandOption.getArgs().length == 0;
        return invalid;
    }

    @Override
    public String process() throws Throwable {
        String bizName = nameCommandOption.getArgs()[0];
        String bizVersion = versionCommandOption.getArgs()[0];
        Biz biz = bizManagerService.getBiz(bizName, bizVersion);
        if (biz != null) {
            if (biz.getBizState() == BizState.UNRESOLVED || biz.getBizState() != BizState.RESOLVED
                || biz.getBizState() != BizState.BROKEN) {
                LOGGER.info(String.format("The specified biz's state must not be %s.",
                    biz.getBizState()));
                return String
                    .format("The specified biz's state must not be %s.", biz.getBizState());
            } else {
                bizManagerService.activeBiz(bizName, bizVersion);
                LOGGER.info(String.format("Switch biz:\'%s\' to be activated.", biz.getIdentity()));
                return String.format("Biz:\'%s\' is activated.", biz.getIdentity());
            }
        } else {
            LOGGER.info("The specified biz does not exists.");
            return "The specified biz does not exists.";
        }
    }
}