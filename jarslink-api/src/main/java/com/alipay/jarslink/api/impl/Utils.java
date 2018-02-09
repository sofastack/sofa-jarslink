/*
 *
 *  * Copyright 1999-2018 Alibaba Group Holding Ltd.
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *      http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */
package com.alipay.jarslink.api.impl;

import com.alipay.jarslink.api.Module;
import org.slf4j.Logger;

/**
 * @author ljun20160606
 */
public class Utils {
  static void destroyQuietly(Module module, Logger logger) {
    if (module != null) {
      try {
        if (logger.isDebugEnabled()) {
          logger.debug("Destroy module: {}", module.getName());
        }
        module.destroy();
      } catch (Exception e) {
        logger.error("Failed to destroy module " + module, e);
      }
    }
  }
}
