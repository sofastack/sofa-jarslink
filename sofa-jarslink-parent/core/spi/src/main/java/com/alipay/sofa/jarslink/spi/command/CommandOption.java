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
package com.alipay.sofa.jarslink.spi.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author qilong.zql
 * @since 0.1.0
 */
public class CommandOption {

    public static final String OPTION_MARKER  = "-";
    public static final String INVALID_OPTION = "-UNKNOWN";

    private String             option;

    private List<String>       args           = new ArrayList<>();

    public String getOption() {
        return option;
    }

    public CommandOption setOption(String option) {
        this.option = option;
        return this;
    }

    public String[] getArgs() {
        return args.toArray(new String[] {});
    }

    public CommandOption addArgs(String... args) {
        this.args.addAll(Arrays.asList(args));
        return this;
    }
}