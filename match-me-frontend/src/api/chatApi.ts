import { ChatMessage } from "../interfaces/Chat";

export async function CreateChatRoom(userB: number) {
  const token = localStorage.getItem("jwt");
  if (!token) {
    throw new Error("User is not logged in");
  }
  const response = await fetch(
    "/chat/room?otherUserId=" + userB,
    {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
        Authorization: `Bearer ${token}`,
      },
    }
  );
  if (!response.ok) throw new Error("Creating Chat Room failed");
  const responseData = await response.json();
  return responseData;
}

export async function LoadChatRoomMessages(
  chatRoomId: number,
  page = 0,
  size = 20
): Promise<{ messages: ChatMessage[]; totalPages: number }> {
  const token = localStorage.getItem("jwt");
  if (!token) throw new Error("User is not logged in");

  const response = await fetch(`/chat/chats/${chatRoomId}/messages?page=${page}&size=${size}`,
    {
      method: "GET",
      headers: {
        "Content-Type": "application/json",
        Authorization: `Bearer ${token}`,
      },
    }
  );

  if (!response.ok) throw new Error("Loading Chat Room Messages failed");

  const data = await response.json();
  return { messages: data.content, totalPages: data.totalPages };
}

export async function MarkMessagesAsRead(chatId: number) {
  const token = localStorage.getItem("jwt");
  if (!token) {
    throw new Error("User is not logged in");
  }
  const response = await fetch("chats/" + chatId + "/mark-read", {
      method: "PUT",
      headers: {
        "Content-Type": "application/json",
        Authorization: `Bearer ${token}`,
      },
    }
  );
  if (!response.ok) throw new Error("Marking chat as read failed");
  return response.status;
}

export async function GetUnreadMessageCount() {
  const token = localStorage.getItem("jwt");
  if (!token) {
    throw new Error("User is not logged in");
  }
  const response = await fetch("/chat/chats/unread-count", {
    method: "GET",
    headers: {
      "Content-Type": "application/json",
      Authorization: `Bearer ${token}`,
    },
  });
  if (!response.ok) {
    throw new Error("Getting Unread Messages Count failed");
  }
  const text = await response.text();
  return Number(text); 
}

export async function GetChats() {
  const token = localStorage.getItem("jwt");
  if (!token) {
    throw new Error("User is not logged in");
  }
  const response = await fetch("/chat/chats", {
    method: "GET",
    headers: {
      "Content-Type": "application/json",
      Authorization: `Bearer ${token}`,
    },
  });
  if (!response.ok) {
    throw new Error("Getting Unread Messages Count failed");
  }
  const responseData = await response.json();
  return responseData; 
}