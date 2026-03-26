import { UserProfile } from "./ProfileInfo";

export interface SendMessagePayload {
  chatRoomId: number;
  message: string;
}

export interface ChatMessageResponse {
  id: number;
  senderId: number;
  receiverId: number;
  message: string;
  createdAt: string;
}

export interface ChatMessage {
  id: number;
  chatRoomId: number;
  senderId: number;
  receiverId: number;
  message: string;
  seen: boolean;
  timestamp: string;
}

export interface ChatObject {
  chatRoomId: number;
  otherUserId: number;
  lastMessage: string;
  lastMessageTime: string;
  unreadCount: number;
}

export interface ChatWithUser extends ChatObject {
  user: UserProfile;
}
