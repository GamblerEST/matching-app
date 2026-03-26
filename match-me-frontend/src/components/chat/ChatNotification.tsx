import { useState, useEffect } from "react";
import { Badge, Button, OverlayTrigger, Tooltip } from "react-bootstrap";
import { GetUnreadMessageCount } from "../../api/chatApi";
import { useNavigate } from "react-router-dom";

export default function ChatNotification() {
    const [unreadCount, setUnreadCount] = useState(0);

    const navigate = useNavigate();

    useEffect(() => {
        const fetchUnread = async () => {
            try {
                const count = await GetUnreadMessageCount();
                setUnreadCount(count);
            } catch (err) {
                console.error(err);
            }
        };
        fetchUnread();
        const interval = setInterval(fetchUnread, 5000);
        return () => clearInterval(interval);
    }, []);

    return (
        <OverlayTrigger
            placement="left"
            container={document.body}
            popperConfig={{
                strategy: "fixed",
            }}
            overlay={
                <Tooltip id="chat-tooltip">
                    {unreadCount} unread messages
                </Tooltip>
            }
        >
            <Button
                variant="light"
                className="me-2 position-relative"
                onClick={() => navigate("/chats")}
            >
                💬
                {unreadCount > 0 && (
                    <Badge
                        bg="danger"
                        pill
                        className="position-absolute top-0 start-100 translate-middle"
                    >
                        {unreadCount}
                    </Badge>
                )}
            </Button>
        </OverlayTrigger>
    );
}
