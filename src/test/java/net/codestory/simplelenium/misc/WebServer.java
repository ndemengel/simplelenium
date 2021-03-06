/**
 * Copyright (C) 2013 all@code-story.net
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */
package net.codestory.simplelenium.misc;

import java.io.*;
import java.net.*;
import java.util.*;

import org.simpleframework.http.core.*;
import org.simpleframework.transport.connect.*;

public class WebServer {
  private final Container container;
  private int port;
  private SocketConnection connection;

  public WebServer(Container container) {
    this.container = container;
  }

  private WebServer start(int port) throws IOException {
    this.connection = new SocketConnection(new ContainerServer(container));
    this.connection.connect(new InetSocketAddress(port));
    this.port = port;

    return this;
  }

  public WebServer startOnRandomPort() {
    Random random = new Random();
    for (int i = 0; i < 30; i++) {
      try {
        int port = 8183 + random.nextInt(10000);
        start(port);
        return this;
      } catch (Exception e) {
        System.err.println("Unable to bind server " + e);
      }
    }
    throw new IllegalStateException("Unable to start server");
  }

  public int port() {
    return port;
  }

  public void stop() throws IOException {
    connection.close();
  }
}
