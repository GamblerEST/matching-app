import { Modal, Button } from "react-bootstrap";

type AlertModalProps = {
    show: boolean;
    onClose: () => void;
    title: string;
    message: string;
    variant?: "success" | "danger" | "warning" | "info";
    onConfirm?: () => void;   // 👈 optional
    confirmText?: string;
};

export default function AlertModal({
    show,
    onClose,
    title,
    message,
    variant = "info",
    onConfirm,
    confirmText = "OK",
}: AlertModalProps) {
    return (
        <Modal show={show} onHide={onClose} centered>
            <Modal.Header closeButton>
                <Modal.Title className={`text-${variant}`}>
                    {title}
                </Modal.Title>
            </Modal.Header>

            <Modal.Body>{message}</Modal.Body>

            <Modal.Footer>
                <Button variant="secondary" onClick={onClose}>
                    {onConfirm ? "Cancel" : "Close"}
                </Button>

                {onConfirm && (
                    <Button
                        variant={variant}
                        onClick={() => {
                            onConfirm();
                            onClose();
                        }}
                    >
                        {confirmText}
                    </Button>
                )}
            </Modal.Footer>
        </Modal>
    );
}