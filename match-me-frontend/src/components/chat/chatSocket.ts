import { Client } from "@stomp/stompjs";
import SockJS from "sockjs-client";

export const stompClient = new Client({
  webSocketFactory: () => new SockJS("http://localhost:8080/ws"),
  debug: (str) => console.log(str),
  reconnectDelay: 0,
});

export function connectStomp() {
  stompClient.connectHeaders = {
    Authorization: `Bearer ${localStorage.getItem("jwt")}`,
  };
  if (!stompClient.active) {
    stompClient.activate();
  }
}

export function connectStompWithAuth() {
  const token = localStorage.getItem("jwt");
  if (stompClient.active) {
    stompClient.deactivate();
  }
  stompClient.connectHeaders = {
    Authorization: `Bearer ${token}`,
  };
  stompClient.activate();
}

export function disconnectStomp() {
  if (stompClient.active) {
    stompClient.deactivate();
  }
}