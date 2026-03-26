import { useState, useRef, useEffect, useCallback } from "react";
import { Offcanvas, Container, Row, Col, Form, Button, Toast } from "react-bootstrap";
import { useNavigate, useParams, useSearchParams } from "react-router-dom";
import { stompClient } from "../components/chat/chatSocket";
import { ChatMessage } from "../interfaces/Chat";
import { LoadChatRoomMessages } from "../api/chatApi";
import { MarkMessagesAsRead } from "../api/chatApi";
import { GetUserProfile } from "../api/profileApi";
import { UserProfile } from "../interfaces/ProfileInfo";
import { jwtDecode } from "jwt-decode";

interface JwtPayload {
    sub: string;
}

export default function Chat() {

    const navigate = useNavigate();
    const { chatRoomId } = useParams<{ chatRoomId: string }>();
    const token = localStorage.getItem("jwt");
    const currentUserId: number | null = token ? Number(jwtDecode<JwtPayload>(token).sub) : null;
    const [messages, setMessages] = useState<ChatMessage[]>([]);
    const [message, setMessage] = useState("");

    const [isTyping, setIsTyping] = useState(false);
    const typingSentRef = useRef(false);

    const [page, setPage] = useState(0);
    const [hasMore, setHasMore] = useState(true);

    const bodyRef = useRef<HTMLDivElement>(null);
    const atBottomRef = useRef(true);
    const typingTimerRef = useRef<NodeJS.Timeout | null>(null);
    const loadingOlderRef = useRef(false);
    const markReadTimeoutRef = useRef<NodeJS.Timeout | null>(null);

    const [searchParams] = useSearchParams();
    const otherUserId = searchParams.get("otheruserid");

    const [otherUserName, setOtherUserName] = useState("");
    const [otherUserAvatar, setOtherUserAvatar] = useState("");

    useEffect(() => {
        if (!otherUserId) return;
        async function fetchProfile() {
            try {
                const profile: UserProfile = await GetUserProfile(Number(otherUserId));
                setOtherUserName(profile.displayName);
                setOtherUserAvatar(profile.avatarUrl);
            } catch (err) {
                console.log(err);
            }
        }
        fetchProfile();
    }, [otherUserId]);

    const markReadDebounced = useCallback(() => {
        if (markReadTimeoutRef.current) return;
        markReadTimeoutRef.current = setTimeout(() => {
            MarkMessagesAsRead(Number(chatRoomId));
            markReadTimeoutRef.current = null;
        }, 300);
    }, [chatRoomId]);

    useEffect(() => {

        const handleNewMessage = (e: any) => {
            const payload: ChatMessage = e.detail;
            if (payload.chatRoomId === Number(chatRoomId)) {
                setMessages((prev) => [...prev, payload]);
                setTimeout(scrollToBottomIfNeeded, 50);
                if (atBottomRef.current && payload.senderId !== currentUserId) {
                    markReadDebounced();
                }
            }
        };

        const handleTyping = (e: any) => {
            const payload = e.detail;
            if (payload.chatRoomId === Number(chatRoomId)) {
                setIsTyping(true);
                if (typingTimerRef.current) clearTimeout(typingTimerRef.current);
                typingTimerRef.current = setTimeout(() => setIsTyping(false), 2000);
                setTimeout(scrollToBottomIfNeeded, 50);
            }
        };

        window.addEventListener("newMessage", handleNewMessage);
        window.addEventListener("typing", handleTyping);

        return () => {
            window.removeEventListener("newMessage", handleNewMessage);
            window.removeEventListener("typing", handleTyping);
            if (typingTimerRef.current) {
                clearTimeout(typingTimerRef.current);
            }
        };

    }, [chatRoomId, currentUserId, markReadDebounced]);

    useEffect(() => {
        if (!chatRoomId) return;
        const loadInitialMessages = async () => {
            try {
                loadingOlderRef.current = true;
                const { messages: initialMessages, totalPages } = await LoadChatRoomMessages(
                    Number(chatRoomId),
                    0
                );
                setMessages(initialMessages.reverse());
                setPage(1);
                setHasMore(totalPages > 1);
                loadingOlderRef.current = false;
                setTimeout(() => {
                    if (bodyRef.current) bodyRef.current.scrollTop = bodyRef.current.scrollHeight;
                    markReadDebounced();
                }, 50);
            } catch (err) {
                console.error(err);
                loadingOlderRef.current = false;
            }
        };
        loadInitialMessages();
    }, [chatRoomId, currentUserId, markReadDebounced]);

    useEffect(() => {
        const container = bodyRef.current;
        if (!container) return;
        const handleScroll = async () => {
            const threshold = 20;
            atBottomRef.current =
                container.scrollHeight - container.scrollTop - container.clientHeight < threshold;
            if (container.scrollTop === 0 && hasMore && !loadingOlderRef.current) {
                loadingOlderRef.current = true;
                try {
                    const { messages: olderMessages } = await LoadChatRoomMessages(
                        Number(chatRoomId),
                        page
                    );
                    if (olderMessages.length === 0) {
                        setHasMore(false);
                        return;
                    }
                    const oldScrollHeight = container.scrollHeight;
                    setMessages((prev) => [...olderMessages.reverse(), ...prev]);
                    setPage((prev) => prev + 1);
                    setTimeout(() => {
                        if (bodyRef.current) {
                            bodyRef.current.scrollTop =
                                bodyRef.current.scrollHeight - oldScrollHeight;
                        }
                    }, 50);
                } catch (err) {
                    console.error(err);
                } finally {
                    loadingOlderRef.current = false;
                }
            }
        };
        container.addEventListener("scroll", handleScroll);
        return () => container.removeEventListener("scroll", handleScroll);
    }, [chatRoomId, page, hasMore]);

    function handleTyping(value: string) {
        setMessage(value);
        if (!typingSentRef.current) {
            stompClient.publish({
                destination: "/app/chat.typing",
                body: JSON.stringify({ chatRoomId }),
            });
            typingSentRef.current = true;
            setTimeout(() => {
                typingSentRef.current = false;
            }, 1000);
        }
    }

    async function handleSubmit(e: React.FormEvent<HTMLFormElement>) {
        e.preventDefault();
        if (!message.trim()) return;
        stompClient.publish({
            destination: "/app/chat.send",
            body: JSON.stringify({
                chatRoomId,
                message,
            }),
        });
        setMessage("");
        setTimeout(() => {
            if (atBottomRef.current) scrollToBottom();
        }, 100);
    }

    const scrollToBottom = () => {
        if (bodyRef.current) {
            bodyRef.current.scrollTop = bodyRef.current.scrollHeight;
        }
    };

    const scrollToBottomIfNeeded = () => {
        const container = bodyRef.current;
        if (!container) return;

        const distanceFromBottom =
            container.scrollHeight - container.scrollTop - container.clientHeight;

        const threshold = 350;
        if (distanceFromBottom < threshold) {
            container.scrollTop = container.scrollHeight;
        }
    };

    function formatTimestamp(input: string): string {
        const date = new Date(input);
        const hours = date.getHours().toString().padStart(2, "0");
        const minutes = date.getMinutes().toString().padStart(2, "0");
        const month = (date.getMonth() + 1).toString().padStart(2, "0");
        const day = date.getDate().toString().padStart(2, "0");
        const year = date.getFullYear();
        return `${hours}:${minutes} ${month}.${day}.${year}`;
    }

    return (
        <Offcanvas
            style={{ width: "100vw" }}
            show={true}
            onHide={() => navigate("/chats")}
            placement="start"
            backdrop={false}
        >
            <Container className="h-100 d-flex flex-column p-0">
                <Offcanvas.Header closeButton className="border-bottom">
                    <Offcanvas.Title>
                        <h5 className="mb-0">Chatting with {otherUserName}</h5>
                    </Offcanvas.Title>
                </Offcanvas.Header>

                <Offcanvas.Body ref={bodyRef} className="flex-grow-1 overflow-auto">
                    <div className="d-flex flex-column gap-2">
                        {messages.map((msg) => (
                            <Toast
                                key={msg.id}
                                bg={`${msg.senderId !== Number(otherUserId)
                                    ? "primary text-white"
                                    : ""
                                    }`}
                                className={`rounded p-2 ${msg.senderId !== Number(otherUserId)
                                    ? "align-self-end"
                                    : "align-self-start"
                                    }`}>
                                <Toast.Header closeButton={false}>
                                    {msg.senderId !== currentUserId &&
                                        <img
                                            src={otherUserAvatar === null ? "../images/noImage.png" : otherUserAvatar}
                                            width={40}
                                            height={40}
                                            className="rounded me-2"
                                            alt=""
                                        />
                                    }
                                    <strong className="me-auto">
                                        {msg.senderId === currentUserId ? "You" : [otherUserName]}
                                    </strong>
                                    <small>{formatTimestamp(msg.timestamp)}</small>
                                </Toast.Header>
                                <Toast.Body>{msg.message}</Toast.Body>
                            </Toast>
                        ))}
                        {isTyping && (
                            <Toast
                                className="align-self-start rounded p-2">
                                <Toast.Header closeButton={false}>
                                    <img
                                        src={otherUserAvatar === null ? "../images/noImage.png" : otherUserAvatar}
                                        width={40}
                                        height={40}
                                        className="rounded me-2"
                                        alt=""
                                    />
                                    <strong className="me-auto">
                                        {otherUserName} is typing ...
                                    </strong>
                                </Toast.Header>
                            </Toast>
                        )}
                    </div>
                </Offcanvas.Body>
                <Form onSubmit={handleSubmit}>
                    <div className="border-top p-2">
                        <Row className="g-2">
                            <Col xs={9}>
                                <Form.Control
                                    type="text"
                                    placeholder="Type a message..."
                                    value={message}
                                    onChange={(e) => handleTyping(e.target.value)}
                                />
                            </Col>
                            <Col xs={3}>
                                <Button className="w-100" type="submit" variant="primary">
                                    Send
                                </Button>
                            </Col>
                        </Row>
                    </div>
                </Form>
            </Container>
        </Offcanvas>
    );
}