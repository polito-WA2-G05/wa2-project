import { Button, Spinner } from "react-bootstrap"

const SubmitButton = ({ disabled, loading, children }) => {
    return <Button
        variant="primary"
        type="submit"
        className="py-2 px-5 rounded-3 my-5 fw-semibold w-100"
        disabled={disabled}
    >
        {loading && (
            <Spinner
                animation="grow"
                size="sm"
                as="span"
                role="status"
                aria-hidden="true"
                className="me-2"
            />
        )}
        {children || "Submit"}
    </Button>
}

export default SubmitButton