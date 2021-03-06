/*
 * Copyright 2020 Haulmont.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.jmix.ui.widget.client.timer;

import com.google.gwt.user.client.Timer;
import io.jmix.ui.widget.JmixTimer;
import com.vaadin.client.ServerConnector;
import com.vaadin.client.communication.RpcProxy;
import com.vaadin.client.communication.ServerRpcQueue;
import com.vaadin.client.communication.StateChangeEvent;
import com.vaadin.client.extensions.AbstractExtensionConnector;
import com.vaadin.shared.ui.Connect;

@Connect(JmixTimer.class)
public class JmixTimerConnector extends AbstractExtensionConnector {

    protected static int DEFERRED_DELAY_MS = 1000;

    protected JmixTimerServerRpc rpc = RpcProxy.create(JmixTimerServerRpc.class, this);

    protected boolean running = false;

    protected Timer jsTimer = new JmixTimerSource();

    public JmixTimerConnector() {
        registerRpc(JmixTimerClientRpc.class, new JmixTimerClientRpc() {
            @Override
            public void setRunning(boolean running) {
                JmixTimerConnector.this.setRunning(running);
            }

            @Override
            public void requestCompleted() {
                JmixTimerConnector.this.requestCompleted();
            }
        });
    }

    @Override
    protected void extend(ServerConnector target) {
    }

    public void setRunning(boolean running) {
        jsTimer.cancel();

        if (running) {
            jsTimer.schedule(getState().delay);
        }

        this.running = running;
    }

    public void onTimer() {
        if (running) {
            if (getState().listeners) {
                ServerRpcQueue rpcQueue = getConnection().getServerRpcQueue();

                if (rpcQueue.isEmpty() || !rpcQueue.isFlushPending()) {
                    // if application stopped we will not schedule new timer event
                    if (getConnection().isApplicationRunning()) {
                        rpc.onTimer();
                    }
                } else {
                    jsTimer.schedule(DEFERRED_DELAY_MS);
                }
            } else {
                requestCompleted();
            }
        }
    }

    public void requestCompleted() {
        if (running && getState().repeating) {
            jsTimer.schedule(getState().delay);
        }
    }

    @Override
    public void onStateChanged(StateChangeEvent stateChangeEvent) {
        super.onStateChanged(stateChangeEvent);

        // in case of page refresh we need to schedule timer here
        if (!running && getState().running && getState().repeating) {
            setRunning(true);
        }
    }

    @Override
    public JmixTimerState getState() {
        return (JmixTimerState) super.getState();
    }

    protected class JmixTimerSource extends Timer {
        @Override
        public void run() {
            onTimer();
        }
    }
}
