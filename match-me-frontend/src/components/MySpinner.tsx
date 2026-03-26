import Spinner from 'react-bootstrap/Spinner';

export default function MySpinner() {
    return (
        <div className="d-flex justify-content-center align-items-center" style={{ maxHeight: "calc(100vh - 56px)" }}>
            <Spinner
                className="mt-4 mb-4"
                animation="border"
                role="status"
                style={{ width: "4rem", height: "4rem" }}
            >
                <span className="visually-hidden">Loading...</span>
            </Spinner>
        </div>
    );
}