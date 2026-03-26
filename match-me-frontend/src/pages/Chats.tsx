
import { useEffect, useState } from "react";
import { GetChats } from "../api/chatApi";
import { ChatWithUser, ChatObject } from "../interfaces/Chat";
import { UserProfile } from "../interfaces/ProfileInfo";
import { GetUserProfile } from "../api/profileApi";
import MySpinner from "../components/MySpinner";
import { Card, Row, Col, Container } from "react-bootstrap";
import { useNavigate } from "react-router-dom";

export default function Chats() {

    const navigate = useNavigate();
    const [chats, setChats] = useState<ChatWithUser[]>([]);
    const [loading, setLoading] = useState<boolean>(true);

    useEffect(() => {
        const fetchChatsAndProfiles = async () => {
            try {
                setLoading(true);
                const chatsResponse: ChatObject[] = await GetChats();
                const chatsWithProfiles = (
                    await Promise.all(
                        chatsResponse.map(async (chat) => {
                            try {
                                const profile: UserProfile = await GetUserProfile(chat.otherUserId);
                                return { ...chat, user: profile };
                            } catch (error: any) {
                                if (error.status === 403) {
                                    return null;
                                }
                                throw error;
                            }
                        })
                    )
                ).filter((chat): chat is ChatWithUser => chat !== null);
                setChats(chatsWithProfiles);
            } catch (err: any) {
                console.error(err);
            } finally {
                setLoading(false);
            }
        };
        fetchChatsAndProfiles();
    }, []);

    if (loading) return <MySpinner />;

    if (chats.length === 0) {
        return (
            <Container>
                <hr className="mt-0 mb-2" />
                <Row className="g-2">
                    <div className="d-flex justify-content-center align-items-center">
                        <h1>No Chats Found !</h1>
                    </div>
                </Row>
            </Container>
        );
    }

    return (
        <Container>
            <hr className="mt-0 mb-2" />
            <Row xs={1} sm={2} md={4} className="g-4">
                {chats.map((chat) => (
                    <Col key={chat.chatRoomId}>
                        <Card
                            style={{ cursor: "pointer" }}
                            onClick={() =>
                                navigate(`/chat/${chat.chatRoomId}?otheruserid=${chat.otherUserId}`)
                            }
                        >
                            <Card.Img
                                variant="top"
                                src={chat.user.avatarUrl ? chat.user.avatarUrl : "images/noImage.png"}
                                alt={chat.user.displayName}
                                style={{ height: "150px", objectFit: "contain" }}
                            />
                            <Card.Body>
                                <Card.Title>{chat.user.displayName}</Card.Title>
                                <small className="text-danger">
                                    Unread: {chat.unreadCount}
                                </small>
                            </Card.Body>
                        </Card>
                    </Col>
                ))}
            </Row>
        </Container>
    );
}