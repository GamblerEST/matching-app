import { UserBio } from "./UserBio";

export interface UserProfile {
    userId: number;
    displayName: string;
    avatarUrl: string;
    aboutMe: string;
    bio: UserBio;
    connectionId?: number;
    connected?: boolean;
    onAcceptConnection?: () => void;
    onDisconnectConnection?: () => void;
    onRejectConnection?: () => void;
    onDismissConnection?: () => void;
}

export interface ProfileInfo {
  id: number;
  userId: number;
  displayName: string | null;
  aboutMe: string | null;
  avatarUrl: string | null;
  complete: boolean | null;
}
